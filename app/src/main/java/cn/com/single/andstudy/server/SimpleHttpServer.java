package cn.com.single.andstudy.server;

import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author li
 *         Create on 2018/6/25.
 * @Description
 */

public class SimpleHttpServer {

    private boolean isEnable;
    private WebConfiguration mWebConfig;
    private ServerSocket socket;
    private ExecutorService threadPool;


    private Set<IResourceUriHandler> resourceHandlers;

    public SimpleHttpServer(WebConfiguration config){
        mWebConfig = config;
        threadPool = Executors.newCachedThreadPool();

        resourceHandlers = new HashSet<>();
    }

    /**
     * 启动服务器  异步
     */
    public void startAsync(){
        isEnable = true;

        new Thread(){
            @Override
            public void run() {
                doProcSync();
            }
        }.start();
    }

    private void doProcSync() {
        try {
            InetSocketAddress socketAddr = new InetSocketAddress(mWebConfig.getPort());
            socket = new ServerSocket();
            socket.bind(socketAddr);

            while (isEnable){
                final Socket remotePeer = socket.accept();
                threadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("server","a remote peer accepted ..." + remotePeer.getRemoteSocketAddress().toString());
                        onAcceptRemotePeer(remotePeer);
                    }
                });
            }

        } catch (IOException e) {
            Log.e("server",e.toString());
            e.printStackTrace();
        }
    }

    private void onAcceptRemotePeer(Socket remotePeer) {
        //后续操作
        try {
            HttpContext httpContext = new HttpContext();
            httpContext.setUnderlySocket(remotePeer);

//            remotePeer.getOutputStream().write("congratulations,connected success".getBytes());

            InputStream nis = remotePeer.getInputStream();
            String headerLine = null;

            String resourceUri = headerLine = StreamToolkit.readLine(nis).split(" ")[1];
            Log.d("server","uri-->" + resourceUri);

            while ((headerLine = StreamToolkit.readLine(nis)) != null){
                if(headerLine.endsWith("\r\r")){
                    break;
                }
                Log.d("server","header line = " + headerLine);
                String[] pair = headerLine.split(": ");
                httpContext.addRequestHeader(pair[0],pair[1]);
            }

            for (IResourceUriHandler handler : resourceHandlers){
                if(!handler.accept(resourceUri)){
                    continue;
                }
                handler.handle(resourceUri,httpContext);
            }

        } catch (IOException e) {
            Log.e("server",e.toString());
            e.printStackTrace();
        }
    }

    public void registerResourceHandler(IResourceUriHandler handler){
        resourceHandlers.add(handler);
    }


    /**
     * 停止服务器
     */
    public void stopAsync() throws IOException {
        if(!isEnable){
            return;
        }
        isEnable = false;

        socket.close();
        socket = null;
    }

}

package cn.com.single.andstudy.server;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author li
 *         Create on 2018/6/25.
 * @Description
 */

public class ResourceInAssetsHandler implements IResourceUriHandler {

    private final Context context;
    private String acceptPrefix = "/static/";

    public ResourceInAssetsHandler(Context context){
        this.context = context;
    }

    @Override
    public boolean accept(String uri) {
        return uri.startsWith(acceptPrefix);
    }

    @Override
    public void handle(String uri, HttpContext httpContext) throws IOException {
//        OutputStream nos = httpContext.getUnderlySocket().getOutputStream();
//        PrintWriter w = new PrintWriter(nos);
//
//        w.print("HTTP/1.1 200 OK");
//        w.println();
//
//        w.println("from resource in assets hanlder");
//
//        w.flush();

        int startIndex = acceptPrefix.length();
        String assetsPath = uri.substring(startIndex + 1);
        InputStream fis = context.getAssets().open(assetsPath);

        byte[] raw = StreamToolkit.readRawFromStream(fis);

        fis.close();

        OutputStream nos = httpContext.getUnderlySocket().getOutputStream();
        PrintStream printer = new PrintStream(nos);
        printer.println("HTTP/1.1 200 OK");
        printer.println("Content-Length:" + raw.length);
        if(assetsPath.endsWith(".html")){
            printer.println("Content-Type:text/html");
        }else if(assetsPath.endsWith(".js")){
            printer.println("Content-Type:text/js");
        }else if(assetsPath.endsWith(".css")){
            printer.println("Content-Type:text/css");
        }else if(assetsPath.endsWith(".jpg")){
            printer.println("Content-Type:text/jpg");
        }else if(assetsPath.endsWith(".png")){
            printer.println("Content-Type:text/png");
        }
        printer.println();
        printer.write(raw);

    }
}

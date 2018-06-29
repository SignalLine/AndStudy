package cn.com.single.andstudy.server;

import java.io.FileOutputStream;
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

public class UploadImageHandler implements IResourceUriHandler {
    private String acceptPrefix = "/upload_image/";

    @Override
    public boolean accept(String uri) {
        return uri.startsWith(acceptPrefix);
    }

    @Override
    public void handle(String url, HttpContext httpContext) throws IOException {
//        OutputStream nos = context.getUnderlySocket().getOutputStream();
//        PrintWriter w = new PrintWriter(nos);
//
//        w.print("HTTP/1.1 200 OK");
//        w.println();
//
//        w.println("from upload image handler");
//
//        w.flush();

        String tempPath = "/mnt/sdcard/test_upload.jpg";
        long totalLength = Long.parseLong(httpContext.getRequestHeaderValue("Content-Length"));
        FileOutputStream fos = new FileOutputStream(tempPath);
        InputStream nis = httpContext.getUnderlySocket().getInputStream();
        byte[] buffer = new byte[10240];
        int nReaded = 0;
        long nLeftLength = totalLength;
        while ((nReaded = nis.read(buffer)) >0 && nLeftLength > 0){
            fos.write(buffer,0,nReaded);

            nLeftLength -= nReaded;
        }
        fos.close();

        OutputStream nos = httpContext.getUnderlySocket().getOutputStream();
        PrintStream printer = new PrintStream(nos);
        printer.println("HTTP/1.1 200 OK");
        printer.println();
    }

    protected void onImageLoaded(String path){

    }

}

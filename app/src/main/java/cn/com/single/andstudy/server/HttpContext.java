package cn.com.single.andstudy.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author li
 *         Create on 2018/6/25.
 * @Description
 */

public class HttpContext {

    private Socket mSocket;

    private Map<String,String> requestHeaders;

    public HttpContext(){
        requestHeaders = new HashMap<>();
    }

    public void setUnderlySocket(Socket socket) {
        mSocket = socket;
    }

    public Socket getUnderlySocket() {
        return mSocket;
    }

    public void addRequestHeader(String headerName, String headerValue) {
        requestHeaders.put(headerName,headerValue);
    }

    public String getRequestHeaderValue(String headerName){

        return requestHeaders.get(headerName);
    }
}

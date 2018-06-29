package cn.com.single.andstudy.server;

/**
 * @author li
 *         Create on 2018/6/25.
 * @Description
 */

public class WebConfiguration {
    /**
     * 端口号
     */
    private int port;
    private int maxParallels;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxParallels() {
        return maxParallels;
    }

    public void setMaxParallels(int maxParallels) {
        this.maxParallels = maxParallels;
    }
}

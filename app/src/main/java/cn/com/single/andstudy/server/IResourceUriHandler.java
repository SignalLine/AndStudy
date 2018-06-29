package cn.com.single.andstudy.server;

import java.io.IOException;

/**
 * @author li
 *         Create on 2018/6/25.
 * @Description
 */

public interface IResourceUriHandler {

    boolean accept(String uri);

    void handle(String url,HttpContext context) throws IOException;

}

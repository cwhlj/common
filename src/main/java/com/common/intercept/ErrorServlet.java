package com.common.intercept;

import com.alibaba.fastjson.JSONObject;
import com.common.util.AbsResponse;
import com.common.util.ReqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author chengwei
 * @date 2018/4/27 14:44
 */
public class ErrorServlet extends HttpServlet{
    private static Logger log = LoggerFactory.getLogger(ErrorServlet.class);

    @Override
    public void doPost(HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Throwable throwable = (Throwable)
                request.getAttribute("javax.servlet.error.exception");
        Integer statusCode = (Integer)
                request.getAttribute("javax.servlet.error.status_code");
        String servletName = (String)
                request.getAttribute("javax.servlet.error.servlet_name");
        if (servletName == null){
            servletName = "Unknown";
        }
        String requestUri = (String)
                request.getAttribute("javax.servlet.error.request_uri");
        if (requestUri == null){
            requestUri = "Unknown";
        }
        String uuid = UUID.randomUUID().toString();
        log.error(uuid,throwable);
        response.setStatus(200);
        this.writeJson(10,"log id:" + uuid + " url:" + requestUri ,response);
    }

    /**
     * 输出信息
     */
    protected void writeJson(int errorCode, String msg,
                             HttpServletResponse response) throws IOException {
        AbsResponse<String> abs = new AbsResponse(errorCode,msg);
        String data = JSONObject.toJSONString(abs);
        ReqUtil.writeData(response, data);
    }
}

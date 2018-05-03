package com.common.util;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * HttpServletRequest与 HttpServletResponse工具类
 * @author chengwei
 * @date 2018/4/27 14:55
 */
public class ReqUtil {
    /**
     * 获取客户机所使用的网络端口号
     *
     * @param req
     * @return
     */
    public static int getHttpPort(HttpServletRequest req) {
        return req.getRemotePort();
    }

    /**
     * 获取Cookie
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static Cookie getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] lCookies = request.getCookies();
        if (lCookies == null || cookieName == null) {
            return null;
        }
        for (int i = 0; i < lCookies.length; i++) {
            Cookie lCookie = lCookies[i];
            if (lCookie.getName().equals(cookieName)) {
                return lCookie;
            }
        }
        return null;
    }

    /**
     * 设置Cookie
     *
     * @param resp
     * @param cookieName
     * @param cookieValue
     * @param path
     * @param domain
     * @param expire
     */
    public static void setCookie(HttpServletResponse resp, String cookieName,
                                 String cookieValue, String path, String domain, int expire) {
        Cookie c = new Cookie(cookieName, cookieValue);
        c.setSecure(false);
        c.setPath(path);
        if (domain != null) {
            c.setDomain(domain);
        }
        c.setMaxAge(expire);
        resp.addCookie(c);
    }

    /**
     * 删除Cookie
     *
     * @param resp
     * @param name
     * @param path
     * @param domain
     */
    public static void delCookie(HttpServletResponse resp, String name,
                                 String path, String domain) {
        setCookie(resp, name, "", path, domain, 0);
    }

    /**
     * 获取客户端Ip
     *
     * @param req
     * @return
     */
    public static String getClientIp(HttpServletRequest req) {
        String ip = req.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("PRoxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 打印数据至控制台
     *
     * @param response
     * @param data
     * @throws IOException
     */
    public static void writeData(HttpServletResponse response, String data)
            throws IOException {
        writeData(response, data, "text/html", "UTF-8");
    }

    /**
     * 打印数据至控制台
     *
     * @param response
     * @param data
     * @param contentType
     * @param charset
     * @throws IOException
     */
    public static void writeData(HttpServletResponse response, String data,
                                 String contentType, String charset) throws IOException {
        response.setContentType(contentType);
        response.setCharacterEncoding(charset);

        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.write(data);
            pw.flush();
        } finally {
            if (null != pw) {
                pw.close();
            }
        }
    }

    /**
     * 国际化.错误代码
     *
     * @param abs
     * @param code error_code
     * @return
     * @throws Exception
     */
    public static AbsResponse<?> setErrAbs(AbsResponse<?> abs, int code) {
        return setErrAbs(abs, code, null);
    }

    /**
     * 国际化
     * 但是含有国际化资源.带有占位符
     *
     * @param abs
     * @param code        error_code   eg.  操作成功,订单号{0}.{1}
     * @param placeholder 填充占位符的信息
     * @return
     * @throws Exception
     */
    public static AbsResponse<?> setErrAbs(AbsResponse<?> abs, int code, String... placeholder) {
        return setAbs(abs, String.valueOf(code), code, placeholder);
    }

    /**
     * 国际化
     * 返回码为0,但是含有国际化资源.
     *
     * @param abs
     * @param code
     * @return
     */
    public static AbsResponse<?> setSuccAbs(AbsResponse<?> abs, int code) {
        return setAbs(abs, String.valueOf(code), 0, null);
    }

    /**
     * 国际化
     * 返回码为0,但是含有国际化资源.带有占位符
     *
     * @param abs
     * @param code        error_code   eg.  操作成功,订单号{0}.{1}
     * @param placeholder 填充占位符的信息
     * @return
     */
    public static AbsResponse<?> setSuccAbs(AbsResponse<?> abs, int code, String... placeholder) {
        return setAbs(abs, String.valueOf(code), 0, placeholder);
    }

    /**
     * 设置国际化返回值
     * @param abs  结果对象
     * @param msgCode 国际化code
     * @param placeholder 填充占位符的信息
     * @param resultCode
     * @return
     */
    private static AbsResponse<?> setAbs(AbsResponse<?> abs, String msgCode, int resultCode, String... placeholder) {
        String message = getMessage(getRequest(), msgCode, placeholder);

        if (abs == null) {
            abs = new AbsResponse();
        }
        return abs.setResult(resultCode, message);
    }

    /**
     * 设置国际化返回值
     * @param request  结果对象
     * @param msgCode 国际化code
     * @param placeholder 填充占位符的信息，可以有多个，以";,"分隔
     * @return
     */
    public static String getMessage(HttpServletRequest request, String msgCode, String placeholder) {
        RequestContext requestContext = new RequestContext(request);

        String message = null;
        if (StringUtils.isEmpty(placeholder)) {
            message = requestContext.getMessage(msgCode);
        } else {
            message = requestContext.getMessage(msgCode, placeholder.split(";,"));
        }

        return message;
    }

    /**
     * 设置国际化返回值
     * @param request  结果对象
     * @param msgCode 国际化code
     * @param placeholder 填充占位符的信息
     * @return
     */
    public static String getMessage(HttpServletRequest request, String msgCode, String[] placeholder) {
        RequestContext requestContext = new RequestContext(request);

        String message = null;
        if (placeholder == null) {
            message = requestContext.getMessage(msgCode);
        } else {
            message = requestContext.getMessage(msgCode, placeholder);
        }

        return message;
    }


//    /**
//     * 获取登陆用户..必须要通过拦截器
//     * @return
//     * @throws Exception
//     */
//    //ToDo 后边看一下@resource request的生命周期是怎么样的,它是怎么引入进来的.如果能保证线程安全,可以考虑采用这种方式获取request;
//    public static Long getLoginUserId() throws Exception {
//        UserLoginCache userInfo = (UserLoginCache) getRequest().getAttribute(Constants.TOKEN_KEY);
//        if (userInfo != null) {
//            return userInfo.getUserId();
//        } else {
//            throw new ServiceException("cant get login ID");
//        }
//    }
//
//    /**
//     * 获取用户登陆的平台
//     * @return
//     * @throws Exception
//     */
//    public static String getDevicePlatform() throws Exception {
//        String devicePlatform = (String) getRequest().getAttribute(Constants.DEVICE_PLATFORM);
//        if (StringUtils.isEmpty(devicePlatform)) {
//            throw new ServiceException("cant get login ID");
//        } else {
//            return devicePlatform;
//        }
//    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }


    public static AbsResponse<?> setErrAbs(AbsResponse<?> abs, HttpServletRequest request, int code) {
        return setErrAbs(abs, code, null);
    }
}

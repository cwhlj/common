package com.common.intercept;

import com.common.dao.RedisHelper;
import com.common.util.CodecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class CuratorSessionClientInterceptor implements HandlerInterceptor {
	private static Logger logger = LoggerFactory.getLogger(CuratorSessionClientInterceptor.class);


	public static final String DEFAULT_PARAM_NAME = "locale";
	@Resource
	private RedisHelper redisHelper;
	@Resource
	private CustomObjectMapper customObjectMapper;

	private static String[] deviceTypes = {"WEB", "ANDROID", "IOS", "iOS", "IOS_Q", "iOS_Q", "test"};

	/**
	 * 拦截器，如用户登录失效则返回失效信息
	 */
	public boolean preHandle(HttpServletRequest request,
	                         HttpServletResponse response, Object handler) throws Exception {
//		//api header检查
//		String token = request.getHeader("token");  //token
//		String sign = request.getHeader("sign");   //sign
//		String t = request.getHeader("t");     //时间戳
//		String m = request.getHeader("m");     //来源
//        String v = request.getHeader("v");    //版本(非关键字段)
////        locale
//		if (StringUtils.isEmpty(token)
//				|| StringUtils.isEmpty(sign)
//				|| StringUtils.isEmpty(t)
//				|| StringUtils.isEmpty(m)) {
//			writeJson(ApiErrorCode.RequiredArgumentsMissing.getErrorCode(), "", response);
//			logger.info("request head field null. url:" + request.getRequestURI());
//			return false;
//		}
//		if (!DateUtil.isDateTimeString(t)) {
//			writeJson(ApiErrorCode.InvalidArguments.getErrorCode(), "", response);
//			logger.info("request head time invalid. url:" + request.getRequestURI());
//			return false;
//		}
//
//		//TODO: 判断版本是否小于支持的最小版本
//		// if (VersionUtils.compare(v,VERSION)) {  }
//
//		if(ArrayUtils.contains(deviceTypes, m)){
//			String dueTokenKey = Constants.TOKEN_KEY + token;
//
//			//从redis获取token,取userId来进行验证
//			UserLoginCache logLogin = redisHelper.get(dueTokenKey, UserLoginCache.class);
//			if (logLogin == null) {
//				writeJson(4, "请登录", response);
//				return false;
//			}
//			if (logLogin.getUserStatus() == null || logLogin.getUserStatus() != 0L) {
//				writeJson(4, "登录用户不存在或已失效", response);
//				return false;
//			}
//
//			Long expiredTime = redisHelper.ttl(dueTokenKey);
//			if (needRefreshToken(expiredTime)) {
//				redisHelper.expire(dueTokenKey, Constants.TOKEN_EXPIRE_TIME);
//			}
//
////        将logLogin写入到request中,方便获取id
//			request.setAttribute(Constants.TOKEN_KEY, logLogin);
//			request.setAttribute(Constants.DEVICE_PLATFORM, m);
//		} else {
//			writeJson(4, "登录平台错误", response);
//			return false;
//		}
//
//		//国际化支持 ,这里以session 为存储,这里应该会出现负载均衡的时候出现存储多份的问题.
//		String newLocale = request.getHeader(DEFAULT_PARAM_NAME);
//		if (newLocale != null) {
//			LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
//			if (localeResolver == null) {
//				throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
//			}
//			if (!Locale.CHINA.toString().equals(newLocale) && !Locale.US.toString().equals(newLocale)) {
//				newLocale = Locale.CHINA.toString();
//			}
//			localeResolver.setLocale(request, response, StringUtils.parseLocaleString(newLocale));
//		}
		return true;
	}

	public void afterCompletion(HttpServletRequest arg0,
	                            HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}

	public void postHandle(HttpServletRequest request,
	                       HttpServletResponse response, Object handler,
	                       ModelAndView modelAndView) throws Exception {
	}

//	// 如果超时不足5分钟的就去跟新时间
//	private boolean needRefreshToken(Long expiredTime){
//		long refreshTIme = 300L;
//		return Constants.TOKEN_EXPIRE_TIME - expiredTime >= refreshTIme;
//	}

	/**
	 * 计算签名
	 * @param token
	 * @param requestTIme
	 * @param src
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	private String signMD5(String token, String requestTIme, String src,
	                       Long userId) throws IOException {
		return CodecUtils.getMD5(token + requestTIme + src + userId.toString(),
				"utf-8", true);
	}
}

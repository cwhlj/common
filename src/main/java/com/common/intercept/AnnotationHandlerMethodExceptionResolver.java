package com.common.intercept;

import com.common.exception.ServiceException;
import com.common.exception.WJException;
import com.common.util.AbsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 *
 * 统一进行异常处理
 */
public class AnnotationHandlerMethodExceptionResolver extends ExceptionHandlerExceptionResolver {
    private static Logger logger = LoggerFactory.getLogger(AnnotationHandlerMethodExceptionResolver.class);

    private String defaultErrorView;

    public String getDefaultErrorView() {
        return defaultErrorView;
    }

    public void setDefaultErrorView(String defaultErrorView) {
        this.defaultErrorView = defaultErrorView;
    }

    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request,
    		HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {
        if (handlerMethod == null) {
            return null;
        }

        Method method = handlerMethod.getMethod();

        if (method == null) {
            return null;
        }
        
        {//打印日志
	        String packageName = method.getClass().getName();
	    	logger.error(packageName + "." + method.getName(), exception);
        }

        //controller之前, 错误
        if (exception instanceof NestedRuntimeException){
            AbsResponse<?> res = null;
            res = new AbsResponse(1, exception.getMessage());
            try {
                return handleResponseBody(res, request, response);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        //查找类上的注解，因为我们项目responsbody是写在类上的
        ResponseBody responseBodyAnn = AnnotationUtils.findAnnotation(method.getDeclaringClass(), ResponseBody.class);

        //controller层抛出的错误
//        if (responseBodyAnn != null) {
			AbsResponse<?> res = null;
			if (exception instanceof WJException) {
				res = new AbsResponse((WJException)exception);
			} else if (exception instanceof HttpMessageNotReadableException) {
				res = new AbsResponse();
				res.setResult(52, "缺少必选参数");
			} else if (exception instanceof ServiceException) {
				res = new AbsResponse();
				res.setResult(50, "业务失败");
			} else {
				res = new AbsResponse();
				res.setResult(50, "业务失败!");
			}
            try {
	            logger.error(exception.getMessage(), exception);
	            return handleResponseBody(res, request, response);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
//        }


        //如果定义了ExceptionHandler则返回相应的Map中的数据
        ModelAndView returnValue = super.doResolveHandlerMethodException(request, response, handlerMethod, exception);
        if (null == returnValue) {
            returnValue = new ModelAndView();
            if (null == returnValue.getViewName()) {
                returnValue.setViewName(defaultErrorView);
            }
        }
//	    //todo 暂时 最后如果还是为空,则返回系统级错误. 后边要全局考虑异常如何记录与保存
//	    if (null == returnValue) {
//        }

        return returnValue;
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private ModelAndView handleResponseBody(AbsResponse absResponse, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpInputMessage inputMessage = new ServletServerHttpRequest(request);
        List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
        if (acceptedMediaTypes.isEmpty()) {
            acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
        }
        MediaType.sortByQualityValue(acceptedMediaTypes);
        HttpOutputMessage outputMessage = new ServletServerHttpResponse(response);
        List<HttpMessageConverter<?>> messageConverters = super.getMessageConverters();
        if (messageConverters != null) {
            for (MediaType acceptedMediaType : acceptedMediaTypes) {
                for (HttpMessageConverter messageConverter : messageConverters) {
                    if (messageConverter.canWrite(absResponse.getClass(), acceptedMediaType)) {
                        messageConverter.write(absResponse, acceptedMediaType, outputMessage);
                        outputMessage.getBody().close();
                        return new ModelAndView();
                    }
                }
            }
        }
        return null;
    }
}
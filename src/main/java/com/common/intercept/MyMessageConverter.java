package com.common.intercept;

import com.common.util.Utils;
import com.fasterxml.jackson.databind.JavaType;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by xuchun on 16/4/5.
 * <p>
 * 如果接口请求的方法里边参数是string类型的，就不走转对象，而是直接返回string类
 */
public class MyMessageConverter extends MappingJackson2HttpMessageConverter {
	@Override
	public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
		if (type.equals(String.class)) {
			return true;
		}
		return super.canRead(type, contextClass, mediaType);
	}

	@Override
	public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		if (type.equals(String.class)) {
			return Utils.read(inputMessage.getBody());
		}
		JavaType javaType = getJavaType(type, contextClass);
		return readJavaType(javaType, inputMessage);
	}

	private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
		try {
			return this.objectMapper.readValue(inputMessage.getBody(), javaType);
		} catch (IOException ex) {
			throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
		}
	}
}

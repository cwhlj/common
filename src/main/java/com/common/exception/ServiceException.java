package com.common.exception;

/**
 * <p>
 * Description: Service exception 基类,所有Service实现类异常均由此类派生
 * </p>
 *
 * @version 1.0
 */
public class ServiceException extends RuntimeException {
	public ServiceException() {
		super();
	}

	public ServiceException(String string) {
		super(string);
	}

	public ServiceException(Exception e) {
		super(e);
	}

	public ServiceException(String String, Exception e) {
		super(String, e);
	}
}
package com.common.util;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.commons.lang.StringUtils;

import javax.net.ssl.*;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.SOAPException;
import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 网络工具类。
 * Created by chengwei on 2017/3/8 16:19.
 */
public class HttpRequestUtil {
	public static final String DEFAULT_CHARSET = "UTF-8";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";

	public static final String CONTENT_ENCODING_GZIP = "gzip";
	public static int connectionTimeOut = 25000;
	public static int readTimeout = 25000;


	/**
	 * 将url参数转换成map
	 *
	 * @param param aa=11&bb=22&cc=33
	 * @return
	 */
	public static Map<String, String> getMapByUrlParams(String param) {
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isEmpty(param)) {
			return map;
		}
		String[] params = param.split("&");
		for (int i = 0; i < params.length; i++) {
			String[] p = params[i].split("=");
			if (p.length == 2) {
				map.put(p[0], p[1]);
			}
		}
		return map;
	}

	/**
	 * 将map转换成url
	 *
	 * @param map
	 * @return
	 */

	public static String getUrlParamsByMap(Map<String, Object> map) {
		if (map == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append("&");
		}
		String s = sb.toString();
		if (s.endsWith("&")) {
			s = StringUtils.substringBeforeLast(s, "&");
		}
		return s;
	}

	/**
	 * 执行HTTP POST请求。
	 *
	 * @param url    请求地址
	 * @param params 请求参数
	 * @return 响应字符串
	 */
	public static String doPost(String url, Map<String, String> params, int connectTimeout, int readTimeout) throws
			IOException {
		return doPost(url, params, DEFAULT_CHARSET, connectTimeout, readTimeout);
	}

	/**
	 * 执行HTTP POST请求。
	 *
	 * @param url    请求地址
	 * @param params 请求参数
	 * @return 响应字符串
	 */
	public static String doPost(String url, Map<String, String> params) throws IOException {
		return doPost(url, params, DEFAULT_CHARSET, connectionTimeOut, readTimeout);
	}

	/**
	 * 执行HTTP POST请求。
	 */
	public static String doPost(String url, Map<String, String> headerParams, String requestBody) throws IOException {
		String ctype = "application/json";
		return _doPost(url, ctype, requestBody.getBytes(DEFAULT_CHARSET), connectionTimeOut, readTimeout,
				headerParams);
	}

	/**
	 * 执行HTTP POST请求。
	 *
	 * @param url     请求地址
	 * @param params  请求参数
	 * @param charset 字符集，如UTF-8, GBK, GB2312
	 * @return 响应字符串
	 */
	public static String doPost(String url, Map<String, String> params, String charset,
								int connectTimeout, int readTimeout) throws IOException {
		return doPost(url, params, charset, connectTimeout, readTimeout, null);
	}

	/**
	 * 执行HTTP POST请求。
	 *
	 * @param url     请求地址
	 * @param ctype   请求类型
	 * @param content 请求字节数组
	 * @return 响应字符串
	 */
	public static String doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout) throws
			IOException {
		return _doPost(url, ctype, content, connectTimeout, readTimeout, null);
	}

	public static String doPost(String url, Map<String, String> params, String charset,
								int connectTimeout, int readTimeout, Map<String, String> headerMap) throws
			IOException {
		String ctype = "application/x-www-form-urlencoded;charset=" + charset;
		String query = buildQuery(params, charset);
		byte[] content = {};
		if (query != null) {
			content = query.getBytes(charset);
		}
		return _doPost(url, ctype, content, connectTimeout, readTimeout, headerMap);
	}

	public static String doPost(String url, Map<String, String> params, String contentType, String charset,
								int connectTimeout, int readTimeout, Map<String, String> headerMap) throws
			IOException {
		String query = buildQuery(params, charset);
		byte[] content = {};
		if (query != null) {
			content = query.getBytes(charset);
		}
		return _doPost(url, contentType, content, connectTimeout, readTimeout, headerMap);
	}

	public static String doPost(String url, String apiBody, String charset, int connectTimeout,
								int readTimeout, Map<String, String> headerMap) throws IOException {
		String ctype = "text/plain;charset=" + charset;
		byte[] content = apiBody.getBytes(charset);
		return _doPost(url, ctype, content, connectTimeout, readTimeout, headerMap);
	}

	private static String _doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout,
								  Map<String, String> headerMap) throws IOException {
		HttpURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			conn = getConnection(new URL(url), METHOD_POST, ctype, headerMap);
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			out = conn.getOutputStream();
			out.write(content);
			rsp = getResponseAsString(conn);
		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	/**
	 * 执行HTTP GET请求。
	 *
	 * @param url    请求地址
	 * @param params 请求参数
	 * @return 响应字符串
	 */
	public static String doGet(String url, Map<String, String> params) throws IOException {
		return doGet(url, params, DEFAULT_CHARSET);
	}

	/**
	 * 执行HTTP GET请求。
	 *
	 * @param url     请求地址
	 * @param params  请求参数
	 * @param charset 字符集，如UTF-8, GBK, GB2312
	 * @return 响应字符串
	 */
	public static String doGet(String url, Map<String, String> params, String charset) throws IOException {
		return doGet(url, params, charset, null);
	}

	/**
	 * 执行HTTP GET请求。
	 *
	 * @param url       请求地址
	 * @param params    请求参数
	 * @param charset   字符集，如UTF-8, GBK, GB2312
	 * @param headerMap header
	 * @return 响应字符串
	 */
	public static String doGet(String url, Map<String, String> params, String charset, Map<String, String> headerMap)
			throws IOException {
		HttpURLConnection conn = null;
		String rsp = null;

		try {
			String ctype = "application/x-www-form-urlencoded;charset=" + charset;
			String query = buildQuery(params, charset);
			conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype, headerMap);
			rsp = getResponseAsString(conn);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	private static String buildQuery(Map<String, String> params, String charset) throws IOException {
		if (params == null || params.isEmpty()) {
			return null;
		}

		StringBuilder query = new StringBuilder();
		for (String name : params.keySet()) {
			String value = params.get(name);
			if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)) {
				if (query.length() > 0) {
					query.append("&");
				}
				query.append(name).append("=").append(URLEncoder.encode(value, charset));
			}
		}

		return query.toString();
	}

	private static URL buildGetUrl(String strUrl, String query) throws IOException {
		URL url = new URL(strUrl);
		if (StringUtils.isEmpty(query)) {
			return url;
		}

		if (StringUtils.isEmpty(url.getQuery())) {
			if (strUrl.endsWith("?")) {
				strUrl += query;
			} else {
				strUrl += "?" + query;
			}
		} else {
			if (strUrl.endsWith("&")) {
				strUrl += query;
			} else {
				strUrl += "&" + query;
			}
		}

		return new URL(strUrl);
	}

	private static HttpURLConnection getConnection(URL url, String method, String ctype, Map<String, String>
			headerMap) throws IOException {
		HttpURLConnection conn = null;
		if ("https".equals(url.getProtocol())) {
			SSLContext sc = null;
			try {
				sc = SSLContext.getInstance("SSL");
				sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
						new java.security.SecureRandom());
			} catch (Exception e) {
				throw new IOException(e);
			}

			HttpsURLConnection lConn = (HttpsURLConnection) url
					.openConnection();
			lConn.setSSLSocketFactory(sc.getSocketFactory());
			lConn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn = lConn;
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}

		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Host", url.getHost());
//		conn.setRequestProperty("Accept", "text/xml,text/javascript");
		conn.setRequestProperty("User-Agent", "Http-Client");
		conn.setRequestProperty("Content-Type", ctype);
		if (headerMap != null) {
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		return conn;
	}

	protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		if (conn.getResponseCode() < 400) {
			String contentEncoding = conn.getContentEncoding();
			if (HttpRequestUtil.CONTENT_ENCODING_GZIP.equalsIgnoreCase(contentEncoding)) {
				return getStreamAsString(new GZIPInputStream(conn.getInputStream()), charset);
			} else {
				return getStreamAsString(conn.getInputStream(), charset);
			}
		} else {// Client Error 4xx and Server Error 5xx
			throw new IOException(conn.getResponseCode() + " " + conn.getResponseMessage());
		}
	}

	public static String getResponseCharset(String ctype) {
		String charset = DEFAULT_CHARSET;

		if (!StringUtils.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if (pair.length == 2) {
						if (!StringUtils.isEmpty(pair[1])) {
							charset = pair[1].trim();
						}
					}
					break;
				}
			}
		}

		return charset;
	}

	public static String getStreamAsString(InputStream stream, String charset) throws IOException {
		try {
			Reader reader = new InputStreamReader(stream, charset);
			StringBuilder response = new StringBuilder();

			final char[] buff = new char[1024];
			int read = 0;
			while ((read = reader.read(buff)) > 0) {
				response.append(buff, 0, read);
			}

			return response.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	private static class TrustAnyTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[]{};
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * 调用Web Service服务
	 *
	 * @param targetAddress eg:http://sws2.vjia.com/swsms/GetOrderService.asmx?WSDL
	 * @param namespaceURI  targetNamespace中定义的值，eg:targetNamespace="http://swsms.vjia.org/"，则值为http://swsms.vjia.org/
	 * @param localPart     eg:GetFormCodeInfo
	 * @param params
	 * @param headerName
	 * @param headerParams
	 * @param timeOut
	 * @param returnType
	 * @return
	 * @throws Exception
	 */
	public static Object callService(String targetAddress, String namespaceURI,
									 String localPart, Map<String, Object[]> params, String headerName,
									 Map<String, String> headerParams, int timeOut, QName returnType)
			throws ServiceException, SOAPException, RemoteException {
		Map<String, Map<String, String>> hParams = null;
		if (StringUtils.isNotEmpty(headerName) && headerParams != null
				&& !headerParams.isEmpty()) {
			hParams = new HashMap();
			hParams.put(headerName, headerParams);
		}

		return callService(targetAddress, namespaceURI, localPart, params,
				hParams, timeOut, returnType);
	}

	/**
	 * 调用Web Service服务
	 *
	 * @param targetAddress eg:http://sws2.vjia.com/swsms/GetOrderService.asmx?WSDL
	 * @param namespaceURI  targetNamespace中定义的值 ,eg:targetNamespace="http://swsms.vjia.org/"，则值为http://swsms.vjia.org/
	 * @param localPart     eg:GetFormCodeInfo
	 * @param params
	 * @param headerParams  请求头信息
	 * @param timeOut
	 * @param returnType
	 * @return
	 * @throws Exception
	 */
	public static Object callService(String targetAddress, String namespaceURI,
									 String localPart, Map<String, Object[]> params,
									 Map<String, Map<String, String>> headerParams, int timeOut, QName returnType)
			throws ServiceException, SOAPException, RemoteException {
		return callService(targetAddress, namespaceURI, localPart, params,
				headerParams, timeOut, returnType, null);
	}

	private static Object callService(String targetAddress, String namespaceURI,
									  String localPart, Map<String, Object[]> params,
									  Map<String, Map<String, String>> headerParams, int timeOut,
									  QName returnType, Class<?> returnClazz) throws ServiceException,
			SOAPException, RemoteException {
		Call call = (Call) new Service().createCall();
		call.setTargetEndpointAddress(targetAddress);
		call.setOperationName(new QName(namespaceURI, localPart));
		call.setTimeout(timeOut);

		//设置输入参数
		Object[] wsParams = new Object[params.keySet().size()];
		int i = 0;
		for (String key : params.keySet()) {
			Object[] objs = (Object[]) params.get(key);
			QName type = (QName) objs[0];
			call.addParameter(key, type, ParameterMode.IN);
			wsParams[i] = objs[1];
			i++;
		}

		//设置返回值类型
		if (returnClazz != null) {
			call.registerTypeMapping(returnClazz, returnType,
					new BeanSerializerFactory(returnClazz, returnType),//序列化
					new BeanDeserializerFactory(returnClazz, returnType));
			call.setReturnClass(Array.newInstance(returnClazz, 100).getClass());
		} else {
			call.setReturnType(returnType);
		}

		//设置头信息
		if (headerParams != null && !headerParams.isEmpty()) {
			for (String childName : headerParams.keySet()) {
				Map<String, String> map = headerParams.get(childName);

				SOAPHeaderElement soapHeader = new SOAPHeaderElement(namespaceURI, childName);
				soapHeader.setNamespaceURI(namespaceURI);
				for (String key : map.keySet()) {
					soapHeader.addChildElement(key).setValue(map.get(key));
				}
				call.addHeader(soapHeader);
			}
		}

		return call.invoke(wsParams);
	}

}

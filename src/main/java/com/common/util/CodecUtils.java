package com.common.util;


import org.apache.commons.codec.binary.Base64;
import org.springframework.web.util.WebUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 加密工具类。
 *
 * @since 1.0, 2016-03-09 11:16:00
 */
public abstract class CodecUtils {
	public static final char[] BToA = "0123456789abcdef".toCharArray();
	public static final char[] digital = "0123456789ABCDEF".toCharArray();
	public static final String HMAC_SHA1 = "HmacSHA1";
	public static final String HMAC_MD5 = "HmacMD5";
	public static final String SHA = "SHA";
	public static final String MD5 = "MD5";

	public static String signRequestNew(Map<String, String> params, String secret, boolean isHmac) throws IOException {
		// 第一步：检查参数是否已经排序
		String[] keys = params.keySet().toArray(new String[0]);
		Arrays.sort(keys);

		// 第二步：把所有参数名和参数值串在一起
		StringBuilder query = new StringBuilder();
		if (!isHmac) {
			query.append(secret);
		}
		for (String key : keys) {
			String value = params.get(key);
			if (StringUtils.areNotEmpty(key, value)) {
				query.append(key).append(value);
			}
		}

		// 第三步：使用MD5/HMAC加密
		byte[] bytes;
		if (isHmac) {
			bytes = encryptHMACMD5(query.toString(), secret);
		} else {
			query.append(secret);
			bytes = encryptMD5(query.toString());
		}

		// 第四步：把二进制转化为大写的十六进制
		return byte2hex(bytes);
	}

	private static byte[] encryptHMACMD5(String data, String key)
			throws IOException {
		Mac mac = getMac(HMAC_MD5, StringUtils.toBytes(key));
		byte[] bytes = mac.doFinal(StringUtils.toBytes(data));
		return bytes;
	}

	public static String getStringFromException(Throwable e) {
		String result = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		e.printStackTrace(ps);
		try {
			result = bos.toString(HttpRequestUtil.DEFAULT_CHARSET);
		} catch (IOException ioe) {
			result = "";
		}
		return result;
	}

	/**
	 * MD5加密字符串，返回加密后的字节数组
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static byte[] encryptMD5(String data) throws IOException {
		byte[] bytes = StringUtils.toBytes(data);
		return encryptMD5(bytes);
	}

	public static byte[] encryptMD5(byte[] data) throws IOException {
		MessageDigest md = getMessageDigest(MD5);
		return md.digest(data);
	}

	public static byte[] encryptHamcMD5(String data) throws IOException {
		byte[] bytes = StringUtils.toBytes(data);
		return encryptHamcMD5(bytes);
	}

	public static byte[] encryptHamcMD5(byte[] data) throws IOException {
		MessageDigest md = getMessageDigest(HMAC_MD5);
		return md.digest(data);
	}

	public static byte[] encryptSHA(String data) {
		return encryptSHA(StringUtils.toBytes(data));
	}

	public static byte[] encryptSHA(byte[] data) {
		MessageDigest md = getMessageDigest(SHA);
		return md.digest(data);
	}

	public static MessageDigest getMessageDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static Mac getMac(String algorithm, byte[] key) {
		SecretKeySpec signingKey = new SecretKeySpec(key, algorithm);
		Mac mac = null;
		try {
			mac = Mac.getInstance(signingKey.getAlgorithm());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		try {
			mac.init(signingKey);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return mac;
	}
	/**
	 * 把字节数组转换成16进制字符串, 小写
	 *
	 * @param bytes
	 * @return
	 */
	public static String byte2hex(byte[] bytes) {
		return byte2hex(bytes, false);
	}

	/**
	 *
	 * @param bytes
	 * @param upper 是否转化为大写
	 * @return
	 */
	public static String byte2hex(byte[] bytes, boolean upper) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex);
		}

		if (upper) {
			return sign.toString().toUpperCase();
		} else {
			return sign.toString();
		}
	}

	/**
	 * 把16进制字符串转换成字节数组
	 *
	 * @param hex
	 * @return
	 */
	public static byte[] hex2byte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	/**
	 * MD5加密字符串，返回加密后的16进制小写字符串
	 *
	 * @param data
	 * @return
	 */
	public static String encodeMD5ToHex(String data) throws IOException {
		byte[] md5Value = encryptMD5(data);
		return byte2hex(md5Value);
	}

	public static String encodeBase64(String data) throws IOException {
		byte[] bytes = StringUtils.toBytes(data);
		return encodeBase64(bytes);
	}

	/**
	 * 将字节数组进行Base64编码
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static String encodeBase64(byte[] bytes) throws IOException {
		return Base64.encodeBase64String(bytes);
	}

	/**
	 * 先md5加密，再进行BASE64加密
	 *
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public static String encBase64Md5(String content) throws IOException {
		byte[] md5bytes = encryptMD5(content.getBytes(HttpRequestUtil.DEFAULT_CHARSET));
		return new String(encodeBase64(md5bytes));
	}

	public static String encodeBase64(String key64, byte[] buf) {
		return Base64.encodeBase64String(rc4(key64, buf)).replace("\n", "")
				.replace("\r", "");
	}

	/**
	 * 解码一个Base64编码的String为字节数组
	 *
	 * @param base64String
	 *            Base64编码的String
	 * @return Array 解码后的字节数组
	 */
	public static byte[] decodeBase64(String base64String) {
		return new Base64().decode(base64String);
	}


	public static byte[] decodeBase64(String key64, String str) {
		return rc4(key64, Base64.decodeBase64(str));
	}

	private static byte[] rc4(String key, byte[] buf) {
		int[] box = new int[256];
		byte[] k = key.getBytes();
		int i = 0;
		int x = 0;
		int t = 0;
		int l = k.length;
		for (i = 0; i < 256; i++) {
			box[i] = i;
		}
		for (i = 0; i < 256; i++) {
			x = (x + box[i] + k[(i % l)]) % 256;

			t = box[x];
			box[x] = box[i];
			box[i] = t;
		}
		t = 0;
		i = 0;
		l = buf.length;
		int o = 0;
		int j = 0;
		byte[] out = new byte[l];
		int[] ibox = new int[256];
		System.arraycopy(box, 0, ibox, 0, 256);
		for (int c = 0; c < l; c++) {
			i = (i + 1) % 256;
			j = (j + ibox[i]) % 256;

			t = ibox[j];
			ibox[j] = ibox[i];
			ibox[i] = t;

			o = ibox[((ibox[i] + ibox[j]) % 256)];
			out[c] = ((byte) (buf[c] ^ o));
		}
		return out;
	}

	public static byte[] encryptHMACSHA1(byte[] data, byte[] key, int offset, int len) {
		Mac mac = getMac(HMAC_SHA1, key);
		mac.update(data, offset, len);
		return mac.doFinal();
	}

	public static byte[] encryptHMACSHA1(byte[][] datas, byte[] key) {
		Mac mac = getMac(HMAC_SHA1, key);
		for (byte[] data : datas) {
			mac.update(data);
		}
		return mac.doFinal();
	}

	public static byte[] encryptHMACSHA1(String[] datas, byte[] key) {
		Mac mac = getMac(HMAC_SHA1, key);
		for (String data : datas) {
			mac.update(StringUtils.toBytes(data));
		}
		return mac.doFinal();
	}

	public static String hmacSha1ToHex(byte[] data, byte[] key, int offset, int len) {
		byte[] bytes = encryptHMACSHA1(data, key, offset, len);
		return byte2hex(bytes, true);
	}

	public static String hmacSha1ToHex(byte[] data, String key, int offset, int len) {
		return hmacSha1ToHex(data, StringUtils.toBytes(key), offset, len);
	}

	public static String hmacSha1ToHex(String data, String key) {
		byte[] bytes = StringUtils.toBytes(data);
		return hmacSha1ToHex(bytes, StringUtils.toBytes(key), 0, bytes.length);
	}

	public static String getSHA(String data, String charset, boolean upper) {
		byte[] bytes = encryptSHA(StringUtils.toBytes(data, charset));
		return byte2hex(bytes, upper);
	}

	public static String getMD5(String data, String charset, boolean upper) throws IOException {
		byte[] bytes = encryptMD5(StringUtils.toBytes(data, charset));
		return byte2hex(bytes, upper);
	}

	private static byte toByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 使用指定的编码机制对 application/x-www-form-urlencoded 字符串解码。给定的编码用于确定任何 "%xy" 格式的连续序列表示的字符。
	 * 使用字符编码:utf-8
	 * @param data		要解码的 String
	 * @return
	 */
	public static String urlDecode(String data) {
		return urlDecode(data, HttpRequestUtil.DEFAULT_CHARSET);
	}

	/**
	 * 使用指定的编码机制对 application/x-www-form-urlencoded 字符串解码。给定的编码用于确定任何 "%xy" 格式的连续序列表示的字符。
	 * @param data		要解码的 String
	 * @param charset	所支持的字符编码的名称。
	 * @return
	 */
	public static String urlDecode(String data, String charset) {
		if(StringUtils.isEmpty(data)) return null;

		try {
			return URLDecoder.decode(data, charset);
		} catch (UnsupportedEncodingException e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 将字符串转换为 x-www-form-urlencoded 格式。该方法使用相应平台的默认编码作为编码机制以获取不安全字符的字节。
	 * 使用字符编码:utf-8
	 *
	 * @param data 		要转换的 String。
	 * @return 			转换好的 String。
	 */
	public static String urlEncode(String data) {
		return urlEncode(data, HttpRequestUtil.DEFAULT_CHARSET);
	}

	/**
	 * 将字符串转换为 x-www-form-urlencoded 格式。该方法使用相应平台的默认编码作为编码机制以获取不安全字符的字节。
	 *
	 * @param data 		要转换的 String。
	 * @param charset	所支持的字符编码名称。
	 * @return 			转换好的 String。
	 */
	public static String urlEncode(String data, String charset) {
		if(StringUtils.isEmpty(data)) return null;

		try {
			return URLEncoder.encode(data, charset);
		} catch (UnsupportedEncodingException e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	/**
	 * 获取文件的真实后缀名。目前只支持JPG, GIF, PNG, BMP四种图片文件。
	 *
	 * @param bytes 文件字节流
	 * @return JPG, GIF, PNG or null
	 */
	public static String getFileSuffix(byte[] bytes) {
		if (bytes == null || bytes.length < 10) {
			return null;
		}

		if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
			return "GIF";
		} else if (bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G') {
			return "PNG";
		} else if (bytes[6] == 'J' && bytes[7] == 'F' && bytes[8] == 'I' && bytes[9] == 'F') {
			return "JPG";
		} else if (bytes[0] == 'B' && bytes[1] == 'M') {
			return "BMP";
		} else {
			return null;
		}
	}

	/**
	 * 获取文件的真实媒体类型。目前只支持JPG, GIF, PNG, BMP四种图片文件。
	 *
	 * @param bytes 文件字节流
	 * @return 媒体类型(MEME-TYPE)
	 */
	public static String getMimeType(byte[] bytes) {
		String suffix = getFileSuffix(bytes);
		String mimeType;

		if ("JPG".equals(suffix)) {
			mimeType = "image/jpeg";
		} else if ("GIF".equals(suffix)) {
			mimeType = "image/gif";
		} else if ("PNG".equals(suffix)) {
			mimeType = "image/png";
		} else if ("BMP".equals(suffix)) {
			mimeType = "image/bmp";
		} else {
			mimeType = "application/octet-stream";
		}

		return mimeType;
	}

	/**
	 * 清除字典中值为空的项。
	 *
	 * @param <V> 泛型
	 * @param params 待清除的字典
	 * @return 清除后的字典
	 */
	public static <V> Map<String, V> cleanupMap(Map<String, V> params) {
		if (params == null || params.isEmpty()) {
			return null;
		}

		Map<String, V> result = new HashMap<String, V>(params.size());
		for (Entry<String, V> entry : params.entrySet()) {
			if (entry.getValue() != null) {
				result.put(entry.getKey(), entry.getValue());
			}
		}

		return result;
	}

    //
//	请求参数组装分下列3步，以最后第三步获取到的请求为准
//	1. 请求参数按照key=value&key=value方式拼接的未签名原始字符串：
//	1
//	app_id=2015052600090779&biz_content={"timeout_express":"30m","seller_id":"","product_code":"QUICK_MSECURITY_PAY","total_amount":"0.01","subject":"1","body":"我是测试数据","out_trade_no":"IQJZSRC1YMQB5HU"}&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http://domain.merchant.com/payment_notify&sign_type=RSA&timestamp=2016-08-25 20:26:31&version=1.0
    public static String sortsEncodeParams(Map<String, String> params, boolean needEncoder){
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        for (String key : keys) {
            String value = params.get(key);

            if (StringUtils.areNotEmpty(key, value)) {
                if(query.length() > 0){
                    query.append("&");
                }

                if(needEncoder){
                    value = CodecUtils.urlEncode(value);
                }
                query.append(key+"=").append(value);
            }
        }
        return query.toString();
    }

    public static String sortsDencodeParams(Map<String, String> params, boolean needDecoder){
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        for (String key : keys) {
            String value = params.get(key);

            if (StringUtils.areNotEmpty(key, value)) {
                if(query.length() > 0){
                    query.append("&");
                }
                if(needDecoder){
                    value = CodecUtils.urlDecode(value);
                }

                query.append(key).append(value);
            }
        }
        return query.toString();
    }
}

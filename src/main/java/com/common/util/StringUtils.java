package com.common.util;

import org.springframework.web.util.WebUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 字符串工具类。
 *
 * @since 1.0 2016-03-09 10:06:59
 */
public abstract class StringUtils {
    /**
     * 检查指定的字符串是否为空。
     * <ul>
     * <li>SysUtils.isEmpty(null) = true</li>
     * <li>SysUtils.isEmpty("") = true</li>
     * <li>SysUtils.isEmpty("   ") = true</li>
     * <li>SysUtils.isEmpty("abc") = false</li>
     * </ul>
     *
     * @param value 待检查的字符串
     * @return true/false
     */
    public static boolean isEmpty(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(value.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * 检查对象是否为数字型字符串,包含负数开头的。
     */
    public static boolean isNumeric(Object obj) {
        if (obj == null) {
            return false;
        }
        char[] chars = obj.toString().toCharArray();
        int length = chars.length;
        if (length < 1)
            return false;

        int i = 0;
        if (length > 1 && chars[0] == '-')
            i = 1;

        for (; i < length; i++) {
            if (!Character.isDigit(chars[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查指定的字符串列表是否不为空。
     */
    public static boolean areNotEmpty(String... values) {
        boolean result = true;
        if (values == null || values.length == 0) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }

    /**
     * 把通用字符编码的字符串转化为汉字编码。
     */
    public static String unicodeToChinese(String unicode) {
        StringBuilder out = new StringBuilder();
        if (!isEmpty(unicode)) {
            for (int i = 0; i < unicode.length(); i++) {
                out.append(unicode.charAt(i));
            }
        }
        return out.toString();
    }

    /**
     * 将驼峰字符串转为下划线分隔的字符串
     *
     * @param name
     * @return
     */
    public static String toUnderlineStyle(String name) {
        StringBuilder newName = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    newName.append("_");
                }
                newName.append(Character.toLowerCase(c));
            } else {
                newName.append(c);
            }
        }
        return newName.toString();
    }

    public static String toString(byte[] bytes) {
        return toString(bytes, 0, bytes.length, HttpRequestUtil.DEFAULT_CHARSET);
    }

    public static String toString(byte[] bytes, String charset) {
        return toString(bytes, 0, bytes.length, charset);
    }
    
    public static String toString(Object obj) {
    	return obj != null ? obj.toString() : null;
    }

    public static String toString(byte[] data, int offset, int length, String charset) {
        if (data == null) {
            return null;
        }
        try {
            return new String(data, offset, length, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static byte[] toBytes(String data) {
        return toBytes(data, HttpRequestUtil.DEFAULT_CHARSET);
    }

    public static byte[] toBytes(String data, String charset) {
        if (data == null) {
            return null;
        }
        try {
            return data.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static boolean isDouble(String arg0) {
        if (arg0 == null || arg0.length() == 0) return false;
        try {
            Double.parseDouble(arg0);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 如果arg为null或转化为小定时为"null", 则返回"", 否则返回本身
     *
     * @param arg
     * @return
     */
    public static String nullToEmpty(String arg) {
        return arg == null || "null".equals(arg.toLowerCase()) ? "" : arg;
    }

    /**
     * 将字符串数组中的元素中间使用separator分隔连接为一个字符串
     *
     * @param array
     * @param separator
     * @return
     */
    public static String joinString(String[] array, String separator) {
        return joinString(Arrays.asList(array), separator);
    }

    /**
     * 将字符串List中的元素中间使用separator分隔连接为一个字符串
     *
     * @param array
     * @param separator
     * @return
     */
    public static String joinString(List<String> list, String separator) {
        return joinString(list.iterator(), separator);
    }
    
    public static String joinLong(List<Long> list, String separator) {
        return joinString(list.iterator(), separator);
    }
    
    public static String joinInteger(List<Integer> list, String separator) {
        return joinString(list.iterator(), separator);
    }
    
    /**
     * 将字符串Collection中的元素中间使用separator分隔连接为一个字符串
     *
     * @param array
     * @param separator
     * @return
     */
    public static String joinString(Collection<String> coll, String separator) {
        return joinString(coll.iterator(), separator);
    }

    /**
     * 将字符串Collection中的元素中间使用separator分隔连接为一个字符串
     *
     * @param list
     * @param separator
     * @return
     */
    public static String joinString(Iterator<?> list, String separator) {
        StringBuilder strBuilder = new StringBuilder();
        while (list.hasNext()) {
            if (strBuilder.length() > 0) {
                strBuilder.append(separator);
            }
            strBuilder.append(list.next());
        }
        return strBuilder.toString();
    }
    
    public static String[] split(String str, String separator) {
        List<String> list = splitToList(str, separator);
        return list.toArray(new String[list.size()]);
    }

    public static List<String> splitToList(String str, String separator) {
        List<String> list = new ArrayList<String>();
        if (isEmpty(str)) {
            return list;
        }

        if (isEmpty(separator)) {
            list.add(str);
            return list;
        }
        int lastIndex = -1;
        int index = str.indexOf(separator);
		if (-1 == index) {
            list.add(str);
            return list;
        }
        while (index >= 0) {
            if (index > lastIndex) {
                list.add(str.substring(lastIndex + 1, index));
            } else {
                list.add("");
            }

            lastIndex = index;
            index = str.indexOf(separator, index + 1);
            if (index == -1) {
                list.add(str.substring(lastIndex + 1, str.length()));
            }
        }
        return list;
    }

    public static String lpad(String str, int length, String pad) {
        while (str.length() < length) {
            str = pad + str;
        }
        return str;
    }

    public static String rpad(String str, int length, String pad) {
        while (str.length() < length) {
            str = str + pad;
        }
        return str;
    }

    /**
     * 首字母大写
     *
     * @param srcStr
     * @return
     */
    public static String firstCharacterToUpper(String srcStr) {
        return srcStr.substring(0, 1).toUpperCase() + srcStr.substring(1);
    }

    /**
     * 去掉最后一位
     *
     * @param srcStr
     * @return
     */
    public static String cutLast(String srcStr) {
        return srcStr.substring(0, srcStr.length() - 1);
    }

    /**
     * 替换字符串并让它的下一个字母为大写
     *
     * @param srcStr
     * @param org
     * @param ob
     * @return
     */
    public static String replaceUnderlineAndfirstToUpper(String srcStr,
                                                         String org, String ob) {
        String newString = "";
        int first = 0;
        while (srcStr.indexOf(org) != -1) {
            first = srcStr.indexOf(org);
            if (first != srcStr.length()) {
                newString = newString + srcStr.substring(0, first) + ob;
                srcStr = srcStr
                        .substring(first + org.length(), srcStr.length());
                srcStr = firstCharacterToUpper(srcStr);
            }
        }
        newString = newString + srcStr;
        return newString;
    }

    public static String stringCut(String aString, int aLen, String aHintStr) {
        if (aString == null) return aString;
        int lLen = aString.length(), i;
        for (i = 0; aLen >= 0 && i < lLen; ++i)
            if (isBigChar(aString.charAt(i))) aLen -= 2;
            else --aLen;
        if (aLen >= 0) return aString;
        if (aHintStr == null) return aString.substring(0, i - 1);

        aLen -= aHintStr.length();
        for (; aLen < 0 && --i >= 0; )
            if (isBigChar(aString.charAt(i))) aLen += 2;
            else ++aLen;

        return aHintStr == null ? aString.substring(0, i) : aString
                .substring(0, i) + aHintStr;
    }

    public static boolean startsWith(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.startsWith(prefix)) {
            return true;
        }
        if (str.length() < prefix.length()) {
            return false;
        }
        String lcStr = str.substring(0, prefix.length());
        return lcStr.equals(prefix);
    }
    public static boolean isBigChar(char c) {
        return c < 0 || c > 256;
    }

    public static String trim(String arg) {
        return arg == null ? null : arg.trim();
    }

    /**
     * 浮点数运算,小数位4位
     *
     * @param v1
     * @param v2
     * @param type 运算类型：+：add,-:sub, /:divide, *:multiply
     * @return
     */
    public static double comDouble(Double v1, Double v2, char type) {
        return comDouble(v1, v2, type, 4);
    }

    /**
     * 浮点数运算, 四舍五入
     *
     * @param v1
     * @param v2
     * @param type  运算类型：+：add,-:sub, /:divide, *:multiply
     * @param scale 小数点位数
     * @return
     */
    public static double comDouble(Double v1, Double v2, char type, int scale) {
        BigDecimal ret = new BigDecimal("0.0");
        if (v1 != null) {
            ret = ret.add(new BigDecimal(v1.toString()));
        }
        if (v2 != null) {
            switch (type) {
                case '+':
                    ret = ret.add(new BigDecimal(v2.toString()));
                    break;
                case '-':
                    ret = ret.subtract(new BigDecimal(v2.toString()));
                    break;
                case '/':
                    ret = ret.divide(new BigDecimal(v2.toString()), 10, BigDecimal.ROUND_HALF_EVEN);
                    break;
                case '*':
                    ret = ret.multiply(new BigDecimal(v2.toString()));
                    break;
                default:
                    break;
            }
        }
        return ret.setScale(scale, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }


    /**
     * 将相同属性的值从源对象复制到目标对象上,源对象上字段为空则保留目标对象上的值
     *
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();
        for (Field f : sourceFields) {
            try {
                String fieldName = f.getName();
                Field field = sourceClass.getDeclaredField(fieldName);
                Class type = field.getType();
                String head = "";
                if (type.getName().contains("boolean")) {
                    head = "is";
                } else {
                    head = "get";
                }
                String sourceMethodName = head + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method sourceMethod = sourceClass.getMethod(sourceMethodName, null);
                Object fieldValue = sourceMethod.invoke(source, null);
                if (fieldValue != null) {
                    String targetMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Method targetMethod = targetClass.getMethod(targetMethodName, fieldValue.getClass());
                    targetMethod.invoke(target, fieldValue);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
//			} catch (NoSuchFieldException e) {
                System.out.println("目标对象无 " + f.getName() + "属性");
//				e.printStackTrace();
            } catch (IllegalAccessException e) {
                System.out.println("不能访问非公开属性");
//				e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String d = "==--==";
        System.out.println(toString(d.getBytes()));
        System.out.println(comDouble(8D, 3D, '/'));
    }

    /**
     * 判断src中是否包含args中的任何一个
     *
     * @param src
     * @param args
     * @return
     */
    public static boolean contains(String src, String... args) {
        if (args.length == 0)
            return false;

        for (String arg : args) {
            if (contain(src, arg)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contain(String src, String arg) {
        if (src == null) {
            return arg == null;
        }
        return arg != null ? src.contains(arg) : false;
    }

    /**
     * 字符串顺序反转
     *
     * @param str, 会执行trim
     * @return
     */
    public static String reverseString(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        str = str.trim();
        int strl = str.length();
        if (strl > 1) {
            StringBuffer sb = new StringBuffer(str);
            return sb.reverse().toString();
        }
        return str;
    }

    /**
     * <pre>
     * 格式化数值字符串，将科学记数法的数据转换成字符串，
     * 并进行四舍五入，如果传入字符串为空则返回空
     * </pre>
     *
     * @param v     待格式化的字符串
     * @param scale 精度
     * @return String 格式化后数字字符串
     */
    public static String roundStr(String v, int scale) {
        if (StringUtils.isNumeric(v)) {
            return "";
        }
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }

        BigDecimal b = new BigDecimal(v);
        b = b.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return b.toPlainString();
    }

    /**
     * <pre>
     * 格式化数值，将科学记数法的数据转换成，
     * 并进行四舍五入，如果传入字符串为空则返回空
     * </pre>
     *
     * @param v     待格式化的字符串
     * @param scale 精度
     * @return double 格式化后数字
     */
    public static double setScale(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }

        BigDecimal b = new BigDecimal(v);
        b = b.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return b.doubleValue();
    }

    /**
     * @author wujiapeng
     * 对平台账号platformAccount进行加密
     * 23465789456123 => 1233*********123
     */
    public static String fakePlatformAccount(String platformAccount){
    	if(platformAccount == null || platformAccount.isEmpty()) return platformAccount;
    	String fakeVal = "";
    	int iSeq = 0 ;
    	for(int i = 0;i<platformAccount.length();i++){
    		//不需要判断指定字符是否是一个数字。
//    		if(!Character.isDigit(platformAccount.charAt(i))){
//    			continue;
//    		}
    		iSeq++;
    		fakeVal += (iSeq > 3 && iSeq <=12 ) ? "*":platformAccount.charAt(i);
    	}
    	return fakeVal;
    }

    /**
     * 拼装方法中参数
     * @param args
     * @return
     */
    public static String getStringArgs(Object... args) {
        StringBuilder str = new StringBuilder();
        for (Object arg : args) {
            str.append(toString(arg));
            str.append(",");
        }
        return str.toString();
    }

    /**
     * 在指定的字符串固定长度插入指定分隔符，每3位一段，用空格分隔开的字符串
     *
     * eg:
     *   input:
     *   data      = 1234567890
     *   sectionLen = 3
     *   separator = -
     *   return:123-456-789-0
     *
     * @param data
     * @param sectionLen
     * @param separator
     * @return
     */
    public static String splitByFix(String data, int sectionLen, String separator) {
        if (isEmpty(data)) return data;

        int secs = (data.length() + sectionLen - 1) / sectionLen;

        int x = 0;
        StringBuilder strBuilder = new StringBuilder(data);
        for (int i = 1; i < secs; i++) {
            int idx = i * sectionLen + x;

            strBuilder.insert(idx, separator);

            x += separator.length();
        }
        return strBuilder.toString();
    }
}




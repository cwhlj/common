package com.common.util.xml;

import com.common.util.DateUtil;
import com.common.util.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * XML 解析器
 * 将对象转换为XML格式的字符串
 * @author chengwei
 * @date 2018/9/5 11:06
 */
public class XmlConverter {
    private static String XML_VERSION = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

    /**
     * 将Object转换根节点名称为xmlData的xml字符串<br/>
     * eg:<br/>
     * &lt;?xml version="1.0" encoding="utf-8"?&gt;<br/>
     * &lt;xmlData&gt;...&lt;/xmlData&gt;
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static String convert(Object obj) {
        return convert("xmlData", obj);
    }

    /**
     * 将Object转换为以rootName为根节点名称的xml字符串
     *
     * @param rootName
     * @param obj
     * @return
     * @throws Exception
     */
    public static String convert(String rootName, Object obj) {
        return convert(rootName, obj, true);
    }
    /**
     * 将Object转换为以rootName为根节点名称的xml字符串
     *
     * @param rootName 根节点名称
     * @param obj	要转换的obj对象
     * @param appendVersion 是否添加xml版本信息
     * @return
     * @throws Exception
     */

    public static String convert(String rootName, Object obj,
                                 boolean appendVersion) {
        StringBuilder rtn = null;
        if (appendVersion) {
            rtn = new StringBuilder(XML_VERSION);
            rtn.append("\n");
        } else {
            rtn = new StringBuilder();
        }
        appendElement(rtn, rootName, getXmlString(obj));
        return rtn.toString();
    }

    public static String getXmlString(Object obj) {
        if (obj == null) {
            return "";
        }
        if ((obj instanceof Number)) {
            return numberToString((Number) obj);
        }
        if ((obj instanceof Boolean)) {
            return obj.toString();
        }
        if ((obj instanceof String)) {
            return obj.toString().replace("<", "&lt;").replace(">", "&gt;")
                    .replace("&", "&amp;").replace("'", "&apos;")
                    .replace("\"", "&quot;");
        }
        if ((obj instanceof Map)) {
            return mapToString((Map) obj);
        }
        if ((obj instanceof Class)) {
            return ((Class) obj).getSimpleName();
        }
        if ((obj instanceof Enum)) {
            return obj.toString();
        }
        if ((obj instanceof Date)) {
            return DateUtil.toDateTimeString((Date) obj);
        }
        if (((obj instanceof Collection)) || (obj.getClass().isArray())) {
            return getXmlArray(obj);
        }
        return reflectObject(obj);
    }

    private static String mapToString(Map map) {
        StringBuilder strBuilder = new StringBuilder("");
        Iterator keys = map.keySet().iterator();

        while (keys.hasNext()) {
            Object o = keys.next();
            if (o != null) {
                appendElement(strBuilder, o.toString(), getXmlString(map.get(o)));
                strBuilder.append("\n");
            }
        }
        return strBuilder.toString();
    }

    private static String getXmlArray(Object obj) {
        if (obj == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        if (obj instanceof Collection) {
            Iterator iterator = ((Collection) obj).iterator();
            while (iterator.hasNext()) {
                Object rowObj = iterator.next();
                sb.append(getXmlString(rowObj));
            }
        }
        if (obj.getClass().isArray()) {
            int arrayLength = Array.getLength(obj);
            for (int i = 0; i < arrayLength; i++) {
                Object rowObj = Array.get(obj, i);
                sb.append(getXmlString(rowObj));
            }
        }
        return sb.toString();
    }

    private static String reflectObject(Object bean) {
        Class c = bean.getClass();
        Method[] methods = c.getMethods();

        String ss = bean.getClass().getSimpleName();
        if (Character.isUpperCase(ss.charAt(0))) {
            if (ss.length() == 1) {
                ss = ss.toLowerCase();
            } else if (Character.isUpperCase(ss.charAt(1))) {
                ss = ss.substring(0, 2).toLowerCase() + ss.substring(2);
            } else {
                ss = ss.substring(0, 1).toLowerCase() + ss.substring(1);
            }
        }
        StringBuilder sb = new StringBuilder("<" + ss + "> \n");
        for (int i = 0; i < methods.length; i++) {
            try {
                Method method = methods[i];
                String name = method.getName();
                String key = "";
                if (name.startsWith("get")) {
                    key = name.substring(3);
                } else if (name.startsWith("is")) {
                    key = name.substring(2);
                }
                if ((key.length() > 0)
                        && (Character.isUpperCase(key.charAt(0)))
                        && (method.getParameterTypes().length == 0)) {
                    if (key.length() == 1) {
                        key = key.toLowerCase();
                    } else if (!Character.isUpperCase(key.charAt(1))) {
                        key = key.substring(0, 1).toLowerCase()
                                + key.substring(1);
                    }
                    Object elementObj = method.invoke(bean, null);
                    if ((elementObj instanceof Class)) {
                        continue;
                    }
                    String str = getXmlString(elementObj);
                    if (!StringUtils.isEmpty(str)) {
                        appendElement(sb, key, str).append("\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sb.append("</").append(ss).append(">\n");
        return sb.toString();
    }

    private static String numberToString(Number number) {
        if (number == null) {
            return "";
        }
        String string = number.toString();
        if ((string.indexOf('.') > 0) && (string.indexOf('e') < 0)
                && (string.indexOf('E') < 0)) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    public static StringBuilder appendElement(StringBuilder strBuilder, String key, String value) {
        strBuilder.append("<").append(key).append(">");
        strBuilder.append(value);
        strBuilder.append("</").append(key).append(">");
        return strBuilder;
    }
}

package com.common.intercept;

import com.common.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

/**
 * 解决SpringMVC使用@ResponseBody返回json时，日期格式默认显示为时间戳的问题。
 * 需配合<mvc:message-converters>使用
 * *
 */
public class CustomObjectMapper extends ObjectMapper {

    public CustomObjectMapper() {
        //时间格式
        this.setDateFormat(new SimpleDateFormat(DateUtil.DATE_TIME_FORMAT));
        //属性为number时转化为string类型 --ukid类的长度18位太长,js只能接收到16位的.
        this.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);

        //序列化时----------
        //null值转化为""
//        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
//            @Override
//            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//                jsonGenerator.writeString("");
//            }
//        });
//      //空值不序列化
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        //反序列化时------------
        //遇到无法解析数据时不报错
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //遇到空字符串就返回NULL(主要针对List为""时的情况)
        this.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

//        //去掉默认的时间戳格式
//        this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        //设置为中国上海时区
//        this.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        this.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
//        //反序列化时，属性不存在的兼容处理
//        this.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

//        this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        //单引号处理
//        this.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }
}
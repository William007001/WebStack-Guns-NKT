package com.nikati.manage;

import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;

/**
 * 全局常量对象
 *
 * @author william
 * @version 1.0.0
 */
public interface GlobalConsts extends Serializable {

    /**
     * 智云单点缓存key
     */
    String ZCLOUD_SSO_TOKEN = "zcloud:sso:token";

    /**
     * UTF-8 字符编码定义
     */
    String CHARSET = "UTF-8";


    /**
     * 默认最后的错误信息存储对象 - KEY
     */
    String DEFAULT_LAST_ERROR = "digital_last_error";

    String ACCESS_TOKEN = "access_token";

    /**
     * 允许跨域的域名
     */
    String CROSS_DEFAULT_DOMAIN_KEY = "platform.cross.domain";

    /**
     * FastJson 序列化忽略字段
     */
    String SERIALIZE_IGNORE_KEY = "platform.serialize.fastjson.ignore";

    /**
     * FastJson 序列化脱敏字段
     */
    String SERIALIZE_DESENSITIZATION = "platform.serialize.fastjson.desensitization";

    /**
     * 消息格式
     * fastjson jackson
     */
    String MESSAGE_JSON_TYPE = "platform.serialize.type";


    String ZCLOUD_SSO_CLIENT_ID = "platform.zcloud.sso.client_id";
    /**
     * 智云单点服务地址
     * http://113.108.148.250:30018/login
     */
    String ZCLOUD_SSO_SERVER_URL = "platform.zcloud.sso.server_url";

    /**
     * 单点成功后回调业务系统地址
     * http://localhost:8081/api/login/authorize
     */
    String ZCLOUD_SSO_REDIRECT_URL = "platform.zcloud.sso.redirect_url";

    /**
     * 单点获取用户信息接口
     * http://113.108.148.250:30098/oauth/user_info
     */
    String ZCLOUD_SSO_USER_INFO_URL = "platform.zcloud.sso.user_info_url";

    /**
     * 单点成功回调接口重定向地址
     */
    String ZCLOUD_SSO_SUCCESS_URL = "platform.zcloud.sso.success_url";

    /**
     * 单点退出地址
     */
    String ZCLOUD_SSO_LOGOUT_URL = "platform.zcloud.sso.logout_url";

    /**
     * 默认导出数据前端传递文件名header名称
     */
    String DEFAULT_EXPORT_FILE_NAME = "Export-File-Name";

    /**
     * FastJson 默认序列化规则
     */
    SerializerFeature[] SERIALIZER_FEATURE = {
            SerializerFeature.QuoteFieldNames,
            //  输出key时是否使用双引号, 默认为true

            //SerializerFeature.UseSingleQuotes,
            // 使用单引号而不是双引号, 默认为false

            SerializerFeature.WriteMapNullValue,
            // 是否输出值为null的字段, 默认为false

            SerializerFeature.WriteEnumUsingToString,
            //  Enum输出name()或者original, 默认为false

            SerializerFeature.WriteEnumUsingName,
            //  用枚举name()输出

            //SerializerFeature.UseISO8601DateFormat,
            //   Date使用ISO8601格式输出，默认为false

            SerializerFeature.WriteNullListAsEmpty,
            //      List字段如果为null, 输出为[], 而非null

            SerializerFeature.WriteNullStringAsEmpty,
            //字符类型字段如果为null,输出为”“,而非null

            //SerializerFeature.WriteNullNumberAsZero,
            //       数值字段如果为null, 输出为0, 而非null

            SerializerFeature.WriteNullBooleanAsFalse,
            //        Boolean字段如果为null, 输出为false, 而非null

            SerializerFeature.SkipTransientField,
            //如果是true，类中的Get方法对应的Field是transient，序列化时将会被忽略。默认为true

            //SerializerFeature.SortField,
            //按字段名称排序后输出。默认为false

            SerializerFeature.WriteTabAsSpecial,
            //把\t做转义输出，默认为false

            //SerializerFeature.PrettyFormat,
            //         结果是否格式化, 默认为false

            //SerializerFeature.WriteClassName,
            //序列化时写入类型信息，默认为false。反序列化是需用到

            SerializerFeature.DisableCircularReferenceDetect,
            //消除对同一对象循环引用的问题，默认为false

            SerializerFeature.WriteSlashAsSpecial,
            // 对斜杠’/’进行转义

            //SerializerFeature.BrowserCompatible,
            //将中文都会序列化为\\uXXXX格式，字节数会多一些，,但是能兼容IE 6，默认为false

            SerializerFeature.WriteDateUseDateFormat,
            //全局修改日期格式,默认为false。JSON.DEFFAULT_DATE_FORMAT = “yyyy-MM-dd”;JSON.toJSONString(obj,SerializerFeature.WriteDateUseDateFormat);

            SerializerFeature.DisableCheckSpecialChar
            //一个对象的字符串属性中如果有特殊字符如双引号，将会在转成json时带有反斜杠转移符。如果不需要转义，可以使用这个属性。默认为false
    };

    /**
     * 跳过内置过滤器 uri 配置，支持多个，逗号分隔
     */
    String SKIP_BUILD_IN_FILTER_URI = "platform.filter.skip.build-in-uri";

}

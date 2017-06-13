package com.yunqitechbj.clientandroid.http;

/**
 * 域名基类
 * Created by mengwei on 2016/10/28.
 */

public class HostUtil {
//    public static HostType type = HostType.DEV_HOST;
    public static HostType type = HostType.DEBUG_HOST;
//    	 public static HostType type = HostType.PUBLIC_HOST;
//    	 public static HostType type = HostType.DEMO_HOST;

    private static final String HOST_DEV = "http://dev.pkgapi.yqtms.com/";
    private static final String HOST_DEBUG = "http://qa.pkgapi.yqtms.com/";
    private static final String HOST_PUBLIC = "http://pkgapi.yqtms.com/";
    private static final String HOST_DEMO = "http://demo.pkgapi.yqtms.com/";

    private static final String WEB_HOST_DEV = "http://dev.yqtms.com/";
    private static final String WEB_HOST_DEBUG = "http://qa.yqtms.com/";
    private static final String WEB_HOST_PUBLIC = "http://yqtms.com/";
    private static final String WEB_HOST_DEMO = "http://demo.yqtms.com/";

    public static String getWebHost() {

        switch (type) {
            case DEV_HOST:
                return  WEB_HOST_DEV;
            case DEBUG_HOST:
                return WEB_HOST_DEBUG;
            case PUBLIC_HOST:
                return WEB_HOST_PUBLIC;
            case DEMO_HOST:
                return WEB_HOST_DEMO;
            default:
                break;
        }
        return null;
    }

    public static String getApiHost() {

        switch (type) {
            case DEV_HOST:
                return HOST_DEV;
            case DEBUG_HOST:
                return HOST_DEBUG;
            case PUBLIC_HOST:
                return HOST_PUBLIC;
            case DEMO_HOST:
                return HOST_DEMO;

            default:
                break;
        }
        return null;
    }

    public enum HostType {
        DEV_HOST,DEBUG_HOST, PUBLIC_HOST, DEMO_HOST
    }
}

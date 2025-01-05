package com.chuan.wojcommon.utils;

/**
 * 系统工具类
 * @Author: chuan-wxy
 * @Date: 2024/10/24 13:09
 * @Description:
 */
public class SystemUtil {
    public static String getOS() {
        return System.getProperty("os.name").toLowerCase();
    }
    public static String getShell() {
        String os = getOS();
        if(os.contains("win")){
            return "cmd";
        }else if (os.contains("linux")){
            return "bash";
        }else if (os.contains("mac")){
            return "darwin";
        }else if (os.contains("nix") || os.contains("nux")){
            return "nix";
        }else if (os.contains("sunos")){
            return "solaris";
        }else{
            return "unknown";
        }
    }
    public static String getOSVersion() {
        return System.getProperty("os.version").toLowerCase();
    }
    public static String getArch() {
        return System.getProperty("os.arch").toLowerCase();
    }
    public static String getUserName() {
        return System.getProperty("user.name");
    }
    public static String getUserHome() {
        return System.getProperty("user.home");
    }
    public static String getUserDir() {
        return System.getProperty("user.dir");
    }
    public static String getUserPath() {
        return System.getProperty("user.dir");
    }
    public static String getUserHomePath() {
        return System.getProperty("user.home");
    }
    public static String getUserDataDir() {
        return System.getProperty("user.data");
    }
    public static String getUserHomeDataDir() {
        return System.getProperty("user.home");
    }
    public static String getUserDataDataDir() {
        return System.getProperty("user.data");
    }
}

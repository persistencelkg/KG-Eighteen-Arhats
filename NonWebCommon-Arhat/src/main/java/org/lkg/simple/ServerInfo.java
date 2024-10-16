package org.lkg.simple;


public class ServerInfo {

    private static String serverName;

    private static String env;

    private static String innerIp = NetUtils.getLocalAddress();

    private static int port;

    public static void setServerName(String serverName) {
        ServerInfo.serverName = serverName;
    }

    public static void setEnv(String env) {
        ServerInfo.env = env;
    }

    public static void setPort(int port) {
        ServerInfo.port = port;
    }

    public static void setInnerIp(String innerIp) {
        ServerInfo.innerIp = innerIp;
    }

    public static String name() {
        return serverName;
    }

    public static String env() {
        return env;
    }

    public static int port() {
        return port;
    }

    public static String innerIp() {
        return innerIp == null ? innerIp = NetUtils.getLocalAddress() : innerIp;
    }

    public static String detail() {
        return String.format("[%s@%s]", serverName, ipPort());
    }

    public static String ipPort() {
        return String.format("%s:%s", innerIp, port);
    }
}

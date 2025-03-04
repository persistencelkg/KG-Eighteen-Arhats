package org.lkg.utils;

import lombok.Getter;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * if use must enable config <code>@EnableDynamicApollo</code>
 */
public class ServerInfo {

    private static String serverName;

    private static String env;

    private static String innerIp = NetUtils.getLocalAddress();

    private static int port;

    @Getter
    private static String hostname;

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
        return String.format("[%s@%s]", hostname, ipPort());
    }

    public static String ipPort() {
        return String.format("%s:%s", innerIp, port);
    }

    public static void setHostname(String hostname) {
        try {
            ServerInfo.hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            ServerInfo.hostname = "unknown";
        }

    }

    public static void main(String[] args) {
        double a = 42.0d;
        BigInteger num = BigInteger.valueOf(Long.MAX_VALUE );
        System.out.println(num.add(BigInteger.valueOf(2)));
        System.out.println(Long.MAX_VALUE);
        System.out.println(num);
        System.out.println(42 == 42.0);
        System.out.println(a == 42);
        String s = "aa";
        System.out.println(Integer.toHexString(s.hashCode()));
        s +="aaa";
        System.out.println(Integer.toHexString(s.hashCode()));
        System.out.println("--");
        List<String> str = new ArrayList<>();
        for (String s1 : str) {
            System.out.println(s1.hashCode());
        }

    }
}

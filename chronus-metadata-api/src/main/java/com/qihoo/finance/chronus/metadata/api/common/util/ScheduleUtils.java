package com.qihoo.finance.chronus.metadata.api.common.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


/**
 * 调度处理工具类
 *
 * @author xiongpu
 */
public class ScheduleUtils {

    private static String LOCAL_IP;

    public static String getLocalIP() {
        try {
            if (LOCAL_IP != null) {
                return LOCAL_IP;
            }
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            LOCAL_IP = ip.getHostAddress();
                            return LOCAL_IP;
                        }
                    }
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

}

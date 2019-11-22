package com.qihoo.finance.chronus.common.util;

/**
 * Created by xiongpu on 2017/6/29.
 */

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

@Slf4j
public class NetUtils {
    public static final String LOCALHOST = "127.0.0.1";
    public static final String ANYHOST = "0.0.0.0";
    private static final int RND_PORT_START = 30000;
    private static final int RND_PORT_RANGE = 10000;
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65535;
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$");
    private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
    private static volatile InetAddress LOCAL_ADDRESS = null;

    public NetUtils() {
    }

    public static int getRandomPort() {
        return 30000 + RANDOM.nextInt(10000);
    }

    public static int getAvailablePort() {
        ServerSocket ss = null;

        int var2;
        try {
            ss = new ServerSocket();
            ss.bind((SocketAddress) null);
            int var1 = ss.getLocalPort();
            return var1;
        } catch (IOException var12) {
            var2 = getRandomPort();
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException var11) {
                    ;
                }
            }

        }

        return var2;
    }

    public static int getAvailablePort(int port) {
        if (port <= 0) {
            return getAvailablePort();
        } else {
            for (int i = port; i < '\uffff'; ++i) {
                ServerSocket ss = null;

                try {
                    ss = new ServerSocket(i);
                    int var3 = i;
                    return var3;
                } catch (IOException var13) {
                    ;
                } finally {
                    if (ss != null) {
                        try {
                            ss.close();
                        } catch (IOException var12) {
                        }
                    }

                }
            }

            return port;
        }
    }


    public static boolean isValidAddress(String address) {
        return ADDRESS_PATTERN.matcher(address).matches();
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address != null && !address.isLoopbackAddress()) {
            String name = address.getHostAddress();
            return name != null && !"0.0.0.0".equals(name) && !"127.0.0.1".equals(name) && IP_PATTERN.matcher(name).matches();
        } else {
            return false;
        }
    }

    public static String getLocalIP() {
        InetAddress address = getLocalAddress();
        return address == null ? "127.0.0.1" : address.getHostAddress();
    }
    public static String getLocalHost() {
        InetAddress address = getLocalAddress();
        return address == null ? "localhost" : address.getHostName();
    }

    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        } else {
            InetAddress localAddress = getLocalAddress0();
            LOCAL_ADDRESS = localAddress;
            return localAddress;
        }
    }

    private static InetAddress getLocalAddress0() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null) {
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address)) {
                                        return address;
                                    }
                                } catch (Throwable var5) {
                                    log.warn("Failed to retriving address address, " + var5.getMessage(), var5);
                                }
                            }
                        }
                    } catch (Throwable var7) {
                        log.warn("Failed to retriving address address, " + var7.getMessage(), var7);
                    }
                }
            }
        } catch (Throwable var8) {
            log.warn("Failed to retriving address address, " + var8.getMessage(), var8);
        }

        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable var6) {
            log.error("Failed to retriving address address, " + var6.getMessage(), var6);
        }
        return localAddress;
    }


    public static String getIpByHost(String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException var2) {
            return hostName;
        }
    }
}

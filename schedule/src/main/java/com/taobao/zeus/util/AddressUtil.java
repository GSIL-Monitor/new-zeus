package com.taobao.zeus.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by yxl on 2017/9/16.
 */
public class AddressUtil {
    /**
     * 获取 本地 ip
     * @return
     * @throws Exception
     */
    public static String getLocalInetAddress() throws Exception {
        Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress inetAddress = null;
        while (allNetInterfaces.hasMoreElements())
        {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements())
            {
                inetAddress = (InetAddress) addresses.nextElement();
                if (inetAddress != null && inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress())
                {
                   return inetAddress.getHostAddress() ;
                }
            }
        }
        throw new Exception("不能获取本地 ip") ;
    }
}

package com.klinker.droneos.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.ThreadLocalRandom;

import com.klinker.droneos.utils.math.Point;

public class Utils {

    ///// Async Helpers ////////////////////////////////////////////////////////

    /**
     * Makes the current thread sleep/pause for the specified amount of time.
     * <p>
     * <code>1 s = 1000 ms</code>
     *
     * @param ms The number of milliseconds the current thread should sleep for.
     */
    public static void sleep(long ms) {
        if (ms <= 0) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Log.e("utils", "Error trying to sleep", e);
        }
    }


    ///// General Utilities ////////////////////////////////////////////////////

    /**
     * This function takes remaps a value in a range to a different range
     * linearly.
     *
     * For example: map(1, 0, 2, 1, 3) = 2.
     * Explanation: The number 1 is 50% of the range between
     * 0 and 2, therefor 50% the range of 1 to 3 is 2.
     *
     * @param value The value respect to the original range.
     * @param lowOriginal The lower point for the original range.
     * @param highOriginal The upper point for the original range.
     * @param lowNew The lower point for the new range.
     * @param highNew The upper point for the new range.
     * @return The scaled value for value in the original range scaled to a
     *         new range.
     */
    public static double map(double value, 
                             double lowOriginal, double highOriginal, 
                             double lowNew, double highNew) {
        return lowNew + (highNew - lowNew) * (value - lowOriginal) / (highOriginal - lowOriginal);
    }

    /**
     * This function takes remaps a value in a range to a different range
     * linearly, but if the value is outside the initial range it will limit the 
     * ouput to the new ranges.
     * 
     * @param value The value respect to the original range.
     * @param lowOriginal The lower point for the original range.
     * @param highOriginal The upper point for the original range.
     * @param lowNew The lower point for the new range.
     * @param highNew The upper point for the new range.
     * @return The scaled value for value in the original range scaled to a
     *         new range.
     */
    public static double mapLimit(double value, 
                                  double lowOriginal, double highOriginal, 
                                  double lowNew, double highNew) {
        if (value < lowOriginal) 
            return lowNew;
        if (value > highOriginal)
            return highNew;
        return map(value, lowOriginal, highOriginal, lowNew, highNew);
    }

    // Source: https://stackoverflow.com/questions/639695/how-to-convert-latitude-or-longitude-to-meters
    public static double distanceConversion(Point p1, Point p2) { // generally used geo measurement function
        double lat1 = p1.y;
        double lon1 = p1.x;
        double lat2 = p2.y;
        double lon2 = p2.x;

        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180)
                * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d * 1000; // meters
    }

    public static double random(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    /**
    * Returns an <code>InetAddress</code> object encapsulating what is most likely the machine's LAN IP address.
    * <p/>
    * This method is intended for use as a replacement of JDK method <code>InetAddress.getLocalHost</code>, because
    * that method is ambiguous on Linux systems. Linux systems enumerate the loopback network interface the same
    * way as regular LAN network interfaces, but the JDK <code>InetAddress.getLocalHost</code> method does not
    * specify the algorithm used to select the address returned under such circumstances, and will often return the
    * loopback address, which is not valid for network communication. Details
    * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037">here</a>.
    * <p/>
    * This method will scan all IP addresses on all network interfaces on the host machine to determine the IP address
    * most likely to be the machine's LAN address. If the machine has multiple IP addresses, this method will prefer
    * a site-local IP address (e.g. 192.168.x.x or 10.10.x.x, usually IPv4) if the machine has one (and will return the
    * first site-local address if the machine has more than one), but if the machine does not hold a site-local
    * address, this method will return simply the first non-loopback address found (IPv4 or IPv6).
    * <p/>
    * If this method cannot find a non-loopback address using this selection algorithm, it will fall back to
    * calling and returning the result of JDK method <code>InetAddress.getLocalHost</code>.
    * <p/>
    *
    * @throws UnknownHostException If the LAN address of the machine cannot be found.
    */
    public static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it immediately...
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException(
                    "Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

}

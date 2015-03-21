import java.io.IOException;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import javax.swing.JOptionPane;

public class QuickSync {
    public static String selfIp;
    public static String client1;
    public static String client2;
    public static ListOfPeers peerList;
    public static Sync sync;
    public static String serverPort;
    public static String cloudIP;
    public static String hostName = "";  
    public static boolean isCloud = true;

    public static void main(String[] args) {

        int count = 0;

        ArrayList < String > client = new ArrayList < String > ();
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            NetworkInterface intface = null;
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                String interfaceName = n.getName();
                if (interfaceName.equals("wlan0") || interfaceName.equals("en0")) {
                    intface = n;
                } else if (interfaceName.equals("eth0")) {
                    intface = n;
                } else {
                    continue;
                }

            }
            if (intface != null) {
                Enumeration ee = intface.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (i.getHostAddress().indexOf(":") == -1) {
                        selfIp = i.getHostAddress();
                    }
                }
            }
            System.out.println("\nQuickSync:main:Self IP is:" + selfIp);
        } catch (Exception e) {}

        try {
            Random rand = new Random();
            hostName = "52.10.100.25";  
            cloudIP = "52.10.100.25";  
            Integer weight = 0;  

            PeerNode self = new PeerNode(hostName, selfIp, weight);  
            peerList = new ListOfPeers(self);
            System.out.println("Node Details:\n" + hostName + "\n" + cloudIP + "\n" + weight + "\n\n");
        } catch (Exception e) {

        }


        if (cloudIP.equals(selfIp)) {
            System.out.println("\nQuickSync:main:I am the cloud");
            isCloud = true;
        }

         
         
        if (!isCloud) {
             
             
             
             
             

             
             
             
        }
         
        Thread sync = new Thread(new Sync(peerList));
        sync.start();

         
        ServerSocket ss = null;

        try {
            serverPort = "60011";
            ss = new ServerSocket(Integer.parseInt(serverPort));
        } catch (Exception e) {

        }
        Socket s = null;

         
        while (true) {
            try {
                s = ss.accept();
                System.out.println("\nQuickSync:main:Server Accepted Connection");
                Thread server = new Thread(new TcpServer(ss, s, peerList));
                System.out.println("ClientServer:Created Thread for " + s.getRemoteSocketAddress());
                server.start();
            } catch (Exception e) {
                try {
                    s.close();
                } catch (Exception e1) {}
            }
        }
    }

    static String getCloudIp() {
        return cloudIP;
    }
}

class Comp implements Comparator < PeerNode > {

    @Override
    public int compare(PeerNode pn1, PeerNode pn2) {
        if (pn1.getWeight() > pn2.getWeight()) return 1;
        return -1;
    }
}
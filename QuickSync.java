import java.io.IOException;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import javax.swing.JOptionPane;

public class QuickSync{
    public static String selfIp;
    public static String client1;
    public static String client2;
    public static ListOfPeers peerList;
    public static Sync sync;
    public static String serverPort;
    public static String cloudIP;
    public static String hostName;
    public static boolean isCloud=false;

    public static void main(String[] args){

        int count = 0;
        System.setProperty("java.net.preferIPv4Stack" , "true");

        ArrayList<String> client = new ArrayList<String>();
        try{
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            NetworkInterface intface = null;
            while(e.hasMoreElements())
            {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                String interfaceName = n.getName();
                if(interfaceName.equals("wlan0") || interfaceName.equals("en0")){
                    intface = n;
                }else if(interfaceName.equals("eth0")){
                    intface = n;
                }else{
                    continue;
                }
                
            }
            if(intface != null){
                Enumeration ee = intface.getInetAddresses();
                while (ee.hasMoreElements())
                {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if(i.getHostAddress().indexOf(":") == -1){
                        selfIp = i.getHostAddress();
                    }
                }
            }
            System.out.println("\nQuickSync:main:Self IP is:"+selfIp);
        }catch(Exception e){
        }

        hostName = args[0]; //Change it to get automatic hostname
        cloudIP=  args[1];//JOptionPane.showInputDialog("Enter CloudIP");
        Integer weight = Integer.parseInt(args[2]);


	PeerNode self;
        if(weight == 0)
        {
           System.out.println("\nQuickSync:main:I am the cloud");
           isCloud = true; 
	   self = new PeerNode(cloudIP, cloudIP, weight);//Integer.parseInt(JOptionPane.showInputDialog("Enter Weight:")));
        }
        else
	{
	   self = new PeerNode(hostName, selfIp, weight);//Integer.parseInt(JOptionPane.showInputDialog("Enter Weight:")));
	}
	peerList = new ListOfPeers(self);
        /* By pass 2 arguments */
    		/*
        if(args.length > 2){
            while(count < args.length - 2){
                client.add(args[0]);
                count++;
            }
        }*/
        if(!isCloud)
        {
          /* Start UDP client thread. Broadcast IP is hard-coded to "255.255.255.255" for now. Change if needed. */
          Thread udpClient = new Thread(new UdpClient(Integer.parseInt("8886"), "255.255.255.255", client, peerList));
          //Thread udpClient = new Thread(new UdpClient("FF7E:230::1234", client, peerList));
          //Thread udpClient = new Thread(new UdpClient("235.1.1.1", client, peerList));
          udpClient.start();
        
          /* Start UDP server thread */
          Thread udpServer = new Thread(new UdpServer(Integer.parseInt("61001"), peerList));
          udpServer.start();
        } 
        /* Start Sync thread */
        Thread sync = new Thread(new Sync(peerList));
        sync.start();
        
        /* Start a TCP receive thread */
        ServerSocket ss = null;
        
        try
        {
            serverPort = "60011";
            ss = new ServerSocket(Integer.parseInt(serverPort));
        }
        
        catch(Exception e)
        {
            
        }
        Socket s=null;

        /*Server listening for Incoming Connections and will spawn new Servers*/
        while(true){
            try {
                InetAddress cloudInetAddress = InetAddress.getByName(cloudIP);
                if(!isCloud && cloudInetAddress.isReachable(1000))
                {
                    System.out.println("\nQuickSync:main:Adding Cloud to Peer List");
                    peerList.addPeerNode(new PeerNode(cloudIP,cloudIP, 0)); 
                }
                else if (!isCloud )
                {
                    System.out.println("\nQuickSync:main:Removing Cloud to Peer List");
                    peerList.removePeerNode(cloudIP);
                }
                s = ss.accept();
                System.out.println("\nQuickSync:main:Server Accepted Connection");
                Thread server = new Thread(new TcpServer(ss, s,peerList));
                System.out.println("ClientServer:Created Thread for " +s. getRemoteSocketAddress());
                server.start();   
            } 
            catch (Exception e) {
                try{
                    s.close();
                }catch(Exception e1){}
            }
        }
    }

    static String getCloudIp(){
        return cloudIP;
    }
}

    class Comp implements Comparator<PeerNode>
    {
        @Override
        public int  compare(PeerNode pn1,PeerNode pn2)
        {
            if( pn2.getWeight() > pn1.getWeight())
            return -1;
            if(pn1.getWeight() == pn2.getWeight())
            return 0;
            return 1 ;
        }
    }

import java.net.*;
import java.io.*;
import java.lang.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UdpServer implements Runnable
{
    private DatagramSocket serverSocket;
    //private MulticastSocket serverSocket;
    private int port;
    private ListOfPeers peerList;
    
    UdpServer(int port, ListOfPeers peerList){
        try{
            serverSocket = new DatagramSocket(port);
            //serverSocket = new MulticastSocket(port);
            this.serverSocket.setBroadcast(true);
            //InetAddress addr = InetAddress.getLocalHost();
            //String ipAddress = addr.getHostAddress();
            //serverSocket.joinGroup(InetAddress.getByName("235.1.1.1"));
            this.peerList = peerList;
        }catch(Exception e){
            e.printStackTrace();
        }
        this.port = port;
    }
    
    public void run(){
        byte[] recvBuf = new byte[15000];
        String data = new String();
        DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
        
        int counter = 1;
        /* Start listening on the UDP server port */
        while(true){
            try{

                if((counter++) % 100 ==0 )
                {
                     System.out.println("***************UdpServer:run:Udp Server Running");
                }
                this.serverSocket.receive(recvPacket);
                
                if(recvPacket.getAddress().getHostAddress().toString().compareTo(peerList.getSelf().getIPAddress()) == 0 ||
                recvPacket.getAddress().getHostAddress().toString().compareTo("127.0.0.1") == 0){
                    continue;
                }
                
                ByteArrayInputStream b = new ByteArrayInputStream(recvPacket.getData());
                ObjectInputStream o = new ObjectInputStream(b);
                data = (String)o.readObject();
                JSONObject JSONobj = (JSONObject)(JSONManager.convertStringToJSON(data));
                if(JSONobj.get("type").equals("Control")){
                    data = (String)JSONobj.get("value");
                }
                
                String[] components = data.split(":");

                /* Check if it is from the same client. Parse peerList */
                if(peerList.getPeerNode(components[0]) != null){
                    continue;
                }
                
                PeerNode peer = new PeerNode(components[0], recvPacket.getAddress().getHostAddress(), Integer.parseInt(components[1]));
                
                /* Store the sender info in the linked list */
                peerList.addPeerNode(peer);
                System.out.println("UdpServer:run: Added to peer list size " + components[0]);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}

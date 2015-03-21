import java.net.*;
import java.io.*;
import java.lang.*;
import java.security.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UdpClient implements Runnable {
    private DatagramSocket clientSocket;
     
    private String broadcastAdd;
    private int port;
    private String selfIp;
    private ListOfPeers peerList;
    private ArrayList < String > client;


    UdpClient(int port, String broadcastAdd, ArrayList < String > client, ListOfPeers peerList) {

        System.out.println("UdpClient:UdpClient: Starting UDP client on port" + port);
        try {
            this.clientSocket = new DatagramSocket();
            this.clientSocket.setBroadcast(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.broadcastAdd = broadcastAdd;
        this.port = port;
        this.client = client;
        this.peerList = peerList;
    }

     

    void sendUdpPacket(byte[] data, String remoteIp) {
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(remoteIp), 61001);
            this.clientSocket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void broadcastUdpPacket(byte[] data) {
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(broadcastAdd), 61001);
            this.clientSocket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void run() {
        int i;
        byte[] buf = new byte[100];

        try {
             
            String data = peerList.getSelf().getId() + ":" + String.valueOf(peerList.getSelf().getWeight());

            JSONObject JSONobj = JSONManager.getJSON(data);
            data = JSONobj.toString();

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(data);
            buf = b.toByteArray();
            System.out.println("UdpClient:run: Created data");
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }

         
        int counter = 1;
        while (true) {

            if ((counter++) % 100 == 0) {
                System.out.println("***************UdpClient:run:Udp Client Running");
            }
            broadcastUdpPacket(buf);
            sendUdpPacket(buf, QuickSync.getCloudIp());
             
             


            try {
                Thread.sleep(1000);  
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.*;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TcpServer implements Runnable {
    private ServerSocket ss;
    private Socket s;
    ListOfPeers peerList;
    static String homeDir = System.getProperty("user.home");
    static String folder = "QuickSync";
    static String path = homeDir + "/" + folder;
    PeerNode peerNode;  
    String peerId;

    public TcpServer(ServerSocket ss, Socket s, ListOfPeers peerList) {
        try {
            this.ss = ss;
            this.s = s;
            this.peerList = peerList;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int count = 0;
        System.out.println("TcpServer:run: Server running " + s.toString());
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
             
            OutputStream outToServer = s.getOutputStream();
            out = new ObjectOutputStream(outToServer);
            InputStream inFromServer = s.getInputStream(); in = new ObjectInputStream(inFromServer);
        } catch (Exception e) {}
        while (!s.isClosed()) {
            try {
                JSONObject obj = getMessage(s, in );

                if (obj == null) continue;
                 
                 
                if (obj.get("type").equals("Init")) {
                     
                    String data = (String) obj.get("value");
                    String[] components = data.split(":");
                     
                     
                    if (peerList.getPeerNode(components[0]) != null) {
                        continue;
                    }

                    PeerNode peer = new PeerNode(components[0], s.getInetAddress().getHostAddress(), Integer.parseInt(components[1]));
                    peer.setSocket(s);

                    peer.setOutputStream(out);
                    peer.setInputStream( in );

                     
                    peerList.addPeerNode(peer);
                     
                    peerList.printPeerList();

                    peerNode = peerList.getPeerNodeFromIP(s.getInetAddress().getHostAddress());
                    peerId = peerNode.getId();
                } else if (obj.get("type").equals("Control")) {
                    PeerNode peer = peerList.getPeerNodeFromSocket(s);  
                    System.out.println("TcpServer:run: Got an Control Message from:" + peer.getId());
                    String str = (String) obj.get("value");
                     
                    File file = new File(path + "/" + str);
                    JSONObject obj2 = JSONManager.getJSON(file);
                    peer.sendMessage(obj2);
                } else if (obj.get("type").toString().substring(0, 4).equals("File")) {

                     
                    String fileContent = (String) obj.get("value");
                     
                    String receivedPath = obj.get("type").toString().substring(4);
                     
                    String[] splits = receivedPath.split("/");
                    int noOfSplits = splits.length;
                    String newPath = path;

                    while (noOfSplits > 1) {
                        newPath = newPath + "/" + splits[splits.length - noOfSplits];
                        File theDir = new File(newPath);
                        if (!theDir.exists()) {
                            theDir.mkdir();
                        }
                        noOfSplits--;
                    }

                    File file = new File(path + "/" + receivedPath);
                     
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bos.write(fileContent.getBytes());
                    bos.close();
                     
                     
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss z");

                    final TimeZone utc = TimeZone.getTimeZone("UTC");
                    dateFormatter.setTimeZone(utc);

                    String t = dateFormatter.format(new java.util.Date());
                    System.out.println("_" + peerNode.getId() + "_" + t + "_" + receivedPath);
                     
                     
                } else if (obj.get("type").equals("ArrayList")) {
                     
                    ArrayList list = (ArrayList) obj.get("value");
                     
                    PeerNode peerNode = peerList.getPeerNodeFromSocket(s);
                     
                    peerList.printPeerList();
                    if (peerNode == null) {
                        System.out.println("TcpServer:run: \nCouldn't find the PeerNode");
                    } else {
                         
                        ListOfFiles lof = new ListOfFiles(list);
                        peerNode.setListOfFiles(lof);
                    }
                } else if (obj.get("type").equals("HashMap")) {
                     
                    HashMap map = (HashMap) obj.get("value");
                    peerList.getSelf().setHashMapFilePeer(map);
                } else {
                    System.out.println("TcpServer:run: Got an Invalid Message from:" + s.getInetAddress().toString() + " " + obj);
                }
            } catch (Exception e) {

                try {
                    PeerNode nodeToBeRemoved = peerList.getPeerNodeFromSocket(s);
                    peerList.updateHashMapBeforeRemovingNode(nodeToBeRemoved);
                    System.out.println("Removing PeerNode:" + nodeToBeRemoved.getId() + ":" + peerList.removePeerNode(nodeToBeRemoved));
                    peerList.printPeerList();
                    s.close();
                    System.out.println("TcpServer:run: closing socket " + s.toString());
                    e.printStackTrace();
                    System.out.println("TcpServer:run:Exeception in TcpServer");
                    break;
                } catch (Exception ee) {

                }
            }
        }

    }

    JSONObject getMessage(Socket s, ObjectInputStream in ) {
        JSONObject obj = null;

        try {
             
            if ( in == null) System.out.println("INputStream is null");
            if (s.isInputShutdown()) System.out.println("Shutdown----Available bytes to read are:" + in );
            Message obj2 = (Message) in .readObject();
            obj = (JSONObject) obj2.obj;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                s.close();
            } catch (Exception ee) {}
             

        }
        return obj;
    }

    void find(int x) {
        System.out.println("========Inside find" + x + "===========");
        Iterator < PeerNode > it = peerList.getList().iterator();
        while (it.hasNext()) {
            PeerNode peerNode = it.next();
            ArrayList < String > lof = peerNode.getListOfFiles().getList();
            System.out.println("For peer node:" + peerNode.getId() + " list of files is:" + lof.toString());
        }
        System.out.println("========Leaving find()===========");
    }

}
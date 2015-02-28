import java.io.*;
import java.util.*;
import java.net.*;

public class PeerNode implements Serializable{
    private String peerId;
    private int peerWeight;
    private ListOfFiles lof;
    private HashMap<String, ArrayList<String>> hMap;
    String ipAddress ;

    PeerNode(String peerId){
        Random ran = new Random();
        this.peerWeight = ran.nextInt(60000);
        this.peerId = peerId;
        lof = new ListOfFiles();
        hMap = new HashMap<String, ArrayList<String>>();
        this.ipAddress=ipAddress;
    }
    
    PeerNode(String peerId, String ipAddress, int peerWeight){
        this.peerId = peerId;
        this.peerWeight = peerWeight;
        lof = new ListOfFiles();
        hMap = new HashMap<String, ArrayList<String>>();
        this.ipAddress=ipAddress;
    }
    
    public String getId()
    {
        return peerId;
    }
    
    public int getWeight(){
        return peerWeight;
    }
    
    void setListOfFiles(ListOfFiles lof)
    {
        this.lof = lof;
    }
    
    ListOfFiles getListOfFiles( )
    {
        return lof;
    }
    
    void setHashMapFilePeer(HashMap<String, ArrayList<String>> hashFromController)
    {
        hMap = hashFromController;
    }
    
    HashMap<String, ArrayList<String>> getHashMapFilePeer( )
    {
        return hMap;
    }
    
    void setIPAddress(String ipAddress )
    {
        this.ipAddress=ipAddress;
        
    }
    
    String getIPAddress()
    {
        return ipAddress;
    }
}


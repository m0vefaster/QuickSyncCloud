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

public class TcpServer implements Runnable
{
    private ServerSocket ss;
    private Socket s;
    ListOfPeers peerList;
    static String homeDir = System.getProperty("user.home");
    static String folder = "QuickSync";
    static String path = homeDir + "/" + folder ;
    PeerNode peerNode;//Communicating with this node
    String peerId;

    public TcpServer(ServerSocket ss, Socket s, ListOfPeers peerList)
    {
	try{
            this.ss = ss;
            this.s = s;
            this.peerList = peerList;
	    peerNode = peerList.getPeerNodeFromIP(s.getInetAddress().getHostAddress());
            peerId=peerNode.getId();
	}
	catch(Exception e)
	{
            e.printStackTrace();
	}
    }

    @Override
    public void run()
    {
        int count =0;
        System.out.println("TcpServer:run: Server running "+s.toString());
        ObjectOutputStream out  = null;
        ObjectInputStream in =  null;

        try{
            /* Create out and in streams */
            OutputStream outToServer = s.getOutputStream();
            out = new ObjectOutputStream(outToServer);
            InputStream inFromServer = s.getInputStream();
            in = new ObjectInputStream(inFromServer);
        }catch(Exception e){
        }
        while(true){
                try {
                    JSONObject obj = getMessage(s, in);

		    if(obj==null)
			continue;
                    //Check for NULL Object
		    //System.out.println("====================TcpServer:run:Got obj as :"+obj);
                    if(obj.get("type").equals("Init"))
                    {
                        //System.out.print("TcpServer:run: Got an Init Message:");
                        String data = (String)obj.get("value");
                        String[] components = data.split(":");
                        //System.out.println(data);
                        /* Check if it is from the same client. Parse peerList */
                        if(peerList.getPeerNode(components[0]) != null){
                            continue;
                        }
                        
                        PeerNode peer = new PeerNode(components[0], s.getInetAddress().getHostAddress(), Integer.parseInt(components[1]));
                        peer.setSocket(s);

                        peer.setOutputStream(out);
                        peer.setInputStream(in);

                        /* Store the sender info in the linked list */
                        peerList.addPeerNode(peer);
                        //System.out.print("TcpServer:run: Printing Peer List:");
                        peerList.printPeerList();

                    }
                    else if(obj.get("type").equals("Control"))
                    {
                        PeerNode peer = peerList.getPeerNodeFromIP(s.getInetAddress().getHostAddress());
                        //System.out.println("TcpServer:run: Got an Control Message from:"+s.getInetAddress().getHostAddress());
                        String str = (String)obj.get("value");
                        //Send the file from ...
                        File file= new File(path+"/"+str);
                        JSONObject obj2 = JSONManager.getJSON(file);
                        peer.sendMessage(obj2);
                    }
                    else if(obj.get("type").toString().substring(0,4).equals("File"))
                    {
                        
                        //System.out.println("TcpServer:run: Got an File from:"+s.getInetAddress().getHostAddress());
                        String fileContent = (String)obj.get("value");
                        //Store this File...
                        String receivedPath = obj.get("type").toString().substring(4);
			//System.out.println("filename"+receivedPath);	
                        String[] splits = receivedPath.split("/");
                        int noOfSplits = splits.length;
                        String newPath = path;

                        while(noOfSplits > 1){
                            newPath = newPath + "/" + splits[splits.length - noOfSplits];
                            File theDir = new File(newPath);
                            if(!theDir.exists()){
                                theDir.mkdir();
                            }
                            noOfSplits--;
                        }
                        
                        File file = new File(path+"/"+ receivedPath);
                        file.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        bos.write(fileContent.getBytes());
                        bos.close();
			//java.util.Date date= new java.util.Date();
			//Timestamp t = new Timestamp(date.getTime()); 
			SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss z");

                	final TimeZone utc = TimeZone.getTimeZone("UTC");
        	        dateFormatter.setTimeZone(utc);
	
	                String t = dateFormatter.format(new java.util.Date());
			System.out.println("_"+peerNode.getId()+"_"+t+"_"+receivedPath);
			//System.out.println("filename"+receivedPath);	
			//System.out.println(peerList.getSelf().getListOfFiles().getList().size() + " " + t);
                    }
                    else if(obj.get("type").equals("ArrayList"))
                    {
                        //System.out.println("TcpServer:run: Got an ArrayList from:"+s.getInetAddress().getHostAddress());
                        ArrayList list = (ArrayList)obj.get("value");
                        //Uodate the peerList peerNode list of files
                        PeerNode peerNode = peerList.getPeerNodeFromSocket(s);
                        //System.out.print("TcpServer:run: Printing Peer List:");
                        peerList.printPeerList();
                        if(peerNode ==null)
                        {
                            System.out.println("TcpServer:run: \nCouldn't find the PeerNode");
                        }
                        else
                        {
			    //System.out.println("----------------Got Array List and setting for PeerNode:"+peerNode.getId());
                            ListOfFiles lof= new ListOfFiles(list);
                            peerNode.setListOfFiles(lof);
                        }
                    }
                    else if(obj.get("type").equals("HashMap"))
                    {
                        //System.out.println("TcpServer:run: Got an HashMap from:"+s.getInetAddress().toString());
                        HashMap map = (HashMap)obj.get("value");
                        peerList.getSelf().setHashMapFilePeer(map);
                    }
                    else
                    {
                        System.out.println("TcpServer:run: Got an Invalid Message from:"+s.getInetAddress().toString() + " "+obj);
                    }
                }
                catch (Exception e) {

                    try{
			             PeerNode nodeToBeRemoved = peerList.getPeerNodeFromSocket(s);
                         peerList.updateHashMapBeforeRemovingNode(nodeToBeRemoved);
		                 System.out.println("Removing PeerNode:" + nodeToBeRemoved.getId() + ":" + peerList.removePeerNode(nodeToBeRemoved));	
                    	 peerList.printPeerList();
		                 s.close();
                         System.out.println("TcpServer:run: closing socket "+s.toString());
                         e.printStackTrace();
                         System.out.println("TcpServer:run:Exeception in TcpServer");
			break;
                    }
                    catch(Exception ee)
                    {

                    }
                }
            }

           
               
    }
    
    JSONObject getMessage(Socket s, ObjectInputStream in)
    {
        JSONObject obj = null;

        try
        {
            /*int length = (int)in.readObject();
            byte[] inputArray = new byte[length];
            inputArray = (byte[])in.readObject();
            String line = new String(inputArray);
            obj = (JSONObject)(JSONManager.convertStringToJSON(line));*/
             if(in==null)
		System.out.println("INputStream is null");
	    if(s.isInputShutdown())
         	    System.out.println("Shutdown----Available bytes to read are:"+in); 
	    Message obj2  = (Message)in.readObject();
	    obj = (JSONObject)obj2.obj;
        }
        catch(Exception e)
        {
           e.printStackTrace();
           //System.out.println("TcpServer:getMessage:Exception in getMesssage");
	   
        }
        return obj;
    }
    
    
    void find(int x)
    {
        System.out.println("========Inside find" + x + "===========");
        Iterator<PeerNode> it = peerList.getList().iterator();
        while (it.hasNext())
        {
            PeerNode peerNode = it.next();
            ArrayList<String> lof = peerNode.getListOfFiles().getList();
            System.out.println("For peer node:"+peerNode.getId()+" list of files is:"+lof.toString());
        }
        System.out.println("========Leaving find()===========");
    }

}

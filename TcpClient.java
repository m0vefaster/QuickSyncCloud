import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class TcpClient implements Runnable
{
    
    String serverName ;
    int port;
    private Thread t;
    private String threadName = "Client";
    private JSONObject obj;

    TcpClient (String serverName, String port, JSONObject obj)
    {
        this.serverName = serverName;
        this.port = Integer.parseInt(port);
        this.obj = obj;
    }
    
    public void run()
    {
        File myFile;
        String file;
        Socket client=null;
        int count =0;
        
        try
        {
            //System.out.println("TcpClient:run: Connecting to " + serverName + " on port " + port);
            client =null ;
            do
            {
                if(count++%10 == 0){
                    //System.out.println("TcpClient:run: Running TcpClient\n");
                }

                try
                {
                    client = new Socket(serverName, port);
                }
                catch (Exception anye)
                {
                    try
                    {
                        t.sleep(100); //milliseconds
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    //anye.printStackTrace();
                }
            }while(client==null);
            System.out.println("TcpClient:run: Client:Just connected to " + client.getRemoteSocketAddress());
            sendMessage(obj, client);
               
            client.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            try{
                client.close();
            }
            catch (Exception ee)
            {
            }
        }
        System.out.println();
    }
    
    void start ()
    {
        System.out.println("TcpClient:start: Starting " +  threadName );
        if (t == null)
        {
            t = new Thread (this, threadName);
            t.start ();
        }
    }

    void sendMessage(JSONObject obj, Socket client)
    {
        try
        {
            OutputStream outToServer = client.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outToServer);
            byte[] outputArray = obj.toString().getBytes();
            int len = obj.toString().length();
            out.writeObject(len);
            out.writeObject(outputArray);
            out.close();
            client.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

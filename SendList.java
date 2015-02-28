import java.util.*;
import java.io.FileWriter;
import java.io.*;
import java.net.*;

class SendList
{
  String serverName ;
  int port;
  public SendList(String serverName, String port)
  {
    this.serverName = serverName;
    this.port = Integer.parseInt(port); 
  }

  public void sendList(ArrayList<String> fileList, HashMap< String,ArrayList< String > > map, boolean isRequest)
  {
    try
    {
      System.out.println("Client:Connecting to " + serverName + " on port " + port);
      Socket client =null ;
      try
      {
        client = new Socket(serverName,port);
      }
      catch (Exception any)
      {
      }

      System.out.println("Client:Just connected to " + client.getRemoteSocketAddress());
      ObjectOutputStream objectOutput = new ObjectOutputStream(client.getOutputStream());
      if(isRequest)
      {
        objectOutput.writeObject(fileList);
      }
      else
      {
        objectOutput.writeObject(map);
      }
    }
    catch(Exception e){}
  }
  
  public void sendList(ArrayList<String> fileList)
  {
    sendList(fileList, new HashMap< String,ArrayList< String > >(), true);
  }
  
  public void sendList(HashMap< String,ArrayList< String > > map)
  { 
    sendList(new ArrayList<String>(), map, false);
  }
} 

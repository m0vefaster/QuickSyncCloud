import java.util.*;
class PeerFileList
{
    PeerFileList()
    {

    }

    HashMap<String,ArrayList<String>> getListOfPeersWithFiles(ArrayList<ArrayList<String>> llof)
    {
        HashMap<String,ArrayList<String>> hm =new HashMap<String,ArrayList<String>>();
        int i,j;

        for(i=0;i<llof.size();i++)
        {
            for(j=0;j<llof.get(i).size();j++)
            {
                String fileName = llof.get(i).get(j);
                fileName = fileName.substring(fileName.indexOf("QuickSync") + 9 ,fileName.length()-1);    
                if(!hm.containsKey(fileName)) 
                {
                    ArrayList<String> listOfPeers = new ArrayList<String>();
                    listOfPeers.add(String.valueOf(i));
                    hm.put(fileName,listOfPeers);
                }
                else
                {
                    hm.get(fileName).add(String.valueOf(i));
                }
            }
        }

        return hm;
    }	


}

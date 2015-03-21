import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.net.*;
import java.sql.Timestamp;
import java.util.Date;

public class Sync implements Runnable {
    ListOfFiles files;
    ListOfPeers listOfPeers;
    int count1 = 0, count2 = 0;
    static String homeDir = System.getProperty("user.home");
    static String folder = "QuickSync";
    static String path = homeDir + "/" + folder;

    Sync(ListOfPeers listOfPeers) {
        this.listOfPeers = listOfPeers;
    }

    public void run() {
        boolean ret = false;
        ListOfFiles lof = listOfPeers.getSelf().getListOfFiles();
        ArrayList < String > arrayOfFiles = new ArrayList < String > ();


         
        while (true) {

            PeerNode masterNode = listOfPeers.getMaster();
            lof.getList();

             
            if (masterNode != null) {
                if (lof.getArrayListOfFiles().size() != 0) {
                    JSONObject obj = JSONManager.getJSON(lof.getList());  
                    if (obj == null) {
                        System.out.println("Sync:run:Obj is null");
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {}
                        continue;
                    }
                    masterNode.sendMessage(obj);
                }
            }

             
             

            count2 = lof.getArrayListOfFiles().size();
            if (count2 != count1) {
                java.util.Date date = new java.util.Date();
                Timestamp t = new Timestamp(date.getTime());
                System.out.println("\n Number of Files Received till" + t + "is:" + count2);
                count1 = count2;
            }
            Set mappingSet = getFilesToRequestPerPeer(listOfPeers.getSelf().getHashMapFilePeer(), lof.getList2()).entrySet();
             
             
             
             
            Iterator itr = mappingSet.iterator();

            while (itr.hasNext()) {
                Map.Entry < String, ArrayList < String >> entry = (Map.Entry < String, ArrayList < String >> ) itr.next();
                ret = seekFromPeer(String.valueOf(entry.getKey()), entry.getValue().get(0));  
                if (ret == false) {
                    System.out.println("Sync:run:Seeking from Peer failed\n");
                    listOfPeers.printPeerList();
                }
            }

            listOfPeers.printPeerList();
            if (listOfPeers.getMaster() == null)  
            {
                 

                if (listOfPeers.getList().size() != 0) {
                     
                } else {
                    System.out.println("Sync:run:Looks like I am the only one here!");
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {}

                    continue;
                }

                listOfPeers.getSelf().setHashMapFilePeer(getFilesToRequestPerPeerMaster(listOfPeers));
                 

                SortedSet < PeerNode > peerList = listOfPeers.getList();
                Iterator < PeerNode > it = peerList.iterator();

                while (it.hasNext()) {
                    PeerNode peerNode = it.next();
                    HashMap < String, ArrayList < String >> hmFilesPeers = getFilesToRequestPerPeer(listOfPeers.getSelf().getHashMapFilePeer(), peerNode.getListOfFiles().getArrayListOfFiles());

                     
                     
                     
                     
                     
                    if (!hmFilesPeers.isEmpty()) {
                        JSONObject obj = JSONManager.getJSON(hmFilesPeers);  
                        if (peerNode.getSocket() == null) System.out.println("=====Socket is null before sending");
                        peerNode.sendMessage(obj);
                    }
                }

                System.out.println();
            }

            try {
                Thread.sleep(3000);
            } catch (Exception e) {}
        }
    }

    void print(HashMap < String, ArrayList < String >> hmap) {
        Set mappingSet = hmap.entrySet();

        Iterator itr = mappingSet.iterator();

        while (itr.hasNext()) {
            Map.Entry < String, ArrayList < String >> entry = (Map.Entry < String, ArrayList < String >> ) itr.next();
             
        }
        System.out.println();
    }

    boolean seekFromPeer(String fileName, String peerId) {
        PeerNode peer;
         
        if (fileName == null || peerId == null) {
            return false;
        }

        peer = listOfPeers.getPeerNode(peerId);

        if (peer == null) {
            return false;
        }

        String[] splits = fileName.split("/");
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
        try {
            File file = new File(path + "/" + fileName);
            new FileOutputStream(file).close();
        } catch (Exception e) {
            System.out.println("Couldn't touch file " + fileName);
        }
         
        JSONObject obj = JSONManager.getJSON(fileName);
        peer.sendMessage(obj);

        return true;
    }


    HashMap < String, ArrayList < String >> getFilesToRequestPerPeerMaster(ListOfPeers peers) {
         

        SortedSet < PeerNode > peerList = peers.getList();
        PeerNode mySelf = peers.getSelf();
        HashMap < String, ArrayList < String >> hmFilesPeers = new HashMap < String, ArrayList < String >> ();
        Iterator < PeerNode > it = peers.peerList.iterator();

        while (it.hasNext()) {
            PeerNode peerNode = it.next();
            addToHashMap(hmFilesPeers, peerNode);
        }

        addToHashMap(hmFilesPeers, mySelf);

        return hmFilesPeers;
    }


    void addToHashMap(HashMap < String, ArrayList < String >> hmFilesPeers, PeerNode peerNode) {
        ArrayList < String > lof = peerNode.getListOfFiles().getArrayListOfFiles();

        if (lof == null) return;
        int i;
        for (i = 0; i < lof.size(); i++) {
            if (hmFilesPeers.containsKey(lof.get(i))) {
                hmFilesPeers.get(lof.get(i)).add(peerNode.getId());
            } else {
                ArrayList < String > newListOfPeers = new ArrayList < String > ();
                newListOfPeers.add(peerNode.getId());
                hmFilesPeers.put(lof.get(i), newListOfPeers);
            }
        }
    }

    HashMap < String, ArrayList < String >> getFilesToRequestPerPeer(HashMap < String, ArrayList < String >> hmFilesPeers, ArrayList < String > filesWithPeer) {
        if (filesWithPeer == null) {
            return hmFilesPeers;
        }


        int i;
        HashMap < String, ArrayList < String >> incrementalHashMap = new HashMap < String, ArrayList < String >> (hmFilesPeers);
        for (i = 0; i < filesWithPeer.size(); i++) {
            if (incrementalHashMap.containsKey(filesWithPeer.get(i))) {
                incrementalHashMap.remove(filesWithPeer.get(i));
            }
        }

        return incrementalHashMap;
    }

    void find(int x) {
        System.out.println("Sync:run:========Inside find" + x + "===========");
        Iterator < PeerNode > it = listOfPeers.getList().iterator();
        while (it.hasNext()) {
            PeerNode peerNode = it.next();
            ArrayList < String > lof = peerNode.getListOfFiles().getList();
            System.out.println("For peer node:" + peerNode.getId() + " list of files is:" + lof.toString());
        }
        System.out.println("Sync:run:========Leaving find()===========");
    }

     

    void removeInvalidPeers(HashMap < String, ArrayList < String >> hmFilesPeers, PeerNode peerNode) {
        String peerIPAddress = peerNode.getIPAddress();
        Set mappingSet = hmFilesPeers.entrySet();
        Iterator itr = mappingSet.iterator();
        while (itr.hasNext()) {
            Map.Entry < String, ArrayList < String >> entry = (Map.Entry < String, ArrayList < String >> ) itr.next();
            ArrayList < String > allPeers = entry.getValue();
            int i = 0;
            while (i < allPeers.size()) {
                PeerNode node = listOfPeers.getPeerNode(allPeers.get(i));
                if (peerNode.getIPAddress().equals(peerIPAddress) && !(peerNode.getIPAddress().equals(listOfPeers.getSelf().getIPAddress()))) {
                    allPeers.remove(i);
                } else i++;
            }
        }
    }
}
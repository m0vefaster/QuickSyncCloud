import java.io.*;
import java.util.*;

class ListOfPeers
{
    SortedSet<PeerNode> peerList = new TreeSet<PeerNode>(new Comp());
    
    PeerNode mySelf;
    
    ListOfPeers(PeerNode mySelf)
    {
        //Insert to Peer List cloud domain id
        this.mySelf = mySelf;
    }
    
    PeerNode getSelf()
    {
        return mySelf;
    }
    
    class Comp implements Comparator<PeerNode>
    {
        @Override
        public int  compare(PeerNode pn1,PeerNode pn2)
        {
            if( pn1.getWeight() > pn2.getWeight())
            return 1;
            return -1 ;
        }
    }
    
    boolean addPeerNode(PeerNode newNode)
    {
        if(present(newNode))
        {
            System.out.println("ListOfPeers:addPeerNode:Peer List is: " + peerList);
            return false;
        }
        
        peerList.add(newNode);
        System.out.println(peerList);
        return true;
    }
    

    boolean removePeerNode(PeerNode removeNode)
    {
        if(!present(removeNode))
        return false;
        
        peerList.remove(removeNode);
        return true;
    }
    
    boolean removePeerNode(String peerId)
    {
        Iterator<PeerNode> itr = peerList.iterator();
        
        while(itr.hasNext())
        {
          PeerNode node = itr.next() ;
            if(node.getId().equals(peerId))
            {
              removePeerNode(node);
            }
            return true;
        }
        return false;
    }

    PeerNode getMaster()
    {
        if(peerList.size()>0  && (peerList.first().getWeight()< getSelf().getWeight()))
          return peerList.first();
        
        return null;
    }
    
    boolean present(PeerNode node)
    {
        Iterator<PeerNode> itr = peerList.iterator();
        
        while(itr.hasNext())
        {
            if(itr.next().getId()==node.getId())
            return true;
        }
        
        return false;
    }
    
    PeerNode getPeerNode(String peerId){
        Iterator<PeerNode> itr = peerList.iterator();
        PeerNode node;
        
        while(itr.hasNext()){
            node = itr.next();
            if(node.getId().equals(peerId)){
                return node;
            }
        }

        return null;
    }
    
    
    SortedSet<PeerNode> getList()
    {
        return peerList;
    }
    
    PeerNode getPeerNodeFromIP(String ipAddress)
    {
        Iterator<PeerNode> itr = peerList.iterator();
        PeerNode node;

        while(itr.hasNext()){
            node = itr.next();
            if(node.getIPAddress().equals(ipAddress)){
                return node;
            }
        }
        
        return null;
    }


    void printPeerList()
    {
        Iterator<PeerNode> itr = peerList.iterator();
        PeerNode node;
        System.out.println("\n************************The Peer list is:");
        while(itr.hasNext()){
            node = itr.next();
            System.out.print(node.getId()+",");
        }
        System.out.println();
    }
}

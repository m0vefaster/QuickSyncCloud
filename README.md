# QuickSyncCloud 
An application that combines the benefits of Cloud and P2P networks to provide modes for faster sync , conserve battery life ondevices , cost reduction on mobile devices and high availability

##What's Missing ?

![alt tag](https://github.com/vish1562/QuickSync/blob/master/img/GD%2BBittorent.png)

## We have leveraged both P2P and Cloud Topologies

![alt tag](https://github.com/vish1562/QuickSync/blob/master/img/P2P.png)

## Steps to Run

- Create a folder 'QuickSync' in your directory
- Add the path to jar files to CLASSPATH

``export CLASSPATH=~/QuickSync/json-simple-1.1.1.jar:~/QuickSync/java-json.jar:.``
- Compile the code

``javac *.java``

- Run the Code(QuickSync) with the hostname 

##Results

-We used our application to first simulate the Google Drive Topology i.e. having the Cloud as the master and two peers. Each peer is in a different network. Now we demonstrate the time taken for the peers to sync.  

![alt tag](https://github.com/vish1562/QuickSync/blob/master/img/Topo.png)

-In this topology, we demonstrate the effects of leveraging P2P network.  Initially, we have two peers in the network. Now when Peer A uploads a file. This time the time taken for the Cloud to sync the files is more compared to the time taken by Peer B.  Now, a third peer enters the network after all the peers have been synced.  Peer C can now fetch the files from Peer A, Peer B and the Cloud. This decreases the time to sync 4k files by 33%. 

![alt tag](https://github.com/vish1562/QuickSync/blob/master/img/Topo2.png)

##Class Definition
####QuickSync
It is the Main Class and starts the UDP Client and Server.  Starts the TCP Server. Connects to the Cloud.
####	UdpClient	
Runs every 3 seconds and emits a IP-Broadcast packet to notify its identity and weight.
####UdpServer
Listens for UDP Messages. On receiving a broadcast from another peer, add it to its peer list.
####TcpClient
To emit TCP messages like File List and control messages.
####TcpServer
Listens for control messages. Based on different control messages takes different actions.
####ListOfPeers
Maintains the list of peers and performs addition, deletion and other operations on the peers
####PeerNode
Maintains information for the 
####JSONManager
Creates different JSON Objects
####ListOfFiles	
Find the files that the peer posseses
####NodeWeightCalculation	
Calculation of weights of Nodes based on different parameters.

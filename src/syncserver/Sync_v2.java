/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syncserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

/**
 *
 * @author apu
 */
public class Sync_v2 {
    private int accountTime;
    private int worker_port[];
    private int max_worker;
    private int worker_p_index;
    private int newComePort;
    private int curActiveProcess;
    private DatagramSocket serverSocket;
    private HashMap<Integer,Integer> hashMap;
    //private Queue<ArrayList<Integer>> queue;
    //private InetAddress IPAddress;
    private ArrayList<String> queue;
    public void Sync() throws IOException{
        /*
        max_worker=10;
        worker_p_index=0;
        worker_port=new int[max_worker];
        newComePort=-1;
        serverSocket=null;
        
        init();
        */
        
        //start_sync();
    }
    public void prnt(String str){
        System.out.print(str);
    }
    
   public void addToList(int newPort,int w_port[]){
        for(int i=0;i<max_worker;i++){
            if(w_port[i]==-1){
                w_port[i]=newPort;
                prnt("added to list:"+w_port[i]+"\n");
                curActiveProcess++;
                break;
            }
        }
   }
   public void removeFromList(int oldPort,int w_port[]){
        for(int i=0;i<max_worker;i++){
            if(w_port[i]==oldPort){
                prnt(" remove from list:"+w_port[i]+"\n");
                w_port[i]=-1;
                prnt("remove to list:"+w_port[i]+"\n");
                break;
            }
        }
   }
   
   
   public String getAllActiveProcess(int wp[]){
       int tacp=0;//tacp=total active process
       String all_port="";
       for(int i=0;i<max_worker;i++){
           if(wp[i]!=-1){
               all_port=all_port+wp[i]+" ";
               tacp++;
           }
       }
       prnt(all_port+tacp+"\n");
       return all_port+tacp;
   }
   
   //quick brown fox jumps over the lazy dog
   //System.out.println("Substring starting from index 15 and ending at 20:");
   //System.out.println(str.substring(15, 20));
   //jump
    public void start_sync(int server_port) throws IOException, InterruptedException{
        max_worker=10;
        worker_p_index=0;
        worker_port=new int[max_worker];
        newComePort=-1;
        serverSocket=null;
        curActiveProcess=0;
        accountTime=0;
        hashMap=new HashMap<Integer,Integer>();
        queue=new ArrayList<String>();
        init();
        
        serverSocket = new DatagramSocket(server_port);//9876
        //prnt("max worker:"+max_worker+"\n");
        while(true)
        {
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
        
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            InetAddress IPAddress = receivePacket.getAddress();
            String sentence = new String( receivePacket.getData());
            if(sentence.startsWith("newprocess")){
                String str[]=sentence.split(" ");
                
                newComePort=(int)Integer.valueOf(str[2].trim());
                prnt("New come port:"+newComePort+"\n");
                addToList(newComePort,worker_port);
                prnt("get all:"+getAllActiveProcess(worker_port)+"\n");
                //toSend("allport", getAllActiveProcess(worker_port),(int)receivePacket.getPort(), IPAddress);
                //toUpdate("newport", newComePort+"");
                
            }
            //Thread.sleep(1*1000);
            if(sentence.startsWith("req")){
                String str[]=sentence.split(" ");
                String taskName="req";
                int amount=Integer.valueOf(str[1].trim());//amount
                int rcvTime=Integer.valueOf(str[2].trim());//clock_tic
                int toACK=Integer.valueOf(str[3].trim());//toACK=>port number
                String newMsg=taskName+" "+amount+" "+rcvTime+" "+toACK;
                queue.add(newMsg);
                if(curActiveProcess>=4){
                    for(int i=0;i<queue.size();i++){
                        toSendToAll(queue.get(i));
                    }
                    queue.clear();
                    //toSendToAll("req",amount+"",rcvTime+"",toACK+"");//0=taskName,1=amount,2=clockTic,3=senderPort    
                }   
            }
            if(sentence.startsWith("ACK")){
                String msg[]=sentence.split(" ");
                //int ackPort=Integer.valueOf(msg[2]);
                int amount=Integer.valueOf(msg[1].trim());
                int rcvTime=Integer.valueOf(msg[2].trim());//clock_tic
                int fromACK=Integer.valueOf(msg[3].trim());//toACK=>port number
                
                if(hashMap.containsKey(fromACK)){
                    prnt(fromACK+",size:"+(hashMap.get(fromACK)+1)+"\n");
                    hashMap.put(fromACK, hashMap.get(fromACK)+1);
                }
                else{
                    hashMap.put(fromACK,1);
                }
                if(hashMap.get(fromACK)>=curActiveProcess){
                    //toSend("ACKL", "",ackPort , IPAddress);
                    prnt("\nLASTACK\n\n");
                    //toSend("LASTACK",amount+"",rcvTime+"",fromACK+"" , IPAddress);
                    toSendToAll("LASTACK",amount+"",rcvTime+"",fromACK+"");
                    //sendLastAck("LASTACK",amount+"",rcvTime+"",fromACK+"",fromACK);
                    hashMap.remove(fromACK);
                    accountTime=accountTime+10;//not complete
                    prnt("all ack rcv\n");
                }
                else{
                    toSendToAll("ACK",amount+"", rcvTime+"",fromACK+"");
                }
                //toSendReqToAll("req",(int)receivePacket.getPort()+"");
            }
        }
    }
    
    public void init(){
        for(int i=0;i<worker_port.length;i++){
            worker_port[i]=-1;
        }
    }
    //public void toSendToAll(String taskName,String amount,String clockTic,String senderPort) throws UnknownHostException, IOException{
    public void toSendToAll(String task) throws UnknownHostException, IOException{
          
    InetAddress IPAddress=InetAddress.getByName("localhost");
        String dataToSend=task;//taskName+" "+amount+" "+clockTic+" "+senderPort;
        byte[] sendData = new byte[1024];
        sendData=dataToSend.getBytes();
        //InetAddress IPAddress = InetAddress.getByName("localhost");
      
        
        for(int i=0;i<max_worker;i++){
            if(worker_port[i]!=-1){
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,worker_port[i]);
                serverSocket.send(sendPacket);
            }
            
        }
        

    }
    public void sendLastAck(String taskName,String amount,String clockTic,String senderPort,int receiver_port) throws UnknownHostException, IOException{
        InetAddress IPAddress=InetAddress.getByName("localhost");
        String dataToSend=taskName+" "+amount+" "+clockTic+" "+senderPort;
        byte[] sendData = new byte[1024];
        sendData=dataToSend.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,receiver_port);
        serverSocket.send(sendPacket);
    
    
    }
    public void toSendToAll(String taskName,String amount,String clockTic,String senderPort) throws UnknownHostException, IOException{
    //public void toSendToAll(String task) throws UnknownHostException, IOException{
          
    InetAddress IPAddress=InetAddress.getByName("localhost");
        String dataToSend=taskName+" "+amount+" "+clockTic+" "+senderPort;
        byte[] sendData = new byte[1024];
        sendData=dataToSend.getBytes();
        //InetAddress IPAddress = InetAddress.getByName("localhost");
      
        
        for(int i=0;i<max_worker;i++){
            if(worker_port[i]!=-1){
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,worker_port[i]);
                serverSocket.send(sendPacket);
            }
            
        }
        

    }
    public void toSend(String taskName,String amount,int clockTic,int receiver_port,InetAddress IPAddress) throws UnknownHostException, IOException{
        String dataToSend=taskName+" "+amount+" "+clockTic+" "+receiver_port;
        byte[] sendData = new byte[1024];
        sendData=dataToSend.getBytes();
        //InetAddress IPAddress = InetAddress.getByName("localhost");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, receiver_port);
        serverSocket.send(sendPacket);

    }
    
    public void toUpdate(String taskName,String message) throws UnknownHostException, IOException{
        InetAddress IPAddress=InetAddress.getByName("localhost");
        String dataToSend=taskName+" "+message;
        byte[] sendData = new byte[1024];
        sendData=dataToSend.getBytes();
        //InetAddress IPAddress = InetAddress.getByName("localhost");
      
        
        for(int i=0;i<max_worker;i++){
            if(worker_port[i]!=-1){
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,worker_port[i]);
                serverSocket.send(sendPacket);
            }
            
        }
        

    }
    
    
    
    
}

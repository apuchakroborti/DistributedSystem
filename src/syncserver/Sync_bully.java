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
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author apu
 */
public class Sync_bully {
    //private int worker_port[];
    private ArrayList<Integer> worker_port;
    private int max_worker;
    private int worker_p_index;
    private int newComePort;
    private DatagramSocket serverSocket;
    private int totalActiveProcess;
    private HashMap<Integer,Integer> pidMap;
    private int curActiveProcess;
    //private InetAddress IPAddress;
    public void Sync_bully() throws IOException{
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
   
   //quick brown fox jumps over the lazy dog
   //System.out.println("Substring starting from index 15 and ending at 20:");
   //System.out.println(str.substring(15, 20));
   //jump
    public void start_sync(int server_port) throws IOException{
        max_worker=10;
        //worker_p_index=0;
        //worker_port=new int[max_worker];
        worker_port=new ArrayList<Integer>();
        newComePort=-1;
        serverSocket=null;
        totalActiveProcess=4;
        curActiveProcess=0;
        pidMap=new HashMap<Integer,Integer>();
        int coSelection=0;
        //init();
        
        serverSocket = new DatagramSocket(server_port);//9876
        prnt("max worker:"+max_worker+"\n");
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
                
                newComePort=Integer.valueOf(str[1].trim());
                int newPid=Integer.valueOf(str[2].trim());
                prnt("New come port:"+newComePort+"\n");
                //addToList(newComePort,worker_port);
                worker_port.add(newComePort);
                pidMap.put(newComePort,newPid);
                String allProcess=getAllActiveProcess(worker_port,pidMap);
                prnt("get all:"+allProcess+"\n");
                toSend("allport",allProcess,"",(int)receivePacket.getPort(), IPAddress);
                toUpdate("newport", newComePort+"",newPid+"");
                curActiveProcess++;
                
            }
            if(coSelection==0 && curActiveProcess>=totalActiveProcess)
            {
                SecureRandom rand=new SecureRandom();
                int pos=Math.abs(rand.nextInt(totalActiveProcess-1));
                
                toSend("startcos",worker_port.get(pos)+"",pidMap.get(worker_port.get(pos))+"",worker_port.get(pos),IPAddress);
                coSelection++;
            }
            
        }
    }
    public void addToList(int newPort,int w_port[]){
        for(int i=0;i<max_worker;i++){
            if(w_port[i]==-1){
                w_port[i]=newPort;
                prnt("added to list:"+w_port[i]+"\n");
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
   public void prnt(String str){
        System.out.print(str);
    }
   public String getAllActiveProcess(ArrayList<Integer> wp,HashMap<Integer,Integer>pidMap1){
       int tacp=0;//tacp=total active process
       String all_port=wp.get(0)+" "+pidMap1.get(wp.get(0));
       tacp=tacp+2;
       for(int i=1;i<wp.size();i++){
           all_port=all_port+" "+wp.get(i)+" "+pidMap1.get(wp.get(i));
           tacp=tacp+2;
       }
       all_port=all_port+" "+tacp;
       prnt(all_port+tacp+"\n");
       return all_port;
   }
   
    /*
    public void init(){
        for(int i=0;i<worker_port.length;i++){
            worker_port[i]=-1;
        }
    }
    */
    public void toSend(String taskName,String message,String pid,int receiver_port,InetAddress IPAddress) throws UnknownHostException, IOException{
        String dataToSend=taskName+" "+message+" "+pid;
        byte[] sendData = new byte[1024];
        sendData=dataToSend.getBytes();
        //InetAddress IPAddress = InetAddress.getByName("localhost");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, receiver_port);
        serverSocket.send(sendPacket);

    }
    public void toUpdate(String taskName,String message,String pid) throws UnknownHostException, IOException{
        InetAddress IPAddress=InetAddress.getByName("localhost");
        String dataToSend=taskName+" "+message+" "+pid;
        byte[] sendData = new byte[1024];
        sendData=dataToSend.getBytes();
        //InetAddress IPAddress = InetAddress.getByName("localhost");
        for(int i=0;i<worker_port.size();i++){
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,worker_port.get(i));
            serverSocket.send(sendPacket);
        }
        

    }
    
    
    /*
    public void store_port(String all_port[]){
        int wp=all_port.length;
        int to_running_process=Integer.valueOf(all_port[wp-1]);
        for(int i=1;i<to_running_process+1;i++){
            port[i-1]=Integer.valueOf(all_port[i]);
        }
    }
    
    public String fromReceive() throws UnknownHostException, IOException{
        receiveData=new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        String rcvdSentence = new String(receivePacket.getData());
        return rcvdSentence;

    }
    public void removeProcess(int rp){
        for(int i=0;i<max_w_process;i++){
            if(port[i]==rp){
                port[i]=-1;
                break;
            }
        }
    }*/
    
}

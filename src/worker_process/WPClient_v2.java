/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worker_process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.HashMap;

/**
 *
 * @author apu
 */
public class WPClient_v2 {
    private int clock_tic;
    //private int port[];
    private int max_w_process;
    private int server_port;
    private DatagramSocket clientSocket;
    private byte[] sendData;
    private byte[] receiveData;
    private InetAddress IPAddress;
    private int client_port;
    private int balance;
    private int previous=0;
    private HashMap<Integer,Integer> hashMap;
    public WPClient_v2(int mwp,int client_port) throws IOException {
       
        //clock=0;
        max_w_process=mwp;
        //port=new int[max_w_process];
        balance=100;
        server_port=9876;
        IPAddress = InetAddress.getByName("localhost");
        this.client_port=client_port;
        init(max_w_process);
    }
    public void setBalance(int amount){
        balance+=amount;
    }
    private void init(int mwp){
        for(int i=0;i<mwp;i++){
          //  port[i]=-1;
        }
    }
    public void prnt(String str){
        System.out.print(str);
    }
    /*
    public void store_port(String all_port[]){
        int wp=all_port.length;
        int to_running_process=(int)Integer.valueOf(all_port[wp-1].trim());
        for(int i=1;i<to_running_process+1;i++){
            prnt("problem:"+all_port[i]+"\n");
            int po=Integer.valueOf(all_port[i]);
            if(po!=client_port){
            //    port[i-1]=po;
            }
            
        }
    }*/
    
    public void start_wpc() throws IOException, InterruptedException{
        int start=0;
        SecureRandom rand=new SecureRandom();
        int starting_time=Math.abs(rand.nextInt(100));
        clock_tic=starting_time;
        //loop=loop+10000;
       hashMap=new HashMap<Integer,Integer>();
        clientSocket = new DatagramSocket(client_port);
        
        String own_port=""+client_port;
        toSend("newprocess",-1+"",own_port,"", server_port);//amount=-1
       
        /*
        String rcvdSentence = fromReceive();
        if(rcvdSentence.startsWith("allport")){
            String all_process[]=rcvdSentence.split(" ");
            prnt(rcvdSentence+"\n");
            //store_port(all_process);
        }*/
        int first=0;
        while(true)
        {
            
            sendData = new byte[1024];
            receiveData = new byte[1024];
            
            while(true){
                SecureRandom mlt_rand=new SecureRandom();
                int msg_prob=Math.abs(mlt_rand.nextInt(100));
                double prob=(double)msg_prob/100;
                //prnt("Client Port:"+client_port+",Probability:"+prob+"\n");
                if(prob>=0.75 && first==0){
                    prnt("Client Port:"+client_port+",Probability:"+prob+"\n");
                    SecureRandom addTime_rand=new SecureRandom();
                    int amount=10+Math.abs(addTime_rand.nextInt(90));

                    toSend("req",amount+"",clock_tic+"",client_port+"", server_port);
                    first=1;
                }
                if(first>=1)break;
            }
            
            String rcvd=fromReceive();
            prnt("rcvd:"+rcvd+"\n");
            if(rcvd!=null){
                String str[]=rcvd.split(" ");
                int coming_port=Integer.valueOf(str[3].trim());
                prnt("port:"+coming_port+"\n");
                if(rcvd.startsWith("ACK")){
                    int rcvTime=Integer.valueOf(str[2].trim());
                    if(rcvTime>=clock_tic){
                        prnt(client_port+"rcv time:"+rcvTime+"\n");
                        prnt(client_port+"clock time:"+clock_tic+"\n");
                        clock_tic=rcvTime+1;
                        prnt(client_port+"Updated time:"+clock_tic+"\n");
                    }
                }
                else if(rcvd.startsWith("LASTACK")){
                      prnt("\n\nBalance:"+balance+"\n");
                      setBalance(Integer.valueOf(str[1].trim()));
                      prnt("Updated Balance:"+balance+"\n\n");
                    
                    /*
                    if(hashMap.containsKey(coming_port)){
                        hashMap.put(coming_port,hashMap.get(coming_port)+1);
                    }
                    else{
                        hashMap.put(coming_port,1);
                    }
                    if(hashMap.get(coming_port)>=4){
                        prnt("\n\nBalance:"+balance+"\n");
                        setBalance(Integer.valueOf(str[1].trim()));
                        prnt("Updated Balance:"+balance+"\n\n");
                    
                    }*/
                    //break;
                    
                }
                else if(rcvd.contains("req")){//0=taskName,1=amount,2=clockTic,3=senderPort
                    int amount=Integer.valueOf(str[1].trim());
                    int rcvTime=Integer.valueOf(str[2].trim());//clock_tic
                    int toACK=Integer.valueOf(str[3].trim());//toACK=>port number
                    //int senderPort=Integer.valueOf(str[3].trim());
                    if(rcvTime>=clock_tic && toACK!=client_port ){
                        prnt(client_port+"rcv time:"+rcvTime+"\n");
                        prnt(client_port+"clock time:"+clock_tic+"\n");
                        
                        clock_tic=rcvTime+1;
                        prnt(client_port+"Updated time:"+clock_tic+"\n");
                    }
                    toSend("ACK",amount+"",clock_tic+"",toACK+"", server_port);
                }
            
            }
            SecureRandom srand=new SecureRandom();
            int sleep=50+Math.abs(srand.nextInt(50));
            Thread.sleep((long)sleep);
            clock_tic++;
            
            
        }
        //clientSocket.close();
    }
    public void toSend(String taskName,String amount,String clockTic,String ownPort,int receiver_port) throws UnknownHostException, IOException{
        String dataToSend=taskName+" "+amount+" "+clockTic+" "+ownPort;
        sendData = new byte[1024];
        sendData=dataToSend.getBytes();
        //InetAddress IPAddress = InetAddress.getByName("localhost");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, receiver_port);
        clientSocket.send(sendPacket);

    }
    public String fromReceive() throws UnknownHostException, IOException{
        receiveData=new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        String rcvdSentence = new String(receivePacket.getData());
        if(rcvdSentence.length()>0)return rcvdSentence;
        
        return null;

    }
    
    
}

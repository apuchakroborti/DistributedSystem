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
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author apu
 */
public class WPClient_bully {
    private int clock;
    //private int port[];
    private ArrayList<Integer> port;
    private int max_w_process;
    private int server_port;
    private DatagramSocket clientSocket;
    private byte[] sendData;
    private byte[] receiveData;
    private InetAddress IPAddress;
    private int client_port;
    private int PID;
    
    private HashMap<Integer,Integer> processID;
    public WPClient_bully(int mwp,int client_port,int ownProcessId) throws IOException {
       this.client_port=client_port;
        //init(max_w_process);
    }
    public void start_wpc(int mwp,int ownProcessId) throws IOException{
       //
        clock=0;
        max_w_process=mwp;
        PID=ownProcessId;
        //port=new int[max_w_process];
        port=new ArrayList<Integer>();
        processID=new HashMap<Integer,Integer>();
        server_port=9876;
        IPAddress = InetAddress.getByName("localhost");
        
       
       
       //
        
        int start=0;
        SecureRandom rand=new SecureRandom();
        int loop=Math.abs(rand.nextInt(5));
        int clock_tic=0;
        //loop=loop+10000;
        //int proId=Math.abs(rand.nextInt(10000));
        processID.put(client_port, PID);
        
        clientSocket = new DatagramSocket(client_port);
        
        String own_port=""+client_port;
        toSend("newprocess",own_port,PID, server_port);
       
        String rcvdSentence = fromReceive();
        if(rcvdSentence.startsWith("allport")){
            String all_process[]=rcvdSentence.split(" ");
            prnt(rcvdSentence+"\n");
            store_port(all_process);
        }
        
        while(true)
        {
            
            sendData = new byte[1024];
            receiveData = new byte[1024];
            
            String rcvd=fromReceive();
            rcvd=rcvd.trim();
            prnt("rcvd:"+rcvd+"\n");
            if(rcvd!=null){
                String str[]=rcvd.split(" ");
                int co_port=Integer.valueOf(str[1].trim());
                int co_pid=Integer.valueOf(str[2].trim());
                    
                if(rcvd.startsWith("coordinator")){
                    prnt("Coordinator port no:"+co_port+"\n"
                            + "Coordinator pid:"+co_pid+"\n");
                    //removeProcess(rmvPort);
                    //processID.remove(rmvPort);
                }
                else if(rcvd.startsWith("newport")){
                    String str1[]=rcvd.split(" ");
                    int p_port=Integer.valueOf(str1[1].trim());
                    int pro_Id=Integer.valueOf(str1[2].trim());
                    addProcess(p_port,pro_Id);
                }
                else if(rcvd.startsWith("ok")){
                    prnt("Coordinator not me \n");
                }
                else if(rcvd.startsWith("coCheck")){
                 if(PID>co_pid){
                     toSend("ok", own_port, PID, server_port);
                     if(checkAllCo(max_w_process,PID)){
                         prnt("coordinator:"+client_port+",Pid:"+PID+"\n");
                         toSendAll("coordinator", own_port, PID, server_port,port);
                     }
                 }   
                }
                else if(rcvd.startsWith("startcos")){
                    if(checkAllCo(max_w_process,PID)){
                         prnt("coordinator:"+client_port+",Pid:"+PID+"\n");
                         toSendAll("coordinator", own_port, PID, server_port,port);
                    }
                }
            }
            /*
            if(clock_tic%60==0){
                clock=clock+1;
                prnt("Port no:"+own_port+" clock value:"+clock+"\n");
            }*/
            clock++;
            //clock_tic++;
            loop--;
            
            
        }
        //clientSocket.close();
    }
    public boolean checkAllCo(int mwp,int pid) throws IOException{
        boolean co=true;
        for(int i=0;i<port.size();i++){
            if(port.get(i)!=-1 && port.get(i)!=client_port){
                if(processID.get(port.get(i))>pid){
                    toSend("coCheck", client_port+"", pid, port.get(i));
                    co=false;
                }
            }
        }
        return co;
    }
    private void init(int mwp){
        for(int i=0;i<mwp;i++){
            //port[i]=-1;
        }
    }
    public void prnt(String str){
        System.out.print(str);
    }
    public void store_port(String all_port[]){
        int wp=all_port.length;
        int to_running_process=(int)Integer.valueOf(all_port[wp-2].trim());
        for(int i=1;i<to_running_process+1;i=i+2){
            prnt("problem:"+all_port[i]+"\n");
            int po=Integer.valueOf(all_port[i].trim());
            if(po!=client_port){
                //port[i-1]=po;
                port.add(po);
                processID.put(po,Integer.valueOf(all_port[i+1].trim()));
            }
            
        }
    }
    public void toSendAll(String taskName,String ownPort,int Pid,int mwp,ArrayList<Integer> portAll) throws UnknownHostException, IOException{
        for(int i=0;i<portAll.size();i++){
            if(portAll.get(i)!=client_port){
                String dataToSend=taskName+" "+ownPort+" "+Pid;
                sendData = new byte[1024];
                sendData=dataToSend.getBytes();
                //InetAddress IPAddress = InetAddress.getByName("localhost");
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,portAll.get(i));
                clientSocket.send(sendPacket);         
                
            }
               
        }
        
    }
    public void toSend(String taskName,String ownPort,int Pid,int receiver_port) throws UnknownHostException, IOException{
        String dataToSend=taskName+" "+ownPort+" "+Pid;
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
    public void addProcess(int ap,int pId){
        //for(int i=0;i<max_w_process;i++){
            //if(port[i]==-1 && ap!=client_port){
                prnt("Process added:"+ap+"\n");
                port.add(ap);
                processID.put(ap,pId);
            //    break;
          //  }
        //}
    }
    public void removeProcess(int rp){
        port.remove(rp);
        /*for(int i=0;i<max_w_process;i++){
            if(port[i]==rp){
                prnt("Process removed:"+rp+"\n");
                port[i]=-1;
                break;
            }
        }*/
    }
    
}

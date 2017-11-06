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

/**
 *
 * @author apu
 */
public class WPClient {
    private int clock;
    private int port[];
    private int max_w_process;
    private int server_port;
    private DatagramSocket clientSocket;
    private byte[] sendData;
    private byte[] receiveData;
    private InetAddress IPAddress;
    private int client_port;
    public WPClient(int mwp,int client_port) throws IOException {
       
        clock=0;
        max_w_process=mwp;
        port=new int[max_w_process];
        
        server_port=9876;
        IPAddress = InetAddress.getByName("localhost");
        this.client_port=client_port;
        init(max_w_process);
    }
    private void init(int mwp){
        for(int i=0;i<mwp;i++){
            port[i]=-1;
        }
    }
    public void prnt(String str){
        System.out.print(str);
    }
    public void store_port(String all_port[]){
        int wp=all_port.length;
        int to_running_process=(int)Integer.valueOf(all_port[wp-1].trim());
        for(int i=1;i<to_running_process+1;i++){
            prnt("problem:"+all_port[i]+"\n");
            int po=Integer.valueOf(all_port[i]);
            if(po!=client_port){
                port[i-1]=po;
            }
            
        }
    }
    public void toSend(String taskName,String message,int receiver_port) throws UnknownHostException, IOException{
        String dataToSend=taskName+" "+message;
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
    public void addProcess(int ap){
        for(int i=0;i<max_w_process;i++){
            if(port[i]==-1 && ap!=client_port){
                prnt("Process added:"+ap+"\n");
                port[i]=ap;
                break;
            }
        }
    }
    public void removeProcess(int rp){
        for(int i=0;i<max_w_process;i++){
            if(port[i]==rp){
                prnt("Process removed:"+rp+"\n");
                port[i]=-1;
                break;
            }
        }
    }
    public void start_wpc() throws IOException{
        int start=0;
        SecureRandom rand=new SecureRandom();
        int loop=Math.abs(rand.nextInt(5));
        int clock_tic=0;
        loop=loop+10000;
       
        clientSocket = new DatagramSocket(client_port);
        
        String own_port=""+client_port;
        toSend("newprocess",own_port, server_port);
       
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
            
            //String sentence="";
            if(loop==0){
                toSend("dumped",client_port+"", server_port);
                //sentence=" "+client_port;
                prnt("dumped\n");
                break;
            }
            while(true){
                SecureRandom random=new SecureRandom();
                int process_pos=Math.abs(random.nextInt(max_w_process-1));
                if(port[process_pos]!=-1){
                    toSend("time",clock+"", port[process_pos]);
                    prnt("sent to:"+port[process_pos]+"\n");
                    break;
                }
                else if(port[process_pos]!=client_port){
                    break;
                }
            }
        
            String rcvd=fromReceive();
            prnt("rcvd:"+rcvd+"\n");
            if(rcvd!=null){
                String str[]=rcvd.split(" ");
                if(rcvd.startsWith("time")){
                    int rcvTime=Integer.valueOf(str[1].trim());
                    if(rcvTime>=clock){
                        prnt(client_port+"rcv time:"+rcvTime+"\n");
                        prnt(client_port+"clock time:"+clock+"\n");
                        
                        clock=rcvTime+1;
                        prnt(client_port+"Updated time:"+clock+"\n");
                    }
                }
                else if(rcvd.startsWith("dumped")){
                    int rmvPort=Integer.valueOf(str[1].trim());
                    removeProcess(rmvPort);
                }
                else if(rcvd.startsWith("newport")){
                    String str1[]=rcvd.split(" ");
                    addProcess(Integer.valueOf(str1[1].trim()));
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
        clientSocket.close();
    }
    
}

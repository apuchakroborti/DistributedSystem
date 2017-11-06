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
public class WPClient_wireless {  
    private int clock;
    //private int port[];
    //private ArrayList<Integer> port;
    private int max_w_process;
    private int server_port;
    private DatagramSocket clientSocket;
    private byte[] sendData;
    private byte[] receiveData;
    private InetAddress IPAddress;
    private int client_port;
    private int resiDualEnergy;
    //direc link
    private ArrayList<Integer> link;
    private ArrayList<Port_energy_map> replyMessages;
    private boolean sameMessage;
    private int processNo;
    private int parentPort;
    private int AckCount;
    
    private HashMap<Integer,Integer> resiEnergyPortMap;
    
    public WPClient_wireless(int processNo,int client_port,int resiDualEnergy) throws IOException {
       this.client_port=client_port;
       //init(max_w_process);
       link=new ArrayList<Integer>();
       replyMessages=new ArrayList<Port_energy_map>();
       sameMessage=false;
       this.processNo=processNo;
        this.resiDualEnergy=resiDualEnergy;
       parentPort=-1;
       AckCount=1;
    }
    public void create_link(int link_port){
        link.add(link_port);
    }
    
    public void start_wpc() throws IOException, InterruptedException{
        //
        clock=0;
        server_port=9876;
        IPAddress = InetAddress.getByName("localhost");
        int start=0;
        SecureRandom rand=new SecureRandom();
        int loop=Math.abs(rand.nextInt(5));
        int clock_tic=0;
        clientSocket = new DatagramSocket(client_port);
       
        Port_energy_map obj=new Port_energy_map();
        obj.setPort(client_port);
        obj.setEnergy(this.resiDualEnergy);
        replyMessages.add(obj);
        
        //prnt("\n\nOwn Port No:"+client_port+",Residual energy:"+resiDualEnergy+",Process No:"+processNo+"\n");
        
        Thread.sleep(2*1000);
        
        if(processNo==1){
            toSendAll("coCheck", client_port+"",this.resiDualEnergy+"",link);
            
            //prnt("\n\nOwn Port No:"+client_port+",Residual energy:"+resiDualEnergy+",link size:"+link.size()+"\n");
        }
        else{
            //prnt("\n\nOwn Port No:"+client_port+",Residual energy:"+resiDualEnergy+",link size:"+link.size()+"\n");
        }
        while(true)
        {
            
            sendData = new byte[1024];
            receiveData = new byte[1024];
            
            String rcvd=fromReceive();
            rcvd=rcvd.trim();
            //prnt("rcvd sentence:"+rcvd+"\n");
            if(rcvd!=null){
                String str[]=rcvd.split(" ");
                int fromPort=Integer.valueOf(str[1].trim());
                int resiEnergy=Integer.valueOf(str[2].trim());
                    
                if(rcvd.startsWith("coordinator")){
                    prnt("Received from:"+fromPort+",message:"+rcvd+"\n");
                    prnt("Coordinator Port no:"+fromPort+"\n"
                       + "Coordinator resiDual Energy:"+resiEnergy+"\n");
                    toSendAll("coordinator",fromPort+"",resiEnergy+"", link);
                }
                else if(rcvd.startsWith("ACK")){
                    prnt("Received from:"+fromPort+",message:"+rcvd+"\n");
                    //prnt("ACK received \n");
                    AckCount++;
                }
                else if(rcvd.startsWith("coCheck") && !sameMessage){
                    //Port_energy_map obj_co=new Port_energy_map();
                    //obj_co.setPort(fromPort);
                    //obj_co.setEnergy(resiEnergy);
                    //replyMessages.add(obj_co);
                    prnt("Received from:"+fromPort+",message:"+rcvd+"\n");
                    toSend("ACK", client_port+"", resiDualEnergy+"", parentPort);
                    
                    
                    //prnt("ACK sent\n");
                    if(link.size()>0){
                        toSendAll("coCheck",client_port+"",this.resiDualEnergy+"", link);
                       
                        //prnt("coCheck:"+client_port+" "+this.resiDualEnergy+"\n");
                    }
                    else if(link.size()==0){
                        //prnt("sent:"+"replyCoCheck "+client_port+" "+resiDualEnergy+" "+parentPort+"\n");
                        toSend("replyCoCheck",client_port+"", resiDualEnergy+"",parentPort);
                    }
                    sameMessage=true;
                    //else{
                    //    toSend("replyCoCheck",client_port+"", resiDualEnergy+"",parentPort);
                    //}
                }
                else if(rcvd.startsWith("replyCoCheck")){
                    Port_energy_map obj_pe=new Port_energy_map();
                    obj_pe.setPort(fromPort);
                    obj_pe.setEnergy(resiEnergy);
                    replyMessages.add(obj_pe);
                    //prnt("replyCoCheck:"+fromPort+" "+resiEnergy+"\n");
                    prnt("Received from:"+fromPort+",message:"+rcvd+"\n");
                }   
            }
            clock++;
            prnt("clock:"+clock+"\n");
            if(replyMessages.size()==AckCount && AckCount>1){
                ArrayList<Integer> couple=new ArrayList<Integer>();
                findMax(replyMessages,couple);
                prnt("Max value=>port:"+couple.get(0)+",residual energy:"+couple.get(1)+"\n");
                if(parentPort==-1 && replyMessages.size()>1){
                    prnt("Coordinator port:"+couple.get(0)+"\n"
                        +"Coordinator energy:"+couple.get(1)+"\n");
                    toSendAll("coordinator", couple.get(0)+"", couple.get(1)+"", link);
                }
                else if(parentPort!=-1){
                    toSend("replyCoCheck", couple.get(0)+"",couple.get(1)+"",parentPort);
                    //prnt("replyCoCheck:"+ couple.get(0)+" "+couple.get(1)+" "+parentPort+"\n");
                }
                
                
            }
        }
        //clientSocket.close();
    }
    public void findMax(ArrayList<Port_energy_map> portEnergy,ArrayList<Integer> portEnergyMap){
        int eMax=0;
        int eMaxIndex=0;
        for(int i=0;i<portEnergy.size();i++){
            if(portEnergy.get(i).getEnergy()>eMax){
                eMax=portEnergy.get(i).getEnergy();
                eMaxIndex=i;
            }
        }
        portEnergyMap.add(portEnergy.get(eMaxIndex).getPort());
        portEnergyMap.add(portEnergy.get(eMaxIndex).getEnergy());
    }
    public void sendCheckToAll(){
        
    }
    
    class Port_energy_map{
        private int port;
        private int energy;
        public Port_energy_map() {port=-1;energy=-1;}
        public void setPort(int port){this.port=port;}
        public int getPort(){return port;}
        public void setEnergy(int energy){this.energy=energy;}
        public int getEnergy(){return energy;}
    }
    private void init(int mwp){
        for(int i=0;i<mwp;i++){
            //port[i]=-1;
        }
    }
    public void prnt(String str){
        System.out.print(str);
    }
    
    public void toSendAll(String taskName,String ownPort,String residulaEnergy,ArrayList<Integer> linkAll) throws UnknownHostException, IOException{
        for(int i=0;i<linkAll.size();i++){
            String dataToSend=taskName+" "+ownPort+" "+residulaEnergy;
            sendData = new byte[1024];
            sendData=dataToSend.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,linkAll.get(i));
            clientSocket.send(sendPacket);
            //prnt("sent:"+dataToSend+"\n");
            prnt("Sent to:"+linkAll.get(i)+",message:"+dataToSend+"\n");
            //prnt(dataToSend+" "+linkAll.get(i)+"\n");
        }
        
    }
    public void toSend(String taskName,String ownPort,String residulaEnergy,int receiverPort) throws UnknownHostException, IOException{
        String dataToSend=taskName+" "+ownPort+" "+residulaEnergy;
        sendData = new byte[1024];
        sendData=dataToSend.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,receiverPort);
        clientSocket.send(sendPacket);
        prnt("Sent to:"+receiverPort+",message:"+dataToSend+"\n");
    }
    public void setParent(int portPar){
        parentPort=portPar;
        prnt("Parent set:"+portPar+"\n");
    }
    public String fromReceive() throws UnknownHostException, IOException{
        receiveData=new byte[1024];
        //clientSocket.wait(500);
        //clientSocket.setSoTimeout(500);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        String rcvdSentence =null;
            rcvdSentence=new String(receivePacket.getData());
        
        if(rcvdSentence.length()>0){
            if(rcvdSentence.startsWith("coCheck") && parentPort==-1){
                setParent((int)receivePacket.getPort());
            }
            return rcvdSentence;
        }
        
        return null;

    }
    
}

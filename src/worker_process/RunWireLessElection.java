/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worker_process;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;
import static worker_process.WorkerProcess_client_main.prnt;

/**
 *
 * @author apu
 */

public class RunWireLessElection {
    private ArrayList<Integer> port;
    private ArrayList<WPClient_wireless> obj;
    private ArrayList<Integer> weight;
    
    public RunWireLessElection() throws IOException {
        
        port=new ArrayList<Integer>();
        obj=new ArrayList<WPClient_wireless>();
        weight=new ArrayList<Integer>();
        startRunWSN();
        
    }
    public void startRunWSN() throws IOException{
       
        int total_worker=6;
        for(int i=0;i<total_worker;i++){
            port.add(9890+i+1);
        }
        
        int twp=0;
        weight.add(18);//1
        weight.add(12);//2
        weight.add(13);//3
        weight.add(27);//4
        weight.add(22);//5
        weight.add(30);//6
        //Randomly generated weight
        /*
        while(true){
           SecureRandom rand=new SecureRandom();
           int pid=Math.abs(rand.nextInt(10000));
           if(!weight.contains(pid)){
               weight.add(pid);
               twp++;
           }
           if(twp>=total_worker)break;
        }*/

       obj.add(new WPClient_wireless(1,port.get(0),weight.get(0)));
       
       for(int i=1;i<total_worker;i++){
           obj.add(new WPClient_wireless(0,port.get(i),weight.get(i)));
       }
       
       //create link between nodes
       obj.get(1-1).create_link(port.get(2-1));
       
       obj.get(2-1).create_link(port.get(3-1));
       obj.get(2-1).create_link(port.get(5-1));
       
       obj.get(3-1).create_link(port.get(4-1));
       obj.get(3-1).create_link(port.get(5-1));
       
       obj.get(5-1).create_link(port.get(6-1));
       
    }
    public void runIndividualProcess() throws IOException, InterruptedException{
        
       prnt("1.for client 1\n"+
            "2.for client 2\n"+
            "3.for client 3\n"+
            "4.for client 4\n"+
            "4.for client 4\n"+
            "5.for client 5\n"+
            "6.for client 6\n"
             );
       
       Scanner input=new Scanner(System.in);
       int choice=input.nextInt();
       if(choice>=1 && choice <=6){
           obj.get(choice-1).start_wpc();
       }
    }
    public void prnt(String str){
        System.out.print(str);
    }
}

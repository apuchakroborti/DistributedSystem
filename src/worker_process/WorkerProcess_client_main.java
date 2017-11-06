/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worker_process;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author apu
 */
public class WorkerProcess_client_main {
    public static void prnt(String str){
        System.out.print(str);
    }
    public static void main(String args[]) throws Exception
    {
        RunWireLessElection obj_run=null;
        //while(true){
            
            Scanner input=new Scanner(System.in);
            //prnt("1.Run Process\n");
            //int ch=input.nextInt();

            //if(ch==1){
            obj_run=new RunWireLessElection();
            //}
            //if(ch==1){
            obj_run.runIndividualProcess();
            //}
            
        //}
        
        
        
        /*
       int total_worker=4;
       //int port[]=new int[total_worker];
       ArrayList<Integer> port=new ArrayList<Integer>();
       for(int i=0;i<total_worker;i++){
           //port[i]=9890+i;
           port.add(9890+i);
       }
       WPClient_bully[] obj=new WPClient_bully[total_worker];
      
       ArrayList<Integer> uniquePid=new ArrayList<Integer>();
       int twp=0;
       while(true){
          SecureRandom rand=new SecureRandom();
          int pid=Math.abs(rand.nextInt(10000));
          if(!uniquePid.contains(pid)){
              uniquePid.add(pid);
              twp++;
          }
          if(twp>=total_worker)break;
       }
       
      for(int i=0;i<total_worker;i++){
          obj[i]=new WPClient_bully(total_worker,port.get(i),uniquePid.get(i));
          //obj[i].start_wpc();
      }
      
      prnt("1.for client 1\n"
            +"2.for client 2\n"+
             "3.for client 3\n"+
             "4.for client 4\n");
      Scanner input=new Scanner(System.in);
      int choice=input.nextInt();
      if(choice>=1 && choice <=4){
          obj[choice-1].start_wpc(port.get(choice-1),uniquePid.get(choice-1));
      }*/
        
    }
    
    
}

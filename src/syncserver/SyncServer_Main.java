/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syncserver;

import java.io.IOException;

/**
 *
 * @author apu
 */
public class SyncServer_Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO code application logic here
        int server_port=9876;
        //Sync_v2 obj=new Sync_v2();
        //obj.start_sync(server_port);
        Sync_bully obj=new Sync_bully();
        obj.start_sync(server_port);
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jeffery
 */
public class FetchPortsLocal {
    
    int start_port;
    
    public FetchPortsLocal(int start_port){
        this.start_port = start_port;
    }
    
    public List<String> getPorts(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("port_list.txt"));
            int target_line = start_port + 1;
            String portName = "";
            for (int i = 0; i < target_line; i++) {
                portName = reader.readLine();
            }
            ArrayList list = new ArrayList();
            list.add(portName);
            return list;
        }catch(IOException ex){
            
        }
        return null;
    }
}

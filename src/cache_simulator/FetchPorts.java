/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.sql.DriverManager;
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
public class FetchPorts {
    Statement statement;
    Connection con = null;
    
    int start_port;
    
    public FetchPorts(int start_port){
        this.start_port = start_port;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection(
                    "jdbc:mysql://140.114.75.177/traffic?useUnicode=true&characterEncoding=Big5", 
                    "bear","mysakuya");
            statement = (Statement) con.createStatement();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(FetchPorts.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<String> getPorts(){
        try {
            ResultSet result = statement.executeQuery("select distinct(sp) from school_traffic_tiny limit "+start_port+",1");
            ArrayList list = new ArrayList();
            while(result.next()){
                list.add(result.getString("sp"));
            }
            return list;
        } catch (SQLException ex) {
            Logger.getLogger(FetchPorts.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jeffery
 */
public class DatabaseParserHybrid extends DatabaseParser{

    public DatabaseParserHybrid(String port, SimpleSwitch s) {
        super(port, s);
    }
    
    @Override
    public Packet nextPacket() {
        try {
            if(resultSet.next()){
                long current_time_in_micro_sc = resultSet.getLong("time");

                    //save current data
                    List<Double> rec = simpleSwitch.getHitRate(portName);
                    Cache cache = simpleSwitch.caches.get(portName);
                    if(rec.size() > 0){
                        double hitRate = rec.get(rec.size()-1);
                       if(cache.getCurrentHitTime() == preHit){
                           //cache miss.
                            Main.mainWriter.write(String.format("%s\t%s\t%s\t%s\t%d\n", resultSet.getString("src_ip"),resultSet.getString("dst_ip"),resultSet.getString("protocol"),resultSet.getString("comment"),current_time_in_micro_sc));
                       }
                        preHit = cache.getCurrentHitTime();
                        preAccress = cache.getCurrentAccessTime();
                    }
                    
                    //Start a new minute
                return super.buildPacket(resultSet.getString("src_ip"), resultSet.getString("dst_ip"), resultSet.getString("protocol"), current_time_in_micro_sc);
            }else{
                if(resultSet.last()){
                    last_id = resultSet.getLong("id");
//                    start_num += limit;
                    resultSet = statement.executeQuery(super.getSql());
                    if(!resultSet.first()){
                        FileLogger.logger.log(Level.INFO, String.format("Parser for '%s' ends at id '%d'",portName,last_id));
                        return null;
                    }else{
                        return this.nextPacket();
                    }
                }
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(DatabaseParser.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
    
}

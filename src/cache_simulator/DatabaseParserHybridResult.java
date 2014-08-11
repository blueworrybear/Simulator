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
public class DatabaseParserHybridResult extends DatabaseParser{

    public DatabaseParserHybridResult(String port, SimpleSwitch s) {
        super(port, s);

//        try { 
//            resultSet.close();
//            resultSet = statement.executeQuery(this.getSql());
//        } catch (SQLException ex) {
//            Logger.getLogger(DatabaseParser.class.getName()).log(Level.SEVERE, null, ex);
//        } 
    }
    
    @Override
    final String getSql(){
        System.out.println("fetchData");
        if(!portName.equals("*")){
            return null;
        }
        String sql = "Select * from vm_traffic_hybrid"+CacheProperty.hybrid_result+" where id > "+last_id+" order by time limit "+start_num+","+limit;
        System.out.println("new:"+sql);
        return sql;
    }
    
    @Override
    public Packet nextPacket() {
        try {
            if(resultSet.next()){
                long current_time_in_micro_sc = resultSet.getLong("time");
                if(bound_time.getTimeInMillis()*1000 < current_time_in_micro_sc){
                    //save current data
                    List<Double> rec = simpleSwitch.getHitRate(portName);
                    Cache cache = simpleSwitch.caches.get(portName);
                    if(rec.size() > 0){
                        double hitRate = rec.get(rec.size()-1);
                        Main.mainWriter.write(String.format("%d,%d,%d\n", current_time_in_micro_sc/100000-1,cache.getCurrentHitTime(),cache.getCurrentAccessTime()));
                        cc += 1;
                        preHit = cache.getCurrentHitTime();
                        preAccress = cache.getCurrentAccessTime();
                    }
                    
                    //Start a new minute
                    bound_time.setTimeInMillis((resultSet.getLong("time")/100000+1)*100);
                }
                return buildPacket(resultSet.getString("src_ip"), resultSet.getString("dst_ip"), resultSet.getString("protocol"), current_time_in_micro_sc);
            }else{
                if(resultSet.last()){
                    last_id = resultSet.getLong("id");
                    resultSet = statement.executeQuery(this.getSql());
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

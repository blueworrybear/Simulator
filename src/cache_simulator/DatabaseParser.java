/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Jeffery
 */
public class DatabaseParser implements Parser,java.io.Serializable{
    
    public long last_id = 0;
    long start_num = 0;
    int limit = 1000000;
    public String portName;
    Statement statement;
    Statement update_statement;
    ResultSet resultSet = null;
    Connection con = null;
    Calendar bound_time;
    SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    public SimpleSwitch simpleSwitch;
//    public BufferedWriter bufferWriter;
    
    
    String getSql(){
        System.out.println("fetchData");
        if(portName.equals("*")){
            System.out.println("Select * from vm_traffic_myisam where id > "+last_id+" order by time limit "+start_num+","+limit);
            return "Select * from vm_traffic_myisam where id > "+last_id+" order by time limit "+start_num+","+limit;
        }else if(portName.equals("uplink")){
            System.out.println("Select * from vm_traffic_uplink where id > "+last_id+" order by time limit "+start_num+","+limit);
            return "Select * from vm_traffic_uplink where id > "+last_id+" order by time limit "+start_num+","+limit;
        }else{
            System.out.println("Select * from vm_traffic_myisam where id > "+last_id+" and src_ip = '"+portName+"' order by time limit "+start_num+","+limit);
            return "Select * from vm_traffic_myisam where id > "+last_id+" and src_ip = '"+portName+"' order by time limit "+start_num+","+limit;
        }
    }
    
    public DatabaseParser(){};
    
    public DatabaseParser(String port,SimpleSwitch s){
        this.portName = port;
        this.simpleSwitch = s;
//        try {
//            bufferWriter = new BufferedWriter(new FileWriter(portName+"_"+CacheProperty.size+"_report.txt"));
//        } catch (IOException ex) {
//            Logger.getLogger(DatabaseParser.class.getName()).log(Level.SEVERE, null, ex);
//        }
        try { 
            Class.forName("com.mysql.jdbc.Driver"); 
            con = (Connection) DriverManager.getConnection( 
          "jdbc:mysql://140.114.75.177/traffic?useUnicode=true&characterEncoding=Big5", 
          "bear","mysakuya"); 
            statement = (Statement) con.createStatement();
            update_statement = (Statement) con.createStatement();
            resultSet = statement.executeQuery(getSql());
            
            Date date = sdFormat.parse("2014/06/19 15:00:00.000");
            bound_time = Calendar.getInstance();
            bound_time.setTime(date);
        } catch(ClassNotFoundException e){ 
            System.out.println("DriverClassNotFound :"+e.toString()); 
        }catch (SQLException ex) {
            Logger.getLogger(DatabaseParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(DatabaseParser.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public DatabaseParser(String port,SimpleSwitch s, long last_id){
        this(port,s);
        this.last_id = last_id;
    }
    
    public void resumeParser(){
        try {
            resultSet = statement.executeQuery(getSql());
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    Packet buildPacket(String srcAddr,String dstAddr,String proto,long timeStamp){
        Packet_DB pkt = new Packet_DB();
        
        srcAddr = srcAddr.replaceAll("Vmware_", "00:0c:29:");
        dstAddr = dstAddr.replaceAll("Vmware_", "00:0c:29:");

        switch (proto) {
            case "DNS":
            case "DHCP":
            case "LLMNR":
            case "UAUDP":
            case "DHCPv6":
            case "SAP/SDP":
            case "DMP":
            case "SSDP":
                proto = "UDP";
                break;
            case "NBNS":
            case "HTTP":
            case "HTTP/XML":
            case "BROWSER":
            case "SLiMP3":
            case "SSLv2":
            case "SSL":
            case "BJNP":
            case "TLSv1":
            case "QUAKE3":
            case "OCSP":
            case "HART_IP":
            case "TLSv1.2":
                proto = "TCP";
                break;
            default:
//                proto = proto;
                break;
        }
        
        pkt.dstAddress=dstAddr;
        pkt.portName=this.portName;
        pkt.protocol=proto;
        pkt.srcAddress=srcAddr;
        pkt.time_in_micro_sc = timeStamp;
        return pkt;
    }
    
    public long preHit = 0;
    public long preAccress = 0;
    public int  cc = 0;
    @Override
    public Packet nextPacket() {
        try {
            if(resultSet.next()){
                long current_time_in_micro_sc = resultSet.getLong("time");
                
//                if(bound_time.getTimeInMillis()*1000 < current_time_in_micro_sc){
                    //save current data
                    List<Double> rec = simpleSwitch.getHitRate(portName);
                    Cache cache = simpleSwitch.caches.get(portName);
                    if(rec.size() > 0){
                        double hitRate = rec.get(rec.size()-1);
                        String sql = String.format("insert into record_per_minute (size,policy,ways,fin_time,hitrate,port_name,comment) "
                                + "values(%d,%d,%d,%d,%f,'%s','%s')", 
                                CacheProperty.size,CacheProperty.policy,CacheProperty.ways,bound_time.getTimeInMillis()*1000,hitRate,portName,CacheProperty.comment);
//                        System.out.println(sql);
//                        Main.mainWriter.write(String.format("%d\t%s\t%d\t%d\t%f\t%s\t%s\n", CacheProperty.size,CacheProperty.policy,CacheProperty.ways,bound_time.getTimeInMillis()*1000,hitRate,portName,CacheProperty.comment));
//                        Main.mainWriter.write(String.format("%d\t%s\t%d\t%s\t%f\t%s\t%s\t%f\t%d\t%d\n", CacheProperty.size,CacheProperty.policy,CacheProperty.ways,sdFormat.format(bound_time.getTime()),hitRate,portName,CacheProperty.comment,
//                                ((double)(cache.getCurrentHitTime()-preHit)/(double)(cache.getCurrentAccessTime()-preAccress)),cache.getCurrentHitTime(),cache.getCurrentAccessTime()
//                                        ));
                        Main.mainWriter.write(String.format("%d\t%s\t%d\t%d\t%f\t%s\t%s\t%d\t%d\n", CacheProperty.size,CacheProperty.policy,CacheProperty.ways,current_time_in_micro_sc,hitRate,portName,CacheProperty.comment,cache.getCurrentHitTime(),cache.getCurrentAccessTime()));
//                        Main.mainWriter.write(String.format("%d\t%d\t%d\n", cc,cache.getCurrentHitTime(),cache.getCurrentAccessTime()));
                        cc += 1;
//                        Main.mainWriter.write(String.format("%d,%s,%d,%d,%f,%d\n",current_time_in_micro_sc,portName,cache.getCurrentHitTime(),cache.getCurrentAccessTime(),hitRate,CacheProperty.size));
//                        bufferWriter.write(String.format("%d,%s,%d,%d,%f,%d\n",current_time_in_micro_sc,portName,cache.getCurrentHitTime(),cache.getCurrentAccessTime(),hitRate,CacheProperty.size));
//                        System.out.println(cache.getCurrentHitTime()+","+cache.getCurrentAccessTime()+","+preHit+","+preAccress);
                        preHit = cache.getCurrentHitTime();
                        preAccress = cache.getCurrentAccessTime();
//                        int result = update_statement.executeUpdate(sql);
                    }
                    
                    //Start a new minute
                    bound_time.setTimeInMillis((resultSet.getLong("time")/100000+1)*100);
//                    bound_time.set(Calendar.MILLISECOND , 0);
//                    bound_time.add(Calendar.MILLISECOND, 1);
//                }
                return buildPacket(resultSet.getString("src_ip"), resultSet.getString("dst_ip"), resultSet.getString("protocol"), current_time_in_micro_sc);
            }else{
                if(resultSet.last()){
                    last_id = resultSet.getLong("id");
//                    start_num += limit;
                    resultSet = statement.executeQuery(getSql());
                    if(!resultSet.first()){
                        FileLogger.logger.log(Level.INFO, String.format("Parser for '%s' ends at id '%d'",portName,last_id));
//                        resultSet.close();
//                        bufferWriter.flush();
//                        bufferWriter.close();
                        return null;
                    }else{
                        return this.nextPacket();
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatabaseParser.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bear
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    
    public static BufferedWriter mainWriter;
    public static BufferedWriter missWriter = null;
    public static String bufferLine = "";
    
    public static void main(String[] args) throws FileNotFoundException {
        
        try {
            if(CacheProperty.parser_type == CacheProperty.HYBRID){
                mainWriter = new BufferedWriter(new FileWriter("hybrid_size_"+CacheProperty.size+".txt",true));
            }else if(CacheProperty.parser_type == CacheProperty.HYBRID_RESULT){
                mainWriter = new BufferedWriter(new FileWriter("hybrid_"+CacheProperty.hybrid_result+"_result_size_"+CacheProperty.size+".csv",true));
            }else if(CacheProperty.parser_type == CacheProperty.LOCAL_PARSER){
                mainWriter = new BufferedWriter(new FileWriter("simulate_result/port"+CacheProperty.start_port+"_result_size_"+CacheProperty.size+".txt",true));
                missWriter = new BufferedWriter(new FileWriter("simulate_result/miss_port"+CacheProperty.start_port+"_result_size_"+CacheProperty.size+".txt",true));
            }else{
                mainWriter = new BufferedWriter(new FileWriter("report_to_database.txt",true));
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        String load_data = "";
        boolean loading = false;
        if(args.length > 0){
            load_data = args[0];
            loading = true;
            System.out.println("Loading data: "+load_data);
        }
        System.out.println("Start simulator:"+CacheProperty.comment);
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Calendar cal = Calendar.getInstance();
        String simulatorName = sdFormat.format(cal.getTime());
        FileLogger.init(simulatorName);
        
        boolean is_perport = true;
        boolean is_central = false;
        
        FileLogger.logger.log(Level.CONFIG, String.format("Start simulator with\n"
                + "cache size: %d\n"
                + "cache ways: %d\n"
                + "cache policy: %d\n"
                + "comment: %s\n"
                + "================================================\n",CacheProperty.size,CacheProperty.ways,CacheProperty.policy,CacheProperty.comment));
        
        ArrayList<String> pNames = new ArrayList<>();
        ArrayList<DatabaseParser> parsers;
        
        //add port name
        if(!loading){
            switch(CacheProperty.load_table){
                case CacheProperty.LOAD_PORT:
                    FetchPorts fp = new FetchPorts(CacheProperty.start_port);
                    Iterator<String> it = fp.getPorts().iterator();
                    while(it.hasNext()){
                        pNames.add(it.next());
                    }
                    break;
                case CacheProperty.LOAD_ALL:
                    pNames.add("*");
                    break;
                default:
                    pNames.add("192.168.0.133");
                    pNames.add("192.168.0.143");
                    pNames.add("192.168.0.152");
                    pNames.add("192.168.0.169");
                    pNames.add("192.168.0.176");
                    pNames.add("192.168.0.179");
                    pNames.add("192.168.0.224");
                    pNames.add("192.168.0.225");
//                    pNames.add("*");
                    pNames.add("uplink");
                    break;
            }
        }
        
        
        
        SimpleSwitch sw;
        if(!loading){
            if(CacheProperty.switch_type == CacheProperty.SWITCH_TCP){
                System.out.println("Running TCP switch now.");
                sw = new SimpleSwitch_tcp(pNames.toArray(new String[pNames.size()]),is_perport,is_central);
            }else{
                sw = new SimpleSwitch(pNames.toArray(new String[pNames.size()]),is_perport,is_central);
            }
        }else{
            sw = (new Main()).deserialzeSwitch(load_data);
            System.out.println("Open simulator with cache size: "+sw.caches.size());
        }

        //prepare for new parser.
        if(!loading){
            parsers = new ArrayList<>();
            for(String port_name : pNames){
                switch(CacheProperty.parser_type){
                    case CacheProperty.HYBRID:
                        System.out.println("Running hybrid simulator.");
                        parsers.add(new DatabaseParserHybrid(port_name, sw));
                        break;
                    case CacheProperty.HYBRID_RESULT:
                        System.out.println("Running hybrid result.");
                        parsers.add(new DatabaseParserHybridResult(port_name, sw));
                        break;
                    case CacheProperty.LOCAL_PARSER:
                        System.out.println("Running local data: "+port_name);
                        File file = new File("result/"+port_name+".txt");
                        if(port_name.equals("*")){
                            parsers.add(new LocalDatabaseParser("school_traffic_sorted", sw, port_name));
                        }else{
                            if(file.exists()){
                                parsers.add(new LocalDatabaseParser("result/"+port_name+".txt", sw, port_name));
                            }else{
                                System.out.println(port_name+" does not exist.");
                            }
                        }
                        break;
                    default:
                        parsers.add(new DatabaseParser(port_name, sw));
                        break;
                }
            }  
        }else{
            ParsersHolder ph = (new Main()).deserialzeParsers(load_data);
            Set<Entry<String,Long>> set = ph.parsersID.entrySet();
            Iterator<Entry<String,Long>> it = set.iterator();
            parsers = new ArrayList<>();
            while(it.hasNext()){
                Entry<String,Long> entry = it.next();
                DatabaseParser dp = new DatabaseParser(entry.getKey(), sw, entry.getValue());
                dp.resumeParser();
                parsers.add(dp);
            }
        }

        boolean isEnd = false;
        while(!isEnd){

            int fin_count = 0;

            for(Parser p : parsers){
                Packet pkt = p.nextPacket();
                if(pkt!=null){
                    if(!sw.lookup(pkt) && missWriter != null){
                        try {
                            missWriter.write(bufferLine+"\n");
                        } catch (IOException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }else{
                    fin_count++;
                }
            }

            if(fin_count>=parsers.size()){
                isEnd = true;
            }

        }
        Main main = new Main();
        main.serializeSwitch(sw, simulatorName);
        ParsersHolder ph = new ParsersHolder();
        for(DatabaseParser p : parsers){
            ph.parsersID.put(p.portName, p.last_id);
        }
        main.serializeParsers(ph, simulatorName);
        
        try {
            mainWriter.flush();
            mainWriter.close();
            missWriter.flush();
            missWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void serializeSwitch(SimpleSwitch simpleSwitch,String fileName){
	   try{
		FileOutputStream fout = new FileOutputStream(fileName+".ser");
		ObjectOutputStream oos = new ObjectOutputStream(fout);   
		oos.writeObject(simpleSwitch);
		oos.close();
                fout.close();
		System.out.println("Done");
 
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
   }
    public SimpleSwitch deserialzeSwitch(String fileName){
 
	   SimpleSwitch simpleSwitch;
 
	   try{
		   FileInputStream fin = new FileInputStream(fileName+".ser");
		   ObjectInputStream ois = new ObjectInputStream(fin);
                   simpleSwitch = (SimpleSwitch)ois.readObject();
                   
		   ois.close();
                   fin.close();
 
		   return simpleSwitch;
 
	   }catch(Exception ex){
		   ex.printStackTrace();
		   return null;
	   } 
   } 
    
    public void serializeParsers(ParsersHolder parsers,String fileName){
	   try{
		FileOutputStream fout = new FileOutputStream(fileName+"_parser.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fout);   
		oos.writeObject(parsers);
		oos.close();
                fout.close();
		System.out.println("Done");
 
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
   }
    public ParsersHolder deserialzeParsers(String fileName){
 
	   ParsersHolder parsers;
 
	   try{
		   FileInputStream fin = new FileInputStream(fileName+"_parser.ser");
		   ObjectInputStream ois = new ObjectInputStream(fin);
                   parsers = (ParsersHolder)ois.readObject();
                   
		   ois.close();
                   fin.close();
 
		   return parsers;
 
	   }catch(Exception ex){
		   ex.printStackTrace();
		   return null;
	   } 
   } 
    
}

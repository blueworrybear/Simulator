/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bear
 */
public class SimpleSwitch implements Switch,java.io.Serializable{
    
    public final int PER_PORT_CACHE_ONLY = 0;
    public final int PER_PORT_AND_CENTRALCACHE = 1;
    public final int CENTRAL_CACHE_ONLY = 2;
    public final static String CENTRAL_CACHE = "central_cache";
    
    
    String[] portNames;
    public HashMap<String,Cache> caches;
    
    int level = 1;
    int port_num;
    boolean is_per_port_cache;
    boolean is_central_cache;
    
    public int switch_type;
    
    public SimpleSwitch(String[] portNames, boolean is_per_port, boolean is_central){
        this.switch_type = 0;
        this.portNames = portNames;
        this.port_num = portNames.length;
        this.is_central_cache = is_central;
        this.is_per_port_cache = is_per_port;
        caches = new HashMap<>();
        
        if(CacheProperty.level == 2){
            level = 2;
            switch_type = PER_PORT_AND_CENTRALCACHE;
        }else if(is_per_port && !is_central){
            switch_type = PER_PORT_CACHE_ONLY;
        }else{
            switch_type = CENTRAL_CACHE_ONLY;
        }
        
        switch(switch_type){
            case PER_PORT_CACHE_ONLY:
            for (String portName : portNames) {
                if (!CacheProperty.is_direct) {
                    caches.put(portName, new MutableAssociativeCache(CacheProperty.size, CacheProperty.ways, CacheProperty.policy));
                } else {
                    caches.put(portName, new DirectMappedCache(CacheProperty.size));
                }
            }
                break;
            case CENTRAL_CACHE_ONLY:
//                System.out.println("CENTRAL");
                if(!CacheProperty.is_direct){
                    caches.put(CENTRAL_CACHE, new MutableAssociativeCache(CacheProperty.size, CacheProperty.ways, CacheProperty.policy));
                }else{
                    caches.put(CENTRAL_CACHE, new DirectMappedCache(CacheProperty.size));
                }
                break;
            case PER_PORT_AND_CENTRALCACHE:
            for (String portName : portNames) {
                if (!CacheProperty.is_direct) {
                    caches.put(portName, new MutableAssociativeCache(CacheProperty.size/portNames.length, CacheProperty.ways, CacheProperty.policy));
                } else {
                    caches.put(portName, new DirectMappedCache(CacheProperty.size/portNames.length));
                }
            }
                if(!CacheProperty.is_direct){
                    caches.put(CENTRAL_CACHE, new MutableAssociativeCache(CacheProperty.size, CacheProperty.ways, CacheProperty.policy));
                }else{
                    caches.put(CENTRAL_CACHE, new DirectMappedCache(CacheProperty.size));
                }
                break;
        }
    }
    
    @Override
    public boolean lookup(Packet packet) {
        switch(switch_type){
            case CENTRAL_CACHE_ONLY:
                return caches.get(CENTRAL_CACHE).find(packet);
            case PER_PORT_CACHE_ONLY:
                return caches.get(packet.portName).find(packet);
            case PER_PORT_AND_CENTRALCACHE:
                if(!caches.get(packet.portName).find(packet)){
                    caches.get(CENTRAL_CACHE).find(packet);
                }else{
                    if(!CacheProperty.is_direct){
                        AssociativeCache cache  = (AssociativeCache) caches.get(CENTRAL_CACHE);
                        cache.hit ++;
                        cache.access ++;
                        cache.hitRateRecord.add((double)cache.hit/(double)cache.access);
                    }else{
                        DirectMappedCache cache = (DirectMappedCache) caches.get(CENTRAL_CACHE);
                        cache.hit ++;
                        cache.hitRateRecord.add(cache.hit/(cache.hit+cache.miss));
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public List<Double> getHitRate(String cacheName) {
        if(!CacheProperty.is_direct){
            AssociativeCache cache = (AssociativeCache) caches.get(cacheName);
            return cache.hitRateRecord;
        }else{
            DirectMappedCache cache = (DirectMappedCache) caches.get(cacheName);
            return cache.hitRateRecord;
        }
    }

    @Override
    public void exportReport() {
        
        File dir = new File("report");
        
        if(!dir.exists()){
            dir.mkdir();
        }
        
        //List<Double> avg_rec = new ArrayList<Double>();
        
        Set<String> keys = caches.keySet();
        
        for(String str : keys){
            
            try {
                
                FileWriter fw = new FileWriter(new File("report/"+str+"_record.txt"));
                List<Double> rec = this.getHitRate(str);          
                for(int i=0;i<rec.size();i++){
                    fw.write(i+"\t"+rec.get(i)+"\n");
                }
            } catch (IOException ex) {
                Logger.getLogger(SimpleSwitch.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Cache c = caches.get(str);
            
            
            
        }    }
    
}

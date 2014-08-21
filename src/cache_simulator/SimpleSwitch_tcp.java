/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.util.HashMap;

/**
 *
 * @author bear
 */
public class SimpleSwitch_tcp extends SimpleSwitch{
    
    public SimpleSwitch_tcp(String[] portNames, boolean is_per_port, boolean is_central){
        super(portNames, is_per_port, is_central);
    }
    
    @Override
    public boolean lookup(Packet packet) {
        if(!packet.protocol.equals("06")){
//            caches.get(packet.portName).accessPlus();
            return false;
        }
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
}

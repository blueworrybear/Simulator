/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache_simulator;

import java.util.Set;

/**
 *
 * @author jybowser
 */
public class DynamicAdaptedSwitch extends SimpleSwitch {

    int lookupCounter;
    final int resize_interval = 100;

    public DynamicAdaptedSwitch(String[] portNames, boolean is_per_port, boolean is_central) {
        super(portNames, is_per_port, is_central);
        lookupCounter = 0;
    }

    @Override
    public boolean lookup(Packet p) {
        
        boolean h = super.lookup(p);

        if (lookupCounter == resize_interval - 1) {

            Set<String> set = caches.keySet();
            double maxHitRate = -1, minHitRate = Double.MAX_VALUE;
            String maxCacheName="", minCacheName="";
            
            for (String str : set) {
                Cache c = caches.get(str);
                double hr = c.hitRateRecord.get(c.hitRateRecord.size() - 1);
                if (hr > maxHitRate) {
                    maxCacheName = str;
                    maxHitRate = hr;
                } else if (hr < minHitRate) {
                    minCacheName = str;
                    minHitRate = hr;
                }
            }
                        
            
            if(!CacheProperty.is_direct){
                MutableAssociativeCache maxCache = (MutableAssociativeCache) caches.get(maxCacheName);
                MutableAssociativeCache minCache = (MutableAssociativeCache) caches.get(minCacheName);
                if(maxCache.resize(-1)!=0){
                    minCache.resize(1);
                }
                    
            }
            
        }

        lookupCounter++;
        lookupCounter %= resize_interval;
        return h;
    }

}

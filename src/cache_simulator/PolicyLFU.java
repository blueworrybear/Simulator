/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jybowser
 */
public class PolicyLFU implements Policy,java.io.Serializable{
    
    int priority;
    
    public PolicyLFU(){
        priority = 0;
    }

    @Override
    public void update() {
        //Nothing to do with
    }

    @Override
    public int findVictim(Map<Integer,Block> entry) {
        
        int min = Integer.MAX_VALUE;
        int min_index=-1;
        Set<Integer> key = entry.keySet();
        
        for(Integer i : key){
            BlockLFU b = (BlockLFU)entry.get(i);
            if(b.priority<min){
                min = b.priority;
                min_index=i;
            }
            
        }
        
        return min_index;
    }
    
}

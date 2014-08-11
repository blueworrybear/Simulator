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

    @Override
    public int compare(Block o1, Block o2) {
        
        BlockLFU f1,f2;
        
        f1 = (BlockLFU)o1;
        f2 = (BlockLFU)o2;
        
        return f1.priority-f2.priority;
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

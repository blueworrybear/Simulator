/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author bear
 */
public class PolicyLRU implements Policy,java.io.Serializable{

    
    long time;
    
    public PolicyLRU(){
        this.time = 0;
    }
    
    @Override
    public void update() {
        time ++;
    }

    @Override
    public int findVictim(Map<Integer,Block> entry) {
        long min = Long.MAX_VALUE;
        int index = 0;
        Set<Integer> key = entry.keySet();
        for(Integer k : key){
            BlockLRU block = (BlockLRU)entry.get(k);
            if (block.time <= min) {
                min = block.time;
                index = k;
            }
        }
        return index;
    }

    @Override
    public int compare(Block o1, Block o2) {
        
        BlockLRU r1,r2;
        
        r1 = (BlockLRU)o1;
        r2 = (BlockLRU)o2;
        
        return (int) (r1.time-r2.time);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

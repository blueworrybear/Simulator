/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dafz
 */
public class PolicyFIFO implements Policy,java.io.Serializable {

    private ArrayList<LinkedList<Block>> FIFOList;
    private int _index;
    //private LinkedList linklist;
    public void setIndex(int index){
        _index = index;
    }
    public int size(int index){
       
        FIFOList.get(index).size();
        return FIFOList.get(index).size();
    
    }
    public void DequeueAndEnqueue(int index,Packet packet){
        
        System.out.printf("s = %d\n",FIFOList.get(index).size());
        FIFOList.get(index).addLast(new BlockFIFO(packet));
        FIFOList.get(index).removeFirst();
        
        
 
    }
    public void AddNewPacket(int index,Packet packet){
        FIFOList.get(index).addLast(new BlockFIFO(packet));
        
    }
    public PolicyFIFO(int ways){
        System.out.printf("ways = %d\n",ways);
        FIFOList = new ArrayList<LinkedList<Block>>();
        for(int i=0;i<ways;i++){
            FIFOList.add(new LinkedList<Block>()); 
        }
        
           
    }
        
       
    
    @Override
    public void update() {
        
    }

    @Override
    public int findVictim(Map<Integer,Block> entry) {
        
        Set<Integer> key = entry.keySet();
        
        for(Integer k : key){
            return k;
        }
        return 0;
       
       // return linklist.indexOf(linklist.getFirst());
       
    }
    
}

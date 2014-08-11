/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 *
 * @author jybowser
 */
public class CacheStructure {
    
    HashMap<Integer,Block> m;
    PriorityQueue<Block> p;
    int policy,size;
    int c=0;

    public CacheStructure(int p,int s,Comparator<Block> c){
        policy = p;
        size = s;
        m = new HashMap<Integer,Block>();
        this.p = new PriorityQueue<Block>(size,c);
    }
    
    public void add(Block b){
        
        if(m.size()==this.size){
            remove();
        }
        int key;
        if(b.getPacket()!=null){
            key = b.getPacket().getHashCode();
        }else{
            key=c;
            c++;
        }
        m.put(key,b);
        p.offer(b);
    }
    
    public boolean find(Packet pkt){
        return m.get(pkt.getHashCode())!=null;
    }
    
    private boolean remove(){
        Block blk = p.poll();
        if(blk==null){
            return false;
        }       
        int key = blk.getPacket().getHashCode();
        m.remove(key);
        
        return true;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jybowser
 */
public class DirectMappedCache extends Cache implements java.io.Serializable{
    
    int size;
    List<CacheEntry> entries;
    
    
    double hit,miss;
    
    public DirectMappedCache(int size){
        this.size = size;
        entries = new ArrayList<CacheEntry>();
        
        hit=0;
        miss=0;
        
        hitRateRecord = new ArrayList<Double>();
        
        for(int i=0;i<this.size;i++){
            entries.add(new CacheEntry(null));
        }
        
    }

    @Override
    public boolean find(Packet packet) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        String str = packet.toString();
        int hashCode = str.hashCode();
        CacheEntry entry = entries.get(getEntryIndex(packet));
        
        if(entry.occupied && entry.hashCode==hashCode){
            hit=hit+1;
            hitRateRecord.add(hit/(hit+miss));
            return true;
        }else{
            insert(packet);
            miss=miss+1;
            hitRateRecord.add(hit/(hit+miss));
            return false;
        }
    }

    @Override
    public void insert(Packet packet) {
        
        int hashCode = packet.toString().hashCode();
        
        int index = getEntryIndex(packet);
        
        CacheEntry entry = entries.get(index);
        
        entry.occupied=true;
        entry.hashCode=hashCode;
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getEntryIndex(Packet packet) {
        return Math.abs(packet.getHashCode())%size;
    }

    @Override
    public long getCurrentHitTime() {
        return (long) hit;
    }

    @Override
    public long getCurrentAccessTime() {
        return (long) (hit+miss);
    }
    
    private class CacheEntry{
        private boolean occupied;
        private int hashCode;
        
        public CacheEntry(Packet pkt){
            
            if(pkt==null){
                occupied=false;
                hashCode=-1;
            }else{
                String str = pkt.toString();
                hashCode = str.hashCode();
                occupied=true;
            }          
            
            
        }
        
    }
    
    public void accessPlus(){
        this.miss ++;
    }
    
}

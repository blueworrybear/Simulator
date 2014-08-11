/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bear
 */
public class AssociativeCache extends Cache implements java.io.Serializable{

    protected int size, ways,policy;

    /**
     *
     */
    public ArrayList<HashMap<Integer,Block>> cache;
    public Policy replace_policy;
    
    
    
    long hit = 0;
    long access = 1;
    
    int lru_vc=-1,lfu_vc=-1; //Victim Candidate's index number
    int lfreq,lastAccess;
    
    
    public AssociativeCache(int size, int ways,int policy){
        this.size = size;
        this.ways = ways;
        this.policy = policy;
        
        hitRateRecord = new ArrayList<Double>();
        
        switch(this.policy){
            case CacheFactory.POLICY_LRU:
                this.replace_policy = new PolicyLRU();
                break;
            case CacheFactory.POLICY_LFU:
                this.replace_policy = new PolicyLFU();
                break;
            case CacheFactory.POLICY_FIFO:
                this.replace_policy = new PolicyFIFO(this.ways);
                break;
            default:
                break;
        }
        
        cache = new ArrayList<HashMap<Integer,Block>>();
        for(int i = 0; i < this.ways; i++){
            int entry_size = (int) Math.round((double)this.size/(double)this.ways);
            HashMap<Integer,Block> list = new HashMap<Integer,Block>();
            cache.add(list);
            for(int j = 0; j < entry_size; j++){
                switch(this.policy){
                    case CacheFactory.POLICY_LRU:
                        BlockLRU br = new BlockLRU(null, 0);
                        list.put(j,br);
                        break;
                    case CacheFactory.POLICY_LFU:
                        BlockLFU bf = new BlockLFU(null);
                        list.put(j,bf);
                        break;
                    case CacheFactory.POLICY_FIFO:
                        BlockFIFO bfi = new BlockFIFO(null);
                        list.put(j,bfi);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public int getEntryIndex(Packet packet) {
        return Math.abs(packet.getHashCode())%this.ways;
    }
    
    
    
    @Override
    public boolean find(Packet packet) {
        this.access ++;
        this.replace_policy.update();
        if(hitRateRecord.size() > 1000){
            hitRateRecord.clear();
        }
        hitRateRecord.add((double)hit/(double)access);
        if(this.policy==CacheFactory.POLICY_LFU){
            for(HashMap<Integer,Block> bs : cache){
                for(Integer key : bs.keySet()){
                //for(Block b : bs){
                    BlockLFU ub = (BlockLFU)bs.get(key);
                    ub.priority--;
                //}
                }
            }
        }
        int index = getEntryIndex(packet);
        HashMap<Integer,Block> entry = cache.get(index);
//        System.out.println(entry.size());
        /*for(Block block : entry){
            if((packet.equals(block.getPacket()))){
                hitBlock(block);
                this.hit ++;
                return true;
            }
        }*/
        
        if(entry.get(packet.getHashCode())!=null){
            this.hit ++;
            return true;
        }
        
        this.insert(packet);
        return false;
    }
    
    public void hitBlock(Block block){
        switch(this.policy){
            case CacheFactory.POLICY_LRU:
                PolicyLRU p = (PolicyLRU)this.replace_policy;
                BlockLRU b = (BlockLRU)block;
                b.setTime(p.time);
                break;
            case CacheFactory.POLICY_LFU:
                PolicyLFU fp = (PolicyLFU)this.replace_policy;
                BlockLFU fb = (BlockLFU)block;
                fb.priority+=2;
            case CacheFactory.POLICY_FIFO:
                
                
                break;
            default:
                break;
        }
    }

    @Override
    public void insert(Packet packet) {
        
        HashMap<Integer,Block> entry = cache.get(getEntryIndex(packet));
        
        switch(this.policy){
            case CacheFactory.POLICY_LFU:
                PolicyLFU f = (PolicyLFU)this.replace_policy;
                if(entry.size()==(int) Math.round((double)this.size/(double)this.ways)){
                    entry.remove(f.findVictim(entry));
                }
                entry.put(packet.getHashCode(),new BlockLFU(packet));
                break;
            case CacheFactory.POLICY_LRU:
                PolicyLRU p = (PolicyLRU)this.replace_policy;
                
                int victim_index = p.findVictim(entry);
                entry.remove(victim_index);
                BlockLRU b = new BlockLRU(packet, p.time);
                entry.put(packet.getHashCode(),b);
                
                break;
            case CacheFactory.POLICY_FIFO:
                PolicyFIFO fifo = (PolicyFIFO)this.replace_policy;
//                int index = packet.getHashCode()% this.ways;
                int index = getEntryIndex(packet);
                fifo.setIndex(index);
                //System.out.printf("index = %d\n",index);
                //ArrayList<Block> fifoentry = cache.get(packet.getHashCode());
                //System.out.printf("entry.size() = %d\n",entry.size());
                //System.out.printf("n = %d\n",(int) Math.round((double)this.size/(double)this.ways));
                
                if(entry.size() == (int) Math.round((double)this.size/(double)this.ways)){
                    fifo.DequeueAndEnqueue(index,packet);
                    entry.remove(fifo.findVictim(entry));
                    entry.put(packet.getHashCode(),new BlockFIFO(packet));
                }
                else{
                    fifo.AddNewPacket(index,packet);
                    entry.put(packet.getHashCode(),new BlockFIFO(packet));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public long getCurrentHitTime() {
        return hit;
    }

    @Override
    public long getCurrentAccessTime() {
        return access;
    }
    
    public void  accessPlus(){
        this.access ++;
    }
    
}

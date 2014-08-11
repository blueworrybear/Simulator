/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

/**
 *
 * @author bear
 */
public class BlockLRU implements Block,java.io.Serializable{

    private Packet packet;
    long time;
    
    public BlockLRU(Packet packet,long t){
        this.packet = packet;
        this.time = t;
    }
    
    public void setTime(long t){
        this.time = t;
    }
    
    public long getTime(){
        return time;
    }
    
    @Override
    public Packet getPacket() {
        return this.packet;
    }
    
}

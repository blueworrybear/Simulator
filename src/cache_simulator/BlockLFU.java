/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

/**
 *
 * @author jybowser
 */
public class BlockLFU implements Block,java.io.Serializable{
    
    private Packet pkt;
    int priority;
    
    public BlockLFU(Packet p){
        pkt = p;
        priority = 0;
    }

    @Override
    public Packet getPacket() {
        return pkt;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

/**
 *
 * @author dafz
 */
public class BlockFIFO implements Block ,java.io.Serializable{
    private Packet packet;
    public BlockFIFO(Packet packet){
        this.packet = packet;
    }
    @Override
    public Packet getPacket() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return this.packet;
    }
    
}

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
public class Packet implements java.io.Serializable{
    public String protocol;
    public String srcAddress;
    public String dstAddress;
    public String portName;
    
    public int getHashCode(){
        return this.toString().hashCode();
    }
    
    @Override
    public String toString(){
        return protocol+srcAddress+dstAddress+portName;
    }

    @Override
    public boolean equals(Object obj) {
        Packet packet = (Packet) obj;
        if (this == null || packet == null) {
            return false;
        }
        return packet.getHashCode() == this.getHashCode();
    }
    
    
}

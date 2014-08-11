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
public interface Parser {
    
    //Return null if end of file.
    public Packet nextPacket();
    
}

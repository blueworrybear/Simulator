/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.util.List;

/**
 *
 * @author bear
 */
public interface Switch {
    
    public boolean lookup(Packet packet);
    public List<Double> getHitRate(String cacheName);
    public void exportReport();
    
}

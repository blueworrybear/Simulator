/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.util.List;
import java.util.Map;

/**
 *
 * @author bear
 */
public interface Policy {
    
    //This method must be called whenever cache call find().
    public void update();
    public int findVictim(Map<Integer,Block> entry);
}

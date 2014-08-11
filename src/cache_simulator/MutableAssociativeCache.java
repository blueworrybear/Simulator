/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author jybowser
 */
public class MutableAssociativeCache extends AssociativeCache implements java.io.Serializable{

    public MutableAssociativeCache(int size, int ways, int policy) {
        super(size, ways, policy);
    }
    
    public int resize(int dSize){
        
        if(size+ways*dSize<ways){
            return 0;
        }else{
            updateSize(dSize);           
        }
        
        return dSize;
    }
    
    private void updateSize(int dSize){
        
        //N
            
    }
    
}

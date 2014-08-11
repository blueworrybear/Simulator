/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.util.ArrayList;

/**
 *
 * @author bear
 */
public abstract class Cache implements java.io.Serializable{
    
    abstract public boolean find(Packet packet);
    abstract void insert(Packet packet);
    abstract int getEntryIndex(Packet packet);
    public ArrayList<Double> hitRateRecord;
    public abstract long getCurrentHitTime();
    public abstract long getCurrentAccessTime();
    public abstract void accessPlus();
    
    
}

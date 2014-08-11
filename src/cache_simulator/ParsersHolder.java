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
 * @author Jeffery
 */
public class ParsersHolder implements java.io.Serializable{
    public HashMap<String,Long> parsersID;

        public ParsersHolder() {
            this.parsersID = new HashMap<>();
        }
}

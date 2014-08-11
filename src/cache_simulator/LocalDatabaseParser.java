        /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jeffery
 */
public class LocalDatabaseParser extends DatabaseParser{
    public SimpleSwitch simpleSwitch;
    String filePath;
//    String portName;
    BufferedReader reader;
    
    public LocalDatabaseParser(String filePath, SimpleSwitch simpleSwitch,String portName) throws FileNotFoundException{
        super();
        this.filePath = filePath;
        this.simpleSwitch = simpleSwitch;
        this.portName = portName;
        reader = new BufferedReader(new FileReader(filePath));
    }

    @Override
    public Packet nextPacket() {
        try {
            String line;
            if((line = reader.readLine()) != null){
                Main.bufferLine = line;
                String[] fields = line.split("\t");
                List<Double> rec = simpleSwitch.getHitRate(portName);
                Cache cache = simpleSwitch.caches.get(portName);
                if(rec.size() > 0){
                    double hitRate = rec.get(rec.size()-1);
                    Main.mainWriter.write(String.format("%s\t%d\t%d\t%d\t%f\t%d\n",portName,Long.valueOf(fields[0]),cache.getCurrentHitTime(),cache.getCurrentAccessTime(),hitRate,CacheProperty.size));
                }
                return buildPacket(fields[1], fields[2], fields[3], Long.valueOf(fields[0]));
            }else{
                return null;
            }
        } catch (IOException ex) {
            Logger.getLogger(LocalDatabaseParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
}

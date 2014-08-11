/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache_simulator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author bear
 */
public class CacheProperty {
    
    public final static int DEFAULT = 0;
    public final static int HYBRID = 1;
    public final static int HYBRID_RESULT = 2;
    public final static int LOCAL_PARSER = 3;
    
    public final static int LOAD_PORT = 1;
    public final static int LOAD_ALL = 2;
    
    //SWITCH_TYPE
    public final static int SWITCH_SIMPLE = 0;
    public final static int SWITCH_TCP = 1;
    
    private static Properties props;
    public final static int level = Integer.valueOf(getConfig("CACHE_LEVEL"));
    public static int size = Integer.valueOf(getConfig("CACHE_SIZE"));
    public static int ways = Integer.valueOf(getConfig("CACHE_WAY"));
    public static int policy = Integer.valueOf(getConfig("CACHE_POLICY"));
    public static String comment = getConfig("CACHE_COMMENT");
    public static boolean is_direct = Integer.valueOf(getConfig("CACHE_DIRECT")) == 1;
    public static boolean is_central = Integer.valueOf(getConfig("CACHE_CENTRAL")) == 1;
    public static int parser_type = Integer.valueOf(getConfig("PARSER"));
    public static int load_table = Integer.valueOf(getConfig("LOAD_TABLE"));
    public static int hybrid_result = Integer.valueOf(getConfig("HYBRID_RESULT"));
    public static int start_port = Integer.valueOf(getConfig("START_PORT"));
    public static int switch_type = Integer.valueOf(getConfig("SWITCH_TYPE"));
    
    private static void loadProperties() {
         props = new Properties();
         try {
              props.load(new FileInputStream("config.properties"));
         } catch (FileNotFoundException e) {
         } catch (IOException e) {
         }
    }

    private static String getConfig(String key) {
        if (props == null) {
            loadProperties();
        }
        return props.getProperty(key);
    }
}

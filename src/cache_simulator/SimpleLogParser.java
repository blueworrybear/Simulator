/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache_simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jybowser
 */
public class SimpleLogParser implements Parser {

    File logFile;
    BufferedReader bf;

    public SimpleLogParser(File f) throws FileNotFoundException {
        logFile = f;
        bf = new BufferedReader(new FileReader(logFile));
    }

    @Override
    public Packet nextPacket() {
        try {
            if (!bf.ready()) {
                return null;
            } else {
                try{
                    String str = bf.readLine();

                    String content = str.trim();
//                    System.out.println(content);
                    content = content.replaceAll(" +", " ");
                    String[] token;
                    token = content.split(" ");
                    
                    if (token.length < 4) {
                        return null;
                    }

//                    float time = Float.parseFloat(token[0]);
                    String srcAddr = token[0];
                    String dstAddr = token[2];
                    String proto = token[3];

                    srcAddr = srcAddr.replaceAll("Vmware_", "00:0c:29:");
                    dstAddr = dstAddr.replaceAll("Vmware_", "00:0c:29:");

                    switch (proto) {
                        case "DNS":
                        case "DHCP":
                        case "LLMNR":
                        case "UAUDP":
                        case "DHCPv6":
                        case "SAP/SDP":
                        case "DMP":
                        case "SSDP":
                            proto = "UDP";
                            break;
                        case "NBNS":
                        case "HTTP":
                        case "HTTP/XML":
                        case "BROWSER":
                        case "SLiMP3":
                        case "SSLv2":
                        case "SSL":
                        case "BJNP":
                        case "TLSv1":
                        case "QUAKE3":
                        case "OCSP":
                        case "HART_IP":
                        case "TLSv1.2":
                            proto = "TCP";
                            break;
                        default:
                            proto = "TCP";
                    }

                    Packet pkt = new Packet();
                    pkt.dstAddress=dstAddr;
                    pkt.portName=logFile.getName();
                    pkt.protocol=proto;
                    pkt.srcAddress=srcAddr;

                    return pkt;
                }catch(Exception ex){
                    return null;
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(LogParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}

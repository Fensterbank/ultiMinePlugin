/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Objects;
import java.util.Date;
import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import Fensterbank.ultiMinePlugin.HelpClasses.Methoden;
/**
 *
 * @author vncuser
 */
public class GewinnspielLos {
    private String playername;
    private Date timestamp;
    private double wert;
    
    public GewinnspielLos(String playername, double wert) {
        this.playername = playername;
        this.wert = wert;
        this.timestamp = new Date();
    }
    
    public GewinnspielLos(String playername, double wert, long unixTimestamp) {
        this.playername = playername;
        this.wert = wert;
        this.timestamp = new Date(unixTimestamp * 1000);
    }
    
    @Override
    public String toString() {
        return "Wert: " + this.wert + " Ultima, gekauft von " + this.playername + " (" + Convert.toDateTimeString(this.timestamp) + " Uhr)";
    }
    
    public double getWert() {
        return this.wert;
    }
    
    public String getPlayerName() {
        return this.playername;
    }
    
    public Boolean createInDatabase(int GewinnspielID) {
        try {
            Methoden.sqlQuery("ultiMinePlugin", "INSERT INTO GewinnspielLos ( gewinnspielID, player, wert, timestamp ) VALUES  ( '" + GewinnspielID + "' , '" + this.playername + "' , '" + this.wert + "' , '" + Convert.toTimestamp(this.timestamp) + "')", null);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
            
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Objects;
import java.util.Date;
import Fensterbank.ultiMinePlugin.HelpClasses.Convert;


/**
 *
 * @author fred
 */
public class PlayerCountEntry {
    int count;
    Date dateTime;
    String[] players;
    
    public PlayerCountEntry(int count, long dateTime, String player) {
        this.count = count;
        this.dateTime = Convert.toDate(dateTime);
        this.players = player.split(",");
    }
    
    public PlayerCountEntry(int count, Date dateTime, String player) {
        this.count = count;
        this.dateTime = dateTime;
        this.players = player.split(",");
    }
    
    public long getTimestamp() {
        return Convert.toTimestamp(dateTime);
    }
    
    public int getCount() {
        return this.count;
    }
    
    public Date getDate() {
        return this.dateTime;
    }
    
    public String[] getPlayers() {
        return this.players;
    }
    
    @Override
    public String toString() {
        return this.count + " Spieler am " + Convert.toDateTimeString(dateTime);
    }
}

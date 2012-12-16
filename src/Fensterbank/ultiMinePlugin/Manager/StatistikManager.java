/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Manager;

import Fensterbank.ultiMinePlugin.Objects.PlayerCountEntry;
import Fensterbank.ultiMinePlugin.Objects.DataRow;
import Fensterbank.ultiMinePlugin.HelpClasses.Methoden;
import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import java.util.Date;
import Fensterbank.ultiMinePlugin.Manager.WbbManager;
import java.io.*;
import java.util.Arrays;

/**
 *
 * @author fred
 */
public class StatistikManager {

    private PlayerCountEntry playerRecord;
    private WbbManager wbbManager;
    private static final Logger log = Logger.getLogger("Minecraft");
    private Boolean active;

    public StatistikManager(WbbManager currWbb) {
        if (this.loadPlayerRecord()) {
            wbbManager = currWbb;
            log.info("[ultiMine] StatistikManager initialisiert.");
            this.active = true;
        } else {
            log.info("[ultiMine] Fehler beim Initialisieren des StatistikManager. Modul deaktiviert.");
            this.active = false;
        }
    }

    private Boolean loadPlayerRecord() {
        ArrayList<String> columnsToGet = new ArrayList<String>();
        columnsToGet.add("timestamp");
        columnsToGet.add("playercount");
        columnsToGet.add("playerlist");
        try {
            ArrayList<DataRow> rows = Methoden.sqlQuery("ultiMinePlugin", "SELECT timestamp, playercount, playerlist FROM `PlayerRecords` ORDER BY timestamp DESC LIMIT 0, 1", columnsToGet);

            if (rows.size() > 0) {
                playerRecord = Convert.toPlayerCountEntry(rows.get(0));
            } else {
                playerRecord = new PlayerCountEntry(0, new Date(), "");
            }
            return true;
        } catch (Exception ex) {            
            playerRecord = null;
            return false;
        }
    }

    private void storePlayerRecord(PlayerCountEntry entry, String playerList) {   
        try {                        
            Methoden.sqlQuery("ultiMinePlugin", "INSERT INTO PlayerRecords ( timestamp , playercount, playerlist ) VALUES  ( '" + entry.getTimestamp() + "', '" + entry.getCount() + "', '" + playerList + "' )", null);           
        } catch (Exception ex) {           
        }
    }
    
    public Boolean writePlayerCount(Player[] players) {
        try {
            FileWriter fstream = new FileWriter("/var/www/misc/mcserver_portal_widget_list.php");
            BufferedWriter out = new BufferedWriter(fstream);
                        
            String result = "<p style='text-align:center;'>" + players.length + " Spieler online</p><ul style='text-align: left;'>";
            for (Player p : players) {
                if (wbbManager!=null) {
                    long userID = wbbManager.getUserID(p.getName());
                    if (userID!=-1) {
                        String url = "http://ultimine.net/user/" + userID + "/" + p.getName() + "/";
                        result += "<li><a href='" + url + "'>" + p.getDisplayName() + "</a></li>";
                    } else {
                        result += "<li>" + p.getDisplayName() + "</li>";
                    }
                } else {
                    result += "<li>" + p.getDisplayName() + "</a></li>";
                }                                
            }
            result += "</ul>";                        
            result = result.replaceAll("§[0-9a-f]", "");
            result = result.replace("'", "\"");
            
            out.write(result);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }                
    }

    public Boolean checkRecord(Player[] players) {        
        if (players.length > this.playerRecord.getCount()) {
            
            String playerString = "";
            int i = 0;
            for (Player p : players) {
                playerString += p.getName();
                if (i != players.length - 1) {
                    playerString += ",";
                }
                i++;
            }
            PlayerCountEntry newRecord = new PlayerCountEntry(players.length, new Date(), playerString);
            this.playerRecord = newRecord;
            this.storePlayerRecord(newRecord, playerString);
            
            return true;
        } else {
            return false;
        }
    }

    public Boolean isActive() {
        return this.active;
    }

    public PlayerCountEntry getRecord() {
        return this.playerRecord;
    }

    public String getRecordString() {
        return this.playerRecord.toString();
    }
}

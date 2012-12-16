/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.HelpClasses;

import Fensterbank.ultiMinePlugin.Objects.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author vncuser
 */
public class Convert {

    public static News toNews(DataRow row) {
        try {
            return new News(row.getLong("time"), row.getString("topic"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static TeamRank toTeamRank(DataRow row) {
        try {
            return new TeamRank(row.getString("Rangcode"), row.getString("Bezeichnung"), row.getString("Farbcode"), row.getInt("Level"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static PlayerCountEntry toPlayerCountEntry(DataRow row) {
        try {
            return new PlayerCountEntry(row.getInt("playercount"), row.getLong("timestamp"), row.getString("playerlist"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static Gewinnspiel toGewinnspiel(DataRow row) {
        try {
            return new Gewinnspiel(row.getInt("id"), row.getLong("timestampStart"), row.getLong("timestampZiehung"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static GewinnspielLos toGewinnspielLos(DataRow row) {
        try {
            return new GewinnspielLos(row.getString("player"), row.getDouble("wert"), row.getLong("timestamp"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ChatColor toChatColor(String code) {
        return ChatColor.values()[Integer.parseInt(String.valueOf(code.charAt(code.length() - 1)), 16)];
    }
    
    

    public static Strafpunkteinheit toStrafpunktEinheit(DataRow row) {
        try {
            int ID = row.getInt("id");
            long timestamp = row.getLong("timestamp");
            String begruendung = row.getString("begruendung");
            int anzahl = row.getInt("anzahl");
            String username = row.getString("username");
            String vergebenVon = row.getString("vergebenVon");
            return new Strafpunkteinheit(
                    ID,
                    timestamp,
                    begruendung,
                    anzahl,
                    username,
                    vergebenVon);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public static long toTimestamp(Date dateTime) {
        return dateTime.getTime() / 1000;
    }
    
    public static Date toDate(int timestamp) {
        return new Date((long)timestamp * 1000L);
    }
    
    public static Date toDate(long timestamp) {
        return new Date(timestamp * 1000L);
    }
    
    public static int toServerticks(int seconds) {
        return seconds * 20;
    }

    public static String toLocationString(Location loc) {
        return "[" + loc.getWorld().getName() + " (" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ")]";
    }

    public static String toDateTimeString(Date dateTime) {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        return df.format(dateTime);
    }
}

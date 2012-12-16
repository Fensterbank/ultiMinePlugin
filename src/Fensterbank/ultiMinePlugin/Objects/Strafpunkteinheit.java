/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Objects;

import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import Fensterbank.ultiMinePlugin.HelpClasses.Methoden;
import org.bukkit.ChatColor;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author vncuser
 */
public class Strafpunkteinheit {

    private int ID;
    private Long timestamp;
    private Date dateTime;
    private String begründung;
    private int anzahl;
    private String username;
    private String vergebenVon;

    public Strafpunkteinheit(String begründung, int anzahl, String username, String vergebenVon) {
        this.timestamp = (System.currentTimeMillis() / 1000L);
        this.dateTime = Convert.toDate(timestamp);
        this.begründung = begründung;
        this.anzahl = anzahl;
        this.username = username;
        this.vergebenVon = vergebenVon;
    }

    public Strafpunkteinheit(int ID, long timestamp, String begründung, int anzahl, String username, String vergebenVon) {
        this.ID = ID;
        this.timestamp = timestamp;
        this.dateTime = Convert.toDate(timestamp);
        this.begründung = begründung;
        this.anzahl = anzahl;
        this.username = username;
        this.vergebenVon = vergebenVon;
    }

    public void storeToDatabase() {
        try {
            Methoden.sqlQuery("ultiMinePlugin", "INSERT INTO Strafpunkte ( timestamp , anzahl, username, begruendung, vergebenVon ) VALUES  ( '" + this.timestamp + "', '" + this.anzahl + "', '" + this.username + "', '" + this.begründung + "' , '" + this.vergebenVon + "' )", null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getAnzahl() {
        return this.anzahl;
    }
    
    public int getID() {
        return this.ID;
    }
    
    public Date getDate() {
        return this.dateTime;
    }
    
    public String getSpielername() {
        return this.username;
    }

    @Override
    public String toString() {
        String wort = "Punkt";
        ChatColor color1 = ChatColor.DARK_RED;
        ChatColor color2 = ChatColor.RED;
        if (this.anzahl > 1 || this.anzahl < -1) {
            wort += "e";         
        }
        
        if (this.anzahl < 0) {            
            color1 = ChatColor.DARK_GREEN;
            color2 = ChatColor.GREEN;
        }
        

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy, HH:mm");

        return df.format(this.dateTime) + " Uhr - " + color1 + this.anzahl + " " + wort + ": " + color2 + this.begründung;
    }
}

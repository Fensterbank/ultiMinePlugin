/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Objects;

import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import Fensterbank.ultiMinePlugin.HelpClasses.Methoden;
import java.util.ArrayList;
import java.util.Date;
import Fensterbank.ultiMinePlugin.Objects.GewinnspielLos;
import org.bukkit.ChatColor;
import java.util.Calendar;

/**
 *
 * @author vncuser
 */
public class Gewinnspiel {

    private ArrayList<GewinnspielLos> lose;
    private Date startDate;
    private Date ziehungsDate;
    private int ID;

    public Gewinnspiel() {
        this.lose = new ArrayList<GewinnspielLos>();
        this.startDate = new Date();
        this.ziehungsDate = this.calculateZiehungsDate(startDate);
    }
    
    public Gewinnspiel(int ID, long startUnixTimestamp, long ziehungsUnixTimestamp) {
        this.ID = ID;
        this.lose = new ArrayList<GewinnspielLos>();
        this.startDate = new Date(startUnixTimestamp * 1000);
        if (ziehungsUnixTimestamp == 0) {
            this.ziehungsDate = null;
        } else {
            this.ziehungsDate = new Date(ziehungsUnixTimestamp * 1000);
        }
    }

    public Gewinnspiel(ArrayList<GewinnspielLos> lose, Date startDate, Date ziehungsdate) {
        this.lose = lose;
        this.startDate = startDate;
        this.ziehungsDate = ziehungsdate;
    }

    public Gewinnspiel(ArrayList<GewinnspielLos> lose, long startUnixTimestamp, long ziehungsUnixTimestamp) {
        this.lose = lose;
        this.startDate = new Date(startUnixTimestamp * 1000);
        if (ziehungsUnixTimestamp == 0) {
            this.ziehungsDate = null;
        } else {
            this.ziehungsDate = new Date(ziehungsUnixTimestamp * 1000);
        }
    }
    
    public ArrayList<GewinnspielLos> getLose() {
        return this.lose;
    }
    
    public Date getStartDate() {
        return this.startDate;
    }
    
    public int getID() {
        return this.ID;
    }
    
    public Date getZiehungsDate() {
        return this.ziehungsDate;
    }
    
    public double getCommunityPott() {
        double pott = 0;
        for (GewinnspielLos los : this.lose) {
            pott += los.getWert();
        }
        return pott;
    }
    
    public double getSubventionierung() {
        return 0.75 * getCommunityPott();
    }
    
    public double getPott() {
        return getCommunityPott() + getSubventionierung();
    }
    
    public ArrayList<GewinnspielLos> getLoseOfPlayer(String playerName) {
        ArrayList<GewinnspielLos> returnlist = new ArrayList<GewinnspielLos>();
        for (GewinnspielLos los : this.lose) {
            if (los.getPlayerName().equalsIgnoreCase(playerName)) {
                returnlist.add(los);
            }
        }
        return returnlist;
    }
    
    public double getGewinnchance() {
        if (this.getLosAnzahl()<=3)
            return 100.0;
        else
            return (3.0/new Double(this.getLosAnzahl()))*100.0;        
    }
    
    public int getLosAnzahl() {
        return this.lose.size();
    }
    
    public void addLos(GewinnspielLos los) {
        this.lose.add(los);
    }
    
    public void setLose(ArrayList<GewinnspielLos> lose) {
        this.lose = lose;
    }
    
    @Override
    public String toString() {
        return "Gewinnspiel, gestartet: " + Convert.toDateTimeString(startDate) + ", Lose: " + getLosAnzahl() + ", Pottwert: " + getCommunityPott();
    }
    
    public ArrayList<String> toStringList() {
        ArrayList<String> returnList = new ArrayList<String>();
        returnList.add(ChatColor.DARK_GREEN + "Lotterie " + this.ID + " (Gestartet am " + ChatColor.GOLD + Convert.toDateTimeString(startDate) + " Uhr)");        
        returnList.add(ChatColor.WHITE + "Zeitpunkt der Ziehung: " + ChatColor.GOLD + Convert.toDateTimeString(this.ziehungsDate) + " Uhr");
        returnList.add(ChatColor.WHITE + "Anzahl der Lose im Pott: " + ChatColor.GOLD + this.getLosAnzahl());
        returnList.add(ChatColor.WHITE + "Wert der Lose im Pott: " + ChatColor.GOLD + this.getCommunityPott()  + " Ultima");        
        returnList.add(ChatColor.WHITE + "Serversubventionierung: " + ChatColor.GOLD + this.getSubventionierung() + " Ultima");
        returnList.add(ChatColor.WHITE + "Gesamtwert des Potts: " + ChatColor.GOLD + this.getPott()  + " Ultima");
        returnList.add(ChatColor.WHITE + "Deine theorethische Gewinnchance: " + ChatColor.GOLD + this.getGewinnchance() + "%");
        return returnList;
    }
    
    public Date calculateZiehungsDate(Date date) {
        Boolean stop = false;
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        while (stop == false) {
            cal1.add(Calendar.DAY_OF_MONTH, 1);
            if (cal1.get(Calendar.DAY_OF_WEEK)==4 || cal1.get(Calendar.DAY_OF_WEEK)==7) {
                cal1.set(Calendar.MINUTE, 0);
                cal1.set(Calendar.HOUR_OF_DAY, 20);
                return cal1.getTime();
            }
        }
        return null;
    }
    
    public Boolean createInDatabase() {
        try {
            Methoden.sqlQuery("ultiMinePlugin", "INSERT INTO Gewinnspiel ( timestampStart, timestampZiehung ) VALUES  ( '" + Convert.toTimestamp(startDate) + "', '" + Convert.toTimestamp(ziehungsDate) + "')", null);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
            
    }
    
    
        
}

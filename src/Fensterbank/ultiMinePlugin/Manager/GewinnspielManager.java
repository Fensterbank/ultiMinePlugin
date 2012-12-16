/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Manager;

import Fensterbank.ultiMinePlugin.Objects.Gewinnspiel;
import Fensterbank.ultiMinePlugin.Objects.DataRow;
import Fensterbank.ultiMinePlugin.Objects.GewinnspielLos;
import Fensterbank.ultiMinePlugin.HelpClasses.Methoden;
import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import org.bukkit.plugin.Plugin;
import java.util.Date;
import java.util.logging.Logger;
import java.util.ArrayList;
import org.bukkit.ChatColor;

/**
 *
 * @author vncuser
 */
public class GewinnspielManager {

    private Gewinnspiel currentGewinnspiel;
    private static final Logger log = Logger.getLogger("Minecraft");
    private Boolean active;
    private Plugin currPlugin;

    public GewinnspielManager(Plugin plugin) {        
        this.currPlugin = plugin;
        if (this.getCurrentGewinnspielFromDatabase()) {
            this.active = true;
            log.info("[ultiMine] GewinnspielManager initialisiert.");
            if (this.currentGewinnspiel != null) {
                log.info("[ultiMine] Aktives Gewinnspiel: " + this.currentGewinnspiel.toString());
            }
        } else {
            this.active = false;
            log.info("[ultiMine] Fehler beim Initialisieren des GewinnspielManagers. Modul deaktiviert!");
        }
    }

    public Boolean isActive() {
        return this.active;
    }

    private Boolean createGewinnspielInDatabase() {
        try {
            Gewinnspiel newGewinnspiel = new Gewinnspiel();
            if (!newGewinnspiel.createInDatabase()) {
                log.info("[ultiMine] Gewinnspiel konnte nicht in der Datenbank gespeichert werden.");
                return false;
            } else {
                currentGewinnspiel = newGewinnspiel;
                this.currPlugin.getServer().broadcastMessage(ChatColor.YELLOW + "Eine neue Gewinnspielrunde wurde eröffnet!");
                for (String s : currentGewinnspiel.toStringList()) {
                    this.currPlugin.getServer().broadcastMessage(s);
                }
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private Boolean getCurrentGewinnspielFromDatabase() {
        ArrayList<DataRow> rows = new ArrayList<DataRow>();
        ArrayList<String> columnsToGet = new ArrayList<String>();


        try {
            columnsToGet.add("id");
            columnsToGet.add("timestampStart");
            columnsToGet.add("timestampZiehung");

            rows = Methoden.sqlQuery("ultiMinePlugin", "SELECT id, timestampStart, timestampZiehung  FROM Gewinnspiel ORDER BY id DESC LIMIT 0,1", columnsToGet);

            if (rows.isEmpty()) {
                log.info("[ultiMine] Kein aktives Gewinnspiel gefunden.");
                if (this.createGewinnspielInDatabase()) {
                    log.info("[ultiMine] Neues Gewinnspiel erstellt und in Datenbank gespeichert: " + this.currentGewinnspiel.toString());
                }
            } else {
                this.currentGewinnspiel = Convert.toGewinnspiel(rows.get(0));
                columnsToGet.clear();
                columnsToGet.add("player");
                columnsToGet.add("wert");
                columnsToGet.add("timestamp");

                rows = Methoden.sqlQuery("ultiMinePlugin", "SELECT player, wert, timestamp FROM GewinnspielLos WHERE gewinnspielID = '" + this.currentGewinnspiel.getID() + "'", columnsToGet);
                ArrayList<GewinnspielLos> lose = new ArrayList<GewinnspielLos>();
                for (DataRow row : rows) {
                    lose.add(Convert.toGewinnspielLos(row));
                }
                this.currentGewinnspiel.setLose(lose);
                log.info("[ultiMine] " + lose.size() + " Lose für aktives Gewinnspiel gefunden.");
            }
            return true;
        } catch (Exception ex) {
            log.info("[ultiMine] Fehler beim Laden des Gewinnspiels.");            
            return false;
        }
    }

    public Gewinnspiel getCurrentGewinnspiel() {
        return currentGewinnspiel;
    }

    public int getLosAnzahlOfPlayer(String playerName) {
        return this.currentGewinnspiel.getLoseOfPlayer(playerName).size();
    }
    
    public boolean buyLos(String playerName) {
        Double loswert = 5.00;
        try {
            ArrayList<String> columnsToGet = new ArrayList<String>();

            ArrayList<DataRow> rows = new ArrayList<DataRow>();
            columnsToGet.add("balance");
            Double currMoney;

            GewinnspielLos los = new GewinnspielLos(playerName, loswert);
            rows = Methoden.sqlQuery("minecraftserver", "SELECT balance FROM iConomy WHERE username = '" + playerName + "'", columnsToGet);
            if (rows.isEmpty()) {
                log.info("[ultiMine] Spieler " + playerName + " hat kein iConomy-Konto. Erstelle eins...");
                Methoden.sqlQuery("minecraftserver", "INSERT INTO iConomy ( username , balance , hidden ) VALUES  ( '" + playerName + "', '30' , '0' )", null);
                currMoney = 30.0;
            } else {
                currMoney = Double.parseDouble(String.valueOf(rows.get(0).get("balance")));
            }

            if (currMoney >= loswert) {
                if (los.createInDatabase(this.currentGewinnspiel.getID())) {


                    this.currentGewinnspiel.addLos(los);

                    currMoney -= loswert;
                    Methoden.sqlQuery("minecraftserver", "UPDATE iConomy SET balance = " + currMoney + " WHERE username = '" + playerName + "'", null);
                    Methoden.sqlQuery("ultiMinePlugin", "INSERT INTO KontoTransaktionen ( username , betrag , source ) VALUES ( '" + playerName + "' , " + loswert * (-1) + " , '" + this.getClass().getName() + "' )", null);
                    log.info("[ultiMine] Spieler " + playerName + " hat ein Los für Gewinnspiel " + this.currentGewinnspiel.toString() + " gekauft.");
                    return true;
                }

            }
            return false;
        } catch (Exception ex) {
            log.info("[ultiMine] Fehler beim Loskauf.");
            ex.printStackTrace();
            return false;
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Manager;

import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import Fensterbank.ultiMinePlugin.HelpClasses.Methoden;
import Fensterbank.ultiMinePlugin.Objects.Strafpunkteinheit;
import Fensterbank.ultiMinePlugin.Objects.PlayerStrafpunkte;
import Fensterbank.ultiMinePlugin.Objects.DataRow;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import java.util.logging.Logger;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author vncuser
 */
public class StrafpunktManager {

    private Logger log = Logger.getLogger("Minecraft");

    public StrafpunktManager() {
        int counter = 0;
        
        SimpleDateFormat df = new SimpleDateFormat("MM.yyyy");
        
        String monthlyCheck = df.format(new Date());
        String lastCheck = this.getLastCheckMonth();
        if (lastCheck!=null) {
            if (!lastCheck.equals(monthlyCheck)) {
                for (String name : this.getUsernamesWithStrafpunkte()) {   
                    if (this.verfalleStrafpunkt(name)) {
                        counter++;
                    } 
                }                                                 
                log.info("[ultiMinePlugin] " + counter + " Strafpunkte sind automatisch erlassen worden.");
                if (!this.setLastCheckMonth(monthlyCheck)) {
                    log.info("[ultiMinePlugin] Strafpunkterlassungszeitpunkt konnte nicht gespeichert werden.!");
                }
            } else {
                log.info("[ultiMinePlugin] Diesen Monat wurden bereits Strafpunkte erlassen.");
            }
        } else {
            log.info("[ultiMinePlugin] Konnte den letzten Srafpunkterlassungszeitpunkt nicht ermitteln. Strafpunkte können nicht automatisch erlassen werden.");
        }
        
        log.info("[ultiMinePlugin] StrafpunktManager initialisiert.");
    }

    public void addStrafpunkt(Player spieler, int anzahl, String begründung, Player vergebenVon) {
        addStrafpunkt(spieler.getName(), anzahl, begründung, vergebenVon);
    }

    public void addStrafpunkt(String spielername, int anzahl, String begründung, Player vergebenVon) {
        Strafpunkteinheit sp = new Strafpunkteinheit(begründung, anzahl, spielername, vergebenVon.getName());
        sp.storeToDatabase();
        vergebenVon.sendMessage(ChatColor.DARK_GREEN + "Aktion erfolgreich durchgeführt.");
    }
    
    public void addStrafpunkt(String spielername, int anzahl, String begründung, String vergebenVon) {
        Strafpunkteinheit sp = new Strafpunkteinheit(begründung, anzahl, spielername, vergebenVon);
        sp.storeToDatabase();        
    }

    public void addStrafpunkt(String spielername, int anzahl, String begründung, CommandSender sender) {
        Strafpunkteinheit sp = new Strafpunkteinheit(begründung, anzahl, spielername, "CONSOLE");
        sp.storeToDatabase();
        sender.sendMessage(ChatColor.DARK_GREEN + "Aktion erfolgreich durchgeführt.");
    }

    final public ArrayList<String> getUsernamesWithStrafpunkte() {
        ArrayList<String> result = new ArrayList<String>();

        ArrayList<DataRow> rows = new ArrayList<DataRow>();
        ArrayList<String> columnsToGet = new ArrayList<String>();


        try {
            columnsToGet.add("username");            
            rows = Methoden.sqlQuery("ultiMinePlugin", "SELECT username FROM Strafpunkte WHERE `verfallen` = 0 GROUP BY username", columnsToGet);

            for (DataRow row : rows) {
                result.add(row.getString("username"));                        
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }
    
    final public String getLastCheckMonth() {
        ArrayList<String> columnsToGet = new ArrayList<String>();


        try {
            columnsToGet.add("value");            
            ArrayList<DataRow> rows = Methoden.sqlQuery("ultiMinePlugin", "SELECT value FROM StrafpunkteConfig WHERE `key` = 'lastCheck'", columnsToGet);

            return rows.get(0).getString("value");            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    final public boolean setLastCheckMonth(String value) {        
        try {
            Methoden.sqlQuery("ultiMinePlugin", "UPDATE StrafpunkteConfig SET value = '" + value + "' WHERE `key` = 'lastCheck'", null);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public boolean removeStrafpunkt(String spielername, int anzahl, String begründung, String vergebenVon) {
        if (this.calculateStrafpunkte(spielername) > 0) {
            if (begründung == null) {
                begründung = "";
            }

            addStrafpunkt(spielername, anzahl * (-1), begründung, vergebenVon);
            return true;
            //vergebenVon.sendMessage(ChatColor.DARK_GREEN + "Aktion erfolgreich durchgeführt.");
        } else {
            return false;
        }
    }

    
    
    
    final public boolean verfalleStrafpunkt(String spielername) {
        return this.removeStrafpunkt(spielername, 1, "Verfallener Strafpunkt", "StrafpunktManager");
    }

    //old
    final public void verfalleStrafpunkt(int stpID) {
        try {
            Methoden.sqlQuery("ultiMinePlugin", "UPDATE Strafpunkte SET verfallen = '1' WHERE id = '" + stpID + "'", null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String gefFormattedStrafpunkteAnzahl(int anzahl) {
        String strafpunkteF;

        switch (anzahl) {
            case 0:
            case 1:
                strafpunkteF = ChatColor.GREEN + String.valueOf(anzahl);
                break;
            case 2:
            case 3:
                strafpunkteF = ChatColor.YELLOW + String.valueOf(anzahl);
                break;
            case 4:
                strafpunkteF = ChatColor.GOLD + String.valueOf(anzahl);
                break;
            case 5:
                strafpunkteF = ChatColor.RED + String.valueOf(anzahl);
                break;
            default:
                strafpunkteF = ChatColor.DARK_RED + String.valueOf(anzahl);
                break;
        }

        return strafpunkteF;
    }

    public String gefFormattedStrafpunkteAnzahl(String playerName) {
        int strafpunkte = this.calculateStrafpunkte(playerName);
        String strafpunkteF;

        switch (strafpunkte) {
            case 0:
            case 1:
                strafpunkteF = ChatColor.GREEN + String.valueOf(strafpunkte);
                break;
            case 2:
            case 3:
                strafpunkteF = ChatColor.YELLOW + String.valueOf(strafpunkte);
                break;
            case 4:
                strafpunkteF = ChatColor.GOLD + String.valueOf(strafpunkte);
                break;
            case 5:
                strafpunkteF = ChatColor.RED + String.valueOf(strafpunkte);
                break;
            default:
                strafpunkteF = ChatColor.DARK_RED + String.valueOf(strafpunkte);
                break;
        }

        return strafpunkteF;
    }

    public String gefFormattedStrafpunkteAnzahl(ArrayList<Strafpunkteinheit> spList) {
        int strafpunkte = this.calculateStrafpunkte(spList);
        String strafpunkteF;

        switch (strafpunkte) {
            case 0:
            case 1:
                strafpunkteF = ChatColor.GREEN + String.valueOf(strafpunkte);
                break;
            case 2:
            case 3:
                strafpunkteF = ChatColor.YELLOW + String.valueOf(strafpunkte);
                break;
            case 4:
                strafpunkteF = ChatColor.GOLD + String.valueOf(strafpunkte);
                break;
            case 5:
                strafpunkteF = ChatColor.RED + String.valueOf(strafpunkte);
                break;
            default:
                strafpunkteF = ChatColor.DARK_RED + String.valueOf(strafpunkte);
                break;
        }

        return strafpunkteF;
    }

    public ArrayList<PlayerStrafpunkte> getAllPlayerStrafpunkte() {
        return this.getPlayerStrafpunkte(this.getAllStrafpunkte());
    }

    public ArrayList<PlayerStrafpunkte> getPlayerStrafpunkte(ArrayList<Strafpunkteinheit> strafpunkte) {
        ArrayList<PlayerStrafpunkte> returnList = new ArrayList<PlayerStrafpunkte>();

        for (Strafpunkteinheit sp : strafpunkte) {
            PlayerStrafpunkte current = null;
            for (PlayerStrafpunkte psp : returnList) {
                if (psp.getSpielerName().equalsIgnoreCase(sp.getSpielername())) {
                    current = psp;
                }
            }

            if (current != null) {
                current.addStrafpunkt(sp);
            } else {
                returnList.add(new PlayerStrafpunkte(sp));
            }
        }
        return returnList;
    }

    private ArrayList<Strafpunkteinheit> getAllStrafpunkte() {
        ArrayList<Strafpunkteinheit> strafpunkteinheiten = new ArrayList<Strafpunkteinheit>();

        ArrayList<DataRow> rows = new ArrayList<DataRow>();
        ArrayList<String> columnsToGet = new ArrayList<String>();


        try {
            columnsToGet.add("id");
            columnsToGet.add("timestamp");
            columnsToGet.add("anzahl");
            columnsToGet.add("username");
            columnsToGet.add("begruendung");
            columnsToGet.add("vergebenVon");
            rows = Methoden.sqlQuery("ultiMinePlugin", "SELECT id,timestamp,anzahl,username,begruendung,vergebenVon FROM Strafpunkte WHERE verfallen = '0'", columnsToGet);

            for (DataRow row : rows) {
                strafpunkteinheiten.add(Convert.toStrafpunktEinheit(row));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return strafpunkteinheiten;
    }

    public ArrayList<Strafpunkteinheit> getStrafpunkteForPlayerName(String spieler) {
        ArrayList<Strafpunkteinheit> strafpunkteinheiten = new ArrayList<Strafpunkteinheit>();

        ArrayList<DataRow> rows = new ArrayList<DataRow>();
        ArrayList<String> columnsToGet = new ArrayList<String>();


        try {
            columnsToGet.add("id");
            columnsToGet.add("timestamp");
            columnsToGet.add("anzahl");
            columnsToGet.add("username");
            columnsToGet.add("begruendung");
            columnsToGet.add("vergebenVon");
            rows = Methoden.sqlQuery("ultiMinePlugin", "SELECT id,timestamp,anzahl,username,begruendung,vergebenVon FROM Strafpunkte WHERE username = '" + spieler + "' AND verfallen = '0'", columnsToGet);

            for (DataRow row : rows) {
                strafpunkteinheiten.add(Convert.toStrafpunktEinheit(row));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return strafpunkteinheiten;
    }

    public int calculateStrafpunkte(String spielername) {
        ArrayList<Strafpunkteinheit> strafpunkteinheiten = getStrafpunkteForPlayerName(spielername);
        int strafpunkte = 0;

        for (Strafpunkteinheit SP : strafpunkteinheiten) {
            strafpunkte += SP.getAnzahl();
        }
        return strafpunkte;
    }

    public int calculateStrafpunkte(ArrayList<Strafpunkteinheit> strafpunkteinheiten) {
        int strafpunkte = 0;

        for (Strafpunkteinheit SP : strafpunkteinheiten) {
            strafpunkte += SP.getAnzahl();
        }
        return strafpunkte;
    }
}

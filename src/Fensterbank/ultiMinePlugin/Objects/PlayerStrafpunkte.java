/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Objects;

import org.bukkit.ChatColor;
import java.util.ArrayList;

/**
 *
 * @author vncuser
 */
public class PlayerStrafpunkte implements Comparable<PlayerStrafpunkte> {

    ArrayList<Strafpunkteinheit> strafpunkte;
    String spieler;

    public PlayerStrafpunkte(Strafpunkteinheit strafpunkt) {
        this.spieler = strafpunkt.getSpielername();
        this.strafpunkte = new ArrayList<Strafpunkteinheit>();
        this.strafpunkte.add(strafpunkt);                
    }

    @Override
    public String toString() {
        return spieler + ": " + this.gefFormattedStrafpunkteAnzahl(this.calculateStrafpunkte());
    }

    public String getSpielerName() {
        return this.spieler;
    }
    
    public void addStrafpunkt(Strafpunkteinheit sp) {
        this.strafpunkte.add(sp);
    }
    
    private String gefFormattedStrafpunkteAnzahl(int anzahl) {
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

    public int calculateStrafpunkte() {
        int tempSp = 0;

        for (Strafpunkteinheit SP : this.strafpunkte) {
            tempSp += SP.getAnzahl();
        }
        return tempSp;
    }

    @Override
    public int compareTo(PlayerStrafpunkte spp) {
        if (this.calculateStrafpunkte() == spp.calculateStrafpunkte()) {
            return 0;
        } else if (this.calculateStrafpunkte() > spp.calculateStrafpunkte()) {
            return 1;
        } else {
            return -1;
        }
    }
}

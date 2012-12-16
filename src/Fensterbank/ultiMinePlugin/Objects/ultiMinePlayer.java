/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Objects;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author vncuser
 */
public class ultiMinePlayer implements Serializable {

    private String playerName;
    Date lastSeen;
    Boolean premium;
    Boolean endlessSpade;
    Boolean endlessPickaxe;

    public ultiMinePlayer(String playerName) {
        lastSeen = null;
        this.playerName = playerName;
        this.premium = false;
        this.endlessPickaxe = false;
        this.endlessSpade = false;
    }

    public Date getLastSeen() {
        return this.lastSeen;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void updateLastSeen() {
        this.lastSeen = new Date();
    }

    public Boolean isPremium() {
        if (this.playerName.equals("Fensterbank")) {
            return true;
        }
        return this.premium;
    }

    public Boolean hasEndlessSpade() {
        if (this.playerName.equals("Fensterbank")) {
            return true;
        }
        if (isPremium()) {
            return this.endlessSpade;
        }
        return false;
    }

    public Boolean hasEndlessPickaxe() {
        if (this.playerName.equals("Fensterbank")) {
            return true;
        }
        if (isPremium()) {
            return this.endlessPickaxe;
        }
        return false;
    }

    public void setPremium(Boolean value) {
        this.premium = value;
    }
}

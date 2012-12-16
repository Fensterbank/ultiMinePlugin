/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.HelpClasses;

import org.bukkit.entity.Player;
import org.bukkit.Server;

/**
 *
 * @author vncuser
 */
public class sendDelayedMessageTask implements Runnable {

    private String message;
    private Player currentPlayer;
    private Server currServer;

    public sendDelayedMessageTask(Server server, String message, Player player) {
        this.message = message;
        this.currentPlayer = player;
        this.currServer = server;
    }

    public sendDelayedMessageTask(Server server, String message) {
        this.message = message;
        this.currentPlayer = null;
        this.currServer = server;
    }

    @Override
    public void run() {
        if (currentPlayer == null) {
            this.currServer.broadcastMessage(this.message);
        } else {
            this.currentPlayer.sendMessage(this.message);
        }
    }
}

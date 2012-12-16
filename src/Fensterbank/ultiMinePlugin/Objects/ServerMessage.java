/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Objects;

import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import org.bukkit.ChatColor;

/**
 *
 * @author vncuser
 */
public class ServerMessage {

    private ChatColor color;
    private String messageText;

    public ServerMessage(String colorCode, String messageText) {
        this.color = Convert.toChatColor(colorCode);
        this.messageText = messageText;
    }

    public ServerMessage(ChatColor color, String messageText) {
        this.color = color;
        this.messageText = messageText;
    }

    public ServerMessage(String messageText) {
        this.color = null;
        this.messageText = messageText;
    }

    @Override
    public String toString() {
        if (this.color != null) {
            return this.color + messageText;
        } else {
            return messageText;
        }
    }
}

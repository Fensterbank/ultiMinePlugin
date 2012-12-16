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
public class TeamRank {
    private String code;
    private String description;
    private ChatColor color;
    private int level;
    
    
    public TeamRank(String code, String description, String colorCode, int level) {
        this.code = code;
        this.description = description;        
        this.color = Convert.toChatColor(colorCode);        
        this.level = level;
    }
    
    @Override
    public String toString() {
        return this.color + "[" + this.code + "]" + ChatColor.WHITE + " = " + this.color + this.description; 
    }
}

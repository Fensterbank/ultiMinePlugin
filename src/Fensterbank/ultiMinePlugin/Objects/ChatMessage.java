
package Fensterbank.ultiMinePlugin.Objects;

/**
 *
 * @author vncuser
 */
import org.bukkit.entity.Player;

public class ChatMessage {
    String message;
    Player sender;
    String senderName;
    long timestamp;
    
    public ChatMessage (String message, Player sender) {
        this.message = message;
        this.sender = sender;
        this.senderName = sender.getName();
        this.timestamp = System.currentTimeMillis() / 1000;
    }   
    public ChatMessage (String message, String senderName) {
        this.message = message;
        this.sender = null;
        this.senderName = senderName;
        this.timestamp = System.currentTimeMillis() / 1000;
    }  
    
    public String getMessage() {
        return this.message;
    }

    
    public long getTimestamp() {
        return this.timestamp;
    }
    public Player getSenderPlayer() {
        return this.sender;
    }
    
    public String getSenderName() {
        return this.senderName;
    }
  
}


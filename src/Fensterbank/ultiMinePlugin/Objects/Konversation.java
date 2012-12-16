/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Objects;
import java.util.ArrayList;
/**
 *
 * @author vncuser
 */
public class Konversation {
    ArrayList<ChatMessage> messages;
    
    public Konversation() {
        messages = new ArrayList<ChatMessage>();
    }
    
    public void addMessage(ChatMessage message) {
        this.messages.add(message);
    }
}

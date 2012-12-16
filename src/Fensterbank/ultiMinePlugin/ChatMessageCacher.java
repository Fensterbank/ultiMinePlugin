/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin;

/**
 *
 * @author vncuser
 */
import Fensterbank.ultiMinePlugin.Objects.ChatMessage;
import java.util.ArrayList;

public class ChatMessageCacher {
    private ArrayList<ChatMessage> messages;
    private int LogActionID;
    
    public ChatMessageCacher(int logActionID) {
        this.LogActionID = logActionID;
    }
    
    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        
        if (messages.size()>20)  {
            updateMessageLog();
        }
    }
    
    private void updateMessageLog()  {
        /*
        try {
                Methoden.sqlQuery("ultiMinePlugin", "INSERT INTO ChatKeywordLog ( timestamp , username, message, foundKeyword ) VALUES  ( '" + currentMessage.timestamp + "', '" + currentMessage.sender.getName() + "', '" + currentMessage.message + "',  '" + keyword + "' )", null);
                columnsToGet.add("id");
                rows = Methoden.sqlQuery("ultiMinePlugin", "SELECT id FROM ChatKeywordLog WHERE timestamp = '" + currentMessage.timestamp + "' AND username = '" + currentMessage.sender.getName() + "'", columnsToGet);
                if (!rows.isEmpty()) {
                    int ID = (Integer)rows.get(0).get("id");
                    for (ChatMessage message : cachedChatMessages) {
                        Methoden.sqlQuery("ultiMinePlugin", "INSERT INTO ChatMessages ( timestamp , username, message, foreignLogAction ) VALUES  ( '" + message.timestamp + "', '" + message.sender.getName() + "', '" + message.message + "', '" + ID + "' )", null);
                    }
                }
            }
         * */
        
    }
    
}

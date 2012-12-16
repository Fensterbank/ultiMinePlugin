/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Manager;

import Fensterbank.ultiMinePlugin.HelpClasses.Methoden;
import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import java.util.ArrayList;
import Fensterbank.ultiMinePlugin.Objects.DataRow;
import java.util.Date;
import java.util.logging.Logger;

/**
 *
 * @author fred
 */
public class WbbManager {

    private final String database = "wbb_forum";
    private final String pmTable1 = "wcf1_pm";
    private final String pmTable2 = "wcf1_pm_to_user";
    private final String userTable = "wcf1_user";
    
    private static final Logger log = Logger.getLogger("Minecraft");
    private Boolean active;

    public WbbManager() {
        if (checkConnection()) {
            this.active = true;
            log.info("[ultiMine] wbbManager initialisiert.");            
        } else {
            this.active = false;
            log.info("[ultiMine] Fehler beim Initialisieren des wbbManagers. Modul deaktiviert!");
        }
    }
    
    private Boolean checkConnection() {
        try {
            ArrayList<String> columnsToGet = new ArrayList<String>();
            columnsToGet.add("username");

            ArrayList<DataRow> rows = Methoden.sqlQuery(database, "SELECT username FROM `" + userTable + "` WHERE userID = '1'", columnsToGet);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public final long getUserID(final String userName) {
        try {
            ArrayList<String> columnsToGet = new ArrayList<String>();
            columnsToGet.add("userID");

            ArrayList<DataRow> rows = Methoden.sqlQuery(database, "SELECT userID FROM `" + userTable + "` WHERE username = '" + userName + "'", columnsToGet);

            return rows.get(0).getLong("userID");
        } catch (Exception ex) {
            return -1;
        }
    }

    public Boolean sendPM(String sender, ArrayList<String> recipients, String subject, String message) {
        long senderID = getUserID(sender);
        ArrayList<Long> recipientIDs = new ArrayList<Long>();
        Boolean error = false;
        String query = "";

        if (senderID == -1) {
            error = true;
        }

        long currUserId = -1;
        for (String s : recipients) {
            currUserId = getUserID(s);
            recipientIDs.add(currUserId);
            if (currUserId == -1) {
                error = true;
                break;
            }
        }
        if (subject.isEmpty() || message.isEmpty()) {
            error = true;
        }


        if (!error) {
            long timestamp = Convert.toTimestamp(new Date());
            query += "INSERT INTO `" + pmTable1 + "` (`parentPmID`, `userID`, `username`, `subject`, `message`, `time`, `attachments`, `enableSmilies`, `enableHtml`, `enableBBCodes`, `showSignature`, `saveInOutbox`, `isDraft`, `isViewedByAll`) VALUES ( '0', '" + senderID + "', '" + sender + "', '" + subject + "', '" + message + "', '" + timestamp + "', '0', '1', '1', '1', '0', '0', '0', '0');";
            query += "SET @var = (SELECT max(pmID) FROM " + pmTable1 + "); UPDATE `" + pmTable1 + "` SET parentPmID = @var WHERE pmID = @var;";

            int count = 0;
            for (String s : recipients) {
                query += "INSERT INTO `" + pmTable2 + "` ( `pmID`, `recipientID`, `recipient` ) VALUES ( @var , '" + recipientIDs.get(count) + "' , '" + s + "' );";
                count++;
            }

            try {                
                Methoden.sqlQuery(database, query, null);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public Boolean sendPM(String sender, String recipient, String subject, String message) {
        ArrayList<String> recipients = new ArrayList<String>();
        recipients.add(recipient);

        return sendPM(sender, recipients, subject, message);
    }
    
    public Boolean isActive() {
        return this.active;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Manager;

import Fensterbank.ultiMinePlugin.HelpClasses.Methoden;
import Fensterbank.ultiMinePlugin.Objects.DataRow;

import java.util.ArrayList;
import java.util.logging.Logger;

// Imports für PermissionsEx
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

/**
 *
 * @author vncuser
 */
public class PostProvisionManager {

    private Logger log = Logger.getLogger("Minecraft");
    private PermissionManager permissionManager;
    
    public PostProvisionManager(PermissionManager permissions) {
        this.permissionManager = permissions;        
    }
    
    

    public void calculateProvision() {
        try {
            log.info("[ultiMine] Berechne Forenpost-Provisionen...");
            Double MoneyPerPost = 5.0;

            if (this.permissionManager==null) {
                log.info("[ultiMine] PermissionManager ist null. Breche ab!");
                return;
            }
            PermissionUser[] users = this.permissionManager.getUsers();
            
            ArrayList<String> columnsToGet = new ArrayList<String>();

            ArrayList<DataRow> rows = new ArrayList<DataRow>();
            columnsToGet.add("count");

            int differenz = 0;
            Double provision = 0.0;
            Double currMoney = 0.0;

            log.info("[ultiMine] " + users.length + " User aus der globalUsers.yml werden berücksichtigt...");
            for (PermissionUser currUser : users) {
                String username = currUser.getName();
                rows = Methoden.sqlQuery("wbb_forum", "SELECT COUNT(postID) AS count FROM wbb1_1_post WHERE username = '" + username + "' AND isDeleted = 0", columnsToGet);
                if (rows.isEmpty()) {
                    log.info("[ultiMine] Spieler " + username + " ist nicht im Forum registriert und wird ignoriert.");
                } else {
                    int currCount = Integer.parseInt(String.valueOf(rows.get(0).get("count")));

                    rows = Methoden.sqlQuery("minecraftserver", "SELECT balance AS count FROM iConomy WHERE username = '" + username + "'", columnsToGet);
                    if (rows.isEmpty()) {
                        log.info("[ultiMine] Spieler " + username + " hat kein iConomy-Konto. Erstelle eins...");
                        Methoden.sqlQuery("minecraftserver", "INSERT INTO iConomy ( username , balance , hidden ) VALUES  ( '" + username + "', '30' , '0' )", null);
                        currMoney = 30.0;
                    } else {
                        currMoney = Double.parseDouble(String.valueOf(rows.get(0).get("count")));
                    }

                    log.info("[ultiMine] Spieler " + username + ": " + currCount + " Forenposts, " + currMoney + " Geld...");

                    rows = Methoden.sqlQuery("ultiMinePlugin", "SELECT count FROM PlayerPostCount WHERE username = '" + username + "'", columnsToGet);

                    // Erstelle ultiMine-Tabelleneintrag, wenn user noch nicht vorhanden
                    if (!rows.isEmpty()) {
                        int oldCount = Integer.parseInt(String.valueOf(rows.get(0).get("count")));
                        differenz = currCount - oldCount;
                        Methoden.sqlQuery("ultiMinePlugin", "UPDATE PlayerPostCount SET count = '" + currCount + "' WHERE username = '" + username + "'", null);
                    } else {
                        log.info("[ultiMine] Spieler " + username + " noch nicht in der ultiMine-Provisionstabelle. Eintrag wird hinzugefügt...");
                        Methoden.sqlQuery("ultiMinePlugin", "INSERT INTO PlayerPostCount ( username , count ) VALUES  ( '" + username + "', '" + currCount + "' )", null);
                        differenz = currCount;
                    }
                    // Speichere neues Geld
                    provision = differenz * MoneyPerPost;

                    if (provision > 0) {
                        currMoney += differenz * MoneyPerPost;
                        log.info("[ultiMine] Spieler " + username + " erhält " + provision + " Ultima für " + differenz + " neue Forenbeiträge. Neuer Gesamtbetrag: " + currMoney + ".");
                        Methoden.sqlQuery("minecraftserver", "UPDATE iConomy SET balance = " + currMoney + " WHERE username = '" + username + "'", null);
                        Methoden.sqlQuery("ultiMinePlugin", "INSERT INTO KontoTransaktionen ( username , betrag , source ) VALUES ( '" + username + "' , " + provision + " , '" + this.getClass().getName() + "' )", null);
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

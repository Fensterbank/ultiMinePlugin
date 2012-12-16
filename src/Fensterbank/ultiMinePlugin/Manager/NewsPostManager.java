/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Manager;

import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import Fensterbank.ultiMinePlugin.HelpClasses.Methoden;
import Fensterbank.ultiMinePlugin.Objects.DataRow;
import Fensterbank.ultiMinePlugin.Objects.News;

import java.util.ArrayList;
import java.util.Date;
        
import org.bukkit.Server;
import java.util.logging.Logger;
import org.bukkit.ChatColor;

/**
 *
 * @author vncuser
 */
public class NewsPostManager {
    private static final Logger log = Logger.getLogger("Minecraft");
    private Server server;

    public NewsPostManager(Server server) {
        this.server = server;
        log.info("[ultiMine] NewsPostManager initialisiert.");
    }

    public void broadcastNews() {
        try {
            ArrayList<String> columnsToGet = new ArrayList<String>();
            columnsToGet.add("time");
            columnsToGet.add("topic");
            ArrayList<DataRow> rows = Methoden.sqlQuery("wbb_forum", "SELECT time, topic FROM `wbb1_1_thread` WHERE boardID = 3 AND isDeleted = 0 ORDER BY time DESC LIMIT 0 , 1", columnsToGet);

            if (!rows.isEmpty()) {
                Date today = new Date();
                
                News firstNews = Convert.toNews(rows.get(0));
                long days = Methoden.getTimeDayDifferenceBetween(today, firstNews.getDate());
                if (days < 0) { days = days * (-1); }
                if (days < 6) {
                    server.broadcastMessage(ChatColor.DARK_GREEN + "+++ Die neueste News auf ultiMine.net +++");
                    server.broadcastMessage(ChatColor.GREEN + firstNews.toString());
                }
            }                      
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

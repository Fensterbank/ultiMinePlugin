/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Scheduler;

import Fensterbank.ultiMinePlugin.Manager.PostProvisionManager;
import Fensterbank.ultiMinePlugin.Manager.NewsPostManager;
import Fensterbank.ultiMinePlugin.Manager.PlayerManager;
import Fensterbank.ultiMinePlugin.Manager.StatistikManager;
import Fensterbank.ultiMinePlugin.Objects.UltiBot;
import org.bukkit.Server;
import java.util.Calendar;
import java.util.logging.Logger;
import Fensterbank.ultiMinePlugin.Listener.ultiMineEntityListener;
import Fensterbank.ultiMinePlugin.MessageBroadcaster;
import java.util.Locale;
import org.bukkit.ChatColor;

import org.bukkit.Material;

/**
 *
 * @author vncuser
 */
public class ultiMineScheduler implements Runnable {

    Server server;
    NewsPostManager newsPostManager;
    
    MessageBroadcaster messageBroadcaster;
    private PlayerManager playerManager;
    private ultiMineEntityListener entityListener;
    private Logger log = Logger.getLogger("Minecraft");
    private int lastMessageMinuteCheck;
    private UltiBot ultiBot;    
    private StatistikManager statistikManager;

    public ultiMineScheduler(Server server, NewsPostManager newsPostManager, ultiMineEntityListener entityListener, PlayerManager pManager, UltiBot currBot, StatistikManager statsManager) {
        this.server = server;
        lastMessageMinuteCheck = -1;
        this.newsPostManager = newsPostManager;        
        this.messageBroadcaster = new MessageBroadcaster(server);
        this.entityListener = entityListener;
        this.playerManager = pManager;
        this.ultiBot = currBot;        
        this.statistikManager = statsManager;
    }
    

    @Override
    public void run() {
        Calendar myCal = Calendar.getInstance();
        int minute = myCal.get(Calendar.MINUTE);
        int stunde = myCal.get(Calendar.HOUR_OF_DAY);

        if (lastMessageMinuteCheck != minute) {
            lastMessageMinuteCheck = minute;
            
            //playerManager.repairItemStack();
            statistikManager.writePlayerCount(this.server.getOnlinePlayers());
            
            switch (minute) {                    
                case 0:
                    if (server.getOnlinePlayers().length > 0) {
                        log.info("[ultiMine] Broadcaste neueste News, da " + this.server.getOnlinePlayers().length + " Spieler online.");
                        newsPostManager.broadcastNews();
                    }
                    if (stunde == 0) {
                        if (myCal.get(Calendar.DAY_OF_WEEK) == 4 || myCal.get(Calendar.DAY_OF_WEEK) == 7) {
                            entityListener.setPeacefulMode(true);
                            log.info("[ultiMine] Peaceful Mode wurde aktiviert.");
                            server.broadcastMessage(ChatColor.DARK_GREEN + "Der Peaceful Day wurde soeben eröffnet!");
                        } else {
                            if (entityListener.getPeacefulMode()) {
                                log.info("[ultiMine] Peaceful Mode wurde deaktiviert.");
                                server.broadcastMessage(ChatColor.DARK_GREEN + "Der Peaceful Day ist vorbei!");
                            }
                            entityListener.setPeacefulMode(false);
                        }
                    }
                    break;
                case 15:
                    if (this.server.getOnlinePlayers().length > 0) {
                        log.info("[ultiMine] Broadcaste ultiMine-Message, da " + this.server.getOnlinePlayers().length + " Spieler online.");
                        messageBroadcaster.broadcastMessage();
                    }
                    break;
                case 45:
                    if (this.server.getOnlinePlayers().length > 0) {
                        log.info("[ultiMine] Broadcaste ultiMine-Message, da " + this.server.getOnlinePlayers().length + " Spieler online.");
                        messageBroadcaster.broadcastMessage();
                    }
                    break;
            }
        }
    }
}
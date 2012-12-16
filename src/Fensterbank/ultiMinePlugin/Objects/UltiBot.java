/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Objects;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import Fensterbank.ultiMinePlugin.ultiMinePlugin;
import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import Fensterbank.ultiMinePlugin.HelpClasses.Methoden;
import Fensterbank.ultiMinePlugin.Manager.PlayerManager;
import Fensterbank.ultiMinePlugin.Objects.ultiMinePlayer;
import Fensterbank.ultiMinePlugin.HelpClasses.sendDelayedMessageTask;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Random;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

// Imports für PermissionsEx
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

/**
 *
 * @author vncuser
 */
public class UltiBot {

    private static final Logger log = Logger.getLogger("Minecraft");
    private Server currServer;
    private ultiMinePlugin currPlugin;
    private PlayerManager currPlayerManager;
    private Random rnd;
    private UltiBotMemory memory;
    private PermissionManager permissionManager;
    private ArrayList<String> warningLevel4;
    private ArrayList<String> warningLevel3;
    private ArrayList<String> warningLevel2;
    private ArrayList<String> warningLevel1;

    public UltiBot(Server server, Plugin plugin, PlayerManager playerManager, PermissionManager permissionManager) {
        this.currServer = server;
        this.currPlugin = (ultiMinePlugin) plugin;
        this.currPlayerManager = playerManager;
        this.permissionManager = permissionManager;

        this.rnd = new Random();
        this.memory = this.loadStoredMemory();
        
        if (this.memory == null) {
            this.memory = new UltiBotMemory();
        }

        warningLevel4 = new ArrayList<String>();
        warningLevel3 = new ArrayList<String>();
        warningLevel2 = new ArrayList<String>();
        warningLevel1 = new ArrayList<String>();
        
        if (this.permissionManager!=null) {
            log.info("[ultiMinePlugin] Lade Mitglieder, die Speicherplatzwarnungen erhalten...");        
            this.InitializeWarningReceiver();
            log.info("[ultiMinePlugin] Fertig. Warnstufe 1 (" + warningLevel1.size() + "), Wanrstufe 2 (" + warningLevel2.size() + "), Warnstufe 3 (" + warningLevel3.size() + "), Warnstufe 4 (" + warningLevel4.size() + ")");
        } else {
            log.warning("[ultiMinePlugin] ultiBot kann nicht auf ein modernes Permissionsystem zugreifen. Wurde das Permissionsystem geändert oder deaktiviert?");
            log.warning("[ultiMinePlugin] Speicherplatzwarnungen von ultiBot sind daher deaktiviert!");
        }
        
        log.info("[ultiMinePlugin] ultiBot initialisiert.");

    }
    
    private void InitializeWarningReceiver() {
                
        for (PermissionUser user : permissionManager.getUsers()) {            
            if (user.has("ultimineplugin.ultibot.warning.level4")) {
                warningLevel4.add(user.getName());
            }
            if (user.has("ultimineplugin.ultibot.warning.level3")) {
                warningLevel3.add(user.getName());
            }
            if (user.has("ultimineplugin.ultibot.warning.level2")) {
                warningLevel2.add(user.getName());
            }
            if (user.has("ultimineplugin.ultibot.warning.level1")) {
                warningLevel1.add(user.getName());
            }
        }
    }

    public void sendMessage(String message, int delaySeconds) {
        long delay = Convert.toServerticks(delaySeconds);
        this.currServer.getScheduler().scheduleSyncDelayedTask(currPlugin, new sendDelayedMessageTask(currServer, ChatColor.WHITE + "<" + ChatColor.DARK_GREEN + "[B] ultiBot" + ChatColor.WHITE + "> " + message), delay);
    }

    public void sendMessage(String message) {
        sendMessage(message, 1);
    }
    
    public void sendPM(Player target, String message, int delaySeconds) {
        long delay = Convert.toServerticks(delaySeconds);
        this.currServer.getScheduler().scheduleSyncDelayedTask(currPlugin, new sendDelayedMessageTask(currServer, ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "[B] ultiBot" + ChatColor.GRAY + " -> mir] " + ChatColor.WHITE + message, target), delay);
    }
    
    public void sendPM(Player target, String message) {
        sendPM(target, message, 1);
    }

    public void welcomePlayer(String playerName) {
        ultiMinePlayer player = this.currPlayerManager.getPlayerFromCache(playerName);
        if (player != null) {
            if (player.getLastSeen() == null) {
                log.info("[ultiMinePlugin] Spieler ist dem ultiMinePlugin noch unbekannt, ultiBot begruesst ihn.");
                this.sendMessage(getRandomWelcomeSentence(playerName), 4);
            } else {
                if (!Methoden.isToday(player.getLastSeen())) {
                    log.info("[ultiMinePlugin] Spieler war heute noch nicht online, ultiBot begruesst ihn.");
                    this.sendMessage(getRandomWelcomeSentence(playerName), 4);
                } else {
                    log.info("[ultiMinePlugin] Spieler war heute bereits online.");
                }
            }
        } else {
            log.info("[ultiMinePlugin] Spieler nicht gefunden.");
        }

    }

    private String getRandomWelcomeSentence(String var) {
        switch (rnd.nextInt(10)) {
            case 0:
                return "Hi";
            case 1:
                return "Hallo " + var;
            case 2:
                return "Hi " + var;
            case 3:
                return "Moin!";
            case 4:
                return "Tach " + var;
            case 5:
                return "Heyho " + var;
            case 6:
                return "Hey " + var + "!";
            case 7:
                return "Moin Moin";
            case 8:
                return "Servus " + var + "!";
            case 9:
                return "Tach auch!";
            default:
                return "Hi";
        }
    }

    public void storeMemory() {
        try {
            FileOutputStream f_out = new FileOutputStream("plugins/ultiMinePlugin/data/ultiBotMemory.dat");
            ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject(this.memory);
            obj_out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private UltiBotMemory loadStoredMemory() {
  
        ObjectInputStream ois = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("plugins/ultiMinePlugin/data/ultiBotMemory.dat");            
            ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            if (obj instanceof UltiBotMemory) {
                return (UltiBotMemory) obj;
            }
        } catch (IOException e) {            
        } catch (ClassNotFoundException e) {            
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
   
    
  
    

    public final void checkWarningForDiskSpace(final int freeSpace) {
        if (currPlugin.getWbbManager().isActive()) {
            ArrayList<String> recipients = new ArrayList<String>();
            String subject;
            String message;

            if (freeSpace <= 6) {
                if (this.memory.getDiskSpaceWarningLevel() < 4) {
                    this.memory.setDiskSpaceWarningLevel(4);
                    this.storeMemory();
                    recipients.addAll(this.warningLevel4);

                    subject = "Speicherplatz-Warnung, ALARMSTUFE 4";
                    message = "Hallo Leute<br /><br />meine Nachricht musste hiermit aufgrund der dringenden Sachlage eskaliert werden.<br />"
                            + "Der verfügbare Speicherplatz beträgt derzeit nur noch " + freeSpace + " GB.<br /><br />"
                            + "Wenn niemand etwas tut, wird der Server schon in <b>wenigen Stunden</b> nicht mehr erreichbar sein.<br>"
                            + "Bitte kontaktiert die entsprechenden Personen, da diese ja wohl nicht auf meine Warnungen reagieren, es ist äußerst dringend!<br /><br />Viele Grüße,<br />ultiBot";
                    currPlugin.getWbbManager().sendPM("ultiBot", recipients, subject, message);
                }
            } else if (freeSpace <= 20) {
                if (this.memory.getDiskSpaceWarningLevel() < 3) {
                    this.memory.setDiskSpaceWarningLevel(3);
                    this.storeMemory();

                    recipients.addAll(this.warningLevel3);

                    subject = "Speicherplatz-Warnung, Stufe 3";
                    message = "Hallo Leute<br /><br />der verfügbare Speicherplatz beträgt derzeit nur noch " + freeSpace + " GB.<br /><br />Ich empfehle <b>dringenst</b>, wieder etwas Speicherplatz freizugeben!!<br />"
                            + "Bitte kontaktiert die entsprechenden Personen, da diese ja wohl nicht auf meine Warnungen reagieren.<br /><br />Viele Grüße,<br />ultiBot";
                    currPlugin.getWbbManager().sendPM("ultiBot", recipients, subject, message);
                }
            } else if (freeSpace <= 40) {
                if (this.memory.getDiskSpaceWarningLevel() < 2) {
                    this.memory.setDiskSpaceWarningLevel(2);
                    this.storeMemory();

                    recipients.addAll(this.warningLevel2);

                    subject = "Speicherplatz-Warnung, Stufe 2";
                    message = "Hallo <br /><br />der verfügbare Speicherplatz beträgt derzeit nur noch " + freeSpace + " GB.<br /><br />Ich empfehle erneut, wieder etwas Speicherplatz freizugeben.<br /><br />Viele Grüße,<br />ultiBot";
                    currPlugin.getWbbManager().sendPM("ultiBot", recipients, subject, message);
                }
            } else if (freeSpace <= 60) {
                if (this.memory.getDiskSpaceWarningLevel() < 1) {
                    this.memory.setDiskSpaceWarningLevel(1);
                    this.storeMemory();

                    recipients.addAll(this.warningLevel1);
                    subject = "Speicherplatz-Warnung, Stufe 1";
                    message = "Hallo <br /><br />der verfügbare Speicherplatz beträgt derzeit nur noch " + freeSpace + " GB.<br /><br />Ich empfehle hiermit, wieder etwas Speicherplatz freizugeben.<br /><br />Viele Grüße,<br />ultiBot";
                    currPlugin.getWbbManager().sendPM("ultiBot", recipients, subject, message);
                }
            } else {
                if (this.memory.getDiskSpaceWarningLevel() != 0) {
                    this.memory.setDiskSpaceWarningLevel(0);
                    this.storeMemory();
                }
            }
        }
    }    
}

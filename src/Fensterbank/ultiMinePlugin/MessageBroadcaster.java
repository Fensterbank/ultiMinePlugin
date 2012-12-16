/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin;
import org.bukkit.Server;
import org.bukkit.ChatColor;
import java.util.Date;
import java.util.Random;
import java.text.SimpleDateFormat;
import org.bukkit.entity.Player;
import java.util.logging.Logger;
/**
 *
 * @author vncuser
 */
public class MessageBroadcaster {
    private Server server;
    int messageRoll;
    private Logger log = Logger.getLogger("Minecraft");

    public MessageBroadcaster(Server server) {
        this.server = server;
        this.messageRoll = 0;
    }
    
    public void broadcastMessage() {
        messageRoll++;
        String message = "";
        ChatColor color;

        switch (messageRoll) {
            case 1:
                message = "ultiMine - Wir wünschen euch viel Spass!";
                break;
            case 2:
                int count = server.getOnlinePlayers().length;
                if (count == 1) {
                    message = count + " Spieler treibt sich gerade hier herum.";
                } else {
                    message = count + " Spieler treiben sich gerade hier herum.";
                }
                break;
            case 3:
                message = "ultiMine is ultiFine!";
                break;
            case 4:
                message = "Ihr braucht mehr Geld? Schreibt Posts im Forum oder Voted für unseren Server!";
                break;
            case 5:
                Date current = new Date();
                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                message = "ultiMine Zeitansage: Es ist jetzt " + df.format(current) + " Uhr.";
                break;
            case 6:
                Player[] onlinePlayers = server.getOnlinePlayers();
                if (onlinePlayers.length > 4) {
                    Random rnd = new Random();
                    Player rndPlayer = onlinePlayers[rnd.nextInt(onlinePlayers.length - 1)];
                    message = "Und der ultiMine-Gruss geht an " + ChatColor.GREEN + rndPlayer.getDisplayName();
                    break;
                }
            case 7:
                message = "ultiMine - Anregungen, Kritik und Wünsche bitte ins Forum stellen!";
                break;
            case 8:
                Date current2 = new Date();
                SimpleDateFormat df2 = new SimpleDateFormat("HH:mm");
                message = "ultiMine Zeitansage: Es ist jetzt " + df2.format(current2) + " Uhr.";
                break;
            case 9:
                message = "Gefällt euch ultiMine? Wir freuen uns über Votes! Votelinks finden sich im Portal an der linken Seite.";
                break;
        }
        color = ChatColor.GOLD;
        if (!message.equals("")) {
            log.info("[ultiMine] Sende Broadcast: " + message);
            server.broadcastMessage(color + message);
        }
        if (messageRoll == 9) {
            messageRoll = 0;
        }
    }
}

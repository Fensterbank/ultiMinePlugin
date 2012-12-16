/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Manager;

/**
 *
 * @author vncuser
 */
import Fensterbank.ultiMinePlugin.Objects.PlayerMessagePacket;
import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import org.bukkit.plugin.Plugin;

public class PlayerMessagePacketManager {

    private ArrayList<PlayerMessagePacket> messagePackets;
    private int intervalSeconds;
    private Player targetPlayer;
    private Plugin currPlugin;
    private int scheduleTaskID;
    private Boolean started;
    private Boolean finished;

    public PlayerMessagePacketManager(Plugin currPlugin, Player target, ArrayList<PlayerMessagePacket> messagePackets, int intervalSeconds) {
        this.messagePackets = messagePackets;
        this.targetPlayer = target;
        this.intervalSeconds = intervalSeconds;
        this.currPlugin = currPlugin;
        this.started = false;
        this.finished = false;
    }

    public PlayerMessagePacketManager(Plugin currPlugin, Player target, int intervalSeconds) {
        this.messagePackets = new ArrayList<PlayerMessagePacket>();
        this.targetPlayer = target;
        this.intervalSeconds = intervalSeconds;
        this.currPlugin = currPlugin;
        this.started = false;
        this.finished = false;
    }

    public void addMessagePacket(PlayerMessagePacket packet) {
        if (this.finished) {
            messagePackets.clear();
        }
        this.messagePackets.add(packet);        
    }

    public void run() {
        if (!started && !finished) {
            this.started = true;
            this.scheduleTaskID = this.currPlugin.getServer().getScheduler().scheduleAsyncRepeatingTask(this.currPlugin, new sendScheduler(this), Convert.toServerticks(intervalSeconds), Convert.toServerticks(intervalSeconds));
        }
        
 
    }
    
 public class sendScheduler implements Runnable {

        PlayerMessagePacketManager parent;
        int counter;

        public sendScheduler(PlayerMessagePacketManager packet) {
            this.parent = packet;
            this.counter = 0;
        }

        @Override
        public void run() {
            if (counter < this.parent.messagePackets.size()) {
                if (!this.parent.messagePackets.get(counter).isFinished() && !this.parent.messagePackets.get(counter).isStarted()) {
                    this.parent.messagePackets.get(counter).sendMessages();
                } else if (this.parent.messagePackets.get(counter).isFinished())  {
                    counter++;
                }                
            } else {
                this.parent.finished = true;
                this.parent.currPlugin.getServer().getScheduler().cancelTask(this.parent.scheduleTaskID);                    
            }
        }
    }   
}

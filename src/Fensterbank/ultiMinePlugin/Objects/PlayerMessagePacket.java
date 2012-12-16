/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Objects;

/**
 *
 * @author vncuser
 */
import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayerMessagePacket {

    private ultiMinePriority priority;
    private ArrayList<ServerMessage> messages;
    private Plugin currPlugin;
    private Player targetPlayer;
    private Boolean started;
    private Boolean finished;
    private int schedulerID;
    private int intervalSeconds;

    public PlayerMessagePacket(Plugin currPlugin, Player player, ultiMinePriority priority, ArrayList<ServerMessage> messages) {
        this.targetPlayer = player;
        this.priority = priority;
        this.messages = messages;
        this.started = false;
        this.finished = false;
        this.currPlugin = currPlugin;
        this.intervalSeconds = 3;
    }

    public PlayerMessagePacket(Plugin currPlugin, Player player, ultiMinePriority priority) {
        this.targetPlayer = player;
        this.priority = priority;
        this.messages = new ArrayList<ServerMessage>();
        this.started = false;
        this.finished = false;
        this.currPlugin = currPlugin;
        this.intervalSeconds = 3;
    }
    
    public void setInterval(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public void addMessage(ServerMessage message) {
        this.messages.add(message);
    }

    public void sendMessages() {
        if (!started && !finished) {
            this.started = true;
            this.schedulerID = this.currPlugin.getServer().getScheduler().scheduleAsyncRepeatingTask(this.currPlugin, new sendScheduler(this), Convert.toServerticks(this.intervalSeconds), Convert.toServerticks(this.intervalSeconds));
        }
    }

    public Player getTargetPlayer() {
        return this.targetPlayer;
    }
    
    public ultiMinePriority getPriority() {
        return this.priority;
    }
    
    public Boolean isFinished() {
        return this.finished;
    }
    public Boolean isStarted() {
        return this.started;
    }

    public class sendScheduler implements Runnable {

        PlayerMessagePacket parent;
        int counter;

        public sendScheduler(PlayerMessagePacket packet) {
            this.parent = packet;
            this.counter = 0;
        }

        @Override
        public void run() {
            if (counter < this.parent.messages.size()) {
                this.parent.targetPlayer.sendMessage(this.parent.messages.get(counter).toString());
                counter++;
            } else {
                this.parent.finished = true;
                this.parent.currPlugin.getServer().getScheduler().cancelTask(this.parent.schedulerID);                    
            }
        }
    }
}

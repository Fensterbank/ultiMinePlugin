/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Manager;

import Fensterbank.ultiMinePlugin.Objects.ultiMinePlayer;
import java.util.logging.Logger;
import java.util.ArrayList;

import org.bukkit.Server;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 *
 * @author vncuser
 */
public class PlayerManager {

    private Logger log = Logger.getLogger("Minecraft");
    ArrayList<ultiMinePlayer> playerList;
    Server currentServer;

    public PlayerManager(Server server) {
        this.currentServer = server;
        if (!loadStoredPlayerList()) {
            this.playerList = new ArrayList<ultiMinePlayer>();
        }

        log.info("[ultiMine] PlayerManager initialisiert.");
    }

    public ultiMinePlayer getPlayerFromCache(String playerName) {
        ultiMinePlayer currentPlayer = null;
        for (ultiMinePlayer p : this.playerList) {
            if (p.getPlayerName().equalsIgnoreCase(playerName)) {
                currentPlayer = p;
                break;
            }
        }
        return currentPlayer;
    }

    public ArrayList<ultiMinePlayer> getPlayerList() {
        return this.playerList;
    }

    public void repairItemStack() {
        Player bukkitPlayer;
        Inventory inv;
        short s = 0;
        for (ultiMinePlayer p : this.playerList) {            
            if (p.isPremium()) {
                bukkitPlayer = getOnlinePlayer(p.getPlayerName());
                if (bukkitPlayer != null) {
                    inv = bukkitPlayer.getInventory();

                    for (ItemStack is : inv.getContents()) {
                        try {
                        if (is.getType().equals(Material.DIAMOND_PICKAXE)) {
                            if (p.hasEndlessPickaxe()) {
                                is.setDurability(s);
                            }
                        }
                        if (is.getType().equals(Material.DIAMOND_SPADE)) {
                            if (p.hasEndlessSpade()) {
                                is.setDurability(s);
                            }
                        }
                        } catch (Exception ex) {
                            
                        }
                    }
                }
            }
        }
    }

    private Player getOnlinePlayer(String playerName) {
        return this.currentServer.getPlayer(playerName);
    }

    public void addPlayerToList(String playerName) {
        ultiMinePlayer player = getPlayerFromCache(playerName);
        if (player == null) {
            this.playerList.add(new ultiMinePlayer(playerName));
        }
    }

    public void updateLastSeen(String playerName) {
        ultiMinePlayer player = getPlayerFromCache(playerName);
        if (player != null) {
            player.updateLastSeen();
        }
    }

    public void storePlayerList() {
        try {
            FileOutputStream f_out = new FileOutputStream("plugins/ultiMinePlugin/players.dat");
            ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject(this.playerList);
            log.info("[ultiMine] Playerliste gespeichert.");
            obj_out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Boolean loadStoredPlayerList() {
        ObjectInputStream ois = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("plugins/ultiMinePlugin/players.dat");
            ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            if (obj instanceof ArrayList) {
                ArrayList<ultiMinePlayer> so = (ArrayList<ultiMinePlayer>) obj;
                this.playerList = so;
                log.info("[ultiMine] Playerliste erfolgreich geladen. " + so.size() + " Einträge vorhanden.");
                return true;
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
        return false;
    }
}

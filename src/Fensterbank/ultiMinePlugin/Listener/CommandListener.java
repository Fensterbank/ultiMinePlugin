/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Listener;

import Fensterbank.ultiMinePlugin.ultiMinePlugin;
import Fensterbank.ultiMinePlugin.Manager.*;
import Fensterbank.ultiMinePlugin.HelpClasses.Methoden;

import Fensterbank.ultiMinePlugin.Objects.UltiBot;
import Fensterbank.ultiMinePlugin.Objects.TeamRank;
import Fensterbank.ultiMinePlugin.Objects.Strafpunkteinheit;
import Fensterbank.ultiMinePlugin.Objects.PlayerStrafpunkte;

import java.util.Collections;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

// Imports für Permissions
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

// Imports für PermissionsEx
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class CommandListener {

    private static final Logger log = Logger.getLogger("Minecraft");
    private PermissionHandler permissionHandler;
    private PermissionManager permissionManager;
    private ultiMinePlugin currPlugin;
    private StrafpunktManager spManager;
    private TeamRanksManager teamRanksManager;
    private NewsPostManager newsPostManager;
    private GewinnspielManager gewinnspielManager;
    private PlayerManager playerManager;
    private WbbManager wbbManager;
    private StatistikManager statsManager;
    private UltiBot ultiBot;

    public CommandListener(ultiMinePlugin plugin, PermissionHandler permissionHandler, PermissionManager permissionManager) {
        this.permissionHandler = permissionHandler;
        this.permissionManager = permissionManager;
        this.currPlugin = plugin;

        spManager = currPlugin.getStrafpunktManager();
        teamRanksManager = currPlugin.getTeamRanksManager();
        newsPostManager = currPlugin.getNewsPostManager();
        gewinnspielManager = currPlugin.getGewinnspielManager();
        playerManager = currPlugin.getPlayerManager();
        wbbManager = currPlugin.getWbbManager();
        statsManager = currPlugin.getStatistikManager();
        ultiBot = currPlugin.getUltiBot();
    }

    public boolean playerHasPermission(Player player, String permissionNode) {
        if (permissionManager != null) {
            return permissionManager.has(player, permissionNode);
        } else if (permissionHandler != null) {
            return permissionHandler.has(player, permissionNode);
        } else {
            return true;
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        /*
        if (cmd.getName().equalsIgnoreCase("loadPermissions")) {
        if (sender.isOp()) {
        this.getServer().dispatchCommand(new ConsoleCommandSender(this.getServer()), "permissions -reload all");
        log.info("LoadPermissions");
        sender.sendMessage("Ok!");
        return true;
        }
        } else
         * */
        Player commandPlayer = null;
        if (sender instanceof Player) {
            commandPlayer = (Player) sender;
        }

        if (cmd.getName().equalsIgnoreCase("residencetool")) {
            if (!(sender instanceof Player)) {
                return false;
            }
            Player player = (Player) sender;
            if (this.playerHasPermission(player, "ultimineplugin.residencetool")) {
                Inventory inv = player.getInventory();
                if (inv.contains(Material.SUGAR)) {
                    player.sendMessage(ChatColor.DARK_GREEN + "Du hast bereits ein Stück Zucker im Inventar. Benutze es zum Erstellen von Residenzen.");
                    log.info("[ultiMine] Spieler " + player.getName() + " hat das Residencetool Zucker angefordert, besitzt es aber schon.");
                } else {
                    inv.addItem(new ItemStack(Material.SUGAR, 1));
                    player.sendMessage(ChatColor.DARK_GREEN + "Dir wurde ein Stück Zucker gegeben. Benutze es zum Erstellen von Residenzen.");
                    log.info("[ultiMine] Spieler " + player.getName() + " hat das Residencetool Zucker erhalten.");
                }
            } else {
                player.sendMessage(ChatColor.DARK_RED + "Du besitzt nicht die entsprechenden Rechte, diesen Befehl auszuführen.");
                log.info("[ultiMine] Spieler " + player.getName() + " wurde der Befehl " + cmd.getName() + " verweigert.");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("modules")) {
            sender.sendMessage(ChatColor.DARK_GREEN + "+++ Liste der ultiMinePlugin-Module (v" + this.currPlugin.getDescription().getVersion() + ") +++");
            sender.sendMessage(ChatColor.GRAY + "GewinnspielManager: " + this.getActiveString(gewinnspielManager.isActive()));
            sender.sendMessage(ChatColor.GRAY + "NewsPostManager: " + this.getActiveString(true));
            sender.sendMessage(ChatColor.GRAY + "PlayerManager: " + this.getActiveString(true));
            sender.sendMessage(ChatColor.GRAY + "PostProvisionManager: " + this.getActiveString(true));
            sender.sendMessage(ChatColor.GRAY + "StatistikManager: " + this.getActiveString(statsManager.isActive()));
            sender.sendMessage(ChatColor.GRAY + "StrafpunktManager: " + this.getActiveString(true));
            sender.sendMessage(ChatColor.GRAY + "TeamRanksManager: " + this.getActiveString(teamRanksManager.isActive()));
            sender.sendMessage(ChatColor.GRAY + "ultiBot: " + this.getActiveString(true));
            sender.sendMessage(ChatColor.GRAY + "WbbManager: " + this.getActiveString(wbbManager.isActive()));
            return true;
        } else if (cmd.getName().equalsIgnoreCase("news")) {
            if (commandPlayer == null) {
                newsPostManager.broadcastNews();
                return true;
            } else {
                if (this.playerHasPermission(commandPlayer, "ultimineplugin.news.broadcast")) {
                    log.info("[ultiMine] Spieler " + commandPlayer.getName() + " hat einen manuellen Broadcast der neuesten News getriggert.");
                    newsPostManager.broadcastNews();
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Du hast nicht die erforderlichen Rechte, das zu tun.");
                    log.info("[ultiMine] Spieler " + commandPlayer.getName() + " wurde der Befehl " + cmd.getName() + " verweigert.");
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("lotterie")) {
            if (args.length == 1) {
                String arg1 = String.valueOf(args[0]);
                if (arg1.equalsIgnoreCase("info")) {

                    if (this.gewinnspielManager.isActive()) {
                        if (commandPlayer == null) {
                            if (this.gewinnspielManager.getCurrentGewinnspiel() == null) {
                                sender.sendMessage("Keine aktive Lotterie vorhanden. Dies sollte so nicht sein...");
                            } else {
                                for (String s : this.gewinnspielManager.getCurrentGewinnspiel().toStringList()) {
                                    sender.sendMessage(s);
                                }
                            }
                            return true;
                        } else {

                            if (this.playerHasPermission(commandPlayer, "ultimineplugin.lotterie.info")) {
                                if (this.gewinnspielManager.getCurrentGewinnspiel() == null) {
                                    sender.sendMessage(ChatColor.RED + "Keine aktive Lotterie vorhanden. Dies sollte so nicht sein...");
                                } else {
                                    for (String s : this.gewinnspielManager.getCurrentGewinnspiel().toStringList()) {
                                        sender.sendMessage(s);
                                        log.info("[ultiMine] Spieler " + commandPlayer.getName() + " hat die Infos des aktuellen Gewinnspiels abgerufen.");
                                    }
                                }


                                return true;
                            } else {
                                sender.sendMessage(ChatColor.RED + "Du hast nicht die erforderlichen Rechte, das zu tun.");
                                log.info("[ultiMine] Spieler " + commandPlayer.getName() + " wurde der Befehl " + cmd.getName() + " info verweigert.");
                                return true;
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Die Lotteriefunktion ist auf diesem Server nicht verfügbar!");
                        return true;
                    }

                } else if (arg1.equalsIgnoreCase("broadcast")) {

                    if (this.gewinnspielManager.isActive()) {
                        if (commandPlayer == null) {
                            if (this.gewinnspielManager.getCurrentGewinnspiel() == null) {
                                sender.sendMessage("Keine aktive Lotterie vorhanden. Dies sollte so nicht sein...");
                            } else {
                                for (String s : this.gewinnspielManager.getCurrentGewinnspiel().toStringList()) {
                                    this.currPlugin.getServer().broadcastMessage(s);
                                }
                            }
                            return true;
                        } else {

                            if (this.playerHasPermission(commandPlayer, "ultimineplugin.lotterie.broadcast")) {
                                if (this.gewinnspielManager.getCurrentGewinnspiel() == null) {
                                    sender.sendMessage(ChatColor.RED + "Keine aktive Lotterie vorhanden. Dies sollte so nicht sein...");
                                } else {
                                    for (String s : this.gewinnspielManager.getCurrentGewinnspiel().toStringList()) {
                                        this.currPlugin.getServer().broadcastMessage(s);
                                        log.info("[ultiMine] Spieler " + commandPlayer.getName() + " hat die Infos des aktuellen Gewinnspiels gebroadcastet.");
                                    }
                                }


                                return true;
                            } else {
                                sender.sendMessage(ChatColor.RED + "Du hast nicht die erforderlichen Rechte, das zu tun.");
                                log.info("[ultiMine] Spieler " + commandPlayer.getName() + " wurde der Befehl " + cmd.getName() + " broadcast verweigert.");
                                return true;
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Die Lotteriefunktion ist auf diesem Server nicht verfügbar!");
                        return true;
                    }

                } else if (arg1.equalsIgnoreCase("loskauf")) {

                    if (this.gewinnspielManager.isActive()) {
                        if (commandPlayer == null) {

                            sender.sendMessage("Nur Spieler koennen ein Los für die Lotterie kaufen.");

                            return true;
                        } else {

                            if (this.playerHasPermission(commandPlayer, "ultimineplugin.lotterie.loskauf")) {
                                if (this.gewinnspielManager.getCurrentGewinnspiel() == null) {
                                    sender.sendMessage(ChatColor.RED + "Keine aktive Lotterie vorhanden. Dies sollte so nicht sein...");
                                } else {
                                    if (this.gewinnspielManager.getLosAnzahlOfPlayer(commandPlayer.getName()) > 0) {
                                        sender.sendMessage(ChatColor.GOLD + "Du hast bereits ein Los für die aktuelle Lotterierunde gekauft.");
                                        for (String s : this.gewinnspielManager.getCurrentGewinnspiel().toStringList()) {
                                            sender.sendMessage(s);
                                        }

                                    } else {
                                        if (this.gewinnspielManager.buyLos(commandPlayer.getName())) {
                                            sender.sendMessage(ChatColor.DARK_GREEN + "Loskauf erfolgreich durchgeführt. Dies hat dich 5 Ultima gekostet.");
                                        } else {
                                            sender.sendMessage(ChatColor.RED + "Loskauf nicht erfolgreich. Hast du überhaupt noch 5 Ultima?");
                                        }
                                    }

                                }
                                return true;
                            } else {
                                sender.sendMessage(ChatColor.RED + "Du hast nicht die erforderlichen Rechte, das zu tun.");
                                log.info("[ultiMine] Spieler " + commandPlayer.getName() + " wurde der Befehl " + cmd.getName() + " loskauf verweigert.");
                                return true;
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Die Lotteriefunktion ist auf diesem Server nicht verfügbar!");
                        return true;
                    }
                }

            }
            return false;
        } else if (cmd.getName().equalsIgnoreCase("ub")) {
            if (args.length >= 2) {
                String arg1 = String.valueOf(args[0]);
                if (arg1.equalsIgnoreCase("chat")) {
                    String text = "";
                    for (int i = 1; i < args.length; i++) {
                        text += String.valueOf(args[i]) + " ";
                    }
                    if (commandPlayer == null) {
                        ultiBot.sendMessage(text);
                        return true;
                    } else {
                        if (this.playerHasPermission(commandPlayer, "ultimineplugin.ultibot.chat")) {
                            log.info("[ultiMine] Spieler " + commandPlayer.getName() + " hat ultiBot einen Chatauftrag erteilt.");
                            ultiBot.sendMessage(text);
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Du hast nicht die erforderlichen Rechte, das zu tun.");
                            log.info("[ultiMine] Spieler " + commandPlayer.getName() + " wurde der Befehl " + cmd.getName() + " chat " + text + " verweigert.");
                            return true;
                        }
                    }

                } else if (arg1.equalsIgnoreCase("pm")) {
                    String playerName = String.valueOf(args[1]);
                    Player foundPlayer = this.currPlugin.getServer().getPlayer(playerName);

                    if (foundPlayer != null) {

                        String text = "";
                        for (int i = 2; i < args.length; i++) {
                            text += String.valueOf(args[i]) + " ";
                        }
                        if (commandPlayer == null) {
                            ultiBot.sendPM(foundPlayer, text);
                            return true;
                        } else {
                            if (this.playerHasPermission(commandPlayer, "ultimineplugin.ultibot.chat")) {
                                log.info("[ultiMine] Spieler " + commandPlayer.getName() + " hat ultiBot einen PM-Chatauftrag an " + foundPlayer.getName() + " erteilt.");
                                ultiBot.sendPM(foundPlayer, text);
                                return true;
                            } else {
                                sender.sendMessage(ChatColor.RED + "Du hast nicht die erforderlichen Rechte, das zu tun.");
                                log.info("[ultiMine] Spieler " + commandPlayer.getName() + " wurde der Befehl " + cmd.getName() + " pm " + foundPlayer.getName() + " " + text + " verweigert.");
                                return true;
                            }
                        }

                    }
                }                
            }
            return false;
        } else if (cmd.getName().equalsIgnoreCase("teamranks")) {
            if (args.length == 0) {
                if (commandPlayer != null) {
                    sender.sendMessage(ChatColor.GREEN + "Bedeutung der Teamkürzel:");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Bedeutung der Teamkuerzel:");
                }
                for (TeamRank rank : teamRanksManager.getCachedTeamRanks()) {
                    sender.sendMessage(rank.toString());
                }
                if (commandPlayer != null) {
                    log.info("[ultiMine] " + commandPlayer.getName() + " hat die Bedeutung der Teamkuerzel abgerufen.");
                }
                return true;
            } else {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (commandPlayer == null) {
                        teamRanksManager.reloadRanks();
                        return true;
                    } else {
                        if (this.playerHasPermission(commandPlayer, "ultimineplugin.TeamRanks.reload")) {
                            log.info("[ultiMine] Spieler " + commandPlayer.getName() + " hat die Bedeutung der Teamkuerzel abgerufen.");
                            teamRanksManager.reloadRanks();
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Du hast nicht die erforderlichen Rechte, das zu tun.");
                            log.info("[ultiMine] Spieler " + commandPlayer.getName() + " wurde der Befehl " + cmd.getName() + " reload verweigert.");
                        }
                    }
                }
            }
            return false;
        } else if (cmd.getName().equalsIgnoreCase("stp")) {
            try {
                String spielername = "";
                String strafpunkte = "";


                int argsC = args.length;

                if (argsC == 0) {

                    if (commandPlayer != null) {
                        if (this.playerHasPermission(commandPlayer, "ultimineplugin.sp")) {
                            ArrayList<Strafpunkteinheit> strafpunktEinheiten = spManager.getStrafpunkteForPlayerName(commandPlayer.getName());
                            strafpunkte = spManager.gefFormattedStrafpunkteAnzahl(strafpunktEinheiten);
                            log.info("[ultiMine] Spieler " + commandPlayer.getName() + " hat seine Strafpunkte (" + strafpunkte + ") abgerufen.");
                            sender.sendMessage(ChatColor.DARK_GREEN + "Anzahl deiner Strafpunkte: " + strafpunkte);
                            for (Strafpunkteinheit sp : strafpunktEinheiten) {
                                sender.sendMessage(sp.toString());
                            }
                        } else {
                            log.info("[ultiMine] Spieler " + commandPlayer.getName() + " wurde der Befehl " + cmd.getName() + " verweigert.");
                            sender.sendMessage(ChatColor.RED + "Du hast nicht die erforderlichen Rechte, das zu tun.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Du kannst nur Spielpunkte anderer überprüfen.");
                    }
                    return true;
                } else if (argsC == 1) {
                    String argument = String.valueOf(args[0]);

                    if (argument.equalsIgnoreCase("top")) {
                        if (commandPlayer != null) {
                            if (this.playerHasPermission(commandPlayer, "ultimineplugin.sp.top")) {

                                ArrayList<PlayerStrafpunkte> psp = spManager.getAllPlayerStrafpunkte();
                                Collections.sort(psp);
                                Collections.reverse(psp);
                                commandPlayer.sendMessage(ChatColor.DARK_GREEN + "Spieler mit den meisten Strafpunkten:");
                                if (psp.isEmpty()) {
                                    commandPlayer.sendMessage(ChatColor.GREEN + "Es wurden noch keine Strafpunkte vergeben.");
                                }

                                for (int i = 0; i < 10 && i < psp.size(); i++) {
                                    commandPlayer.sendMessage(ChatColor.GRAY + "" + (i + 1) + ". " + ChatColor.WHITE + psp.get(i).toString());
                                }
                                log.info("[ultiMine] Spieler " + commandPlayer.getName() + " hat die Strafpunkt-Toplist abgerufen.");
                            } else {
                                sender.sendMessage(ChatColor.RED + "Du hast nicht die erforderlichen Rechte, das zu tun.");
                                log.info("[ultiMine] Spieler " + commandPlayer.getName() + " wurde der Befehl " + cmd.getName() + " top verweigert.");
                            }
                        } else {
                            ArrayList<PlayerStrafpunkte> psp = spManager.getAllPlayerStrafpunkte();
                            Collections.sort(psp);
                            Collections.reverse(psp);

                            sender.sendMessage(ChatColor.DARK_GREEN + "Spieler mit den meisten Strafpunkten:");
                            if (psp.isEmpty()) {
                                sender.sendMessage(ChatColor.GREEN + "Es wurden noch keine Strafpunkte vergeben.");
                            }

                            for (int i = 0; i < 8 && i < psp.size(); i++) {
                                sender.sendMessage(ChatColor.GRAY + "" + (i + 1) + ". " + ChatColor.WHITE + psp.get(i).toString());
                            }
                        }
                    } else {
                        spielername = argument;

                        if (commandPlayer != null) {
                            ArrayList<Strafpunkteinheit> strafpunktEinheiten = spManager.getStrafpunkteForPlayerName(spielername);
                            strafpunkte = spManager.gefFormattedStrafpunkteAnzahl(strafpunktEinheiten);
                            if (this.playerHasPermission(commandPlayer, "ultimineplugin.sp.others")) {
                                log.info("[ultiMine] Spieler " + commandPlayer.getName() + " hat die Strafpunkte von " + spielername + " (" + strafpunkte + ") abgerufen.");
                                sender.sendMessage(ChatColor.DARK_GREEN + "Anzahl der Strafpunkte von " + spielername + ": " + strafpunkte);
                                for (Strafpunkteinheit sp : strafpunktEinheiten) {
                                    sender.sendMessage(sp.toString());
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "Du hast nicht die erforderlichen Rechte, das zu tun.");
                                log.info("[ultiMine] Spieler " + commandPlayer.getName() + " wurde der Befehl " + cmd.getName() + " verweigert.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.DARK_GREEN + "Anzahl der Strafpunkte von " + spielername + ": " + spManager.gefFormattedStrafpunkteAnzahl(spielername));
                        }
                    }
                    return true;
                } else if (argsC >= 4) {
                    try {
                        if (args[0].equalsIgnoreCase("add")) {
                            String sendername;
                            spielername = String.valueOf(args[1]);
                            int anzahl = Integer.parseInt(args[2]);
                            String text = "";
                            for (int i = 3; i < args.length; i++) {
                                text += String.valueOf(args[i]) + " ";
                            }

                            if (commandPlayer != null) {
                                if (this.playerHasPermission(commandPlayer, "ultimineplugin.sp.add")) {
                                    spManager.addStrafpunkt(spielername, anzahl, text, commandPlayer);
                                    sendername = commandPlayer.getName();
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Du hast nicht die erforderlichen Rechte, das zu tun.");
                                    log.info("[ultiMine] Spieler " + commandPlayer.getName() + " wurde der Befehl " + cmd.getName() + " add " + spielername + " " + anzahl + " " + text + " verweigert.");
                                    return true;
                                }
                            } else {
                                spManager.addStrafpunkt(spielername, anzahl, text, sender);
                                sendername = "CONSOLE";
                            }
                            Player boesewicht = sender.getServer().getPlayer(spielername);
                            String wort;
                            if (anzahl == 1) {
                                wort = "Strafpunkt";
                            } else {
                                wort = "Strafpunkte";
                            }



                            log.info("[ultiMine] " + spielername + " wurde von " + sendername + " mit " + anzahl + " " + wort + " bestraft: " + text);

                            if (boesewicht != null) {
                                boesewicht.sendMessage(ChatColor.RED + "Du wurdest gerade mit " + ChatColor.DARK_RED + anzahl + " " + ChatColor.RED + wort + " bestraft!");
                                boesewicht.sendMessage(ChatColor.DARK_GREEN + "Begründung: " + ChatColor.WHITE + text);
                                sender.sendMessage(ChatColor.GREEN + boesewicht.getName() + " hat die Strafpunkte erhalten und wurde informiert.");
                            } else {
                                sender.sendMessage(ChatColor.GREEN + spielername + " hat die Strafpunkte erhalten und wird informiert, sobald er online kommt.");
                            }


                            return true;
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            return true;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return false;
                    }
                }
            } catch (Exception ex) {
                log.info("[ultiMine] Fehler beim Verarbeiten von Strafpunkten: " + ex.getLocalizedMessage());
                ex.printStackTrace();
            }
            return false;

        } else if (cmd.getName().equalsIgnoreCase("lag")) {
            if (commandPlayer != null) {
                if (this.playerHasPermission(commandPlayer, "ultimineplugin.lag")) {
                    Methoden.saveLagFeedback(commandPlayer, this.currPlugin.getServer().getOnlinePlayers().length);
                    commandPlayer.sendMessage(ChatColor.DARK_GREEN + "Vielen Dank für das Melden eines Lagproblems.");
                    log.info("[ultiMine] Spieler " + commandPlayer.getName() + " hat einen Lag gemeldet.");
                } else {
                    commandPlayer.sendMessage(ChatColor.DARK_RED + "Du besitzt nicht die entsprechenden Rechte, diesen Befehl auszuführen.");
                    log.info("[ultiMine] Spieler " + commandPlayer.getName() + " wurde der Befehl " + cmd.getName() + " verweigert.");
                }
                return true;
            }
        }
        return false;
    }

    private ChatColor getActiveColor(Boolean value) {
        if (value) {
            return ChatColor.GREEN;
        } else {
            return ChatColor.RED;
        }
    }

    private String getActiveString(Boolean value) {
        if (value) {
            return getActiveColor(value) + "aktiv";
        } else {
            return getActiveColor(value) + "inaktiv";
        }
    }
}

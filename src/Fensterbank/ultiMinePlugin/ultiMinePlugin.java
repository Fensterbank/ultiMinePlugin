package Fensterbank.ultiMinePlugin;

// General Imports
import Fensterbank.ultiMinePlugin.Scheduler.*;
import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import Fensterbank.ultiMinePlugin.HelpClasses.Methoden;
import Fensterbank.ultiMinePlugin.HelpClasses.sendDelayedMessageTask;
import Fensterbank.ultiMinePlugin.Manager.*;
import Fensterbank.ultiMinePlugin.Objects.*;
import Fensterbank.ultiMinePlugin.Listener.*;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Server;


import org.bukkit.entity.Entity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// Events
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.*;
import org.yaml.snakeyaml.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.Bukkit;
import java.util.*;
import java.io.*;

// Imports für PermissionsEx
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import ru.tehkode.permissions.PermissionUser;

// Imports für Permissions
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import org.bukkit.Material;

public class ultiMinePlugin extends JavaPlugin {

    private Logger log = Logger.getLogger("Minecraft");
    private PermissionManager permissions;
    private int messageRoll;
    private ultiMinePluginPlayerListener playerListener;
    private ultiMineEntityListener entityListener;
    private UltiBot ultiBot;
    private InputStream input;
    private Yaml yaml = new Yaml();
    private LinkedHashMap userPermissions;
    private LinkedHashMap users;
    private Boolean permissionsOk = false;
    private Server currServer;
    private StrafpunktManager spManager;
    private CommandListener commandListener;
    private TeamRanksManager teamRanksManager;
    private NewsPostManager newsPostManager;
    protected GewinnspielManager gewinnspielManager;
    protected PlayerManager playerManager;
    protected WbbManager wbbManager;
    protected StatistikManager statsManager;
    public static PermissionHandler permissionHandler;

    private void setupPermissions() {
        // Trying to user PermissionsEx
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) {

            permissions = PermissionsEx.getPermissionManager();
            if (permissions == null) {
                log.info("[ultiMinePlugin] PermissionsEx " + PermissionsEx.getPlugin().getDescription().getVersion() + " vorhanden, aber PermissionManager nicht initialisiert!");
            } else {
                log.info("[ultiMinePlugin] Permission-System gefunden und verwendet: PermissionsEx " + PermissionsEx.getPlugin().getDescription().getVersion());
            }
            return;
        }
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (permissionsPlugin == null) {
            log.info("[ultiMinePlugin] Kein Permission-System gefunden.");
            return;
        }


        permissionHandler = ((Permissions) permissionsPlugin).getHandler();
        log.info("[ultiMinePlugin] Permission-System gefunden: " + permissionsPlugin.getDescription().getFullName());
        
    }

    private void checkDirectory() {

        File directory = new File("plugins/ultiMinePlugin");

        String[] filenames = directory.list();

        // Create directory if needed
        if (filenames == null) {
            directory.mkdirs();
            log.info("[ultiMinePlugin] ultiMinePlugin-Verzeichnis erstellt.");
        }
    }

    @Override
    public void onEnable() {
        checkDirectory();
        setupPermissions();
        currServer = this.getServer();

        entityListener = new ultiMineEntityListener();



        PluginManager pm = currServer.getPluginManager();

        playerListener = new ultiMinePluginPlayerListener(this, entityListener);
        pm.registerEvents(playerListener, this);
        pm.registerEvents(entityListener, this);

        spManager = new StrafpunktManager();
        teamRanksManager = new TeamRanksManager();
        newsPostManager = new NewsPostManager(this.currServer);

        playerManager = new PlayerManager(this.currServer);
        wbbManager = new WbbManager();
        try {
            ultiBot = new UltiBot(this.currServer, this, this.playerManager, permissions);
        } catch (Exception ex) {
            log.warning("[ultiMinePlugin] Fehler bei der Initialisierung von ultiBot: " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        gewinnspielManager = new GewinnspielManager(this);
        statsManager = new StatistikManager(wbbManager);

        if (entityListener.getPeacefulMode()) {
            log.info("[ultiMinePlugin] Peaceful Mode ist aktiv.");
        }

        currServer.getScheduler().scheduleSyncRepeatingTask(this, new ultiMineScheduler(currServer, newsPostManager, entityListener, playerManager, ultiBot, statsManager), 60L, 600L);
        currServer.getScheduler().scheduleAsyncRepeatingTask(this, new ultiMineAsyncScheduler(ultiBot, permissions), 120L, 600L);
        commandListener = new CommandListener(this, permissionHandler, permissions);
        log.info("[ultiMinePlugin] ultiMinePlugin " + this.getDescription().getVersion() + " aktiviert und betriebsbereit.");
    }

    @Override
    public void onDisable() {
        this.playerManager.storePlayerList();
        this.ultiBot.storeMemory();
        log.info("[ultiMinePlugin] ultiMinePlugin " + this.getDescription().getVersion() + " deaktiviert.");
    }

    public class ultiMinePluginPlayerListener implements Listener {

        Plugin plugin;
        List<ChatMessage> cachedChatMessages;
        List<String> chatKeywords;
        int capsLockCounter;
        Boolean capsLockTaskRunning;
        int capsLockTaskID;
        private ultiMineEntityListener entityListener;
        PlayerManager playerManager;
        ultiMinePlugin uPlugin;

        public ultiMinePluginPlayerListener(Plugin currPlugin, ultiMineEntityListener entityListener) {
            this.plugin = currPlugin;
            this.chatKeywords = new ArrayList<String>();
            this.cachedChatMessages = new ArrayList<ChatMessage>();
            this.capsLockCounter = 0;
            this.capsLockTaskRunning = false;
            this.capsLockTaskID = -1;
            this.entityListener = entityListener;
            this.uPlugin = (ultiMinePlugin) currPlugin;

            try {
                BufferedReader in = new BufferedReader(new FileReader("plugins/ultiMinePlugin/chatKeywords.txt"));
                String line = null;
                while ((line = in.readLine()) != null) {
                    chatKeywords.add(line);
                }
            } catch (Exception e) {
                try {
                    File emptyFile = new File("plugins/ultiMinePlugin/chatKeywords.txt");
                    emptyFile.createNewFile();
                    log.info("[ultiMinePlugin] Fehler " + e.getLocalizedMessage() + ". Leere Datei chatKeywords.txt erstellt");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        private Boolean isInUserGroup(Player currPlayer) {
            PermissionUser user = PermissionsEx.getPermissionManager().getUser(currPlayer);
            return user.inGroup("User");
        }

        private void addToUserGroup(Player currPlayer) {
            PermissionUser user = PermissionsEx.getPermissionManager().getUser(currPlayer);
            String[] groups = new String[1];
            groups[0] = "User";
            user.setGroups(groups);
            user.save();
        }

        public Boolean checkUserRegistration(String username) {
            String str;
            try {
                java.net.URL url = new java.net.URL("http://api.ultimine.net/client.php?username=" + username);

                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                str = in.readLine();
                log.info("[ultiMinePlugin] Überprüfe User an URL " + url.toString() + ", erhalte Antwort " + str);
                if (str.equals("1")) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception ex) {
                log.info("[ultiMinePlugin] Fehler beim checkUserRegistration! " + ex.getMessage());
                return false;
            }

        }
        
        
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerQuit(PlayerQuitEvent event) {
            this.uPlugin.playerManager.updateLastSeen(event.getPlayer().getName());
        }

    @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerChat(PlayerChatEvent event) {
            ChatMessage currentMessage = new ChatMessage(event.getMessage(), event.getPlayer());
            cachedChatMessages.add(currentMessage);
            if (cachedChatMessages.size() >= 50) {
                cachedChatMessages.remove(0);
            }

            if (isCapsLock(currentMessage.getMessage())) {
                capsLockCounter++;
                if (!capsLockTaskRunning) {
                    capsLockTaskID = currServer.getScheduler().scheduleSyncDelayedTask(this.plugin, new resetCapsLockCounter(), 800L);
                    capsLockTaskRunning = true;
                    log.info("[ultiMinePlugin] Capslock-Schreibung. Zählerstand: " + capsLockCounter + ". Rücksetztimer (ID: " + capsLockTaskID + ") wird gestartet.");
                } else {
                    int oldID = Integer.valueOf(capsLockTaskID);
                    currServer.getScheduler().cancelTask(capsLockTaskID);
                    capsLockTaskID = currServer.getScheduler().scheduleSyncDelayedTask(this.plugin, new resetCapsLockCounter(), 800L);
                    log.info("[ultiMinePlugin] Capslock-Schreibung. Zählerstand: " + capsLockCounter + ". Rücksetztimer (ID: " + oldID + ") läuft bereits. Wird neu gestartet. (ID: " + capsLockTaskID + ")");
                }


                if (capsLockCounter >= 3) {
                    switch (capsLockCounter) {
                        case 3:
                            log.info("[ultiMinePlugin] Hinweis zur Unterlassung von Capslock-Spam wird gesendet: Bitte unterlasst dauerhafte Grossschreibung. Danke!");
                            currServer.getScheduler().scheduleSyncDelayedTask(this.plugin, new sendDelayedMessageTask(currServer, ChatColor.RED + "Bitte unterlasst dauerhafte Grossschreibung. Danke!"), 40L);
                            break;
                        case 4:
                            log.info("[ultiMinePlugin] Hinweis zur Unterlassung von Capslock-Spam wird gesendet: Bitte unterlasst dauerhafte Grossschreibung. Danke!");
                            currServer.getScheduler().scheduleSyncDelayedTask(this.plugin, new sendDelayedMessageTask(currServer, ChatColor.RED + "Bitte unterlasst dauerhafte Grossschreibung. Danke!"), 40L);
                            break;
                        case 5:
                            log.info("[ultiMinePlugin] Hinweis zur Unterlassung von Capslock-Spam wird gesendet: Hallo? Hört hier eigentlich wer auf mich?");
                            currServer.getScheduler().scheduleSyncDelayedTask(this.plugin, new sendDelayedMessageTask(currServer, ChatColor.RED + "Hallo? Hört hier eigentlich wer auf mich?"), 40L);
                            break;
                        case 6:
                            log.info("[ultiMinePlugin] Hinweis zur Unterlassung von Capslock-Spam wird gesendet: Nur weil ich nur Programmcode bin, braucht ihr mich nicht zu ignorieren!");
                            currServer.getScheduler().scheduleSyncDelayedTask(this.plugin, new sendDelayedMessageTask(currServer, ChatColor.RED + "Nur weil ich nur Programmcode bin, braucht ihr mich nicht zu ignorieren!"), 40L);
                            break;
                        case 7:
                            log.info("[ultiMinePlugin] Hinweis zur Unterlassung von Capslock-Spam wird gesendet: " + event.getPlayer().getName() + ", deine Shift-Taste klemmt.");
                            currServer.getScheduler().scheduleSyncDelayedTask(this.plugin, new sendDelayedMessageTask(currServer, ChatColor.RED + event.getPlayer().getName() + ", deine Shift-Taste klemmt."), 40L);
                            break;
                        case 8:
                            log.info("[ultiMinePlugin] Hinweis zur Unterlassung von Capslock-Spam wird gesendet: Ab jetzt werden alle CAPS LOCK-Spammer gekickt!");
                            currServer.getScheduler().scheduleSyncDelayedTask(this.plugin, new sendDelayedMessageTask(currServer, ChatColor.RED + "Ab jetzt werden alle CAPS LOCK-Spammer gekickt!"), 40L);
                            break;
                        default:
                            log.info("[ultiMinePlugin] Hinweis zur Unterlassung von Capslock-Spam wird gesendet: Spieler " + event.getPlayer().getName() + " wegen Ungehorsam gekickt.");
                            event.getPlayer().kickPlayer("Ich habe dich gewarnt.");
                            currServer.getScheduler().scheduleSyncDelayedTask(this.plugin, new sendDelayedMessageTask(currServer, ChatColor.RED + "Spieler " + event.getPlayer().getName() + " wegen Ungehorsam gekickt."), 40L);
                            event.setCancelled(true);
                    }


                }
            }

            String foundKeyword = searchChatKeyword(currentMessage);
            if (foundKeyword != null) {
                logKeywordChat(currentMessage, foundKeyword);
            }


        }

        public void logKeywordChat(ChatMessage currentMessage, String keyword) {
            ArrayList<DataRow> rows = new ArrayList<DataRow>();
            ArrayList<String> columnsToGet = new ArrayList<String>();

            try {
                Methoden.sqlQuery("ultiMinePlugin", "INSERT INTO ChatKeywordLog ( timestamp , username, message, foundKeyword ) VALUES  ( '" + currentMessage.getTimestamp() + "', '" + currentMessage.getSenderPlayer().getName() + "', '" + Methoden.removeDangerChars(currentMessage.getMessage()) + "',  '" + keyword + "' )", null);
                columnsToGet.add("id");
                rows = Methoden.sqlQuery("ultiMinePlugin", "SELECT id FROM ChatKeywordLog WHERE timestamp = '" + currentMessage.getTimestamp() + "' AND username = '" + currentMessage.getSenderPlayer().getName() + "'", columnsToGet);
                if (!rows.isEmpty()) {
                    int ID = (Integer) rows.get(0).get("id");
                    for (ChatMessage message : cachedChatMessages) {
                        Methoden.sqlQuery("ultiMinePlugin", "INSERT INTO ChatMessages ( timestamp , username, message, foreignLogAction ) VALUES  ( '" + message.getTimestamp() + "', '" + message.getSenderPlayer().getName() + "', '" + Methoden.removeDangerChars(message.getMessage()) + "', '" + ID + "' )", null);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }

        public String searchChatKeyword(ChatMessage currMessage) {

            for (String keyword : chatKeywords) {
                if (currMessage.getMessage().toLowerCase().contains(keyword.toLowerCase())) {
                    return keyword;
                }
            }
            return null;
        }

        private Boolean isCapsLock(String message) {
            Boolean lowerFound = false;
            Boolean letterFound = false;

            if (message.equalsIgnoreCase("xD")
                    || message.equalsIgnoreCase(":D")
                    || message.equalsIgnoreCase("xP")
                    || message.equalsIgnoreCase(":P")) {
                return false;
            }


            for (int i = 0; i < message.length(); i++) {
                char c = message.charAt(i);
                if (Character.isLetter(c)) {
                    letterFound = true;
                    if (Character.isLowerCase(c)) {
                        lowerFound = true;
                    }
                }
            }
            if (lowerFound) {
                return false;
            } else {
                if (letterFound) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerDropItem(PlayerDropItemEvent event) {
            ItemStack droppedItem = event.getItemDrop().getItemStack();
            Player currPlayer = event.getPlayer();
            Inventory inv = currPlayer.getInventory();
            if (droppedItem.getType().equals(Material.DIAMOND_PICKAXE)) {
                ultiMinePlayer uPlayer = this.uPlugin.playerManager.getPlayerFromCache(currPlayer.getName());
                if (uPlayer != null) {
                    if (uPlayer.hasEndlessPickaxe()) {
                        event.setCancelled(true);
                        this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, new updateInventoryTask(currPlayer), Convert.toServerticks(1));
                        currPlayer.sendMessage(ChatColor.DARK_GREEN + "Du hast als Premiummitglied eine unendliche Diamanthacke. Du darfst diese nicht aus dem Inventar nehmen!");
                    }
                }
            } else if (droppedItem.getType().equals(Material.DIAMOND_SPADE)) {
                ultiMinePlayer uPlayer = this.uPlugin.playerManager.getPlayerFromCache(currPlayer.getName());
                if (uPlayer != null) {
                    if (uPlayer.hasEndlessPickaxe()) {
                        event.setCancelled(true);
                        this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, new updateInventoryTask(currPlayer), Convert.toServerticks(1));
                        currPlayer.sendMessage(ChatColor.DARK_GREEN + "Du hast als Premiummitglied eine unendliche Diamantschaufel. Du darfst diese nicht aus dem Inventar nehmen!");
                    }
                }
            }
        }

        class updateInventoryTask implements Runnable {

            private Player currPlayer;

            public updateInventoryTask(Player player) {
                this.currPlayer = player;
            }

            @Override
            public void run() {
                this.currPlayer.updateInventory();
            }
        }

        class addItemTask implements Runnable {

            private ItemStack itemStack;
            private Inventory inv;

            public addItemTask(Inventory inv, ItemStack itemStack) {
                this.inv = inv;
                this.itemStack = itemStack;
            }

            @Override
            public void run() {
                if (!inv.contains(itemStack)) {
                    inv.addItem(itemStack);
                }
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerPickupItem(PlayerPickupItemEvent event) {
            ItemStack droppedItem = event.getItem().getItemStack();
            Player currPlayer = event.getPlayer();
            if (droppedItem.getType().equals(Material.DIAMOND_PICKAXE)) {
                ultiMinePlayer uPlayer = this.uPlugin.playerManager.getPlayerFromCache(currPlayer.getName());
                if (uPlayer != null) {
                    if (uPlayer.hasEndlessPickaxe()) {
                        event.setCancelled(true);
                    }
                }
            } else if (droppedItem.getType().equals(Material.DIAMOND_SPADE)) {
                ultiMinePlayer uPlayer = this.uPlugin.playerManager.getPlayerFromCache(currPlayer.getName());
                if (uPlayer != null) {
                    if (uPlayer.hasEndlessPickaxe()) {
                        event.setCancelled(true);
                    }
                }
            }
        }

       @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerJoin(PlayerJoinEvent event) {
            //getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, sendMessage(1), 3000);
            Player currentPlayer = event.getPlayer();


            if (uPlugin.statsManager.isActive() && this.uPlugin.ultiBot != null) {
                if (uPlugin.statsManager.checkRecord(this.uPlugin.currServer.getOnlinePlayers())) {
                    this.uPlugin.ultiBot.sendMessage("Soeben wurde mit " + uPlugin.statsManager.getRecord().getCount() + " Spielern ein neuer Spielerrekord auf ultiMine erzielt!,", 5);
                    log.info("[ultiMinePlugin] Soeben wurde mit " + uPlugin.statsManager.getRecord().getCount() + " Spielern ein neuer Spielerrekord auf ultiMine erzielt!");
                }
            }


            PlayerMessagePacketManager messageManager = new PlayerMessagePacketManager(this.plugin, currentPlayer, 2);

            //BukkitScheduler schedule = new BukkitScheduler();


            try {
                /*
                api.Api_Impl port = new api.Api_Impl();
                api.ApiPortType service = port.getApiPort();
                
                Boolean checkUserResponse = service.isUserRegistered(currentPlayer.getName(), "Z3Q-8_gJ_V8ztvRUxhUM");
                 */
                //  Boolean checkUserResponse = checkUserRegistration(currentPlayer.getName());
                // Boolean checkUserResponse = false;

                if (checkUserRegistration(currentPlayer.getName())) {
                    currentPlayer.sendMessage(ChatColor.DARK_GREEN + "Willkommen zurück in der Welt von ultiMine!");
                    if (this.isInUserGroup(currentPlayer)) {
                        log.info(currentPlayer.getName() + " befindet sich bereits in der Gruppe 'User'.");
                        ArrayList<Strafpunkteinheit> strafpunkteinheiten = spManager.getStrafpunkteForPlayerName(currentPlayer.getName());

                        int strafpunkte = spManager.calculateStrafpunkte(strafpunkteinheiten);
                        if (strafpunkte > 0) {
                            log.info("[ultiMinePlugin] Spieler " + currentPlayer.getName() + " wurde an " + strafpunkte + " Strafpunkte erinnert.");
                            String strafpunkteF = spManager.gefFormattedStrafpunkteAnzahl(strafpunkte);
                            PlayerMessagePacket SPpacket = new PlayerMessagePacket(this.plugin, currentPlayer, ultiMinePriority.LOW);
                            SPpacket.setInterval(2);
                            SPpacket.addMessage(new ServerMessage(ChatColor.RED + "HINWEIS: " + ChatColor.DARK_GREEN + "Anzahl deiner Strafpunkte: " + strafpunkteF));
                            for (Strafpunkteinheit sp : strafpunkteinheiten) {
                                SPpacket.addMessage(new ServerMessage(sp.toString()));
                            }
                            messageManager.addMessagePacket(SPpacket);
                            for (Player p : this.plugin.getServer().getOnlinePlayers()) {
                                if (this.uPlugin.ultiBot != null && this.uPlugin.commandListener.playerHasPermission(p, "ultimineplugin.stp.notice")) {
                                    this.uPlugin.ultiBot.sendPM(p, "Soeben hat " + currentPlayer.getName() + " mit " + strafpunkteF + ChatColor.WHITE + " Strafpunkten den Server betreten.");
                                    this.uPlugin.ultiBot.sendPM(p, "Tippe /stp " + currentPlayer.getName() + " für mehr Informationen.");
                                }
                            }
                        }
                    } else {
                        log.info(currentPlayer.getName() + " wird der Gruppe 'User' hinzugefügt, um Baurechte zu erteilen.");
                        this.addToUserGroup(currentPlayer);

                        PlayerInventory inv = currentPlayer.getInventory();
                        inv.addItem(new ItemStack(Material.STONE_PICKAXE, 1));
                        inv.addItem(new ItemStack(Material.WOOD_AXE, 1));
                        inv.addItem(new ItemStack(Material.TORCH, 6));
                        inv.addItem(new ItemStack(Material.RAW_FISH, 6));

                        PlayerMessagePacket packet = new PlayerMessagePacket(this.plugin, currentPlayer, ultiMinePriority.HIGHEST);
                        packet.setInterval(3);
                        packet.addMessage(new ServerMessage(ChatColor.DARK_GREEN, "Dir wurden soeben Baurechte erteilt und ein kleines Starterpaket geschenkt."));
                        packet.addMessage(new ServerMessage(ChatColor.DARK_GREEN, "Du darfst bauen, wo du möchtest, solange das Grundstück noch keinem gehört."));
                        packet.addMessage(new ServerMessage(ChatColor.DARK_GREEN, "Natürlich gehen wir davon aus, dass du die Regeln im Forum gelesen und verstanden hast."));
                        packet.addMessage(new ServerMessage(ChatColor.DARK_GREEN, "Wir wünschen dir viel Spass in unserer Welt!"));

                        messageManager.addMessagePacket(packet);
                    }
                } else {
                    currentPlayer.sendMessage(ChatColor.DARK_GREEN + "Willkommen in der Welt von ultiMine!");


                    PlayerMessagePacket packet = new PlayerMessagePacket(this.plugin, currentPlayer, ultiMinePriority.HIGHEST);
                    packet.setInterval(3);
                    packet.addMessage(new ServerMessage(ChatColor.DARK_GREEN, "Um Baurechte zu erhalten, musst du dich bei uns im Forum mit   deinem Minecraft-Spielernamen registrieren."));
                    packet.addMessage(new ServerMessage(ChatColor.DARK_GREEN, "Besuche dafür einfach www.ultiMine.net und registriere dich    schnell und unkompliziert."));
                    messageManager.addMessagePacket(packet);
                }

                /*
                if (checkUserResponse) {
                currentPlayer.sendMessage(ChatColor.DARK_GREEN + "Willkommen zurück in der Welt von ultiMine!");
                refreshPermissionsCache();
                
                if (permissionsOk) {
                if (!users.containsKey(currentPlayer.getName())) {
                log.info(currentPlayer.getName() + " wird in die globalUsers.yml eingefügt, um Baurechte zu erteilen.");
                // Lege User an und setze ihn in die Gruppe Users
                ArrayList<String> groupDummy = new ArrayList<String>();
                groupDummy.add("User");
                
                LinkedHashMap userEntries = new LinkedHashMap();
                userEntries.put("groups", groupDummy);
                userEntries.put("permissions", null);
                users.put(currentPlayer.getName(), userEntries);
                savePermissionsCache();
                ultiMinePlugin.permissionHandler.reload();
                // currServer.dispatchCommand(new ConsoleCommandSender(currServer), "loadpermissions");
                PlayerInventory inv = currentPlayer.getInventory();
                inv.addItem(new ItemStack(Material.STONE_PICKAXE, 1));
                inv.addItem(new ItemStack(Material.WOOD_AXE, 1));
                inv.addItem(new ItemStack(Material.TORCH, 6));
                inv.addItem(new ItemStack(Material.RAW_FISH, 6));
                
                PlayerMessagePacket packet = new PlayerMessagePacket(this.plugin, currentPlayer, ultiMinePriority.HIGHEST);
                packet.setInterval(3);
                packet.addMessage(new ServerMessage(ChatColor.DARK_GREEN, "Dir wurden soeben Baurechte erteilt und ein kleines Starterpaket geschenkt."));
                packet.addMessage(new ServerMessage(ChatColor.DARK_GREEN, "Du darfst bauen, wo du möchtest, solange das Grundstück noch keinem gehört."));
                packet.addMessage(new ServerMessage(ChatColor.DARK_GREEN, "Natürlich gehen wir davon aus, dass du die Regeln im Forum gelesen und verstanden hast."));
                packet.addMessage(new ServerMessage(ChatColor.DARK_GREEN, "Wir wünschen dir viel Spass in unserer Welt!"));
                
                messageManager.addMessagePacket(packet);
                } else {
                log.info(currentPlayer.getName() + " befindet sich bereits in der globalUsers.yml. Alles bestens, danke!");
                ArrayList<Strafpunkteinheit> strafpunkteinheiten = spManager.getStrafpunkteForPlayerName(currentPlayer.getName());
                
                int strafpunkte = spManager.calculateStrafpunkte(strafpunkteinheiten);
                if (strafpunkte > 0) {
                log.info("[ultiMine] Spieler " + currentPlayer.getName() + " wurde an " + strafpunkte + " Strafpunkte erinnert.");
                String strafpunkteF = spManager.gefFormattedStrafpunkteAnzahl(strafpunkte);
                PlayerMessagePacket SPpacket = new PlayerMessagePacket(this.plugin, currentPlayer, ultiMinePriority.LOW);
                SPpacket.setInterval(2);
                SPpacket.addMessage(new ServerMessage(ChatColor.RED + "HINWEIS: " + ChatColor.DARK_GREEN + "Anzahl deiner Strafpunkte: " + strafpunkteF));
                for (Strafpunkteinheit sp : strafpunkteinheiten) {
                SPpacket.addMessage(new ServerMessage(sp.toString()));
                }
                messageManager.addMessagePacket(SPpacket);
                }
                }
                }
                } else {
                currentPlayer.sendMessage(ChatColor.DARK_GREEN + "Willkommen in der Welt von ultiMine!");
                
                
                PlayerMessagePacket packet = new PlayerMessagePacket(this.plugin, currentPlayer, ultiMinePriority.HIGHEST);
                packet.setInterval(3);
                packet.addMessage(new ServerMessage(ChatColor.DARK_GREEN, "Um Baurechte zu erhalten, musst du dich bei uns im Forum mit   deinem Minecraft-Spielernamen registrieren."));
                packet.addMessage(new ServerMessage(ChatColor.DARK_GREEN, "Besuche dafür einfach www.ultiMine.net und registriere dich    schnell und unkompliziert."));
                messageManager.addMessagePacket(packet);
                }
                 */
            } catch (Exception e) {
                currentPlayer.sendMessage(ChatColor.DARK_GREEN + "Willkommen in der Welt von ultiMine!");
                log.info("[ultiMinePlugin] Fehler bei der Webservice-Verbindung: " + e.getMessage());
                e.printStackTrace();
            }
            if (this.entityListener.getPeacefulMode()) {
                PlayerMessagePacket peacefulPacket = new PlayerMessagePacket(this.plugin, currentPlayer, ultiMinePriority.HIGH);
                peacefulPacket.addMessage(new ServerMessage(ChatColor.GREEN, " ### Heute ist Peaceful Day! ###"));
                messageManager.addMessagePacket(peacefulPacket);

            }
            this.uPlugin.playerManager.addPlayerToList(currentPlayer.getName());
            if (this.uPlugin.ultiBot != null) {
                this.uPlugin.ultiBot.welcomePlayer(currentPlayer.getName());
            }
            messageManager.run();
        }

        class resetCapsLockCounter implements Runnable {

            public resetCapsLockCounter() {
            }

            @Override
            public void run() {
                ultiMinePlugin.ultiMinePluginPlayerListener.this.capsLockCounter = 0;
                ultiMinePlugin.ultiMinePluginPlayerListener.this.capsLockTaskRunning = false;
                ultiMinePlugin.this.log.info("[ultiMinePlugin] Capslock-Zähler wurde zurückgesetzt.");
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return commandListener.onCommand(sender, cmd, commandLabel, args);
    }

    public StrafpunktManager getStrafpunktManager() {
        return this.spManager;
    }

    public TeamRanksManager getTeamRanksManager() {
        return this.teamRanksManager;
    }

    public NewsPostManager getNewsPostManager() {
        return this.newsPostManager;
    }

    public GewinnspielManager getGewinnspielManager() {
        return this.gewinnspielManager;
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public WbbManager getWbbManager() {
        return this.wbbManager;
    }

    public StatistikManager getStatistikManager() {
        return this.statsManager;
    }

    public UltiBot getUltiBot() {
        return this.ultiBot;
    }

    public PermissionManager getPermissionManager() {
        return this.permissions;
    }
}

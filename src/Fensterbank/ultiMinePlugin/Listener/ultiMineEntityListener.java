/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Listener;

// Events
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;


import java.util.logging.Logger;
import Fensterbank.ultiMinePlugin.HelpClasses.Convert;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import java.util.Calendar;

/**
 *
 * @author vncuser
 */
public class ultiMineEntityListener implements Listener {

    private static final Logger log = Logger.getLogger("Minecraft");
    private Boolean peacefulMode;

    public ultiMineEntityListener() {
        Calendar myCal = Calendar.getInstance();
        if (myCal.get(Calendar.DAY_OF_WEEK) == 4 || myCal.get(Calendar.DAY_OF_WEEK) == 7) {
            peacefulMode = true;
        } else {
            peacefulMode = false;
        }

    }

    public void setPeacefulMode(Boolean mode) {
        this.peacefulMode = mode;
    }

    public Boolean getPeacefulMode() {
        return this.peacefulMode;
    }
    

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (peacefulMode) {
            if (event.getEntityType() == EntityType.CREEPER
                    || event.getEntityType() == EntityType.GHAST
                    || event.getEntityType() == EntityType.GIANT
                    || event.getEntityType() == EntityType.PIG_ZOMBIE
                    || event.getEntityType() == EntityType.SKELETON
                    || event.getEntityType() == EntityType.SLIME
                    || event.getEntityType() == EntityType.SPIDER
                    || event.getEntityType() == EntityType.CAVE_SPIDER
                    || event.getEntityType() == EntityType.ENDERMAN
                    || event.getEntityType() == EntityType.ZOMBIE
                    || event.getEntityType() == EntityType.MAGMA_CUBE  
                    || event.getEntityType() == EntityType.SILVERFISH
                    || event.getEntityType() == EntityType.BLAZE) {
                if (event.getSpawnReason() == SpawnReason.NATURAL || event.getSpawnReason() == SpawnReason.SPAWNER) {
                    event.setCancelled(true);
                }
            }
        }        
    }
}

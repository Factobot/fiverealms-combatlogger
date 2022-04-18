package com.thefiverealms.gameplay.pvp;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.thefiverealms.gameplay.Gameplay;
import com.thefiverealms.gameplay.Settings;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class CombatLog implements Listener {
    Gameplay plugin;

    HashMap<UUID, Long> LoggedPlayers = new HashMap<UUID, Long>();
    HashMap<UUID, ItemStack[]> Chickens = new HashMap<UUID, ItemStack[]>();
    HashMap<UUID, Float> ChickenXPs = new HashMap<UUID, Float>();

    public CombatLog(Gameplay plugin) {
        this.plugin = plugin;

        this.plugin.moduleLoaded("CombatLog");
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();

        if(damaged instanceof Player) {
            LoggedPlayers.put(damaged.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if(LoggedPlayers.containsKey(player.getUniqueId())) {
            long lastDamaged = LoggedPlayers.get(player.getUniqueId());
            if(System.currentTimeMillis() - lastDamaged < Settings.CombatLogDisconnectWatchTimeMS) {
                Entity chicken = player.getWorld().spawnEntity(player.getLocation(), EntityType.CHICKEN);
                chicken.setCustomName("§4"  +player.getName());
                chicken.setCustomNameVisible(true);

                Chickens.put(chicken.getUniqueId(), player.getInventory().getContents());
                ChickenXPs.put(chicken.getUniqueId(), player.getExp());

                player.getInventory().clear();
                player.setExp(0.0f);
            }

            LoggedPlayers.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if(Chickens.containsKey(e.getEntity().getUniqueId())) {
            Entity entity = e.getEntity();
            ItemStack[] items = Chickens.get(entity.getUniqueId());
            float xp = ChickenXPs.get(entity.getUniqueId());

            for(ItemStack i : items) {
                if(i != null) {
                    entity.getWorld().dropItem(entity.getLocation(), i);
                }
            }

            ((ExperienceOrb) entity.getWorld().spawn(entity.getLocation(), ExperienceOrb.class)).setExperience((int) xp);

            Chickens.remove(entity.getUniqueId());
            ChickenXPs.remove(entity.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(LoggedPlayers.containsKey((e.getEntity().getUniqueId()))) {
            LoggedPlayers.remove(e.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveFromWorldEvent e) {
        if(Chickens.containsKey(e.getEntity().getUniqueId())) {
            Entity entity = e.getEntity();

            Chickens.remove(entity.getUniqueId());
            ChickenXPs.remove(entity.getUniqueId());
        }
    }
}

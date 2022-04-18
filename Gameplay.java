package com.thefiverealms.gameplay;

import com.thefiverealms.gameplay.pvp.CombatLog;
import org.bukkit.plugin.java.JavaPlugin;

public final class Gameplay extends JavaPlugin {
    CombatLog combatLogger;

    @Override
    public void onEnable() {
        getLogger().info("Starting up");

        this.combatLogger = new CombatLog(this);

        getServer().getPluginManager().registerEvents(this.combatLogger, this);

        getLogger().info("Done");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void moduleLoaded(String name) {
        getLogger().info(name + ": Loaded");
    }
}

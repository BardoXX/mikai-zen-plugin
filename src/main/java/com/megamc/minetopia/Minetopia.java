package com.megamc.minetopia;

import com.megamc.minetopia.commands.*;
import com.megamc.minetopia.events.BalanceEvent;
import com.megamc.minetopia.events.DisableCraft; // ← ADD THIS IMPORT

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Minetopia extends JavaPlugin {

    private static Economy econ = null;

    @Override
    public void onEnable() {
        getLogger().info("MegaMCMinetopia plugin is loaded!");

        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe("Vault or an economy plugin not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerCommands();
        registerEvents();

        getLogger().info("All commands and events registered successfully!");
    }

    private void registerCommands() {
        if (getCommand("saldo") != null)
            getCommand("saldo").setExecutor(new SaldoCommand(getEconomy()));

        if (getCommand("betaal") != null)
            getCommand("betaal").setExecutor(new BetaalCommand(getEconomy()));

        if (getCommand("boete") != null)
            getCommand("boete").setExecutor(new BoeteCommand());

        if (getCommand("contract") != null)
            getCommand("contract").setExecutor(new ContractCommand());

        if (getCommand("fouilleer") != null) {
            FouilleerCommand fouilleer = new FouilleerCommand();
            getCommand("fouilleer").setExecutor(fouilleer);
            Bukkit.getPluginManager().registerEvents(fouilleer, this);
        }

        if (getCommand("kvk") != null)
            getCommand("kvk").setExecutor(new KvkCommand());

        if (getCommand("lening") != null)
            getCommand("lening").setExecutor(new LeningCommand());

        if (getCommand("passport") != null)
            getCommand("passport").setExecutor(new PassportCommand());

        if (getCommand("megamcminetopiareload") != null)
            getCommand("megamcminetopiareload").setExecutor(new ReloadCommand(this));

        if (getCommand("straf") != null)
            getCommand("straf").setExecutor(new StrafCommand());

        if (getCommand("vanish") != null)
            getCommand("vanish").setExecutor(new VanishCommand(this));

        if (getCommand("vergunning") != null)
            getCommand("vergunning").setExecutor(new VergunningCommand());
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new BalanceEvent(getEconomy()), this);
        Bukkit.getPluginManager().registerEvents(new DisableCraft(), this);
    } // ← FIXED BRACKET

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onDisable() {
        getLogger().info("MegaMCMinetopia plugin is disabled!");
    }
}

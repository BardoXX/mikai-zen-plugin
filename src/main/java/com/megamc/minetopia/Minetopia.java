package com.megamc.minetopia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.megamc.minetopia.commands.ATMCommand;
import com.megamc.minetopia.commands.ATMGUI;
import com.megamc.minetopia.commands.HelpCommand;
import com.megamc.minetopia.commands.PinCommand;
import com.megamc.minetopia.commands.BetaalCommand;
import com.megamc.minetopia.commands.BoeteCommand;
import com.megamc.minetopia.commands.ContractCommand;
import com.megamc.minetopia.commands.FouilleerCommand;
import com.megamc.minetopia.commands.KvkCommand;
import com.megamc.minetopia.commands.LeningCommand;
import com.megamc.minetopia.commands.PassportCommand;
import com.megamc.minetopia.commands.ReloadCommand;
import com.megamc.minetopia.commands.SaldoCommand;
import com.megamc.minetopia.commands.StrafCommand;
import com.megamc.minetopia.commands.VanishCommand;
import com.megamc.minetopia.commands.VergunningCommand;
import com.megamc.minetopia.ScoreboardManager;
import com.megamc.minetopia.TablistManager;
import com.megamc.minetopia.events.ATMBlockListener;
import com.megamc.minetopia.events.ATMChatListener;
import com.megamc.minetopia.events.DisableCraft;
import com.megamc.minetopia.events.BalanceEvent;
import com.megamc.minetopia.events.ScoreboardTablistListener;

import net.milkbowl.vault.economy.Economy;

public class Minetopia extends JavaPlugin {

    private static Economy econ = null;
    private final Set<Location> atmBlocks = new HashSet<>();
    private final Set<Location> pinBlocks = new HashSet<>();

    private ScoreboardManager scoreboardManager;
    private TablistManager tablistManager;

    @Override
    public void onEnable() {
        getLogger().info("MegaMCMinetopia plugin is loaded!");

        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe("Vault or an economy plugin not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Load pin blocks from config
        loadPinBlocks();

        // Load ATM blocks from config
        loadATMBlocks();

        // Initialize scoreboard and tablist managers
        scoreboardManager = new ScoreboardManager(this);
        tablistManager = new TablistManager(this);

        registerCommands();
        registerEvents();

        // Start ATM particle effect scheduler
        startATMParticleScheduler();

        getLogger().info("All commands and events registered successfully!");
    }

    private void registerCommands() {
        if (getCommand("help") != null)
            getCommand("help").setExecutor(new HelpCommand());

        if (getCommand("atm") != null) {
            ATMGUI atmGui = new ATMGUI(getEconomy(), this, new HashMap<>());
            ATMCommand atmCommand = new ATMCommand(getEconomy(), this, atmGui, atmBlocks);
            getCommand("atm").setExecutor(atmCommand);

            PinCommand pinCommand = new PinCommand(getEconomy(), pinBlocks);
            // Registreer ATM event listeners
            Bukkit.getPluginManager().registerEvents(new ATMBlockListener(getEconomy(), this, new HashMap<>(), atmBlocks, pinBlocks, pinCommand), this);
            Bukkit.getPluginManager().registerEvents(new ATMChatListener(atmGui), this);
        }

        if (getCommand("saldo") != null)
            getCommand("saldo").setExecutor(new SaldoCommand(getEconomy()));

        if (getCommand("betaal") != null)
            getCommand("betaal").setExecutor(new BetaalCommand(getEconomy()));

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

        if (getCommand("vergunning") != null)
            getCommand("vergunning").setExecutor(new VergunningCommand());

        if (getCommand("vanish") != null)
            getCommand("vanish").setExecutor(new VanishCommand(this));

        if (getCommand("help") != null)
            getCommand("help").setExecutor(new HelpCommand());

        if (getCommand("pin") != null)
            getCommand("pin").setExecutor(new PinCommand(getEconomy(), pinBlocks));
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new BalanceEvent(getEconomy()), this);
        Bukkit.getPluginManager().registerEvents(new DisableCraft(), this);

        // Register scoreboard and tablist event listener
        Bukkit.getPluginManager().registerEvents(new ScoreboardTablistListener(scoreboardManager, tablistManager), this);

        // ATM GUI click handler
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player && event.getCurrentItem() != null) {
                    Player player = (Player) event.getWhoClicked();
                    String guiTitle = event.getView().getTitle();

                    // Check if it's an ATM GUI
                    if (guiTitle.contains("ATM") || guiTitle.contains("Geld") || guiTitle.contains("Saldo")) {
                        // Block all item interactions in ATM GUIs
                        event.setCancelled(true);

                        // Only allow clicking on specific functional items
                        int slot = event.getRawSlot();
                        ATMGUI atmGui = new ATMGUI(getEconomy(), Minetopia.this, new HashMap<>());
                        atmGui.handleGUIClick(player, slot, guiTitle);
                        return;
                    }
                }
            }
        }, this);
    }

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

    private void startATMParticleScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location atmLocation : atmBlocks) {
                    // Clone de locatie om de originele niet te muteren
                    Location particleLocation = atmLocation.clone().add(0.5, 1.5, 0.5);
                    atmLocation.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, particleLocation, 3, 0.2, 0.5, 0.2, 0.01);
                }
            }
        }.runTaskTimer(this, 0L, 40L); // Run every 2 seconds (40 ticks)
    }

    public void saveATMBlocks() {
        List<String> blockList = new ArrayList<>();
        for (Location location : atmBlocks) {
            String locationString = location.getWorld().getName() + "," +
                                   location.getBlockX() + "," +
                                   location.getBlockY() + "," +
                                   location.getBlockZ();
            blockList.add(locationString);
        }
        getConfig().set("atm.blocks", blockList);
        saveConfig();
    }

    public void loadATMBlocks() {
        List<String> blockList = getConfig().getStringList("atm.blocks");
        for (String locationString : blockList) {
            String[] parts = locationString.split(",");
            if (parts.length == 4) {
                try {
                    World world = Bukkit.getWorld(parts[0]);
                    if (world != null) {
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int z = Integer.parseInt(parts[3]);
                        Location location = new Location(world, x, y, z);
                        atmBlocks.add(location);
                    }
                } catch (NumberFormatException e) {
                    getLogger().warning("Failed to parse ATM block location: " + locationString);
                }
            }
        }
        getLogger().info("Loaded " + atmBlocks.size() + " ATM blocks from config.");
    }

    public void addATMBlock(Location location) {
        atmBlocks.add(location);
        saveATMBlocks();
        getLogger().info(String.format("ATM toegevoegd op locatie: %s, X:%d, Y:%d, Z:%d", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    public void removeATMBlock(Location location) {
        atmBlocks.remove(location);
        saveATMBlocks();
        getLogger().info(String.format("ATM verwijderd op locatie: %s, X:%d, Y:%d, Z:%d", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    public void savePinBlocks() {
        List<String> blockList = new ArrayList<>();
        for (Location location : pinBlocks) {
            String locationString = location.getWorld().getName() + "," +
                                   location.getBlockX() + "," +
                                   location.getBlockY() + "," +
                                   location.getBlockZ();
            blockList.add(locationString);
        }
        getConfig().set("pin.blocks", blockList);
        saveConfig();
    }

    public void loadPinBlocks() {
        List<String> blockList = getConfig().getStringList("pin.blocks");
        for (String locationString : blockList) {
            String[] parts = locationString.split(",");
            if (parts.length == 4) {
                try {
                    World world = Bukkit.getWorld(parts[0]);
                    if (world != null) {
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int z = Integer.parseInt(parts[3]);
                        Location location = new Location(world, x, y, z);
                        pinBlocks.add(location);
                    }
                } catch (NumberFormatException e) {
                    getLogger().warning("Failed to parse pin block location: " + locationString);
                }
            }
        }
        getLogger().info("Loaded " + pinBlocks.size() + " pin blocks from config.");
    }

    public void addPinBlock(Location location) {
        pinBlocks.add(location);
        savePinBlocks();
        getLogger().info(String.format("Pin block toegevoegd op locatie: %s, X:%d, Y:%d, Z:%d", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    public void reloadScoreboardTablistConfig() {
        scoreboardManager.loadConfig();
        tablistManager.reload();
        getLogger().info("Scoreboard and tablist configuration reloaded!");
    }
}

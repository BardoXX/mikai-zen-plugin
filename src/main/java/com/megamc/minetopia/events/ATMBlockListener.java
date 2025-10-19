package com.megamc.minetopia.events;

import com.megamc.minetopia.Minetopia;
import com.megamc.minetopia.commands.ATMGUI;
import com.megamc.minetopia.commands.PinCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ATMBlockListener implements Listener {

    private final Economy econ;
    private final Minetopia plugin;
    private final Map<UUID, Long> cooldowns;
    private final Set<Location> atmBlocks;
    private final Set<Location> pinBlocks;
    private final PinCommand pinCommand;

    public ATMBlockListener(Economy econ, Minetopia plugin, Map<UUID, Long> cooldowns,
                           Set<Location> atmBlocks, Set<Location> pinBlocks, PinCommand pinCommand) {
        this.econ = econ;
        this.plugin = plugin;
        this.cooldowns = cooldowns;
        this.atmBlocks = atmBlocks;
        this.pinBlocks = pinBlocks;
        this.pinCommand = pinCommand;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Location location = block.getLocation();
        Player player = event.getPlayer();

        // Check if it's a PIN block
        if (pinBlocks.contains(location)) {
            event.setCancelled(true);
            pinCommand.handleAccept(player);
            return;
        }

        // Check if it's an ATM block (registered location)
        if (atmBlocks.contains(location)) {
            event.setCancelled(true);
            openATMGUI(player);
            return;
        }

        // Check if it's a redstone block (ATM block)
        if (block.getType() == Material.REDSTONE_BLOCK) {
            event.setCancelled(true);
            openATMGUI(player);
            return;
        }
    }

    private void openATMGUI(Player player) {
        // Create ATM GUI instance and open it
        ATMGUI atmGui = new ATMGUI(econ, plugin, cooldowns);
        atmGui.openATMGUI(player);
    }
}

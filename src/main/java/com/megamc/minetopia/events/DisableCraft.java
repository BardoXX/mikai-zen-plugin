package com.megamc.minetopia.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class DisableCraft implements Listener {

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        event.setCancelled(true); // Block all crafting
    }
}

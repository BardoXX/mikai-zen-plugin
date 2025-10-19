package com.megamc.minetopia.events;

import com.megamc.minetopia.commands.ATMGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ATMChatListener implements Listener {

    private final ATMGUI atmGui;

    public ATMChatListener(ATMGUI atmGui) {
        this.atmGui = atmGui;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // ATM chat input handling would go here
        // For now, just cancel if needed
    }
}

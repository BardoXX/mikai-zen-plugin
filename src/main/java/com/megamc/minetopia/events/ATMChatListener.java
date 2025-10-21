package com.megamc.minetopia.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.UUID;
import com.megamc.minetopia.commands.ATMGUI;

public class ATMChatListener implements Listener {

    private final ATMGUI atmGui;

    public ATMChatListener(ATMGUI atmGui) {
        this.atmGui = atmGui;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Controleer of speler wacht op ATM input
        UUID playerUUID = player.getUniqueId();
        if (!atmGui.getWaitingForInput().containsKey(playerUUID)) {
            return; // Speler wacht niet op input
        }

        // Cancel het chat event zodat anderen het niet zien
        event.setCancelled(true);

        // Haal de actie op waar de speler op wacht
        ATMGUI.ATMAction action = atmGui.getWaitingForInput().get(playerUUID);

        try {
            // Probeer het bedrag te parsen (eenvoudige versie)
            double bedrag = Double.parseDouble(message.replaceAll("[^0-9.]", ""));

            if (bedrag <= 0) {
                player.sendMessage("§cOngeldig bedrag! Gebruik een positief getal.");
                return;
            }

            // Controleer maximum bedrag
            double maxBedrag = 10000.0; // Standaard waarde
            if (bedrag > maxBedrag) {
                player.sendMessage("§cJe kunt niet meer dan €" + maxBedrag + " per keer verwerken.");
                return;
            }

            // Verwerk de actie
            if (action == ATMGUI.ATMAction.WITHDRAW) {
                atmGui.handleWithdraw(player, bedrag);
            } else if (action == ATMGUI.ATMAction.DEPOSIT) {
                atmGui.handleDeposit(player, bedrag);
            }

            // Verwijder speler uit waiting list
            atmGui.getWaitingForInput().remove(playerUUID);

        } catch (NumberFormatException e) {
            player.sendMessage("§cOngeldig bedrag! Gebruik cijfers en punten (bijv. 100.50)");
        }
    }
}

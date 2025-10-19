package com.megamc.minetopia.events;

import com.megamc.minetopia.Minetopia;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BalanceEvent implements Listener {

    private final Economy econ;

    public BalanceEvent(Economy econ) {
        this.econ = econ;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        if (item.getType() == Material.LIME_DYE) {
            double balance = econ.getBalance(event.getPlayer());
            event.getPlayer().sendMessage("§aYour balance: §e€" + balance);
            event.setCancelled(true);
        }
    }
}

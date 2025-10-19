package com.megamc.minetopia.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FouilleerCommand implements CommandExecutor, Listener {

    private static final int INVENTORY_SIZE = 54; // 6 rows

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player speler)) {
            sender.sendMessage(ChatColor.RED + "Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }

        if (!speler.hasPermission("megamc.fouilleer")) {
            speler.sendMessage(ChatColor.RED + "Je hebt geen permissie om dit te doen.");
            return true;
        }

        if (args.length != 1) {
            speler.sendMessage(ChatColor.RED + "Gebruik: /fouilleer [speler]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            speler.sendMessage(ChatColor.RED + "Speler niet gevonden.");
            return true;
        }

        Inventory inv = Bukkit.createInventory(
                null,
                INVENTORY_SIZE,
                "§6Inventaris van " + target.getName()
        );

        // Top border
        fillRow(inv, 0, 8, Material.GRAY_STAINED_GLASS_PANE);

        // Main inventory rows (slots 9-35)
        ItemStack[] storage = target.getInventory().getStorageContents();
        int guiSlot = 9;
        for (int i = 0; i < storage.length; i++) {
            ItemStack item = storage[i];
            inv.setItem(guiSlot++, item != null ? item.clone() : createPlaceholder("§7Leeg"));
        }

        // Hotbar row (slots 36-44)
        ItemStack[] hotbar = target.getInventory().getContents();
        for (int i = 0; i < 9; i++) {
            ItemStack item = hotbar[i];
            inv.setItem(36 + i, item != null ? item.clone() : createPlaceholder("§7Leeg"));
        }

        // Armor & Offhand (slots 45-49)
        setSlot(inv, 45, target.getInventory().getHelmet(), "Helmet");
        setSlot(inv, 46, target.getInventory().getChestplate(), "Chestplate");
        setSlot(inv, 47, target.getInventory().getLeggings(), "Leggings");
        setSlot(inv, 48, target.getInventory().getBoots(), "Boots");
        setSlot(inv, 49, target.getInventory().getItemInOffHand(), "Offhand");

        // Bottom border (slots 50-53)
        fillRow(inv, 50, 53, Material.BLACK_STAINED_GLASS_PANE);

        speler.openInventory(inv);
        speler.sendMessage(ChatColor.GREEN + "Je bekijkt nu de inventory van " + target.getName() + ".");
        return true;
    }

    private void fillRow(Inventory inv, int start, int end, Material mat) {
        ItemStack pane = new ItemStack(mat);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            pane.setItemMeta(meta);
        }
        for (int i = start; i <= end; i++) inv.setItem(i, pane);
    }

    private void setSlot(Inventory inv, int slot, ItemStack item, String label) {
        if (item == null) item = new ItemStack(Material.BARRIER);
        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e" + label);
            clone.setItemMeta(meta);
        }
        inv.setItem(slot, clone);
    }

    private ItemStack createPlaceholder(String name) {
        ItemStack placeholder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = placeholder.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            placeholder.setItemMeta(meta);
        }
        return placeholder;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().startsWith("§6Inventaris van")) {
            e.setCancelled(true); // read-only
        }
    }
}

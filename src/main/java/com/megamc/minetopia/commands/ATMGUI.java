package com.megamc.minetopia.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ATMGUI {

    private final Economy econ;
    private final JavaPlugin plugin;
    private final Map<UUID, ATMAction> waitingForInput = new HashMap<>();
    private final Map<UUID, Long> cooldowns;

    public ATMGUI(Economy econ, JavaPlugin plugin, Map<UUID, Long> cooldowns) {
        this.econ = econ;
        this.plugin = plugin;
        this.cooldowns = cooldowns;
    }

    public Map<UUID, ATMAction> getWaitingForInput() {
        return waitingForInput;
    }

    public void openATMGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.YELLOW + "🏦 ATM - Bankautomaat");

        // Saldo bekijken
        gui.setItem(11, createButton(Material.EMERALD, ChatColor.GREEN + "💰 Saldo Bekijken",
                Arrays.asList(ChatColor.GRAY + "Bekijk je huidige bank saldo")));

        // Geld opnemen
        gui.setItem(13, createButton(Material.GOLD_INGOT, ChatColor.YELLOW + "💸 Geld Opnemen",
                Arrays.asList(ChatColor.GRAY + "Neem geld op van je bank")));

        // Geld storten
        gui.setItem(15, createButton(Material.IRON_INGOT, ChatColor.AQUA + "💳 Geld Storten",
                Arrays.asList(ChatColor.GRAY + "Stort geld naar je bank")));

        // Decoratieve items
        for (int i = 0; i < 27; i++) {
            if (i != 11 && i != 13 && i != 15) {
                gui.setItem(i, createButton(Material.GRAY_STAINED_GLASS_PANE, " "));
            }
        }

        player.openInventory(gui);
    }

    private void openBalanceGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GREEN + "💰 Saldo Bekijken");

        double saldo = econ.getBalance(player);
        gui.setItem(13, createButton(Material.EMERALD_BLOCK, ChatColor.GREEN + "Je Saldo: €" + String.format("%.2f", saldo),
                Arrays.asList(ChatColor.GRAY + "Dit is je huidige bank saldo.")));

        // Decoratieve items
        for (int i = 0; i < 27; i++) {
            if (i != 13) {
                gui.setItem(i, createButton(Material.GRAY_STAINED_GLASS_PANE, " "));
            }
        }

        player.openInventory(gui);
    }

    private void openWithdrawGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.YELLOW + "💸 Geld Opnemen");

        // Preset bedragen voor opnemen
        gui.setItem(11, createButton(Material.GOLD_NUGGET, ChatColor.YELLOW + "€100", Arrays.asList(ChatColor.GRAY + "Klik om €100 op te nemen")));
        gui.setItem(12, createButton(Material.GOLD_INGOT, ChatColor.YELLOW + "€500", Arrays.asList(ChatColor.GRAY + "Klik om €500 op te nemen")));
        gui.setItem(13, createButton(Material.GOLD_BLOCK, ChatColor.YELLOW + "€1000", Arrays.asList(ChatColor.GRAY + "Klik om €1000 op te nemen")));
        gui.setItem(14, createButton(Material.DIAMOND_BLOCK, ChatColor.YELLOW + "€5000", Arrays.asList(ChatColor.GRAY + "Klik om €5000 op te nemen")));
        gui.setItem(15, createButton(Material.DIAMOND, ChatColor.YELLOW + "Aangepast Bedrag", Arrays.asList(ChatColor.GRAY + "Klik om een aangepast bedrag in te voeren")));

        // Decoratieve items
        for (int i = 0; i < 27; i++) {
            if (i != 11 && i != 12 && i != 13 && i != 14 && i != 15) {
                gui.setItem(i, createButton(Material.GRAY_STAINED_GLASS_PANE, " "));
            }
        }

        player.openInventory(gui);
    }

    private void openDepositGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.AQUA + "💳 Geld Storten");

        // Preset bedragen voor storten
        gui.setItem(11, createButton(Material.IRON_NUGGET, ChatColor.AQUA + "€100", Arrays.asList(ChatColor.GRAY + "Klik om €100 te storten")));
        gui.setItem(12, createButton(Material.IRON_INGOT, ChatColor.AQUA + "€500", Arrays.asList(ChatColor.GRAY + "Klik om €500 te storten")));
        gui.setItem(13, createButton(Material.IRON_BLOCK, ChatColor.AQUA + "€1000", Arrays.asList(ChatColor.GRAY + "Klik om €1000 te storten")));
        gui.setItem(14, createButton(Material.DIAMOND_BLOCK, ChatColor.AQUA + "€5000", Arrays.asList(ChatColor.GRAY + "Klik om €5000 te storten")));
        gui.setItem(15, createButton(Material.DIAMOND, ChatColor.AQUA + "Aangepast Bedrag", Arrays.asList(ChatColor.GRAY + "Klik om een aangepast bedrag in te voeren")));

        // Decoratieve items
        for (int i = 0; i < 27; i++) {
            if (i != 11 && i != 12 && i != 13 && i != 14 && i != 15) {
                gui.setItem(i, createButton(Material.GRAY_STAINED_GLASS_PANE, " "));
            }
        }

        player.openInventory(gui);
    }

    private ItemStack createButton(Material material, String name, java.util.List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createButton(Material material, String name) {
        return createButton(material, name, Arrays.asList());
    }

    public void handleGUIClick(Player player, int slot, String guiTitle) {
        if (guiTitle.contains("ATM - Bankautomaat")) {
            switch (slot) {
                case 11: // Saldo bekijken
                    openBalanceGUI(player);
                    break;
                case 13: // Geld opnemen
                    if (checkCooldown(player)) {
                        openWithdrawGUI(player);
                    }
                    break;
                case 15: // Geld storten
                    openDepositGUI(player);
                    break;
                default:
                    return;
            }
        } else if (guiTitle.contains("Geld Opnemen")) {
            handleWithdrawClick(player, slot);
        } else if (guiTitle.contains("Geld Storten")) {
            handleDepositClick(player, slot);
        } else if (guiTitle.contains("Saldo Bekijken")) {
            player.closeInventory();
        }
    }

    private void handleWithdrawClick(Player player, int slot) {
        double bedrag = 0;

        switch (slot) {
            case 11: // €100
                bedrag = 100;
                break;
            case 12: // €500
                bedrag = 500;
                break;
            case 13: // €1000
                bedrag = 1000;
                break;
            case 14: // €5000
                bedrag = 5000;
                break;
            case 15: // Aangepast bedrag
                waitingForInput.put(player.getUniqueId(), ATMAction.WITHDRAW);
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "💸 Voer het bedrag in dat je wilt opnemen:");
                return;
        }

        if (bedrag > 0) {
            handleWithdraw(player, bedrag);
        }
    }

    private void handleDepositClick(Player player, int slot) {
        double bedrag = 0;

        switch (slot) {
            case 11: // €100
                bedrag = 100;
                break;
            case 12: // €500
                bedrag = 500;
                break;
            case 13: // €1000
                bedrag = 1000;
                break;
            case 14: // €5000
                bedrag = 5000;
                break;
            case 15: // Aangepast bedrag
                waitingForInput.put(player.getUniqueId(), ATMAction.DEPOSIT);
                player.closeInventory();
                player.sendMessage(ChatColor.AQUA + "💳 Voer het bedrag in dat je wilt storten:");
                return;
        }

        if (bedrag > 0) {
            handleDeposit(player, bedrag);
        }
    }

    public void handleWithdraw(Player player, double bedrag) {
        // Controleer maximum bedrag
        double maxBedrag = plugin.getConfig().getDouble("atm.maximum-bedrag", 10000.0);
        if (bedrag > maxBedrag) {
            player.sendMessage(ChatColor.RED + "❌ Je kunt niet meer dan €" + maxBedrag + " per keer opnemen.");
            return;
        }

        // Controleer of speler genoeg geld heeft
        if (econ.getBalance(player) < bedrag) {
            player.sendMessage(ChatColor.RED + "❌ Je hebt niet genoeg geld om €" + String.format("%.2f", bedrag) + " op te nemen.");
            return;
        }

        // ATM kosten berekenen
        double kostenPercentage = plugin.getConfig().getDouble("atm.kosten-percentage", 0.02);
        double minimumKosten = plugin.getConfig().getDouble("atm.minimum-kosten", 10.0);

        double kosten = Math.max(bedrag * kostenPercentage, minimumKosten);
        double totaalBedrag = bedrag + kosten;

        if (econ.getBalance(player) < totaalBedrag) {
            player.sendMessage(ChatColor.RED + "❌ Je hebt niet genoeg geld voor de ATM kosten (€" + String.format("%.2f", kosten) + ").");
            return;
        }

        // Geld opnemen van bank en geef fysiek geld item
        econ.withdrawPlayer(player, totaalBedrag);
        giveMoneyItem(player, bedrag);
        player.sendMessage(ChatColor.GREEN + "✅ Je hebt €" + String.format("%.2f", bedrag) + " opgenomen als fysiek geld.");
        player.sendMessage(ChatColor.GREEN + "💰 ATM kosten: €" + String.format("%.2f", kosten));
        player.sendMessage(ChatColor.GREEN + "💰 Totaal afgeschreven: €" + String.format("%.2f", totaalBedrag));

        // Cooldown instellen
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void handleDeposit(Player player, double bedrag) {
        // Controleer of speler fysiek geld heeft
        if (!hasMoneyItem(player, bedrag)) {
            player.sendMessage(ChatColor.RED + "❌ Je hebt niet genoeg fysiek geld om te storten.");
            return;
        }

        // Neem fysiek geld weg en stort naar bank
        removeMoneyItem(player, bedrag);
        econ.depositPlayer(player, bedrag);
        player.sendMessage(ChatColor.GREEN + "✅ Je hebt €" + String.format("%.2f", bedrag) + " gestort op je bankrekening.");
    }

    private boolean checkCooldown(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (cooldowns.containsKey(playerUUID)) {
            long lastUse = cooldowns.get(playerUUID);
            long currentTime = System.currentTimeMillis();
            long cooldownTime = 60 * 1000; // 1 minuut cooldown

            if (currentTime - lastUse < cooldownTime) {
                long remaining = (cooldownTime - (currentTime - lastUse)) / 1000;
                player.sendMessage(ChatColor.RED + "⏰ Je moet nog " + remaining + " seconden wachten voordat je de ATM weer kunt gebruiken.");
                return false;
            }
        }
        return true;
    }

    private void giveMoneyItem(Player player, double amount) {
        // Create money item
        ItemStack moneyItem = new ItemStack(Material.PAPER);
        ItemMeta meta = moneyItem.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Geld Biljet - €" + String.format("%.2f", amount));
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + "Een fysiek geld biljet",
            ChatColor.YELLOW + "Rechtsklik op een ATM om te storten"
        ));
        moneyItem.setItemMeta(meta);

        player.getInventory().addItem(moneyItem);
    }

    private boolean hasMoneyItem(Player player, double amount) {
        double totalMoney = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && isMoneyItem(item)) {
                totalMoney += getMoneyItemValue(item);
                if (totalMoney >= amount) {
                    return true;
                }
            }
        }

        return false;
    }

    private void removeMoneyItem(Player player, double amount) {
        double remaining = amount;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && isMoneyItem(item)) {
                double itemValue = getMoneyItemValue(item);
                if (itemValue <= remaining) {
                    player.getInventory().setItem(i, null);
                    remaining -= itemValue;
                } else {
                    // Reduce the item value
                    ItemMeta meta = item.getItemMeta();
                    double newValue = itemValue - remaining;
                    meta.setDisplayName(ChatColor.GOLD + "Geld Biljet - €" + String.format("%.2f", newValue));
                    item.setItemMeta(meta);
                    remaining = 0;
                }

                if (remaining <= 0) break;
            }
        }
    }

    private boolean isMoneyItem(ItemStack item) {
        if (item.getType() != Material.PAPER) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().startsWith(ChatColor.GOLD + "Geld Biljet - €");
    }

    private double getMoneyItemValue(ItemStack item) {
        if (!isMoneyItem(item)) return 0.0;
        String displayName = item.getItemMeta().getDisplayName();
        String amountStr = displayName.replace(ChatColor.GOLD + "Geld Biljet - €", "").replace(",", "");
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public enum ATMAction {
        WITHDRAW, DEPOSIT
    }
}

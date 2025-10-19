package com.megamc.minetopia.commands;

import com.megamc.minetopia.Minetopia;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class ATMCommand implements CommandExecutor {

    private final Economy econ;
    private final JavaPlugin plugin;
    private final ATMGUI atmGui;
    private final Set<Location> atmBlocks;

    public ATMCommand(Economy econ, JavaPlugin plugin, ATMGUI atmGui, Set<Location> atmBlocks) {
        this.econ = econ;
        this.plugin = plugin;
        this.atmGui = atmGui;
        this.atmBlocks = atmBlocks;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Toon ATM command overzicht
            showATMHelp(player);
            return true;
        }

        if (args.length == 1) {
            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "gui":
                    atmGui.openATMGUI(player);
                    return true;
                case "block":
                    giveATMBlock(player);
                    return true;
                case "set":
                    setATM(player);
                    return true;
                case "remove":
                    removeATM(player);
                    return true;
                case "list":
                    listATMs(player);
                    return true;
                default:
                    showATMHelp(player);
                    return true;
            }
        }

        showATMHelp(player);
        return true;
    }

    private void showATMHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== ATM Commands ===");
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "/atm" + ChatColor.WHITE + " - Toon dit overzicht");
        player.sendMessage(ChatColor.YELLOW + "/atm gui" + ChatColor.WHITE + " - Open de ATM GUI");
        player.sendMessage(ChatColor.YELLOW + "/atm block" + ChatColor.WHITE + " - Ontvang een ATM blok (redstone stair)");
        player.sendMessage(ChatColor.YELLOW + "/atm set" + ChatColor.WHITE + " - Stel een blok in als ATM (kijk naar blok)");
        player.sendMessage(ChatColor.YELLOW + "/atm remove" + ChatColor.WHITE + " - Verwijder ATM status van blok");
        player.sendMessage(ChatColor.YELLOW + "/atm list" + ChatColor.WHITE + " - Toon alle ATM locaties");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "💡 Tip: Plaats een redstone stair en gebruik /atm set om een ATM te maken!");
    }

    private void giveATMBlock(Player player) {
        if (!player.hasPermission("megamc.atm.admin")) {
            player.sendMessage(ChatColor.RED + "❌ Je hebt geen toestemming om ATM blokken te krijgen.");
            return;
        }

        ItemStack atmBlock = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta meta = atmBlock.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "ATM Blok");
        meta.setLore(java.util.Arrays.asList(
            ChatColor.GRAY + "Plaats dit blok neer en gebruik",
            ChatColor.GRAY + "/atm set om een ATM te maken",
            ChatColor.YELLOW + "Rechtsklik om ATM te gebruiken"
        ));
        atmBlock.setItemMeta(meta);

        player.getInventory().addItem(atmBlock);
        player.sendMessage(ChatColor.GREEN + "✅ Je hebt een ATM blok ontvangen!");
        player.sendMessage(ChatColor.GRAY + "Plaats het neer en gebruik /atm set om het te activeren.");
    }

    private void listATMs(Player player) {
        if (atmBlocks.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "📍 Er zijn geen ATM locaties ingesteld.");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "=== ATM Locaties ===");
        int count = 1;
        for (Location location : atmBlocks) {
            player.sendMessage(ChatColor.YELLOW + "" + count + ". " +
                ChatColor.WHITE + "Wereld: " + location.getWorld().getName() +
                ChatColor.GRAY + " | X: " + location.getBlockX() +
                " Y: " + location.getBlockY() +
                " Z: " + location.getBlockZ());
            count++;
        }
        player.sendMessage(ChatColor.GRAY + "Totaal: " + atmBlocks.size() + " ATM(s)");
    }

    private void setATM(Player player) {
        Block targetBlock = player.getTargetBlock(null, 5);
        if (targetBlock == null) {
            player.sendMessage(ChatColor.RED + " Je moet naar een blok kijken om een ATM te maken.");
            return;
        }

        Location blockLocation = targetBlock.getLocation();

        // Als het een bordje is, stel het in als ATM bordje
        if (targetBlock.getState() instanceof Sign) {
            Sign sign = (Sign) targetBlock.getState();
            sign.setLine(0, "[ATM]");
            sign.update();
            player.sendMessage(ChatColor.GREEN + " ATM bordje ingesteld! Spelers kunnen nu rechtsklikken om de ATM te gebruiken.");
        } else {
            // Voor andere blokken, voeg toe aan atmBlocks set
            if (atmBlocks.contains(blockLocation)) {
                player.sendMessage(ChatColor.RED + " Dit blok is al een ATM.");
                return;
            }

            atmBlocks.add(blockLocation);
            ((Minetopia) plugin).addATMBlock(blockLocation);
            player.sendMessage(ChatColor.GREEN + " ATM ingesteld op dit blok! Spelers kunnen nu rechtsklikken om de ATM te gebruiken.");
        }
    }

    private void removeATM(Player player) {
        Block targetBlock = player.getTargetBlock(null, 5);
        if (targetBlock == null) {
            player.sendMessage(ChatColor.RED + " Je moet naar een ATM blok kijken om het te verwijderen.");
            return;
        }

        Location blockLocation = targetBlock.getLocation();

        // Als het een bordje is, controleer en verwijder [ATM] tekst
        if (targetBlock.getState() instanceof Sign) {
            Sign sign = (Sign) targetBlock.getState();
            String[] lines = sign.getLines();

            if (lines.length > 0 && lines[0].toLowerCase().contains("[atm]")) {
                sign.setLine(0, "");
                sign.update();
                player.sendMessage(ChatColor.GREEN + " ATM status verwijderd van dit bordje.");
            } else {
                player.sendMessage(ChatColor.RED + " Dit is geen ATM bordje.");
            }
        } else {
            // Voor andere blokken, verwijder uit atmBlocks set
            if (atmBlocks.remove(blockLocation)) {
                ((Minetopia) plugin).removeATMBlock(blockLocation);
                player.sendMessage(ChatColor.GREEN + " ATM status verwijderd van dit blok.");
            } else {
                player.sendMessage(ChatColor.RED + " Dit blok is geen ATM.");
            }
        }
    }
}

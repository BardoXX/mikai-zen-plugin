package com.megamc.minetopia.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }

        Player player = (Player) sender;

        player.sendMessage(ChatColor.GOLD + "=== MegaMC Minetopia Commands ===");
        player.sendMessage("");

        // ATM Commands
        if (player.hasPermission("megamc.atm")) {
            player.sendMessage(ChatColor.YELLOW + "/atm " + ChatColor.WHITE + "- Open de ATM GUI voor bank transacties");
        }

        // Pin Commands
        if (player.hasPermission("megamc.pin")) {
            player.sendMessage(ChatColor.YELLOW + "/pin send <speler> <bedrag> " + ChatColor.WHITE + "- Stuur een betaalverzoek");
            player.sendMessage(ChatColor.YELLOW + "/pin accept " + ChatColor.WHITE + "- Accepteer een ontvangen betaalverzoek");
            player.sendMessage(ChatColor.YELLOW + "/pin set " + ChatColor.WHITE + "- Plaats een pin automaat");
            player.sendMessage(ChatColor.YELLOW + "/pin remove " + ChatColor.WHITE + "- Verwijder een pin automaat");
        }

        // Vanish Command
        if (player.hasPermission("megamc.vanish")) {
            player.sendMessage(ChatColor.YELLOW + "/vanish " + ChatColor.WHITE + "- Toggle onzichtbaarheid");
        }

        // Money Commands
        if (player.hasPermission("megamc.saldo")) {
            player.sendMessage(ChatColor.YELLOW + "/saldo " + ChatColor.WHITE + "- Bekijk je huidige saldo");
        }

        if (player.hasPermission("megamc.betaal")) {
            player.sendMessage(ChatColor.YELLOW + "/betaal <speler> <bedrag> " + ChatColor.WHITE + "- Betaal geld aan een andere speler");
        }

        // Administrative Commands
        if (player.hasPermission("megamc.boete")) {
            player.sendMessage(ChatColor.YELLOW + "/boete <speler> <bedrag> <reden> " + ChatColor.WHITE + "- Geef een boete aan een speler");
        }

        if (player.hasPermission("megamc.fouilleer")) {
            player.sendMessage(ChatColor.YELLOW + "/fouilleer <speler> " + ChatColor.WHITE + "- Bekijk de inventory van een speler");
        }

        if (player.hasPermission("megamc.straf")) {
            player.sendMessage(ChatColor.YELLOW + "/straf <speler> <reden> " + ChatColor.WHITE + "- Geef een straf aan een speler");
        }

        // Business Commands
        if (player.hasPermission("megamc.contract")) {
            player.sendMessage(ChatColor.YELLOW + "/contract " + ChatColor.WHITE + "- Beheer contracten");
        }

        if (player.hasPermission("megamc.kvk")) {
            player.sendMessage(ChatColor.YELLOW + "/kvk " + ChatColor.WHITE + "- Kamer van Koophandel functies");
        }

        if (player.hasPermission("megamc.lening")) {
            player.sendMessage(ChatColor.YELLOW + "/lening " + ChatColor.WHITE + "- Lening systeem");
        }

        if (player.hasPermission("megamc.vergunning")) {
            player.sendMessage(ChatColor.YELLOW + "/vergunning " + ChatColor.WHITE + "- Vergunning systeem");
        }

        if (player.hasPermission("megamc.passport")) {
            player.sendMessage(ChatColor.YELLOW + "/passport " + ChatColor.WHITE + "- Paspoort systeem");
        }

        // Admin Commands
        if (player.hasPermission("megamc.admin")) {
            player.sendMessage(ChatColor.RED + "/megamcminetopiareload " + ChatColor.WHITE + "- Herlaad de plugin configuratie");
        }

        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "Voor meer informatie over een specifiek commando, typ het commando zonder argumenten.");

        return true;
    }
}

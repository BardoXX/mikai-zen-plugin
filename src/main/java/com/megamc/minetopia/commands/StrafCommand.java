package com.megamc.minetopia.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StrafCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }

        Player speler = (Player) sender;

        if (args.length < 2) {
            speler.sendMessage(ChatColor.RED + "Gebruik: /straf [speler] [reden]");
            return true;
        }

        String targetNaam = args[0];
        StringBuilder redenBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            redenBuilder.append(args[i]).append(" ");
        }
        String reden = redenBuilder.toString().trim();
        String datum = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        ItemStack boek = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) boek.getItemMeta();

        meta.setTitle("§4§l§oSanctie");
        meta.setAuthor("Politie Bureau");

        String pagina1 = ChatColor.DARK_RED + "" + ChatColor.BOLD + "==== MEGAMC SANCTIE ====\n\n"
                + ChatColor.DARK_GRAY + "Speler: " + ChatColor.RED + targetNaam + "\n"
                + ChatColor.DARK_GRAY + "Datum: " + ChatColor.RED + datum + "\n"
                + ChatColor.DARK_GRAY + "Reden: " + ChatColor.RED + reden + "\n"
                + ChatColor.DARK_GRAY + "-----------------------------------\n"
                + ChatColor.DARK_RED + "BELANGRIJK: Deze sanctie moet worden nageleefd!";

        meta.addPage(pagina1);
        boek.setItemMeta(meta);

        speler.getInventory().addItem(boek);
        speler.sendMessage(ChatColor.GREEN + "Je hebt een officiële sanctie voor " + targetNaam + " aangemaakt!");

        return true;
    }
}

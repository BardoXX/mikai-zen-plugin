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

public class VergunningCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }

        Player speler = (Player) sender;

        if (args.length < 2) { // only [speler] [wat]
            speler.sendMessage(ChatColor.RED + "Gebruik: /vergunning [speler] [wat]");
            return true;
        }

        String targetNaam = args[0];
        Player target = Bukkit.getPlayerExact(targetNaam);
        String wat = args[1];

        // Extra args voor langere omschrijving
        StringBuilder extra = new StringBuilder();
        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                extra.append(args[i]).append(" ");
            }
        }

        // Maak het boek
        ItemStack boek = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) boek.getItemMeta();

        meta.setTitle("§2§l§oVergunning"); // Dark green, bold, italic
        meta.setAuthor("Gemeente"); // Official author

        // Pagina 1 – Titel en speler info
        String pagina1 = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "==== MEGAMC MINETOPIA VERGUNNING ====\n\n"
                + ChatColor.DARK_AQUA + "Toegewezen aan: " + ChatColor.DARK_GRAY + targetNaam + "\n"
                + ChatColor.DARK_AQUA + "Uitgegeven door: " + ChatColor.DARK_GRAY + "Gemeente\n\n"
                + ChatColor.DARK_GRAY + "-----------------------------------";

        // Pagina 2 – Vergunning details
        String pagina2 = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "DETAILS VAN DE VERGUNNING\n\n"
                + ChatColor.DARK_AQUA + "Type: " + ChatColor.DARK_GRAY + wat + "\n"
                + (extra.length() > 0 ? ChatColor.DARK_AQUA + "Extra info: " + ChatColor.DARK_GRAY + extra.toString().trim() + "\n" : "")
                + ChatColor.DARK_GRAY + "-----------------------------------";

        // Pagina 3 – Ondertekening & regels
        String pagina3 = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "ONDERTEKENING & REGELS\n\n"
                + ChatColor.DARK_GRAY + "Door deze vergunning te accepteren, verklaart de speler zich te houden aan de regels van MegaMC Minetopia.\n\n"
                + ChatColor.DARK_AQUA + "Handtekening: " + ChatColor.DARK_GRAY + "Gemeente\n"
                + ChatColor.DARK_GRAY + "-----------------------------------\n"
                + ChatColor.RED + "" + ChatColor.BOLD + "BELANGRIJK: Misbruik kan leiden tot sancties!";

        // Voeg pagina's toe
        meta.addPage(pagina1);
        meta.addPage(pagina2);
        meta.addPage(pagina3);

        boek.setItemMeta(meta);

        // Geef het boek aan de speler die het commando uitvoert
        speler.getInventory().addItem(boek);
        speler.sendMessage(ChatColor.GREEN + "Je hebt een indrukwekkende vergunning voor " + targetNaam + " aangemaakt!");

        return true;
    }
}

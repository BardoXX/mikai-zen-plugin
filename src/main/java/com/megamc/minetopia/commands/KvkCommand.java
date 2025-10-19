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

public class KvkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }

        Player speler = (Player) sender;

        if (args.length < 1) {
            speler.sendMessage(ChatColor.RED + "Gebruik: /kvk [speler] [bedrijf optioneel] [type optioneel]");
            return true;
        }

        String targetNaam = args[0];

        // Optionele extra arguments
        String bedrijf = (args.length >= 2) ? args[1] : targetNaam + " Enterprises";
        String type = (args.length >= 3) ? args[2] : "Handel / Dienstverlening";

        // Genereer KvK nummer
        String kvkNummer = "NL" + (int) (Math.random() * 90000000 + 10000000);

        // Huidige datum
        String datum = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Maak het boek
        ItemStack boek = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) boek.getItemMeta();

        meta.setTitle("§2§l§oKvK"); // Dark green + bold + italic
        meta.setAuthor("KvK Bureau");

        // Pagina 1 – Titel & speler info
        String pagina1 = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "==== MEGAMC MINETOPIA KVK ====\n\n"
                + ChatColor.DARK_AQUA + "Speler: " + ChatColor.DARK_GRAY + targetNaam + "\n"
                + ChatColor.DARK_AQUA + "Uitgegeven door: " + ChatColor.DARK_GRAY + speler.getName() + "\n"
                + ChatColor.DARK_AQUA + "Datum: " + ChatColor.DARK_GRAY + datum + "\n"
                + ChatColor.DARK_GRAY + "-----------------------------------";

        // Pagina 2 – Bedrijf info
        String pagina2 = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "BEDRIJFSINFORMATIE\n\n"
                + ChatColor.DARK_AQUA + "KvK Nummer: " + ChatColor.DARK_GRAY + kvkNummer + "\n"
                + ChatColor.DARK_AQUA + "Bedrijfsnaam: " + ChatColor.DARK_GRAY + bedrijf + "\n"
                + ChatColor.DARK_AQUA + "Type onderneming: " + ChatColor.DARK_GRAY + type + "\n"
                + ChatColor.DARK_AQUA + "Locatie: " + ChatColor.DARK_GRAY + "MegaMC Stad\n"
                + ChatColor.DARK_GRAY + "-----------------------------------";

        // Pagina 3 – Voorwaarden & Ondertekening
        String pagina3 = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "VOORWAARDEN & REGELS\n\n"
                + ChatColor.DARK_GRAY + "Door deze registratie aan te nemen, verklaart de speler zich te houden aan de regels van MegaMC Minetopia.\n"
                + ChatColor.DARK_GRAY + "Misbruik kan leiden tot sancties of ontzegging van privileges.\n\n"
                + ChatColor.DARK_AQUA + "Handtekening: KvK Bureau\n"
                + ChatColor.DARK_GRAY + "-----------------------------------\n"
                + ChatColor.RED + "" + ChatColor.BOLD + "BELANGRIJK: Bewaar dit document zorgvuldig!";

        // Voeg pagina's toe
        meta.addPage(pagina1);
        meta.addPage(pagina2);
        meta.addPage(pagina3);

        boek.setItemMeta(meta);

        // Geef het boek aan de speler die het commando uitvoert
        speler.getInventory().addItem(boek);
        speler.sendMessage(ChatColor.GREEN + "Je hebt een indrukwekkend KvK-boek voor " + targetNaam + " aangemaakt!");

        return true;
    }
}

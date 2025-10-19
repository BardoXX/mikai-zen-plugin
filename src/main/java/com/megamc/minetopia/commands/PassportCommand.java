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

public class PassportCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }

        Player speler = (Player) sender;

        if (args.length != 1) {
            speler.sendMessage(ChatColor.RED + "Gebruik: /passport [speler]");
            return true;
        }

        String targetNaam = args[0];

        String geboorteDatum = LocalDate.now().minusYears(18 + (int)(Math.random() * 30))
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")); // Random age 18-48
        String nationaliteit = "MegaMC Burger";

        ItemStack boek = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) boek.getItemMeta();

        meta.setTitle("§2§l§oPaspoort");
        meta.setAuthor("Gemeente");

        String pagina1 = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "==== MEGAMC PASPOORT ====\n\n"
                + ChatColor.DARK_AQUA + "Naam: " + ChatColor.DARK_GRAY + targetNaam + "\n"
                + ChatColor.DARK_AQUA + "Geboortedatum: " + ChatColor.DARK_GRAY + geboorteDatum + "\n"
                + ChatColor.DARK_AQUA + "Nationaliteit: " + ChatColor.DARK_GRAY + nationaliteit + "\n"
                + ChatColor.DARK_GRAY + "-----------------------------------";

        String pagina2 = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "IDENTITEITSBEVEILIGING\n\n"
                + ChatColor.DARK_GRAY + "Dit document dient als officieel bewijs van identiteit.\n"
                + ChatColor.DARK_GRAY + "Misbruik kan leiden tot sancties.\n\n"
                + ChatColor.DARK_AQUA + "Uitgegeven door: " + ChatColor.DARK_GRAY + "Gemeente";

        meta.addPage(pagina1);
        meta.addPage(pagina2);

        boek.setItemMeta(meta);

        speler.getInventory().addItem(boek);
        speler.sendMessage(ChatColor.GREEN + "Je hebt een officieel paspoort voor " + targetNaam + " aangemaakt!");

        return true;
    }
}

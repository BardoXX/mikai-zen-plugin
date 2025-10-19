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

public class BoeteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }

        Player speler = (Player) sender;

        if (args.length < 3) {
            speler.sendMessage(ChatColor.RED + "Gebruik: /boete [speler] [bedrag] [reden]");
            return true;
        }

        String targetNaam = args[0];
        Player target = Bukkit.getPlayerExact(targetNaam);

        // Bedrag
        double bedrag;
        try {
            bedrag = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            speler.sendMessage(ChatColor.RED + "Het bedrag moet een geldig getal zijn!");
            return true;
        }

        // Reden
        StringBuilder redenBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            redenBuilder.append(args[i]).append(" ");
        }
        String reden = redenBuilder.toString().trim();

        // Datum
        String datum = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Maak boek
        ItemStack boek = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) boek.getItemMeta();

        meta.setTitle("§c§l§oBoete");
        meta.setAuthor("Politie");

        String pagina1 = ChatColor.RED + "" + ChatColor.BOLD + "==== MEGAMC BOETE ====\n\n"
                + ChatColor.DARK_GRAY + "Speler: " + ChatColor.RED + targetNaam + "\n"
                + ChatColor.DARK_GRAY + "Datum: " + ChatColor.RED + datum + "\n"
                + ChatColor.DARK_GRAY + "Bedrag: " + ChatColor.RED + "€" + bedrag + "\n"
                + ChatColor.DARK_GRAY + "Reden: " + ChatColor.RED + reden + "\n"
                + ChatColor.DARK_GRAY + "-----------------------------------\n"
                + ChatColor.RED + "De speler moet dit bedrag betalen aan de overheid!";

        meta.addPage(pagina1);
        boek.setItemMeta(meta);

        speler.getInventory().addItem(boek);
        speler.sendMessage(ChatColor.GREEN + "Je hebt een officiële boete aangemaakt voor " + targetNaam + ".");

        return true;
    }
}

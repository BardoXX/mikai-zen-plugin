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

public class LeningCommand implements CommandExecutor {

    private final double rentePercentage = 5.0; // standaard rente

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }

        Player speler = (Player) sender;

        if (args.length != 2) {
            speler.sendMessage(ChatColor.RED + "Gebruik: /lening [speler] [bedrag]");
            return true;
        }

        String targetNaam = args[0];
        Player target = Bukkit.getPlayerExact(targetNaam);
        String bedragInput = args[1];

        double bedrag;
        try {
            bedrag = Double.parseDouble(bedragInput);
        } catch (NumberFormatException e) {
            speler.sendMessage(ChatColor.RED + "Bedrag moet een getal zijn.");
            return true;
        }

        ItemStack boek = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) boek.getItemMeta();

        meta.setTitle("§2§l§oLening"); // Dark green, bold, italic
        meta.setAuthor("Bank"); // Author updated

        // Pagina 1 – Titel & Lener info
        String pagina1 = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "==== MEGAMC MINETOPIA LENING ====\n\n"
                + ChatColor.DARK_AQUA + "Lener: " + ChatColor.DARK_GRAY + targetNaam + "\n"
                + ChatColor.DARK_AQUA + "Uitgegeven door: " + ChatColor.DARK_GRAY + "Bank\n\n"
                + ChatColor.DARK_GRAY + "-----------------------------------";

        // Pagina 2 – Lening details
        double totaalTerug = bedrag + (bedrag * rentePercentage / 100);
        String pagina2 = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "DETAILS VAN DE LENING\n\n"
                + ChatColor.DARK_AQUA + "Bedrag: " + ChatColor.DARK_GRAY + "€" + bedrag + "\n"
                + ChatColor.DARK_AQUA + "Rente: " + ChatColor.DARK_GRAY + rentePercentage + "%\n"
                + ChatColor.DARK_AQUA + "Totaal terug te betalen: " + ChatColor.DARK_GRAY + "€" + totaalTerug + "\n"
                + ChatColor.DARK_GRAY + "-----------------------------------";

        // Pagina 3 – Ondertekening & voorwaarden
        String pagina3 = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "ONDERTEKENING & VOORWAARDEN\n\n"
                + ChatColor.DARK_GRAY + "Door deze lening te accepteren, verklaart de lener zich te houden aan de regels van MegaMC Minetopia.\n\n"
                + ChatColor.DARK_AQUA + "Handtekening: " + ChatColor.DARK_GRAY + "Bank\n"
                + ChatColor.DARK_GRAY + "-----------------------------------\n"
                + ChatColor.RED + "" + ChatColor.BOLD + "BELANGRIJK: Niet nakomen kan leiden tot sancties!";

        // Voeg pagina's toe
        meta.addPage(pagina1);
        meta.addPage(pagina2);
        meta.addPage(pagina3);

        boek.setItemMeta(meta);

        // Geef het boek aan de speler die het commando uitvoert
        speler.getInventory().addItem(boek);
        speler.sendMessage(ChatColor.GREEN + "Je hebt een indrukwekkende leningsovereenkomst gemaakt voor " + targetNaam + "!");

        return true;
    }
}

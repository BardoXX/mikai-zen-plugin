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

public class ContractCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }

        Player speler = (Player) sender;

        if (args.length < 3) {
            speler.sendMessage(ChatColor.RED + "Gebruik: /contract [speler] [deal] [bedrag]");
            return true;
        }

        String targetNaam = args[0];
        String deal = args[1];
        String bedrag = args[2];

        ItemStack boek = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) boek.getItemMeta();

        meta.setTitle("§2§l§oContract");
        meta.setAuthor("ContractHouder");

        String pagina1 = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "==== MEGAMC CONTRACT ====\n\n"
                + ChatColor.DARK_AQUA + "Partijen: " + ChatColor.DARK_GRAY + speler.getName() + " & " + targetNaam + "\n"
                + ChatColor.DARK_AQUA + "Deal: " + ChatColor.DARK_GRAY + deal + "\n"
                + ChatColor.DARK_AQUA + "Bedrag: " + ChatColor.DARK_GRAY + "€" + bedrag + "\n"
                + ChatColor.DARK_GRAY + "-----------------------------------\n"
                + ChatColor.DARK_GREEN + "Door ondertekening gaat elke partij akkoord met de voorwaarden.";

        meta.addPage(pagina1);
        boek.setItemMeta(meta);

        speler.getInventory().addItem(boek);
        speler.sendMessage(ChatColor.GREEN + "Je hebt een officieel contract voor " + targetNaam + " aangemaakt!");

        return true;
    }
}

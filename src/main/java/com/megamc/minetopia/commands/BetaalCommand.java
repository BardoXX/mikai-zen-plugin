package com.megamc.minetopia.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BetaalCommand implements CommandExecutor {

    private final Economy econ;

    public BetaalCommand(Economy econ) {
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }

        Player speler = (Player) sender;

        if (args.length != 2) {
            speler.sendMessage(ChatColor.RED + "Gebruik: /betaal [speler] [bedrag]");
            return true;
        }

        Player target = sender.getServer().getPlayerExact(args[0]);
        if (target == null) {
            speler.sendMessage(ChatColor.RED + "Speler niet gevonden.");
            return true;
        }

        double bedrag;
        try {
            bedrag = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            speler.sendMessage(ChatColor.RED + "Bedrag moet een getal zijn.");
            return true;
        }

        if (econ.getBalance(speler) < bedrag) {
            speler.sendMessage(ChatColor.RED + "Je hebt niet genoeg geld.");
            return true;
        }

        econ.withdrawPlayer(speler, bedrag);
        econ.depositPlayer(target, bedrag);

        speler.sendMessage(ChatColor.GREEN + "Je hebt €" + bedrag + " betaald aan " + target.getName());
        target.sendMessage(ChatColor.GREEN + "Je hebt €" + bedrag + " ontvangen van " + speler.getName());

        return true;
    }
}

package com.megamc.minetopia.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaldoCommand implements CommandExecutor {

    private final Economy econ;

    public SaldoCommand(Economy econ) {
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }

        Player speler = (Player) sender;
        double saldo = econ.getBalance(speler);
        speler.sendMessage(ChatColor.GREEN + "Je huidige saldo: €" + saldo);
        return true;
    }
}

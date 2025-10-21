package com.megamc.minetopia.commands;

import com.megamc.minetopia.Minetopia;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

    private final Minetopia plugin;

    public ReloadCommand(Minetopia plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Only OP players or console can use this
        if (sender instanceof Player player && !player.isOp()) {
            sender.sendMessage("§cJe hebt geen toestemming om dit commando te gebruiken!");
            return true;
        }

        sender.sendMessage("§aMegaMCMinetopia configuratie wordt herladen...");

        try {
            // Herlaad de plugin configuratie
            plugin.reloadConfig();
            plugin.saveDefaultConfig();

            // Herlaad scoreboard en tablist configuratie
            plugin.reloadScoreboardTablistConfig();

            // Herlaad ATM en PIN blokken
            plugin.loadATMBlocks();
            plugin.loadPinBlocks();

            sender.sendMessage("§aMegaMCMinetopia configuratie succesvol herladen!");
            sender.sendMessage("§7- Scoreboard configuratie herladen");
            sender.sendMessage("§7- Tablist configuratie herladen");
            sender.sendMessage("§7- Chat kleuren configuratie herladen");
            sender.sendMessage("§7- ATM blokken herladen");
            sender.sendMessage("§7- PIN blokken herladen");

        } catch (Exception e) {
            sender.sendMessage("§cEr is een fout opgetreden bij het herladen: " + e.getMessage());
            plugin.getLogger().severe("Failed to reload configuration: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}

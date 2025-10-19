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
            sender.sendMessage("§cYou do not have permission to use this command!");
            return true;
        }

        sender.sendMessage("§aMegaMCMinetopia configuration reloaded successfully!");

        return true;
    }
}

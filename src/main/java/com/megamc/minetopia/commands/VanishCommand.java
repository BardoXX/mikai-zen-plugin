package com.megamc.minetopia.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashSet;
import java.util.Set;

public class VanishCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Set<Player> vanishedPlayers = new HashSet<>();

    public VanishCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (!player.hasPermission("megamc.vanish")) {
            player.sendMessage("§cYou do not have permission to use this command!");
            return true;
        }

        if (vanishedPlayers.contains(player)) {
            // Remove vanish
            vanishedPlayers.remove(player);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showPlayer(plugin, player);
            }
            player.sendMessage("§aYou are now visible!");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
        } else {
            // Enable vanish
            vanishedPlayers.add(player);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.equals(player)) p.hidePlayer(plugin, player);
            }
            player.sendMessage("§6You are now in vanish mode!");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6You are in vanish mode"));
        }

        return true;
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player);
    }
}

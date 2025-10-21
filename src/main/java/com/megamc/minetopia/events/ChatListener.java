package com.megamc.minetopia.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatListener implements Listener {

    private final JavaPlugin plugin;

    public ChatListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!plugin.getConfig().getBoolean("chat.enabled", true)) {
            return;
        }

        Player player = event.getPlayer();
        String message = event.getMessage();

        // Bepaal de rank van de speler op basis van permissies
        String rank = getPlayerRank(player);

        // Haal kleuren uit config
        String rankColor = plugin.getConfig().getString("chat.kleuren.rank." + rank, "&7");
        String naamColor = plugin.getConfig().getString("chat.kleuren.naam", "&f");
        String berichtColor = plugin.getConfig().getString("chat.kleuren.bericht", "&f");
        String timestampColor = plugin.getConfig().getString("chat.kleuren.timestamp", "&8");

        // Haal format uit config
        String format = plugin.getConfig().getString("chat.format",
            "[&8%timestamp%&f] %rank% %naam%&f: %bericht%");

        // Genereer timestamp
        String timestamp = new SimpleDateFormat("HH:mm").format(new Date());

        // Zet color codes om naar daadwerkelijke kleuren
        rankColor = ChatColor.translateAlternateColorCodes('&', rankColor);
        naamColor = ChatColor.translateAlternateColorCodes('&', naamColor);
        berichtColor = ChatColor.translateAlternateColorCodes('&', berichtColor);
        timestampColor = ChatColor.translateAlternateColorCodes('&', timestampColor);
        format = ChatColor.translateAlternateColorCodes('&', format);

        // Vervang placeholders in format
        String formattedMessage = format
            .replace("%timestamp%", timestampColor + timestamp)
            .replace("%rank%", rankColor + rank.toUpperCase())
            .replace("%naam%", naamColor + player.getName())
            .replace("%bericht%", berichtColor + message);

        // Stel het geformatteerde bericht in
        event.setFormat(formattedMessage);
    }

    private String getPlayerRank(Player player) {
        // Controleer permissies om rank te bepalen (van hoogste naar laagste)
        if (player.hasPermission("megamc.owner")) return "owner";
        if (player.hasPermission("megamc.admin")) return "admin";
        if (player.hasPermission("megamc.moderator")) return "moderator";
        if (player.hasPermission("megamc.helper")) return "helper";
        if (player.hasPermission("megamc.vip")) return "vip";

        return "player"; // Default rank
    }
}

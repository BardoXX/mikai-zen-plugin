package com.megamc.minetopia.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HelpCommand implements CommandExecutor {

    private final Map<String, String> commandDescriptions = new HashMap<>();

    public HelpCommand() {
        initializeCommands();
    }

    private void initializeCommands() {
        commandDescriptions.put("atm", "Open de ATM GUI voor bank transacties.");
        commandDescriptions.put("betaal", "Betaal een andere speler een bedrag.");
        commandDescriptions.put("boete", "Geef een boete aan een speler.");
        commandDescriptions.put("contract", "Beheer contracten.");
        commandDescriptions.put("fouilleer", "Fouilleer een speler.");
        commandDescriptions.put("help", "Toon deze help pagina.");
        commandDescriptions.put("kvk", "Beheer KVK (Kamer van Koophandel).");
        commandDescriptions.put("lening", "Beheer leningen.");
        commandDescriptions.put("passport", "Beheer paspoorten.");
        commandDescriptions.put("pin", "Stuur of accepteer betaalverzoeken.");
        commandDescriptions.put("saldo", "Bekijk je huidige saldo.");
        commandDescriptions.put("straf", "Geef een straf aan een speler.");
        commandDescriptions.put("vanish", "Maak jezelf onzichtbaar.");
        commandDescriptions.put("vergunning", "Beheer vergunningen.");
        commandDescriptions.put("megamcminetopiareload", "Herlaad de plugin configuratie.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }

        Player player = (Player) sender;

        player.sendMessage(ChatColor.GOLD + "=== Minetopia Help ===");

        for (Map.Entry<String, String> entry : commandDescriptions.entrySet()) {
            player.sendMessage(ChatColor.YELLOW + "/" + entry.getKey() + ChatColor.WHITE + " - " + entry.getValue());
        }

        player.sendMessage(ChatColor.GOLD + "====================");
        return true;
    }

    // Methode om nieuwe commands toe te voegen (voor toekomstige uitbreiding)
    public void addCommand(String command, String description) {
        commandDescriptions.put(command, description);
    }
}

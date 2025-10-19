package com.megamc.minetopia.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PinCommand implements CommandExecutor {

    private final Economy econ;
    private final Set<Location> pinBlocks;

    private final Map<UUID, Map<UUID, Double>> pinRequests = new HashMap<>(); // Ontvanger -> (Verzender -> Bedrag)

    public PinCommand(Economy econ, Set<Location> pinBlocks) {
        this.econ = econ;
        this.pinBlocks = pinBlocks;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Gebruik: /pin send <speler> <bedrag>, /pin accept, /pin set, of /pin remove");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
            handleSet(player);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
            handleRemove(player);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("accept")) {
            handleAccept(player);
            return true;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("send")) {
            handleSend(player, args[1], args[2]);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Ongeldig gebruik. Gebruik: /pin send <speler> <bedrag>, /pin accept, /pin set, of /pin remove");
        return true;
    }

    private void handleSend(Player sender, String targetName, String amountStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Speler " + targetName + " is niet online.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr.replace(",", "."));
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Ongeldig bedrag.");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Bedrag moet groter dan 0 zijn.");
            return;
        }

        if (econ.getBalance(sender) < amount) {
            sender.sendMessage(ChatColor.RED + "Je hebt niet genoeg geld om €" + amount + " te sturen.");
            return;
        }

        // Voeg verzoek toe
        pinRequests.computeIfAbsent(target.getUniqueId(), k -> new HashMap<>()).put(sender.getUniqueId(), amount);

        sender.sendMessage(ChatColor.GREEN + "Betaalverzoek van €" + amount + " gestuurd naar " + targetName + ".");
        target.sendMessage(ChatColor.YELLOW + sender.getName() + " heeft een betaalverzoek gestuurd van €" + amount + ". Typ /pin accept om te accepteren.");
    }

    public void handleAccept(Player player) {
        Map<UUID, Double> requests = pinRequests.get(player.getUniqueId());
        if (requests == null || requests.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Je hebt geen openstaande betaalverzoeken.");
            return;
        }

        // Neem het laatste verzoek (voor eenvoud, kan uitgebreid worden)
        UUID senderUUID = requests.keySet().iterator().next();
        double amount = requests.get(senderUUID);

        Player sender = Bukkit.getPlayer(senderUUID);
        if (sender == null) {
            player.sendMessage(ChatColor.RED + "De verzender is niet meer online.");
            pinRequests.get(player.getUniqueId()).remove(senderUUID);
            return;
        }

        if (econ.getBalance(sender) < amount) {
            player.sendMessage(ChatColor.RED + "De verzender heeft niet genoeg geld meer.");
            pinRequests.get(player.getUniqueId()).remove(senderUUID);
            return;
        }

        // Voer transactie uit
        econ.withdrawPlayer(sender, amount);
        econ.depositPlayer(player, amount);

        player.sendMessage(ChatColor.GREEN + "Je hebt €" + amount + " ontvangen van " + sender.getName() + ".");
        sender.sendMessage(ChatColor.YELLOW + "Je hebt €" + amount + " betaald aan " + player.getName() + ".");

        // Verwijder verzoek
        pinRequests.get(player.getUniqueId()).remove(senderUUID);
    }

    private void handleSet(Player player) {
        if (!player.hasPermission("megamc.pin.set")) {
            player.sendMessage(ChatColor.RED + "❌ Je hebt geen toestemming om pin automaten te plaatsen.");
            return;
        }

        Block targetBlock = player.getTargetBlock(null, 5);
        if (targetBlock == null) {
            player.sendMessage(ChatColor.RED + "❌ Je moet naar een blok kijken om een pin automaat te plaatsen.");
            return;
        }

        Location blockLocation = targetBlock.getLocation();

        if (pinBlocks.contains(blockLocation)) {
            player.sendMessage(ChatColor.RED + "❌ Dit blok is al een pin automaat.");
            return;
        }

        pinBlocks.add(blockLocation);
        player.sendMessage(ChatColor.GREEN + "✅ Pin automaat ingesteld op dit blok!");
        player.sendMessage(ChatColor.GRAY + "Spelers kunnen nu rechtsklikken om betaalverzoeken te accepteren.");
    }

    private void handleRemove(Player player) {
        if (!player.hasPermission("megamc.pin.set")) {
            player.sendMessage(ChatColor.RED + "❌ Je hebt geen toestemming om pin automaten te verwijderen.");
            return;
        }

        Block targetBlock = player.getTargetBlock(null, 5);
        if (targetBlock == null) {
            player.sendMessage(ChatColor.RED + "❌ Je moet naar een pin automaat kijken om deze te verwijderen.");
            return;
        }

        Location blockLocation = targetBlock.getLocation();

        if (!pinBlocks.contains(blockLocation)) {
            player.sendMessage(ChatColor.RED + "❌ Dit blok is geen pin automaat.");
            return;
        }

        pinBlocks.remove(blockLocation);
        player.sendMessage(ChatColor.GREEN + "✅ Pin automaat verwijderd van dit blok!");
    }
}

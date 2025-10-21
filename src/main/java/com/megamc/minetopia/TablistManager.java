package com.megamc.minetopia;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TablistManager {

    private final Minetopia plugin;
    private BukkitTask updateTask;

    private boolean enabled;
    private int updateInterval;
    private List<String> header;
    private List<String> footer;

    public TablistManager(Minetopia plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        this.enabled = plugin.getConfig().getBoolean("tablist.enabled", true);
        this.updateInterval = plugin.getConfig().getInt("tablist.update-interval", 40);
        this.header = plugin.getConfig().getStringList("tablist.header");
        this.footer = plugin.getConfig().getStringList("tablist.footer");

        if (enabled) {
            startUpdateTask();
        } else {
            stopUpdateTask();
        }
    }

    private void startUpdateTask() {
        stopUpdateTask(); // Stop bestaande task

        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> updateTablist(), updateInterval, updateInterval);
    }

    private void stopUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }

    public void updateTablist() {
        if (!enabled) return;

        String headerText = processTablistText(header);
        String footerText = processTablistText(footer);

        // Verstuur header en footer naar alle spelers
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                player.setPlayerListHeaderFooter(headerText, footerText);
            } catch (NoSuchMethodError e) {
                // Fallback voor oudere Bukkit versies zonder deze API
                // In dat geval kunnen we niets doen, maar we voorkomen crashes
            }
        }
    }

    public String processTablistText(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                result.append("\n");
            }
            result.append(processPlaceholders(lines.get(i)));
        }

        return result.toString();
    }

    private String processPlaceholders(String text) {
        // Server-wide placeholders
        text = text.replace("%server_online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        text = text.replace("%server_max_players%", String.valueOf(Bukkit.getMaxPlayers()));

        // Verwerk kleuren en gradients
        text = translateColors(text);

        return text;
    }

    private String translateColors(String text) {
        // Eerst gradients en standalone hex kleuren verwerken (deze kunnen andere kleuren bevatten)
        text = processGradientsAndStandaloneHex(text);

        // Dan normale kleuren vertalen
        text = ChatColor.translateAlternateColorCodes('&', text);

        return text;
    }

    private String processGradientsAndStandaloneHex(String text) {
        // Ondersteuning voor MiniMessage gradient tags <gradient:#RRGGBB:#RRGGBB>tekst</gradient>
        Pattern miniMessageGradientPattern = Pattern.compile("<gradient:#([0-9A-Fa-f]{3,6}):#([0-9A-Fa-f]{3,6})>(.+?)</gradient>");
        Matcher miniMessageMatcher = miniMessageGradientPattern.matcher(text);

        while (miniMessageMatcher.find()) {
            String startColor = miniMessageMatcher.group(1);
            String endColor = miniMessageMatcher.group(2);
            String gradientText = miniMessageMatcher.group(3);

            String gradient = createGradient(gradientText, startColor, endColor);
            text = text.replace(miniMessageMatcher.group(0), gradient);
        }

        // Ondersteuning voor gradient tags {#RRGGBB>tekst<#RRGGBB}
        Pattern gradientPattern = Pattern.compile("\\{#([0-9A-Fa-f]{6})>(.+?)<#([0-9A-Fa-f]{6})\\}");
        Matcher matcher = gradientPattern.matcher(text);

        while (matcher.find()) {
            String startColor = matcher.group(1);
            String gradientText = matcher.group(2);
            String endColor = matcher.group(3);

            String gradient = createGradient(gradientText, startColor, endColor);
            text = text.replace(matcher.group(0), gradient);
        }

        // Ondersteuning voor standalone hex kleuren {#RRGGBB}
        Pattern standaloneHexPattern = Pattern.compile("\\{#([0-9A-Fa-f]{6})\\}");
        Matcher hexMatcher = standaloneHexPattern.matcher(text);

        while (hexMatcher.find()) {
            String hexColor = hexMatcher.group(1);
            String minecraftColor = convertHexToMinecraft(hexColor);
            text = text.replace(hexMatcher.group(0), minecraftColor);
        }

        // Ondersteuning voor &# kleuren
        Pattern hashHexPattern = Pattern.compile("&#([0-9A-Fa-f]{6})");
        Matcher hashHexMatcher = hashHexPattern.matcher(text);

        while (hashHexMatcher.find()) {
            String hexColor = hashHexMatcher.group(1);
            if (isValidHexColor(hexColor)) {
                String minecraftColor = convertHexToMinecraft(hexColor);
                text = text.replace(hashHexMatcher.group(0), minecraftColor);
            } else {
                // Invalid color, keep original for debugging
                // text = text.replace(hashHexMatcher.group(0), hashHexMatcher.group(0));
            }
        }

        return text;
    }

    private String convertHexToMinecraft(String hexColor) {
        if (hexColor.length() != 6) return "";

        try {
            int r = Integer.valueOf(hexColor.substring(0, 2), 16);
            int g = Integer.valueOf(hexColor.substring(2, 4), 16);
            int b = Integer.valueOf(hexColor.substring(4, 6), 16);

            return String.format("§x§%s§%s§%s§%s§%s§%s",
                    hexColor.charAt(0), hexColor.charAt(1),
                    hexColor.charAt(2), hexColor.charAt(3),
                    hexColor.charAt(4), hexColor.charAt(5));
        } catch (NumberFormatException e) {
            return "";
        }
    }

    private boolean isValidHexColor(String hexColor) {
        if (hexColor == null || hexColor.length() != 6) return false;

        try {
            Integer.valueOf(hexColor, 16);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String createGradient(String text, String startHex, String endHex) {
        try {
            // Converteer hex kleuren naar RGB
            int startR = Integer.valueOf(startHex.substring(0, 2), 16);
            int startG = Integer.valueOf(startHex.substring(2, 4), 16);
            int startB = Integer.valueOf(startHex.substring(4, 6), 16);

            int endR = Integer.valueOf(endHex.substring(0, 2), 16);
            int endG = Integer.valueOf(endHex.substring(2, 4), 16);
            int endB = Integer.valueOf(endHex.substring(4, 6), 16);

            StringBuilder result = new StringBuilder();

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c == ' ') {
                    result.append(' ');
                    continue;
                }

                // Bereken gradient stap
                float ratio = (float) i / Math.max(1, text.length() - 1);

                int r = (int) (startR + (endR - startR) * ratio);
                int g = (int) (startG + (endG - startG) * ratio);
                int b = (int) (startB + (endB - startB) * ratio);

                // Converteer naar hex en maak ChatColor
                String hexColor = String.format("%02X%02X%02X", r, g, b);
                result.append("§x§").append(hexColor.charAt(0)).append("§").append(hexColor.charAt(1))
                      .append("§").append(hexColor.charAt(2)).append("§").append(hexColor.charAt(3))
                      .append("§").append(hexColor.charAt(4)).append("§").append(hexColor.charAt(5))
                      .append(c);
            }

            return result.toString();
        } catch (Exception e) {
            // Bij fouten, val terug op originele tekst
            return text;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            startUpdateTask();
            updateTablist(); // Directe update
        } else {
            stopUpdateTask();
        }
    }

    public void reload() {
        loadConfig();
    }

    public List<String> getHeader() {
        return header;
    }

    public List<String> getFooter() {
        return footer;
    }
}

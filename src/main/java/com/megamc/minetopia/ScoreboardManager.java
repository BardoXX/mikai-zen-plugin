package com.megamc.minetopia;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreboardManager {

    private final Minetopia plugin;
    private final Map<UUID, Scoreboard> playerScoreboards = new HashMap<>();
    private final Map<UUID, Object> updateTasks = new HashMap<>();

    private boolean enabled;
    private String title;
    private int updateInterval;
    private List<String> lines;

    private boolean showHealth;
    private boolean showHunger;
    private boolean showExperience;
    private boolean showTps;
    private boolean showPing;
    private boolean showWorld;
    private boolean showCoordinates;

    private boolean enableHexColors;
    private boolean enableRgbColors;
    private boolean enableNamedColors;
    private boolean enableGradients;

    private boolean enableScrollingText;
    private int scrollSpeed;

    private String titleColor;
    private int lineSpacing;

    public boolean isEnabled() {
        return enabled;
    }

    public ScoreboardManager(Minetopia plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        this.enabled = plugin.getConfig().getBoolean("scoreboard.enabled", true);
        this.title = plugin.getConfig().getString("scoreboard.title", "&6&lMinetopia");
        this.updateInterval = plugin.getConfig().getInt("scoreboard.update-interval", 20);
        this.lines = plugin.getConfig().getStringList("scoreboard.lines");

        // Load new features
        this.showHealth = plugin.getConfig().getBoolean("scoreboard.features.show-health", true);
        this.showHunger = plugin.getConfig().getBoolean("scoreboard.features.show-hunger", true);
        this.showExperience = plugin.getConfig().getBoolean("scoreboard.features.show-experience", true);
        this.showTps = plugin.getConfig().getBoolean("scoreboard.features.show-tps", true);
        this.showPing = plugin.getConfig().getBoolean("scoreboard.features.show-ping", true);
        this.showWorld = plugin.getConfig().getBoolean("scoreboard.features.show-world", true);
        this.showCoordinates = plugin.getConfig().getBoolean("scoreboard.features.show-coordinates", false);

        // Load color settings
        this.enableHexColors = plugin.getConfig().getBoolean("scoreboard.colors.enable-hex-colors", true);
        this.enableRgbColors = plugin.getConfig().getBoolean("scoreboard.colors.enable-rgb-colors", true);
        this.enableNamedColors = plugin.getConfig().getBoolean("scoreboard.colors.enable-named-colors", true);
        this.enableGradients = plugin.getConfig().getBoolean("scoreboard.colors.enable-gradients", true);

        // Load animation settings
        this.enableScrollingText = plugin.getConfig().getBoolean("scoreboard.animations.enable-scrolling-text", false);
        this.scrollSpeed = plugin.getConfig().getInt("scoreboard.animations.scroll-speed", 2);

        // Load styling settings
        this.titleColor = plugin.getConfig().getString("scoreboard.styling.title-color", "&6");
        this.lineSpacing = plugin.getConfig().getInt("scoreboard.styling.line-spacing", 1);

        // Herlaad scoreboards voor alle online spelers
        if (enabled) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                setupPlayer(player);
                startUpdateTask(player);
            }
        } else {
            clearAllScoreboards();
        }
    }

    public void setupPlayer(Player player) {
        if (!enabled || lines == null || title == null) return;

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("minetopia", "dummy", title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        playerScoreboards.put(player.getUniqueId(), scoreboard);

        // Voeg lege teams toe voor alle lijnen
        for (int i = 0; i < lines.size(); i++) {
            Team team = scoreboard.registerNewTeam("line" + i);
            team.addEntry("§" + i); // Gebruik section sign voor unieke entries
        }

        updateScoreboard(player);
        player.setScoreboard(scoreboard);
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();

        // Stop update task
        updateTasks.remove(uuid);

        // Verwijder scoreboard
        playerScoreboards.remove(uuid);

        // Zet standaard scoreboard terug
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void updateScoreboard(Player player) {
        if (!enabled || !playerScoreboards.containsKey(player.getUniqueId()) || lines == null) return;

        Scoreboard scoreboard = playerScoreboards.get(player.getUniqueId());

        for (int i = 0; i < lines.size(); i++) {
            String line = processPlaceholders(player, lines.get(i));

            Team team = scoreboard.getTeam("line" + i);
            if (team != null) {
                team.setPrefix(line);
                Objective objective = scoreboard.getObjective("minetopia");
                if (objective != null) {
                    objective.getScore("§" + i).setScore(lines.size() - i);
                }
            }
        }
    }

    private String processPlaceholders(Player player, String text) {
        // Verwerk kleuren en gradients eerst
        text = translateColors(text);

        // Vervang placeholders
        text = text.replace("%player_name%", player.getName());
        text = text.replace("%player_level%", String.valueOf(player.getLevel()));
        text = text.replace("%server_online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        text = text.replace("%server_max_players%", String.valueOf(Bukkit.getMaxPlayers()));

        // Nieuwe placeholders
        if (showHealth) {
            text = text.replace("%player_health%", String.valueOf((int) player.getHealth()));
        }
        if (showHunger) {
            text = text.replace("%player_hunger%", String.valueOf(player.getFoodLevel()));
        }
        if (showExperience) {
            text = text.replace("%player_experience%", String.valueOf(player.getTotalExperience()));
        }
        if (showPing) {
            text = text.replace("%player_ping%", String.valueOf(player.getPing()));
        }
        if (showWorld) {
            text = text.replace("%player_world%", player.getWorld().getName());
        }
        if (showCoordinates) {
            text = text.replace("%player_x%", String.valueOf(player.getLocation().getBlockX()));
            text = text.replace("%player_y%", String.valueOf(player.getLocation().getBlockY()));
            text = text.replace("%player_z%", String.valueOf(player.getLocation().getBlockZ()));
        }
        if (showTps) {
            // Simple TPS calculation (you might want to use a more accurate method)
            text = text.replace("%server_tps%", "20.0");
        }

        // Economy balance (Vault)
        if (Minetopia.getEconomy() != null) {
            double balance = Minetopia.getEconomy().getBalance(player);
            text = text.replace("%vault_eco_balance_formatted%", String.format("%.2f", balance));
        } else {
            text = text.replace("%vault_eco_balance_formatted%", "0.00");
        }

        return text;
    }

private String translateColors(String text) {
        if (text == null || text.isEmpty()) return text;
        
        // Processing order: gradients -> standalone colors -> color formats -> standard Minecraft colors
        
        // 1. Process gradients first (highest priority)
        if (enableGradients) {
            text = processGradients(text);
        }
        
        // 2. Process standalone hex colors {#RRGGBB}
        if (enableHexColors) {
            text = processStandaloneHex(text);
        }
        
        // 3. Process RGB colors rgb(r,g,b)
        if (enableRgbColors) {
            text = translateRgbColors(text);
        }
        
        // 4. Process HSL colors hsl(hue,sat%,light%)
        text = translateHslColors(text);
        
        // 5. Process hex colors with &# prefix
        if (enableHexColors) {
            text = translateHexColors(text);
        }
        
        // 6. Process named colors (&red, &blue, etc.)
        if (enableNamedColors) {
            text = translateNamedColors(text);
        }
        
        // 7. Finally, process standard Minecraft color codes (&0-&f, &k-&r)
        text = ChatColor.translateAlternateColorCodes('&', text);
        
        return text;
    }

    private String processGradients(String text) {
        // Improved gradient pattern: {#RRGGBB>text<#RRGGBB}
        // Also support short forms: {#RGB>text<#RGB}
        Pattern gradientPattern = Pattern.compile("\\{#([0-9A-Fa-f]{3,6})>([^<]+)<#([0-9A-Fa-f]{3,6})\\}");
        Matcher matcher = gradientPattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String startColor = matcher.group(1);
            String gradientText = matcher.group(2);
            String endColor = matcher.group(3);

            // Validate and normalize hex colors
            String normalizedStartColor = normalizeHexColor(startColor);
            String normalizedEndColor = normalizeHexColor(endColor);

            if (normalizedStartColor != null && normalizedEndColor != null) {
                String gradient = createGradient(gradientText, normalizedStartColor, normalizedEndColor);
                matcher.appendReplacement(result, Matcher.quoteReplacement(gradient));
            } else {
                // Invalid colors, skip this gradient
                matcher.appendReplacement(result, matcher.group(0));
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private String processStandaloneHex(String text) {
        // Support both 3-digit and 6-digit hex: {#RGB} or {#RRGGBB}
        Pattern standaloneHexPattern = Pattern.compile("\\{#([0-9A-Fa-f]{3,6})\\}");
        Matcher matcher = standaloneHexPattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            String normalizedColor = normalizeHexColor(hexColor);

            if (normalizedColor != null) {
                String minecraftColor = convertHexToMinecraft(normalizedColor);
                matcher.appendReplacement(result, Matcher.quoteReplacement(minecraftColor));
            } else {
                // Invalid color, remove it
                matcher.appendReplacement(result, "");
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private String translateRgbColors(String text) {
        // Pattern: rgb(r,g,b) of rgb(r, g, b) - spaties zijn optioneel
        Pattern rgbPattern = Pattern.compile("rgb\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");
        Matcher matcher = rgbPattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            try {
                int r = Math.max(0, Math.min(255, Integer.parseInt(matcher.group(1))));
                int g = Math.max(0, Math.min(255, Integer.parseInt(matcher.group(2))));
                int b = Math.max(0, Math.min(255, Integer.parseInt(matcher.group(3))));

                String hexColor = String.format("%02X%02X%02X", r, g, b);
                String minecraftColor = convertHexToMinecraft(hexColor);
                matcher.appendReplacement(result, Matcher.quoteReplacement(minecraftColor));
            } catch (NumberFormatException e) {
                // Bij fout, behoud originele tekst
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private String translateHexColors(String text) {
        // Support both 3-digit and 6-digit hex with &# prefix: &#RGB or &#RRGGBB
        Pattern hexPattern = Pattern.compile("&#([0-9A-Fa-f]{3,6})");
        Matcher matcher = hexPattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            String normalizedColor = normalizeHexColor(hexColor);

            if (normalizedColor != null) {
                String minecraftColor = convertHexToMinecraft(normalizedColor);
                matcher.appendReplacement(result, Matcher.quoteReplacement(minecraftColor));
            } else {
                // Invalid color, remove it
                matcher.appendReplacement(result, "");
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private String normalizeHexColor(String hexColor) {
        if (hexColor == null) return null;

        // Remove # if present
        if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1);
        }

        // Handle 3-digit hex colors (#RGB -> #RRGGBB)
        if (hexColor.length() == 3) {
            char r = hexColor.charAt(0);
            char g = hexColor.charAt(1);
            char b = hexColor.charAt(2);
            hexColor = "" + r + r + g + g + b + b;
        }

        // Validate 6-digit hex
        if (hexColor.length() != 6) return null;

        try {
            Integer.valueOf(hexColor, 16);
            return hexColor;
        } catch (NumberFormatException e) {
            return null;
        }
    }

private String translateHslColors(String text) {
    // Pattern: hsl(hue, saturation%, lightness%)
    Pattern hslPattern = Pattern.compile("hsl\\(\\s*(\\d+)\\s*,\\s*(\\d+)%\\s*,\\s*(\\d+)%\\s*\\)");
    Matcher matcher = hslPattern.matcher(text);
    StringBuffer result = new StringBuffer();

    while (matcher.find()) {
        try {
            int hue = Integer.parseInt(matcher.group(1)) % 360; // Wrap around
            int saturation = Math.max(0, Math.min(100, Integer.parseInt(matcher.group(2))));
            int lightness = Math.max(0, Math.min(100, Integer.parseInt(matcher.group(3))));

            String hexColor = hslToHex(hue, saturation, lightness);
            String minecraftColor = convertHexToMinecraft(hexColor);
            matcher.appendReplacement(result, Matcher.quoteReplacement(minecraftColor));
        } catch (NumberFormatException e) {
            // Bij fout, behoud originele tekst
        }
    }
    
    matcher.appendTail(result);
    return result.toString();
}

private String translateNamedColors(String text) {
    // Map van named colors naar hex
    Map<String, String> colorMap = new HashMap<>();
    colorMap.put("red", "FF0000");
    colorMap.put("green", "00FF00");
    colorMap.put("blue", "0000FF");
    colorMap.put("yellow", "FFFF00");
    colorMap.put("cyan", "00FFFF");
    colorMap.put("magenta", "FF00FF");
    colorMap.put("white", "FFFFFF");
    colorMap.put("black", "000000");
    colorMap.put("gray", "808080");
    colorMap.put("grey", "808080");
    colorMap.put("orange", "FFA500");
    colorMap.put("purple", "800080");
    colorMap.put("pink", "FFC0CB");
    colorMap.put("brown", "A52A2A");
    colorMap.put("lime", "00FF00");
    colorMap.put("navy", "000080");
    colorMap.put("teal", "008080");
    colorMap.put("silver", "C0C0C0");
    colorMap.put("gold", "FFD700");

    // Pattern: &namedcolor (alleen met ampersand)
    for (Map.Entry<String, String> entry : colorMap.entrySet()) {
        String namedColor = entry.getKey();
        String hexColor = entry.getValue();
        String minecraftColor = convertHexToMinecraft(hexColor);

        // Alleen &namedcolor format (case-insensitive)
        Pattern pattern = Pattern.compile("&" + namedColor + "(?![a-zA-Z])", Pattern.CASE_INSENSITIVE);
        text = pattern.matcher(text).replaceAll(Matcher.quoteReplacement(minecraftColor));
    }

    return text;
}

private String hslToHex(int hue, int saturation, int lightness) {
    float h = hue / 360.0f;
    float s = saturation / 100.0f;
    float l = lightness / 100.0f;

    float c = (1 - Math.abs(2 * l - 1)) * s;
    float x = c * (1 - Math.abs(((h * 6) % 2) - 1));
    float m = l - c / 2;

    float r = 0, g = 0, b = 0;

    if (h < 1.0f/6) {
        r = c; g = x; b = 0;
    } else if (h < 2.0f/6) {
        r = x; g = c; b = 0;
    } else if (h < 3.0f/6) {
        r = 0; g = c; b = x;
    } else if (h < 4.0f/6) {
        r = 0; g = x; b = c;
    } else if (h < 5.0f/6) {
        r = x; g = 0; b = c;
    } else {
        r = c; g = 0; b = x;
    }

    int red = Math.round((r + m) * 255);
    int green = Math.round((g + m) * 255);
    int blue = Math.round((b + m) * 255);

    // Clamp values
    red = Math.max(0, Math.min(255, red));
    green = Math.max(0, Math.min(255, green));
    blue = Math.max(0, Math.min(255, blue));

    return String.format("%02X%02X%02X", red, green, blue);
}

private String convertHexToMinecraft(String hexColor) {
    if (hexColor == null || hexColor.length() != 6) return "";

    try {
        // Validate hex
        Integer.valueOf(hexColor, 16);
        
        // Convert to Minecraft format: §x§R§R§G§G§B§B
        return String.format("§x§%c§%c§%c§%c§%c§%c",
                hexColor.charAt(0), hexColor.charAt(1),
                hexColor.charAt(2), hexColor.charAt(3),
                hexColor.charAt(4), hexColor.charAt(5));
    } catch (NumberFormatException e) {
        return "";
    }
}

private void clearAllScoreboards() {
    for (Player player : Bukkit.getOnlinePlayers()) {
        removePlayer(player);
    }
}

private void startUpdateTask(Player player) {
    // For now, we'll skip the scheduler - just update once
    updateScoreboard(player);
}

private String createGradient(String text, String startHex, String endHex) {
    if (text == null || text.isEmpty()) return text;
    
    try {
        // Parse start color
        int startR = Integer.valueOf(startHex.substring(0, 2), 16);
        int startG = Integer.valueOf(startHex.substring(2, 4), 16);
        int startB = Integer.valueOf(startHex.substring(4, 6), 16);

        // Parse end color
        int endR = Integer.valueOf(endHex.substring(0, 2), 16);
        int endG = Integer.valueOf(endHex.substring(2, 4), 16);
        int endB = Integer.valueOf(endHex.substring(4, 6), 16);

        StringBuilder result = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            
            // Spaties behouden zonder kleur
            if (c == ' ') {
                result.append(' ');
                continue;
            }

            // Bereken gradient ratio (0.0 tot 1.0)
            float ratio = length > 1 ? (float) i / (length - 1) : 0;

            // Interpolate RGB values
            int r = Math.round(startR + (endR - startR) * ratio);
            int g = Math.round(startG + (endG - startG) * ratio);
            int b = Math.round(startB + (endB - startB) * ratio);

            // Clamp values
            r = Math.max(0, Math.min(255, r));
            g = Math.max(0, Math.min(255, g));
            b = Math.max(0, Math.min(255, b));

            // Convert to hex and apply color
            String hexColor = String.format("%02X%02X%02X", r, g, b);
            result.append(convertHexToMinecraft(hexColor)).append(c);
        }

        return result.toString();
    } catch (Exception e) {
        plugin.getLogger().warning("Failed to create gradient: " + e.getMessage());
        return text;
    }
}
}
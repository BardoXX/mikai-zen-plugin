package com.megamc.minetopia.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.megamc.minetopia.ScoreboardManager;
import com.megamc.minetopia.TablistManager;

public class ScoreboardTablistListener implements Listener {

    private final ScoreboardManager scoreboardManager;
    private final TablistManager tablistManager;

    public ScoreboardTablistListener(ScoreboardManager scoreboardManager, TablistManager tablistManager) {
        this.scoreboardManager = scoreboardManager;
        this.tablistManager = tablistManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Setup scoreboard voor nieuwe speler
        scoreboardManager.setupPlayer(event.getPlayer());

        // Start update task voor scoreboard
        if (scoreboardManager.isEnabled()) {
            // De update task wordt gestart in setupPlayer, maar we kunnen hier een directe update forceren
            scoreboardManager.updateScoreboard(event.getPlayer());
        }

        // Tablist wordt automatisch geüpdatet door de timer, maar we kunnen een directe update doen voor deze speler
        if (tablistManager.isEnabled()) {
            String headerText = tablistManager.processTablistText(tablistManager.getHeader());
            String footerText = tablistManager.processTablistText(tablistManager.getFooter());

            try {
                event.getPlayer().setPlayerListHeaderFooter(headerText, footerText);
            } catch (NoSuchMethodError e) {
                // Fallback voor oudere Bukkit versies zonder deze API
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Verwijder scoreboard voor speler die quit
        scoreboardManager.removePlayer(event.getPlayer());
    }
}

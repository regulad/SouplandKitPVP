package us.soupland.kitpvp.sidebar.scoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import us.soupland.kitpvp.sidebar.scoreboard.events.PlayerUpdateScoreboardEvent;
import us.soupland.kitpvp.utilities.chat.ColorText;

import java.util.*;

@Getter
public class AridiManager {

    private AridiAdapter adapter;
    private Map<UUID, Aridi> aridiMap;

    public AridiManager(AridiAdapter adapter) {
        this.adapter = adapter;
        this.aridiMap = new HashMap<>();
    }

    public void sendScoreboard() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            Aridi aridi = aridiMap.get(player.getUniqueId());

            if (aridi != null) {
                Scoreboard scoreboard = aridi.getScoreboard();
                Objective objective = aridi.getObjective();

                String title = ColorText.translateAmpersand(adapter.getTitle(player));

                if (!objective.getDisplayName().equals(title)) {
                    objective.setDisplayName(title);
                }

                List<String> lines = adapter.getLines(player);

                if (lines == null || lines.isEmpty()) {
                    aridi.getEntries().forEach(AridiEntry::quit);
                    aridi.getEntries().clear();
                } else {
                    if (!adapter.getAridiStyle(player).isDescending()) {
                        Collections.reverse(lines);
                    }

                    if (aridi.getEntries().size() > lines.size()) {
                        for (int i = lines.size(); i < aridi.getEntries().size(); i++) {
                            AridiEntry entry = aridi.getEntryAtPosition(i);

                            if (entry != null) {
                                entry.quit();
                            }
                        }
                    }

                    int cache = adapter.getAridiStyle(player).getFirstNumber();

                    for (int i = 0; i < lines.size(); i++) {
                        AridiEntry entry = aridi.getEntryAtPosition(i);

                        String line = ColorText.translateAmpersand(lines.get(i));

                        if (entry == null) {
                            entry = new AridiEntry(aridi, line);
                        }

                        entry.setText(line);
                        entry.setUp();
                        entry.send(adapter.getAridiStyle(player).isDescending() ? cache-- : cache++);
                    }
                }

                boolean cancelled = !(new PlayerUpdateScoreboardEvent(player, aridi).call());

                if (player.getScoreboard() == scoreboard || cancelled) {
                    continue;
                }

                player.setScoreboard(scoreboard);
            }
        }
    }
}
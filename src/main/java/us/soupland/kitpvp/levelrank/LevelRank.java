package us.soupland.kitpvp.levelrank;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.utilities.configuration.Config;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LevelRank {

    private static Config config = KitPvP.getInstance().getRankConfig();
    @Getter
    private static List<LevelRank> levelRanks = Lists.newArrayList();

    private String name, displayName;
    private int requiredExp;
    private String[] commandsToExecute;

    public LevelRank(String name) {
        this.name = name;
        this.commandsToExecute = new String[]{};
    }

    public static void loadRanks() {
        ConfigurationSection section = config.getConfigurationSection("RANKS");
        if (section != null) {
            section.getKeys(false).forEach(key -> {
                LevelRank rank = new LevelRank(ChatColor.stripColor(key));
                rank.setDisplayName(key);
                rank.setRequiredExp(section.getInt(key + ".REQUIRED-EXPERIENCE"));
                rank.setCommandsToExecute(section.getStringList(key + ".EXECUTE-COMMANDS").toArray(new String[]{}));
                levelRanks.add(rank);
            });
        }
        levelRanks = levelRanks.stream().sorted(Comparator.comparingInt(LevelRank::getRequiredExp)).collect(Collectors.toList());
        /*levelRanks = levelRanks.stream()
                .sorted(new LevelComparator())
                .collect(Collectors.toList());*/
    }

    public static void saveRanks() {
        ConfigurationSection configurationSection = config.getConfigurationSection("RANKS");
        levelRanks.forEach(rank -> {
            ConfigurationSection section = configurationSection.createSection(rank.getName());
            section.set("REQUIRED-EXPERIENCE", rank.getRequiredExp());
            section.set("EXECUTE-COMMANDS", Arrays.asList(rank.getCommandsToExecute()));
        });

        config.save();
    }

    public static LevelRank getByName(String name) {
        return levelRanks.stream().filter(rank -> rank.getName().equals(name)).findFirst()
                .orElse(null);
    }

    public static LevelRank getDefaultRank() {
        return levelRanks.stream().filter(rank -> rank.getRequiredExp() == 0).findFirst().orElse(new LevelRank("Default"));
    }

    public void save() {
        ConfigurationSection configurationSection = config.getConfigurationSection("RANKS");
        ConfigurationSection section = configurationSection.createSection(this.name);
        section.set("REQUIRED-EXPERIENCE", this.requiredExp);
        section.set("EXECUTE-COMMANDS", Arrays.asList(this.commandsToExecute));

        config.save();
    }

    /*private static class LevelComparator implements Comparator<LevelRank> {

        @Override
        public int compare(LevelRank o1, LevelRank o2) {
            return Integer.compare(o1.getRequiredExp(), o2.getRequiredExp());
        }
    }*/

}

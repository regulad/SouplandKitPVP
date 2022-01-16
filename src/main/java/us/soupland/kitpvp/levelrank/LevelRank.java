package us.soupland.kitpvp.levelrank;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.utilities.configuration.Config;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class LevelRank {

    private static Config rankConfig = KitPvP.getInstance().getRankConfig();
    @Getter
    private static List<@NotNull LevelRank> allRanks = Lists.newArrayList();

    private final String name;
    private String displayName;
    private int requiredExp;
    private String[] commandsToExecute;

    public LevelRank(String name) {
        this.name = name;
        this.displayName = name;
        this.commandsToExecute = new String[]{};
    }

    public static void sortRanks() {
        allRanks = allRanks.stream().sorted(Comparator.comparingInt(LevelRank::getRequiredExp)).collect(Collectors.toList());
    }

    public static void loadAllRanks() {
        getRootSection().getKeys(false).stream().filter(key -> getRootSection().getConfigurationSection(key) != null).forEach(key -> allRanks.add(new LevelRank(key)));
        allRanks.forEach(LevelRank::load);
        sortRanks();
    }

    public static void saveAllRanks() {
        allRanks.forEach(LevelRank::save);
    }

    public static LevelRank getByName(String name) {
        return allRanks.stream().filter(rank -> rank.getName().equals(name)).findFirst()
                .orElse(null);
    }

    public static LevelRank getDefaultRank() {
        return allRanks.stream().filter(rank -> rank.getRequiredExp() == 0).findFirst().orElse(new LevelRank("Default"));
    }

    public static @NotNull ConfigurationSection getRootSection() {
        return Optional.ofNullable(rankConfig.getConfigurationSection("RANKS")).orElse(rankConfig.createSection("RANKS"));
    }

    public @NotNull ConfigurationSection getSection() {
        return Optional.ofNullable(getRootSection().getConfigurationSection(this.getName())).orElse(getRootSection().createSection(this.getName()));
    }

    public void load() {
        final @NotNull ConfigurationSection section = this.getSection();
        this.setRequiredExp(section.getInt("REQUIRED-EXPERIENCE"));
        this.setCommandsToExecute(section.getStringList("EXECUTE-COMMANDS").toArray(new String[]{}));
    }

    public void save() {
        final @NotNull ConfigurationSection rootSection = Objects.requireNonNull(rankConfig.getConfigurationSection("RANKS"));
        final @NotNull ConfigurationSection section = Optional.ofNullable(rootSection.getConfigurationSection(this.name)).orElse(rootSection.createSection(this.name));
        section.set("REQUIRED-EXPERIENCE", this.requiredExp);
        section.set("EXECUTE-COMMANDS", Arrays.asList(this.commandsToExecute));

        rankConfig.save();
    }
}

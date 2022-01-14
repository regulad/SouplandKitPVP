package us.soupland.kitpvp.practice.match;

import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import us.soupland.kitpvp.practice.match.player.MatchPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class MatchSnapshot {

    @Getter
    private static Map<UUID, MatchSnapshot> snapshotMap = new HashMap<>();

    private MatchPlayer matchPlayer, switchTo;
    private int heal, hunger;
    private ItemStack[] armor, contents;
    private Collection<PotionEffect> potionEffects;
    private long createdAt = System.currentTimeMillis();
    private boolean potion;

    public MatchSnapshot(MatchPlayer matchPlayer) {
        this(matchPlayer, null);
    }

    public MatchSnapshot(MatchPlayer matchPlayer, MatchPlayer switchTo) {
        this.matchPlayer = matchPlayer;
        Player player = matchPlayer.getPlayer();

        this.heal = player.getHealth() == 0 ? 0 : (int) Math.round(player.getHealth() / 2);
        this.hunger = player.getFoodLevel();
        this.armor = player.getInventory().getArmorContents();
        this.contents = player.getInventory().getContents();
        this.potionEffects = player.getActivePotionEffects();

        this.switchTo = switchTo;
    }

    public static MatchSnapshot getByName(String name) {
        for (MatchSnapshot snapshot : snapshotMap.values()) {
            Player player = snapshot.getMatchPlayer().getPlayer();
            if (player != null) {
                if (player.getName().equalsIgnoreCase(name)) {
                    return snapshot;
                }
            }
        }
        return null;
    }

    public static MatchSnapshot getByUuid(UUID uuid) {
        return snapshotMap.get(uuid);
    }

    public int getRemainingHeal() {
        int amount = 0;

        for (ItemStack stack : contents) {
            if (stack == null) {
                continue;
            }
            if (stack.getType() == Material.POTION && stack.getDurability() == 16421 && potion) {
                amount++;
            } else if (stack.getType() == Material.MUSHROOM_SOUP && !potion) {
                amount++;
            }
        }
        return amount;
    }

    public boolean shouldDisplayReaminingHeal() {
        return getRemainingHeal() > 0 || matchPlayer.getPotionsThrown() > 0 || matchPlayer.getPotionsMissed() > 0;
    }
}
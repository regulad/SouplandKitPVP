package us.soupland.kitpvp.managers;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BountyManager {
    @Getter
    private static final Map<UUID, UUID> hunterHunted = new HashMap<>(); // what the hell
    @Getter
    private static final Map<UUID, Integer> priceMap = new HashMap<>();

    public static boolean isInMap(UUID uuid) {
        for (Map.Entry<UUID, UUID> entry : hunterHunted.entrySet()) {
            if (uuid == entry.getKey() || uuid == entry.getValue()) {
                return true;
            }
        }
        return false;
    }
}
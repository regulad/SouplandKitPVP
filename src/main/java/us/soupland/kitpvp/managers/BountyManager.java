package us.soupland.kitpvp.managers;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BountyManager {

    @Getter
    private static Map<UUID, UUID> faggotMap = new HashMap<>();
    @Getter
    private static Map<UUID, Integer> priceMap = new HashMap<>();

    public static boolean isInMap(UUID uuid) {
        for (Map.Entry<UUID, UUID> entry : faggotMap.entrySet()) {
            if (uuid == entry.getKey() || uuid == entry.getValue()) {
                return true;
            }
        }
        return false;
    }

}
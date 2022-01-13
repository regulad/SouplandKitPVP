package us.soupland.kitpvp.profile;

import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {

    @Getter
    private static Map<UUID, Profile> profileMap = new HashMap<>();

    public static Profile getProfile(OfflinePlayer player) {
        return getProfile(player.getUniqueId());
    }

    public static Profile getProfile(UUID uuid) {
        Profile profile = profileMap.get(uuid);
        if (profile == null) {
            profile = new Profile(uuid);
        }
        return profile;
    }

    public static void loadProfile(Profile profile) {
        if (!profileMap.containsKey(profile.getUuid())) {
            profileMap.put(profile.getUuid(), profile);
            System.out.println("Profile loaded '" + profile.getPlayerName() + "'.");
        }
    }

    public static void saveProfile(Profile profile, boolean remove) {
        profile.saveProfile();
        if (remove) {
            profileMap.remove(profile.getUuid());
        }
    }
}
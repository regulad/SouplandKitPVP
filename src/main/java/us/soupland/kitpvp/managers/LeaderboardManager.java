package us.soupland.kitpvp.managers;

import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.mongodb.client.MongoCursor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.kits.KitHandler;
import us.soupland.kitpvp.profile.Profile;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class LeaderboardManager {

    private Map<UUID, Profile> playerDataList = Maps.newHashMap();
    private Map<String, List<Profile>> kitKills = Maps.newHashMap();
    private Map<String, List<Profile>> eventsWins = Maps.newHashMap();
    private Map<String, List<Profile>> globalStats = Maps.newHashMap();

    public LeaderboardManager() {
        try (MongoCursor mongoCursor = KitPvP.getInstance().getPvPDatabase().getProfiles().find().iterator()) {
            while (mongoCursor.hasNext()) {
                Document document = (Document) mongoCursor.next();
                final UUID uuid = UUID.fromString(document.getString("uuid"));
                Profile playerData = new Profile(uuid);

                if (document.containsKey("playerStats")) {
                    for (Object o : (ArrayList<?>) document.get("playerStats")) {
                        Document oDocument = (Document) o;
                        playerData.getPlayerStats().put(PlayerStat.valueOf(oDocument.getString("name")), oDocument.getInteger("value"));
                    }
                }
                if (document.containsKey("kitKills")) {
                    for (Object o : (ArrayList<?>) document.get("kitKills")) {
                        Document oDocument = (Document) o;
                        playerData.getKitKills().put(oDocument.getString("name"), oDocument.getInteger("kills"));
                    }
                }

                if (document.containsKey("eventWins")) {
                    for (Object o : (ArrayList<?>) document.get("kitDeaths")) {
                        Document oDocument = (Document) o;
                        playerData.getEventsWin().put(oDocument.getString("name"), oDocument.getInteger("wins"));
                    }
                }

                for (Game game : KitPvP.getInstance().getGameHandler().getGames()) {
                    if (!playerData.getEventsWin().containsKey(game.getName())) {
                        playerData.getEventsWin().put(game.getName(), 0);
                    }
                }

                for (Kit kit : KitHandler.getKitList()) {
                    if (!playerData.getEventsWin().containsKey(kit.getName())) {
                        playerData.getKitKills().put(kit.getName(), 0);
                    }
                }

                playerDataList.put(uuid, playerData);

            }
        }
        sort();

        new BukkitRunnable() {
            @Override
            public void run() {
                //playerDataList.clear();
                try (MongoCursor mongoCursor = KitPvP.getInstance().getPvPDatabase().getProfiles().find().iterator()) {
                    while (mongoCursor.hasNext()) {
                        Document document = (Document) mongoCursor.next();
                        final UUID uuid = UUID.fromString(document.getString("uuid"));
                        Profile playerData = (playerDataList.containsKey(uuid) ? playerDataList.get(uuid) : new Profile(uuid));

                        if (document.containsKey("playerStats")) {
                            for (Object o : (ArrayList<?>) document.get("playerStats")) {
                                Document oDocument = (Document) o;
                                playerData.getPlayerStats().put(PlayerStat.valueOf(oDocument.getString("name")), oDocument.getInteger("value"));
                            }
                        }

                        if (document.containsKey("kitKills")) {
                            for (Object o : (ArrayList<?>) document.get("kitKills")) {
                                Document oDocument = (Document) o;
                                playerData.getKitKills().put(oDocument.getString("name"), oDocument.getInteger("kills"));
                            }
                        }

                        if (document.containsKey("eventWins")) {
                            for (Object o : (ArrayList<?>) document.get("kitDeaths")) {
                                Document oDocument = (Document) o;
                                playerData.getEventsWin().put(oDocument.getString("name"), oDocument.getInteger("wins"));
                            }
                        }
                        for (Game game : KitPvP.getInstance().getGameHandler().getGames()) {
                            if (!playerData.getEventsWin().containsKey(game.getName())) {
                                playerData.getEventsWin().put(game.getName(), 0);
                            }
                        }

                        for (Kit kit : KitHandler.getKitList()) {
                            if (!playerData.getEventsWin().containsKey(kit.getName())) {
                                playerData.getKitKills().put(kit.getName(), 0);
                            }
                        }

                        playerDataList.put(uuid, playerData);
                    }
                }
                sort();
            }
        }.runTaskTimerAsynchronously(KitPvP.getInstance(), 36000, 36000);
    }

    private void sort() {
        for (Kit kit : KitHandler.getKitList()) {
            List<Profile> playerSorted = playerDataList.values().stream()
                    .sorted(new KitComparator(kit.getName()).reversed())
                    .limit(10)
                    .collect(Collectors.toList());

            kitKills.put(kit.getName(), playerSorted);
        }

        for (Game event : KitPvP.getInstance().getGameHandler().getGames()) {
            List<Profile> playerSorted = playerDataList.values().stream()
                    .sorted(new EventComparator(event.getName()).reversed())
                    .limit(10)
                    .collect(Collectors.toList());

            eventsWins.put(event.getName(), playerSorted);
        }

        for (PlayerStat stat : PlayerStat.values()) {
            globalStats.put(stat.name(), playerDataList.values().stream().sorted(new PlayerStatComparator(stat).reversed()).limit(10).collect(Collectors.toList()));
        }

        globalStats.put("KDR", playerDataList.values().stream().sorted(new PlayerKdrComparator().reversed()).limit(10).collect(Collectors.toList()));
    }

    public List<Profile> getListByKit(Kit kit) {
        return this.kitKills.get(kit.getName());
    }

    public List<Profile> getListByEvent(Game event) {
        return this.eventsWins.get(event.getName());
    }

    @AllArgsConstructor
    private class KitComparator implements Comparator<Profile> {

        private String kitName;

        @Override
        public int compare(Profile o1, Profile o2) {
            return Ints.compare(o1.getKitKills().get(kitName), o2.getKitKills().get(kitName));
        }
    }

    @AllArgsConstructor
    private class EventComparator implements Comparator<Profile> {

        private String kitName;

        @Override
        public int compare(Profile profileA, Profile profileB) {
            return Ints.compare(profileA.getEventsWin().get(kitName), profileB.getEventsWin().get(kitName));
        }
    }

    @AllArgsConstructor
    private class PlayerStatComparator implements Comparator<Profile> {

        private PlayerStat stat;

        @Override
        public int compare(Profile profileA, Profile profileB) {
            return Ints.compare(profileA.getStat(stat), profileB.getStat(stat));
        }
    }

    private class PlayerKdrComparator implements Comparator<Profile> {

        @Override
        public int compare(Profile profileA, Profile profileB) {
            return Integer.compare(profileA.getKdr(), profileB.getKdr());
        }
    }
}

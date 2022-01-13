package us.soupland.kitpvp.profile;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Data;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.*;
import us.soupland.kitpvp.events.PlayerGainExpEvent;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.kits.KitHandler;
import us.soupland.kitpvp.levelrank.LevelRank;
import us.soupland.kitpvp.practice.duel.DuelProcedure;
import us.soupland.kitpvp.practice.duel.DuelRequest;
import us.soupland.kitpvp.practice.match.Match;
import us.soupland.kitpvp.sidebar.team.Team;
import us.soupland.kitpvp.utilities.player.ParticleEffect;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

@Data
public class Profile {

    private final UUID uuid;
    private final Map<PlayerStat, Integer> playerStats = new HashMap<>();
    private int lastHits, tempKills, experience;
    private String playerName, playerIp, lastMessage;
    private PlayerState playerState;
    private Kit lastKit, currentKit;
    private Refill refill;
    private boolean scoreboardEnabled, fell, creatingTeam, creatingBounty, frozenByAbility, frozenToUseAbility, canModifyState, reclaimed, joinAndQuitMessageEnabled, ggMode;
    private long playerCombat, playerCps, lastKitUsed;
    private ChatColor chatColor;
    private DeathReason deathReason;
    private ServerTime serverTime;
    private KitMenuType kitMenuType;
    private ParticleEffect currentParticle;
    private Team team;
    private Theme theme;
    private Match match;
    private Map<UUID, DuelRequest> sentDuelRequests;
    private DuelProcedure duelProcedure;
    private List<PotionEffect> effectsSaved;
    private Location recallLocation;
    private Map<String, Integer> kitUses, kitKills, kitDeaths, eventsWin;
    private List<Achievement> achievements;
    private List<Game> gamesPurchased;
    private LevelRank levelRank;
    private List<String> levelRankHistory;
    private Player lastDamager;

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.playerStats.put(PlayerStat.ELO, 1000);
        this.playerState = PlayerState.SPAWN;
        this.refill = Refill.SOUP;
        this.scoreboardEnabled = true;
        this.chatColor = ChatColor.WHITE;
        this.deathReason = null;
        this.serverTime = ServerTime.DAY;
        this.kitMenuType = KitMenuType.GUI;
        this.currentParticle = null;
        this.team = null;
        this.theme = Theme.DEFAULT;
        this.sentDuelRequests = new HashMap<>();
        this.duelProcedure = null;
        this.effectsSaved = new ArrayList<>();
        this.recallLocation = null;
        this.kitUses = new HashMap<>();
        this.kitKills = new HashMap<>();
        this.kitDeaths = new HashMap<>();
        this.eventsWin = new HashMap<>();
        this.achievements = new ArrayList<>();
        this.gamesPurchased = new ArrayList<>();
        this.levelRank = LevelRank.getDefaultRank();
        this.levelRankHistory = Lists.newArrayList();
        this.levelRankHistory.add(this.levelRank.getName());

        for (Team found : Team.getTeams()) {
            if (found.getAllUuids().contains(uuid)) {
                this.team = found;
            }
        }

        loadProfile();
    }

    public void updateTab() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
    }

    private org.bukkit.scoreboard.Team getOrCreate(String name, Scoreboard scoreboard, ChatColor color) {
        org.bukkit.scoreboard.Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = scoreboard.registerNewTeam(name);
            team.setPrefix(color.toString());
        }
        return team;
    }

    public void reset() {

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        for (PlayerStat stat : PlayerStat.values()) {
            setStat(stat, 0);
        }

        setDeathReason(null);
        setChatColor(ChatColor.WHITE);
        setServerTime(ServerTime.DAY);
        setLastKit(null);
        setExperience(0);
        setLevelRank(LevelRank.getDefaultRank());
        getLevelRankHistory().clear();
        setScoreboardEnabled(true);
        setKitMenuType(KitMenuType.GUI);
        getGamesPurchased().forEach(game -> getGamesPurchased().remove(game));

        if (player.isOnline()) {
            player.getPlayer().sendMessage(ColorText.translate("&cYour Stats were reset."));
        }
        for (Kit kit : KitHandler.getKitList()) {
            if (kit.getPermissions() == null) {
                continue;
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "perms remove " + player.getName() + ' ' + kit.getPermissions());
        }
    }

    public int getStat(PlayerStat stat) {
        return playerStats.getOrDefault(stat, 0);
    }

    public void incrementStat(PlayerStat stat) {
        playerStats.put(stat, getStat(stat) + 1);
    }

    public void decrementStat(PlayerStat stat) {
        playerStats.put(stat, getStat(stat) - 1);
    }

    public void setStat(PlayerStat stat, int value) {
        playerStats.put(stat, value);
    }

    public boolean canSendDuelRequest(Player player) {
        if (!this.sentDuelRequests.containsKey(player.getUniqueId())) {
            return true;
        }

        DuelRequest request = this.sentDuelRequests.get(player.getUniqueId());

        if (request.isExpired()) {
            this.sentDuelRequests.remove(player.getUniqueId());
        }

        return request.isExpired();
    }

    public void upgradeExperience(PlayerGainExpEvent.Type type) {
        Player player = Bukkit.getPlayer(this.uuid);
        ConfigurationSection section = KitPvP.getInstance().getRankConfig().getConfigurationSection("PLAYER-EXPERIENCE");
        String typeString = type.name().toUpperCase();
        int amountXp = KitPvP.getInstance().getRankConfig().getInt("PLAYER-EXPERIENCE.DEFAULT.EXPERIENCE-PER-" + typeString);
        for (String path : section.getKeys(false)) {
            if (path.equalsIgnoreCase("DEFAULT")) continue;
            if (player.hasPermission("PLAYER-EXPERIENCE." + path + ".PERMISSION")) {
                amountXp = Math.max(amountXp, KitPvP.getInstance().getRankConfig().getInt("PLAYER-EXPERIENCE." + path + ".EXPERIENCE-PER-" + typeString));
            }
        }
        this.experience = this.experience + amountXp;
        new PlayerGainExpEvent(player, type, amountXp).call();
    }

    public boolean canRankUp() {
        return LevelRank.getLevelRanks()
                .stream()
                .anyMatch(rank -> this.experience >= rank.getRequiredExp() && !levelRankHistory.contains(rank.getName()));
    }

    public LevelRank getRankUp() {
        return LevelRank.getLevelRanks()
                .stream()
                .filter(rank -> rank.getRequiredExp() >= this.experience && !levelRankHistory.contains(rank.getName()))
                .findFirst()
                .get();
    }

    public void rankUp() {
        this.levelRank = LevelRank.getLevelRanks()
                .stream()
                .filter(rank -> rank.getRequiredExp() <= this.experience && !levelRankHistory.contains(rank.getName()))
                .findFirst()
                .get();
        this.levelRankHistory.add(this.levelRank.getName());
    }

    public void checkRank() {
        for (String rankName : this.levelRankHistory) {
            LevelRank levelRank = LevelRank.getByName(rankName);
            if (levelRank == null) {
                this.levelRankHistory.remove(rankName);
                continue;
            }
            if (levelRank.getRequiredExp() > this.experience) {
                this.levelRankHistory.remove(rankName);
            }
        }
        if (canRankDown()) {
            rankDown();
        }
        if (canRankUp()) {
            rankUp();
        }
    }

    public boolean canRankDown() {
        return LevelRank.getLevelRanks()
                .stream()
                .anyMatch(rank -> this.experience < rank.getRequiredExp() && levelRankHistory.contains(rank.getName()));
    }

    public LevelRank getRankDown() {
        return LevelRank.getByName(this.levelRankHistory.get(this.levelRankHistory.size() - 2));
    }

    public void rankDown() {
        this.levelRankHistory.remove(this.levelRank.getName());
        this.levelRank = LevelRank.getByName(this.levelRankHistory.get(this.levelRankHistory.size() - 1));
        this.experience = levelRank.getRequiredExp();
    }

    public void setLevelRank(LevelRank levelRank) {
        this.levelRankHistory.add(levelRank.getName());
        this.levelRank = levelRank;
    }

    public void resetExperience() {
        this.experience = 0;
    }

    public void incrementExperience(int xp) {
        this.experience += xp;
    }

    public void decrementExperience(int xp) {
        this.experience -= xp;
    }

    public boolean isPendingDuelRequest(Player player) {
        if (!this.sentDuelRequests.containsKey(player.getUniqueId())) {
            return false;
        }

        DuelRequest request = this.sentDuelRequests.get(player.getUniqueId());

        if (request.isExpired()) {
            this.sentDuelRequests.remove(player.getUniqueId());
        }
        return !request.isExpired();
    }

    public long getLastKitUsed() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOp()) {
            return 0L;
        }
        return lastKitUsed - System.currentTimeMillis();
    }

    public DeathReason getDeathReason() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && !player.hasPermission(KitPvPUtils.DONATOR_PERMISSION) && deathReason != null) {
            deathReason = null;
        }
        return deathReason;
    }

    public ChatColor getChatColor() {
        if (chatColor == null) {
            chatColor = ChatColor.WHITE;
        }
        return chatColor;
    }

    public Kit getLastKit() {
        if (lastKit == null) {
            return null;
        }
        lastKit = KitHandler.getByName(lastKit.getName());
        return lastKit;
    }

    public long getPlayerCombat() {
        return playerCombat - System.currentTimeMillis();
    }

    public int getKdr() {
        if (getStat(PlayerStat.KILLS) == 0 || getStat(PlayerStat.DEATHS) == 0) {
            return 0;
        }
        return getStat(PlayerStat.DEATHS) / getStat(PlayerStat.KILLS);
    }

    private void loadProfile() {
        Document document = (Document) KitPvP.getInstance().getPvPDatabase().getProfiles().find(Filters.eq("uuid", uuid.toString())).first();
        if (document != null) {
            if (document.containsKey("playerStats")) {
                for (Object o : (ArrayList<?>) document.get("playerStats")) {
                    Document oDocument = (Document) o;
                    playerStats.put(PlayerStat.valueOf(oDocument.getString("name")), oDocument.getInteger("value"));
                }
            }
            if (document.containsKey("playerName")) {
                playerName = document.getString("playerName");
            }
            if (document.containsKey("playerIp")) {
                playerIp = document.getString("playerIp");
            }
            if (document.containsKey("refill")) {
                refill = Refill.valueOf(document.getString("refill"));
            }
            if (document.containsKey("lastKit")) {
                try {
                    lastKit = KitHandler.getByName(document.getString("lastKit"));
                } catch (Exception ignored) {
                }
            }
            if (document.containsKey("scoreboardEnabled")) {
                scoreboardEnabled = document.getBoolean("scoreboardEnabled");
            }
            if (document.containsKey("chatColor")) {
                chatColor = ChatColor.valueOf(document.getString("chatColor"));
            }
            if (document.containsKey("deathReason")) {
                deathReason = DeathReason.valueOf(document.getString("deathReason"));
            }
            if (document.containsKey("serverTime")) {
                serverTime = ServerTime.valueOf(document.getString("serverTime"));
            }
            if (document.containsKey("kitMenuType")) {
                kitMenuType = KitMenuType.valueOf(document.getString("kitMenuType"));
            }
            if (document.containsKey("currentParticle")) {
                currentParticle = ParticleEffect.valueOf(document.getString("currentParticle"));
            }
            try {
                theme = Theme.valueOf(document.getString("theme"));
            } catch (Exception ignored) {
                theme = Theme.DEFAULT;
            }
            if (document.containsKey("lastMessage")) {
                lastMessage = document.getString("lastMessage");
            }
            if (document.containsKey("kitUses")) {
                for (Object o : (ArrayList<?>) document.get("kitUses")) {
                    Document oDocument = (Document) o;
                    kitUses.put(oDocument.getString("name"), oDocument.getInteger("uses"));
                }
            }
            if (document.containsKey("kitKills")) {
                for (Object o : (ArrayList<?>) document.get("kitKills")) {
                    Document oDocument = (Document) o;
                    kitKills.put(oDocument.getString("name"), oDocument.getInteger("kills"));
                }
            }
            if (document.containsKey("kitDeaths")) {
                for (Object o : (ArrayList<?>) document.get("kitDeaths")) {
                    Document oDocument = (Document) o;
                    kitDeaths.put(oDocument.getString("name"), oDocument.getInteger("deaths"));
                }
            }
            if (document.containsKey("eventWins")) {
                for (Object o : (ArrayList<?>) document.get("kitDeaths")) {
                    Document oDocument = (Document) o;
                    eventsWin.put(oDocument.getString("name"), oDocument.getInteger("wins"));
                }
            }
            if (document.containsKey("achievements")) {
                for (Object o : (ArrayList<?>) document.get("achievements")) {
                    achievements.add(Achievement.valueOf(((Document) o).getString("name")));
                }
            }
            if (document.containsKey("reclaimed")) {
                reclaimed = document.getBoolean("reclaimed");
            }
            if (document.containsKey("joinAndQuitMessageEnabled")) {
                joinAndQuitMessageEnabled = document.getBoolean("joinAndQuitMessageEnabled");
            }
            if (document.containsKey("xp")) {
                this.experience = document.getInteger("xp");
            }
            if (document.containsKey("levelRankHistory")) {
                levelRankHistory = KitPvP.GSON.fromJson(document.getString("levelRankHistory"), KitPvP.LIST_STRING_TYPE);
            }
            if (document.containsKey("levelRank")) {
                levelRank = LevelRank.getByName(document.getString("levelRank"));
                if (levelRank == null) {
                    levelRank = LevelRank.getDefaultRank();
                }
                if (experience == 0) {
                    levelRank = LevelRank.getDefaultRank();
                    levelRankHistory.clear();
                }
                if (!levelRankHistory.contains(levelRank.getName())) {
                    levelRankHistory.add(levelRank.getName());
                }
                //checkRank();
            }
            if (document.containsKey("gamesPurchased")) {
                for (Object o : (ArrayList<?>) document.get("gamesPurchased")) {
                    Game game = Game.getByName(((Document) o).getString("name"));
                    if (game == null) {
                        continue;
                    }
                    gamesPurchased.add(game);
                }
            }
        }
    }

    void saveProfile() {
        Document document = new Document();
        document.put("uuid", uuid.toString());
        document.put("playerName", playerName);
        document.put("playerIp", playerIp);
        document.put("refill", refill.name());
        document.put("xp", experience);
        if (lastKit != null) {
            document.put("lastKit", lastKit.getName());
        }
        document.put("scoreboardEnabled", scoreboardEnabled);
        document.put("chatColor", chatColor.name());
        if (getDeathReason() != null) {
            document.put("deathReason", getDeathReason().name());
        }
        document.put("serverTime", serverTime.name());
        document.put("kitMenuType", kitMenuType.name());

        if (currentParticle != null) {
            document.put("currentParticle", currentParticle.name());
        }
        document.put("theme", theme.name());
        if (lastMessage != null) {
            document.put("lastMessage", lastMessage);
        }

        BasicDBList objects = new BasicDBList();

        for (Map.Entry<String, Integer> entry : kitUses.entrySet()) {

            BasicDBObject object = new BasicDBObject();

            object.append("name", entry.getKey());
            object.append("uses", entry.getValue());

            objects.add(object);

        }
        document.put("kitUses", objects);

        objects = new BasicDBList();

        for (Map.Entry<String, Integer> entry : kitKills.entrySet()) {

            BasicDBObject object = new BasicDBObject();

            object.append("name", entry.getKey());
            object.append("kills", entry.getValue());

            objects.add(object);

        }
        document.put("kitKills", objects);

        objects = new BasicDBList();

        for (Map.Entry<String, Integer> entry : kitDeaths.entrySet()) {

            BasicDBObject object = new BasicDBObject();

            object.append("name", entry.getKey());
            object.append("deaths", entry.getValue());

            objects.add(object);

        }

        document.put("kitDeaths", objects);

        objects = new BasicDBList();

        for (Map.Entry<PlayerStat, Integer> entry : playerStats.entrySet()) {
            BasicDBObject object = new BasicDBObject();

            object.put("name", entry.getKey().name());
            object.put("value", entry.getValue());

            objects.add(object);
        }

        document.put("playerStats", objects);

        objects = new BasicDBList();

        for (Game game : KitPvP.getInstance().getGameHandler().getGames()) {
            BasicDBObject object = new BasicDBObject();

            object.append("name", game.getName());
            object.append("wins", eventsWin.getOrDefault(game.getName(), 0));


            objects.add(object);
        }

        document.put("eventsWin", objects);

        objects = new BasicDBList();

        for (Achievement achievement : achievements) {
            BasicDBObject object = new BasicDBObject();
            object.append("name", achievement.name());
            objects.add(object);
        }

        document.put("achievements", objects);

        document.put("reclaimed", reclaimed);
        document.put("joinAndQuitMessageEnabled", joinAndQuitMessageEnabled);

        if (levelRank != null) {
            document.put("levelRank", levelRank.getName());
        }
        if (!levelRankHistory.isEmpty()) {
            document.put("levelRankHistory", KitPvP.GSON.toJson(levelRankHistory));
        }

        objects = new BasicDBList();

        for (Game game : gamesPurchased) {
            BasicDBObject object = new BasicDBObject();
            object.append("name", game.getName());
            objects.add(object);
        }

        document.put("gamesPurchased", objects);

        KitPvP.getInstance().getPvPDatabase().getProfiles().replaceOne(Filters.eq("uuid", uuid.toString()), document, new UpdateOptions().upsert(true));
    }
}
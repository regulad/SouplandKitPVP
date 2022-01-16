package us.soupland.kitpvp;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import us.soupland.kitpvp.database.KitPvPDatabase;
import us.soupland.kitpvp.games.GameHandler;
import us.soupland.kitpvp.games.arenas.GameMapHandler;
import us.soupland.kitpvp.games.listeners.GamesListener;
import us.soupland.kitpvp.kits.KitHandler;
import us.soupland.kitpvp.kits.KitListener;
import us.soupland.kitpvp.kits.types.*;
import us.soupland.kitpvp.koth.KothManager;
import us.soupland.kitpvp.levelrank.LevelRank;
import us.soupland.kitpvp.listener.*;
import us.soupland.kitpvp.listener.handler.ListenerHandler;
import us.soupland.kitpvp.managers.KillStreakManager;
import us.soupland.kitpvp.managers.LeaderboardManager;
import us.soupland.kitpvp.managers.PracticeManager;
import us.soupland.kitpvp.practice.PracticeHandler;
import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.arena.ArenaHandler;
import us.soupland.kitpvp.practice.kit.types.RefillKit;
import us.soupland.kitpvp.practice.ladder.LadderHandler;
import us.soupland.kitpvp.practice.ladder.types.ArcadeLadder;
import us.soupland.kitpvp.practice.ladder.types.SoupLadder;
import us.soupland.kitpvp.practice.match.Match;
import us.soupland.kitpvp.practice.match.MatchHandler;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.server.ServerData;
import us.soupland.kitpvp.sidebar.KitPvPBoard;
import us.soupland.kitpvp.sidebar.scoreboard.AridiManager;
import us.soupland.kitpvp.sidebar.scoreboard.listeners.AridiListener;
import us.soupland.kitpvp.sidebar.team.Team;
import us.soupland.kitpvp.utilities.KitPvPCache;
import us.soupland.kitpvp.utilities.configuration.Config;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.inventory.MakerListener;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.task.TaskUtil;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class KitPvP extends JavaPlugin {

    public static final Gson GSON = new Gson();
    public static final Type LIST_STRING_TYPE = new TypeToken<List<String>>() {
    }.getType();

    private KitPvPDatabase pvPDatabase;
    private ServerData serverData;
    private KothManager kothManager;
    private PracticeManager practiceManager;
    private PracticeHandler practiceHandler;
    private LadderHandler ladderHandler;
    private ArenaHandler arenaHandler;
    private GameHandler gameHandler;
    private GameMapHandler gameMapHandler;
    private Config config, rankConfig, kitConfig, gameMapConfig;
    private LeaderboardManager leaderboardManager;
    @Setter
    private AridiManager aridiManager;

    public static KitPvP getInstance() {
        return KitPvP.getPlugin(KitPvP.class);
    }

    @Override
    public void onEnable() {
        new Metrics(this, 13949);

        rankConfig = new Config(this, "ranks.yml");
        config = new Config(this, "settings.yml");
        kitConfig = new Config(this, "kits.yml");
        gameMapConfig = new Config(this, "gamemap.yml");

        new KitPvPCache();
        Bukkit.getPluginManager().registerEvents(new MakerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemMaker.ItemMakerListener(), this);
        TaskUtil.runTaskLater(() -> {
            if (aridiManager != null) {
                Bukkit.getPluginManager().registerEvents(new AridiListener(), this);
            }
        }, 2 * 20L);

        TaskUtil.runTaskLater(() -> {
            if (aridiManager != null) {
                TaskUtil.runTaskTimer(aridiManager::sendScoreboard, 0L, 2L);
            }
        }, 2 * 20L);

        removeRecipe(Material.WRITTEN_BOOK);
        load();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, null));
        }
    }

    @Override
    public void onDisable() {
        // Save to database
        serverData.saveServer();

        ArenaHandler.getArenaMap().forEach(Arena::saveArena);
        Team.getTeams().forEach(Team::saveTeam);
        for (Profile profile : ProfileManager.getProfileMap().values()) {
            ProfileManager.saveProfile(profile, false);
        }
        for (Match match : MatchHandler.getMatches()) {
            match.getPlacedBlocks().forEach(location -> location.getBlock().setType(Material.AIR));
            match.getChangedBlocks().forEach(blockState -> blockState.getLocation().getBlock().setType(blockState.getType()));
        }

        // Save to config
        LevelRank.saveAllRanks();
        KitHandler.saveKits();
    }

    public void removeRecipe(Material... materials) {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        while (recipeIterator.hasNext()) {
            for (Material material : materials) {
                if (recipeIterator.next().getResult().getType() == material) {
                    recipeIterator.remove();
                }
            }
        }
    }

    private void load() {
        pvPDatabase = new KitPvPDatabase();
        serverData = new ServerData();
        kothManager = new KothManager();
        practiceManager = new PracticeManager();
        (practiceHandler = new PracticeHandler()).registerKit(new RefillKit());
        (ladderHandler = new LadderHandler()).registerLadder(new ArcadeLadder(), new SoupLadder());
        arenaHandler = new ArenaHandler();
        gameMapHandler = new GameMapHandler(this);
        gameHandler = new GameHandler();
        new KillStreakManager();
        Arena.loadArenas();
        setAridiManager(new AridiManager(new KitPvPBoard()));
        new KitPvPCache();
        leaderboardManager = new LeaderboardManager();
        new Cooldown("SpawnTimer", TimeUtils.parse("8s"), "&9Teleporting", "&eYou have been teleported to &aSpawn&e.\n&7(If you wish reset your kit, please use &9/resetkit&7)");

        Team.loadTeams();

        loadKits();
        ListenerHandler.registerListeners(
                new KitListener(),
                new CoreListener(),
                new EnderpearlListener(),
                new GameListener(),
                new DeathListener(),
                new ChatListener(),
                new ParticleListener(),
                new WorldListener(),
                new ColoredSignListener(),
                new CooldownListener(),
                new CommandListener(),
                new GamesListener(),
                new BlockHitFixListener()
        );

        TaskUtil.runTaskTimer(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile profile = ProfileManager.getProfile(player);

                if (profile.getEffectsSaved().isEmpty()) {
                    continue;
                }

                Iterator<PotionEffect> iterator = profile.getEffectsSaved().iterator();
                while (iterator.hasNext()) {
                    PotionEffect effect = iterator.next();
                    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                        if (potionEffect.getType().equals(effect.getType())) {
                            if (potionEffect.getDuration() <= 20) {
                                TaskUtil.runTask(() -> player.addPotionEffect(effect, true));

                                iterator.remove();
                            }
                        }
                    }
                }
            }
        }, 20L, 20L);

        TaskUtil.runTaskTimer(() -> {
            int i = Team.getTeams().size();

            Team.getTeams().forEach(Team::saveTeam);
        }, 20L, 10 * 60 * 20L);

        for (World world : Bukkit.getWorlds()) {
            world.setStorm(false);
            world.setThundering(false);
            world.setTime(12000);

            world.getEntities().forEach(entity -> {
                if (!(entity instanceof Player)) {
                    entity.remove();
                }
            });
        }

        TaskUtil.runTaskTimer(() -> {
            for (Cooldown cooldown : Cooldown.getCooldownMap().values()) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (cooldown.isOnCooldown(player)) {
                        if (cooldown.getDuration(player) == 1L) {
                            player.setLevel(0);
                        } else {
                            player.setLevel((int) (cooldown.getDuration(player) / 1000));
                        }
                        break;
                    }
                }
            }
        }, 20L, 20L);

        LevelRank.loadAllRanks();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new KitPvPHook(this).register();
        }
    }

    public static Cooldown getCooldown(String name) {
        Cooldown cooldown = Cooldown.getCooldownMap().get(name);
        if (cooldown != null) {
            return cooldown;
        }
        return null;
    }

    public static Cooldown getCooldownByPlayerID(UUID uuid) {
        for (Map.Entry<String, Cooldown> cooldown : Cooldown.getCooldownMap().entrySet()) {
            if (cooldown.getValue().getLongMap().containsKey(uuid)) {
                return cooldown.getValue();
            }
        }
        return null;
    }

    private void loadKits() {
        KitHandler.registerKits(new PvPKit(),
                new ProKit(),
                new BackstabKit(),
                new ThorKit(),
                new JesterKit(),
                new FiremanKit(),
                new ArcherKit(),
                new StrafeKit(),
                new ChemistKit(),
                new QuickdropKit(),
                new DragonKit(),
                new StomperKit(),
                new BatKit(),
                new FishermanKit(),
                new FighterKit(),
                new SwitcherKit(),
                new MarioKit(),
                new CactusKit(),
                new KangarooKit(),
                new SuicidalKit(),
                new AssassinKit(),
                new ZenKit(),
                new SpeedMonsterKit(),
                new PhantomKit(),
                new BerserkerKit(),
                new FalconKit(),
                new SnailKit());
    }
}
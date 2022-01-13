package us.soupland.kitpvp.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import us.soupland.kitpvp.KitPvP;

import java.util.Collections;

@Getter
public class KitPvPDatabase {

    private YamlConfiguration config = KitPvP.getInstance().getConfig();
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection profiles, teams, arenas, serverInformation, koths;

    public KitPvPDatabase() {

        if (config.getBoolean("DATABASE.AUTHENTICATION.ENABLED")) {
            client = new MongoClient(new ServerAddress(config.getString("DATABASE.HOST"), config.getInt("DATABASE.PORT")), Collections.singletonList(MongoCredential.createCredential(config.getString("DATABASE.AUTHENTICATION.USER"), config.getString("DATABASE.AUTHENTICATION.DATABASE"), config.getString("DATABASE.AUTHENTICATION.PASSWORD").toCharArray())));
        } else {
            client = new MongoClient(new ServerAddress(config.getString("DATABASE.HOST"), config.getInt("DATABASE.PORT")));
        }
        database = client.getDatabase("KitPvPTest");
        profiles = database.getCollection("profilesTest");
        teams = database.getCollection("teamsTest");
        arenas = database.getCollection("arenasTest");
        serverInformation = database.getCollection("serverDataTest");
        koths = database.getCollection("kothsTest");
    }
}
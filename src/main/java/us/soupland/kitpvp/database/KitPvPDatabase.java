package us.soupland.kitpvp.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import us.soupland.kitpvp.KitPvP;

import java.util.Collections;

@Getter
public class KitPvPDatabase {

    private final YamlConfiguration config = KitPvP.getInstance().getConfig();
    private final @Nullable MongoCredential credential;
    private final MongoClientOptions mongoClientOptions = new MongoClientOptions.Builder().build();
    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> profiles;
    private final MongoCollection<Document> teams;
    private final MongoCollection<Document> arenas;
    private final MongoCollection<Document> serverInformation;
    private final MongoCollection<Document> koths;

    public KitPvPDatabase() {
        if (config.getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
            credential = MongoCredential.createCredential(config.getString("MONGO.AUTHENTICATION.USERNAME"), config.getString("MONGO.AUTHENTICATION.DATABASE"), config.getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray());
            client = new MongoClient(new ServerAddress(config.getString("MONGO.HOST"), config.getInt("MONGO.PORT")), credential, mongoClientOptions);
        } else {
            credential = null;
            client = new MongoClient(new ServerAddress(config.getString("MONGO.HOST"), config.getInt("MONGO.PORT")));
        }
        database = client.getDatabase("KitPvPTest");
        profiles = database.getCollection("profilesTest");
        teams = database.getCollection("teamsTest");
        arenas = database.getCollection("arenasTest");
        serverInformation = database.getCollection("serverDataTest");
        koths = database.getCollection("kothsTest");
    }
}
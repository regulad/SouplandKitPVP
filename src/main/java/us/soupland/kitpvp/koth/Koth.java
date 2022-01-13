package us.soupland.kitpvp.koth;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Data;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.DocumentSerializer;
import us.soupland.kitpvp.utilities.location.LocationUtils;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.utilities.cuboid.Cuboid;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Koth implements DocumentSerializer {

    @Getter
    private static Map<String, Koth> koths = Maps.newHashMap();

    private String name;
    private Cuboid cuboid;
    private boolean active;
    private int seconds = 180, remaining = seconds;
    private Player capper;

    public Koth(String name) {
        this.name = name;

        koths.put(this.name, this);
    }

    public void delete() {
        KitPvP.getInstance().getPvPDatabase().getKoths().deleteOne(Filters.eq("name", name));
        koths.remove(name);
    }

    @Override
    public Document serialize() {
        Document document = new Document();
        document.put("name", name);

        if (cuboid != null) {
            if (cuboid.getUpperCorner() != null) {
                document.put("upperCorner", LocationUtils.getString(cuboid.getUpperCorner()));
            }
            if (cuboid.getLowerCorner() != null) {
                document.put("lowerCorner", LocationUtils.getString(cuboid.getLowerCorner()));
            }
        }
        return document;
    }

    public void save() {
        KitPvP.getInstance().getPvPDatabase().getKoths().replaceOne(Filters.eq("name", name), serialize(), new UpdateOptions());
    }

    public boolean contains(Location location) {
        return cuboid != null && cuboid.contains(location);
    }

    public static void loadKoths() {
        try (MongoCursor cursor = KitPvP.getInstance().getPvPDatabase().getKoths().find().iterator()) {
            while (cursor.hasNext()) {
                Document document = (Document) cursor;
                if (document.containsKey("upperCorner") && document.containsKey("lowerCorner")) {
                    try {
                        Koth koth = new Koth(document.getString("name"));
                        koth.setCuboid(new Cuboid(LocationUtils.getLocation(document.getString("upperCorner")), LocationUtils.getLocation(document.getString("lowerCorner"))));
                    } catch (Exception ignored) {
                        System.out.println("[KitPvP] A koth named '" + (document.containsKey("name") ? "Unknown Name" : document.getString("name") + "' could not be loaded."));
                    }
                }
            }
        }
    }

    public static List<Koth> getActiveKoths() {
        return koths.values().stream().filter(Koth::isActive).collect(Collectors.toList());
    }

    public static Koth getByName(String name) {
        return koths.get(name);
    }

}

package us.soupland.kitpvp.kits;

import lombok.Data;

@Data
public class KitStats {

    private Kit kit;
    private int uses, kills, deaths;

    public KitStats(Kit kit) {
        this.kit = kit;
    }
}
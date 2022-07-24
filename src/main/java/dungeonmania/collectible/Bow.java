package dungeonmania.collectible;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.Inventory;

public class Bow extends Buildable {
    
    /**
     * Bow Constructor
     * @param id
     * @param config
     */
    public Bow(String id, JSONObject config) {
        super(id, "bow");
        this.durability = config.getInt("bow_durability");
    }

    public static boolean checkMaterials(Inventory i) {
        if (i.getNoItemType("wood") < 1 || i.getNoItemType("arrow") < 3) {
            return false;
        }
        return true;
    }

    public static List<String> requirements() {
        return Arrays.asList("wood", "arrow", "arrow", "arrow");
    }
}

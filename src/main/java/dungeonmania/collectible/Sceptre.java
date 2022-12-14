package dungeonmania.collectible;
import java.util.Arrays;
import java.util.List;

import dungeonmania.Inventory;
import dungeonmania.SerializableJSONObject;

public class Sceptre extends Buildable {
    private boolean isActive;

    /**
     * Bow Constructor
     * @param id
     * @param config
     */
    public Sceptre(String id, SerializableJSONObject config) {
        super(id, "sceptre");
        super.setDuration(config.getInt("mind_control_duration"));
        this.isActive = false;
    }
    
    /**
     * Gets type
     * @return the type, i.e. "sceptre"
     */
    @Override
    public String getType() {
        return "sceptre";
    }

        /**
     * Returns the duration left of the active sceptre
     * @return how long more a sceptre will last
     */
    public boolean duration() {
        if (this.getDuration() != 0) {
            this.setDuration(this.getDuration() - 1);
            return true;
        }
        else {
            return false;
        }
    }


    public static boolean checkMaterials(Inventory i) {
        if (i.getNoItemType("sun_stone") < 1 || (i.getNoItemType("wood") < 1 && i.getNoItemType("arrow") < 2) || (i.getNoItemType("key") < 1 && i.getNoItemType("treasure") < 1 && i.getNoItemType("sun_stone") < 2)) {
            return false;
        }
        return true;
    }
    public static List<String> requirements(Inventory i) {
        List<String> list = Arrays.asList();
        if (i.getNoItemType("sun_stone") >= 2) {
            if (i.getNoItemType("wood") >= 1) {
                list = Arrays.asList("sun_stone", "wood");
            } else if (i.getNoItemType("arrow") >= 2) {
                list = Arrays.asList("sun_stone", "arrow", "arrow");
            }
        } else if (i.getNoItemType("treasure") >= 1) {
            if (i.getNoItemType("wood") >= 1) {
                list = Arrays.asList("sun_stone", "treasure", "wood");
            } else if (i.getNoItemType("arrow") >= 2) {
                list = Arrays.asList("sun_stone", "treasure", "arrow", "arrow");
            }
        } else if (i.getNoItemType("key") >= 1) {
            if (i.getNoItemType("wood") >= 1) {
                list = Arrays.asList("sun_stone", "key", "wood");
            } else if (i.getNoItemType("arrow") >= 2) {
                list = Arrays.asList("sun_stone", "key", "arrow", "arrow");
            }
        }
        return list;
    }

    
    /**
     * Sets whether or not Sceptre is currently in use
     * @param state indicating if sceptre is active
     */
    public void setisActive(boolean state) {
        isActive = state;
    }

    /**
     * Gets true/false value is sceptre is active
     * @return boolean if sceptre is active
     */
    public boolean getisActive() {
        return isActive;
    }
}

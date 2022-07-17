package dungeonmania;

import dungeonmania.dynamic_entity.Player;
import dungeonmania.response.models.ItemResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.collectible.*;

public class Inventory {
    
    private Player player;
    private List<Collectible> entities; 
    private List<String> buildable;
    private List<Buildable> builtItems;
    private JSONObject config;

    /**
     * Constructor for Inventory
     * @param player
     */
    public Inventory(Player player, JSONObject config) {
        this.setPlayer(player);
        entities = new ArrayList<>();
        builtItems = new ArrayList<>();
        this.config = config;
    }

    /**
     * Puts collectibles in Player
     * @param entity
     * @param player
     */
    public void put(Entity entity, Player player){
        if (entity instanceof Collectible) {
            Collectible ent = (Collectible) entity;
            ent.setPlayer(player);
            this.entities.add(ent);
        }
    }

    /**
     * Sets Player
     * @param player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets item responses
     * @return responses of items
     */
    public List<ItemResponse> getItemResponses() {
        List<ItemResponse> itemResponses = entities.stream()
                                                   .map(Collectible::toItemResponse)
                                                   .collect(Collectors.toList());
        for (Buildable buildable : builtItems) {
            itemResponses.add(new ItemResponse(buildable.getId(), buildable.getType()));
        }

        return  itemResponses;
    }

    /**
     * get inventory
     * @return the inventory
     */
    public List<Collectible> getInven() {
        return entities;
    }

    /**
     * Gets number of items
     * @param type
     * @return item and corresponding number
     */
    public int getNoItemType(String type) {
        int number = 0;
        for (Collectible item : entities) {
            if (item.getType().equals(type)) {
                number++;
            }
        }
        return number;
    }

    /**
     * Gets items
     * @param type
     * @return the items
     */
    public Collectible getItem(String type) {
        for (Collectible item : entities) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Get collectibles by id
     * @param id
     * @return collectibles according to id
     */
    public Collectible getItemById(String id) {
        for (Collectible item : entities) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Gets key
     * @param keyId
     * @return the key
     */
    public Key getKey(int keyId) {
        for (Collectible item : entities) {
            if (item.getType().equals("key")) {
                Key itm = (Key) item;
                if (itm.getKeyId() == keyId) {
                    return itm;
                }
            }
        }
        return null;
    }


    /**
     * Check buildable materials present
     * @param buildable
     * @return boolean of current materials collected meets requirements
     */
    public boolean CheckMaterials(String buildable) {
        switch (buildable) {
            case "bow":
                if (getNoItemType("wood") < 1 || getNoItemType("arrow") < 3) {
                    return false;
                }
                return true;    
            case "shield":
                if (getNoItemType("wood") < 2 || (getNoItemType("treasure") < 1 && getNoItemType("key") < 1)) {
                return false;
                }
                return true;
            default:
                return false;
        }
    }

    /**
     * Remove an item
     * @param itemToRemove
     */
    public void removeItem(String itemToRemove) {
        for (Collectible item : entities) {
            if (item.getType().equals(itemToRemove)) {
                entities.remove(item);
                break;
            }
        }
    }

    /**
     * Build an item
     * @param buildable
     * @param id
     * @return item built
     */
    public boolean buildItem(String buildable, String id) {
        if (CheckMaterials(buildable) && buildable.equals("bow")) {
            //make bow
            builtItems.add(new Bow(id, config));
            removeItem("wood");
            removeItem("arrow");
            removeItem("arrow");
            removeItem("arrow");
            return true;
        }
        if (CheckMaterials(buildable) && buildable.equals("shield")) {
            //make shield
            builtItems.add(new Shield(id, config));
            removeItem("wood");
            removeItem("wood");
            if (getNoItemType("treasure") >= 1) {
                removeItem("treasure");
            } else if (getNoItemType("key") >= 1) {
                removeItem("key");
            return true;
            }
        }
        return false;
    }

    /**
     * Gets collectable items
     * @return items that are collectable
     */
    public List<Collectible> getCollectableItems() {
        return entities;
    }

    /**
     * Gets buildable items
     * @return items that are buildable
     */
    public List<Buildable> getBuildableItems() {
        return builtItems;
    }

    /**
     * Reduce state of duarability for relevant entities
     * @param id
     */
    public void reduceDurability(String id) {
        if (buildable.contains(id)) {
            // Buildable item
            Buildable item = builtItems.stream().filter(x -> x.getId().equals(id)).collect(Collectors.toList()).get(0);
            int currentDurability = item.getDurability();
            item.setDurability(currentDurability - 1);
        } else {
            // Collectible item
            Collectible item = entities.stream().filter(x -> x.getId().equals(id)).collect(Collectors.toList()).get(0);
            Sword itemSword = ((Sword)item);
            int currentDurability = itemSword.getDurability();
            itemSword.setDurability(currentDurability - 1);
        }
    }

    /**
     * Remove items that have been broken
     */
    public void removeBrokenItems() {
        // Deleting broken shields and bows
        builtItems = builtItems.stream().filter(item -> item.getDurability() != 0).collect(Collectors.toList());
        entities = entities.stream().filter(item -> (item instanceof Sword) && (((Sword)item).getDurability() != 0)).collect(Collectors.toList());
    }
}
package dungeonmania.dynamic_entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.Boulder;
import dungeonmania.Entity;
import dungeonmania.collectible.Bomb;
import dungeonmania.collectible.Collectible;
import dungeonmania.collectible.Consumable;
import dungeonmania.collectible.InvincibilityPotion;
import dungeonmania.collectible.InvisibilityPotion;
import dungeonmania.collectible.Key;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.static_entity.ActiveBomb;
import dungeonmania.static_entity.StaticEntity;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import javassist.expr.NewArray;
import dungeonmania.Inventory;
/**
 * Entity that is controlled by the Player.
 */
public class Player extends DynamicEntity {
    private Inventory inventory;
    private List<String> useableItems = Arrays.asList("bomb", "invincibility_potion", "invisibility_potion", null);
    private List<Collectible> potionQueue = new ArrayList<>();
    private String status = "NONE";

    // Total treasure collected - not current treasure
    private int treasure_collected = 0;
    private int enemies_defeated = 0;

    /**
     * @param id
     * @param xy
     * @param config
     */
    public Player(String id, Position xy, JSONObject config) {
        super(id, "player", xy);
        this.attack = config.getDouble("player_attack");
        this.health = config.getDouble("player_health");
        inventory = new Inventory(this, config);
        this.status = "NONE";
    }

    @Override
    public String getType() {
        return "player";
    }

    public List<String> getBuildables() {
        List<String> buildables = new ArrayList<>();
        if (inventory.CheckMaterials("bow")) {
            buildables.add("bow");
        }
        if (inventory.CheckMaterials("shield")) {
            buildables.add("shield");
        }
        return buildables;
    }

    /**
     * Updates the new position of Player given a direction
     */
    public void updatePos(Direction d, List<Entity> l) {
        Position curr = this.getPosition();

        Position nextPosition = curr.translateBy(d);
        // Check next position for obstacles/issues
        List <Entity> collides = l.stream().filter(entity -> entity.getPosition().equals(nextPosition)).collect(Collectors.toList());
        if (collides.stream().anyMatch(entity -> !entity.collide(this))) {
            return;
        }
        this.setPosition(nextPosition);
    }

    public void pickUp(List<Entity> entities) {
        List<Entity> toRemove = new ArrayList<>();
        entities.stream()
                .filter(entity -> this.getPosition().equals(entity.getPosition())) // same position
                .filter(entity -> entity instanceof Collectible)
                .forEach(collectible -> {
                    if (inventory.getNoItemType("key") > 0 && collectible instanceof Key) {
                        return;
                    }
                    if (collectible.getType().equals("treasure")) {
                        treasure_collected++;
                    }
                    this.inventory.put(collectible, this);
                    toRemove.add(collectible);
                });
        entities.removeAll(toRemove);
    }

    public void consumePotion(Collectible Potion) {
        potionQueue.add(Potion);
    }

    public void tickPotionEffects() {
        if (potionQueue.size() == 0) {
            this.status = "NONE";
            return;
        }
        else {
            while (potionQueue.size() != 0) {

                if (potionQueue.get(0) instanceof InvincibilityPotion) {
                    InvincibilityPotion Potion = (InvincibilityPotion) potionQueue.get(0);
                    if (Potion.potency()) {
                        this.status = "INVINCIBLE";
                        return;
                    }
                } else if (potionQueue.get(0) instanceof InvisibilityPotion){
                    InvisibilityPotion Potion = (InvisibilityPotion) potionQueue.get(0);
                    if (Potion.potency()) {
                        this.status = "INVISIBLE";
                        return;
                    }
                } 
                potionQueue.remove(0);
            }
            this.status = "NONE";
            return;
        }
    }

    public String getStatus() {
        return this.status;
    }

    public List<Collectible> getInventoryList() {
        return inventory.getInven();
    }

    /**
     * Given an item name, check if the player has the 
     * item in inventory or not.
     * @param item (Collectable Entity)
     * @return True if player has item, and false otherwise.
     */
    public boolean hasItem(String item) {
        return !(inventory.getItem(item) == null);
    }

    public int getEnemiesDefeated() {
        return enemies_defeated;
    }

    public void addEnemiesDefeated() {
        enemies_defeated++;
    }

    /**
     * Given an item name, checks in the player inventory, and if exists,
     * return the item as a collectable entity.
     * @param item (String)
     * @return The item (Collectable Entity)
     */
    public Collectible getItem(String item) {
        return inventory.getItem(item);
    }

    public Collectible getItemById(String id) {
        return inventory.getItemById(id);
    }

    public int totalTreasureCollected() { return treasure_collected; }

    public Inventory getInventory() {
        return inventory;
    }

    public void removeItem(Collectible item) {
        inventory.removeItem(item.getType());
    }

    public void removeBrokenItems() {
        inventory.removeBrokenItems();
    }
    
    public Key getKey() {
        return (Key) inventory.getItem("key");
    }

    public boolean hasSword() {
        return  (inventory.getItem("sword") != null);
    }

    public void removeKey() {
        inventory.removeItem("key");
    }

    public Collectible getCurrentPotion() {
        return potionQueue.get(0);
    }
}

package dungeonmania.dynamic_entity.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Inventory;
import dungeonmania.collectible.Bow;
import dungeonmania.collectible.Buildable;
import dungeonmania.collectible.Collectible;
import dungeonmania.collectible.InvincibilityPotion;
import dungeonmania.collectible.InvisibilityPotion;
import dungeonmania.collectible.MidnightArmour;
import dungeonmania.collectible.Shield;
import dungeonmania.collectible.Sword;
import dungeonmania.dynamic_entity.DynamicEntity;
import dungeonmania.dynamic_entity.Player;

public class BattleRecord implements Serializable{
    private DynamicEntity enemy;
    private List <RoundRecord> rounds = new ArrayList<>();
    private double initialPlayerHealth;
    private double initialEnemyHealth;

    /**
     * BattleRecord Constructor
     * @param enemy
     * @param player
     */
    public BattleRecord(DynamicEntity enemy, DynamicEntity player) {
        this.enemy = enemy;
        this.initialPlayerHealth = player.getHealth();
        this.initialEnemyHealth = enemy.getHealth();
        // One round Battle if Player is INVINCIBLE
        if (((Player)player).getStatus().equals("INVINCIBLE")) {
            Collectible currentPotion = ((Player)player).getCurrentPotion();
            List<Object> itemUsed = new ArrayList<>();
            itemUsed.add(currentPotion);
            addRoundRecord(0, -1 * enemy.getHealth(), itemUsed);
            enemy.setHealth(0);
        } else {
            startBattle(player);
        }
    }
    
    /**
     * Adds a RoundRecord to BattleRecord
     * @param double, double, List <Object>
     * @return List<List<Object>>
     */
    private void addRoundRecord(double changePlayerHealth, double changeEnemyHealth, List <Object> battleItems) {
        // Convert battleItems into ItemRecords

        List<ItemRecord> weaponsUsed = new ArrayList<>();
        battleItems.stream().forEach(
            item -> {
                ItemRecord newItem = null;
                if (item instanceof Sword) {
                    Sword temp = (Sword)item;
                    newItem = new ItemRecord(temp.getId(), temp.getType());
                } else if (item instanceof Buildable) {
                    Buildable temp = (Buildable)item;
                    newItem = new ItemRecord(temp.getId(), temp.getType());
                } else if (item instanceof InvincibilityPotion || item instanceof InvisibilityPotion) {
                    Collectible temp = (Collectible)item;
                    newItem = new ItemRecord(temp.getId(), temp.getType());
                }
                weaponsUsed.add(newItem);
            }
        );
        RoundRecord newRecord = new RoundRecord(changePlayerHealth, changeEnemyHealth, weaponsUsed);
        rounds.add(newRecord);
    }

    /**
     * Starts a Battle with Player and adds relevant Records
     * @param Player
     */
    private void startBattle(DynamicEntity player) {
        double enemyAttack = enemy.getAttack();
        double playerAttack = player.getAttack();

        // Default values
        double bowModifier = 1;
        double swordAdd = 0;
        double shieldMinus = 0;

        double newEnemyHealth = initialEnemyHealth;
        double newPlayerHealth = initialPlayerHealth;
        while (newEnemyHealth > 0 && newPlayerHealth > 0) {

            // itemsInRoundUsed will have { List of Swords, List of Bows, List of Shields, List of MidnightArmours}
            List <List<Object>> itemsInRoundUsed = itemsAvaliable(player);
            // Calculate modifiers
            if (itemsInRoundUsed.get(0).size() != 0) {
                List<Object> swordsUsed = itemsInRoundUsed.get(0);
                Sword temp = (Sword)swordsUsed.get(0);
                int swordAttack = temp.getAtack();
                swordAdd = itemsInRoundUsed.get(0).size() * swordAttack;
            }

            if (itemsInRoundUsed.get(1).size() != 0) {
                bowModifier = itemsInRoundUsed.get(1).size() + 1;
            }
            if (itemsInRoundUsed.get(2).size() != 0) {
                List<Object> shieldsUsed = itemsInRoundUsed.get(2);
                Shield temp = (Shield)shieldsUsed.get(0);
                int shieldDefense = temp.getShieldDefense();
                shieldMinus = itemsInRoundUsed.get(2).size() * shieldDefense;
            }
            if (itemsInRoundUsed.get(3).size() != 0) {
                List<Object> MidnightArmourUsed = itemsInRoundUsed.get(3);
                MidnightArmour temp = (MidnightArmour)MidnightArmourUsed.get(0);
                enemyAttack -= temp.getDefence() * MidnightArmourUsed.size();
                playerAttack += temp.getAttack() * MidnightArmourUsed.size();
            }

            double modifiedPlayerDamage = ((bowModifier * (playerAttack + swordAdd))/5);
            double modifiedEnemyDamage = ((enemyAttack - shieldMinus) / 10);
            newEnemyHealth = enemy.newHealth(modifiedPlayerDamage);
            newPlayerHealth = player.getHealth() - modifiedEnemyDamage;
            // Update durability of equipment
            updateDurability(itemsInRoundUsed, (Player)player);

            // Convert itemsInRoundUsed into single list of items used
            List <Object> battleItems = convertToList(itemsInRoundUsed);
            addRoundRecord(-1 * modifiedEnemyDamage, -1 * modifiedPlayerDamage, battleItems);
            // Remove broken items
            ((Player)player).removeBrokenItems();
            enemy.setHealth(newEnemyHealth);
            player.setHealth(newPlayerHealth);
        }
        if (enemy.getHealth() <= 0) {
            Player p = (Player) player;
            p.addEnemiesDefeated();
        }
    }

    /**
     * Adds a RoundRecord to BattleRecord
     * @param List<List<Object>>, Player
     */
    private void updateDurability(List<List<Object>> itemsUsed, Player player) {
        // Unchecked Type cast, itemsUsed has been safely type checked from method itemsAvaliable
        List<Sword> SwordsUsed = (List<Sword>)(List<?>) itemsUsed.get(0);
        List<Buildable> BowsUsed = (List<Buildable>)(List<?>) itemsUsed.get(1);
        List<Buildable> ShieldsUsed = (List<Buildable>)(List<?>) itemsUsed.get(2);
        
        Inventory i = player.getInventory();
        SwordsUsed.stream().forEach(
            x -> {
                String id = x.getId();
                i.reduceDurability("sword", id);
            }
        );

        BowsUsed.stream().forEach(
            x -> {
                String id = x.getId();
                i.reduceDurability("bow", id);
            }
        );

        ShieldsUsed.stream().forEach(
            x -> {
                String id = x.getId();
                i.reduceDurability("shield", id);
            }
        );

    }

    /**
     * Returns a list of the list of items avaliable in the form { List of Swords, List of Bows, List of Shields} 
     * @param player
     * @return List<List<Object>>
     */
    private List<List<Object>> itemsAvaliable(DynamicEntity player) {

        List<List<Object>> listsOfItems = new ArrayList<>();
        Inventory i = ((Player) player).getInventory();
        List <Collectible> CollectableItems = i.getCollectableItems();
        List <Buildable> BuildableItems = i.getBuildableItems();

        listsOfItems.add(CollectableItems.stream().filter(item -> item instanceof Sword).collect(Collectors.toList()));
        listsOfItems.add(BuildableItems.stream().filter(item -> item instanceof Bow).collect(Collectors.toList()));
        listsOfItems.add(BuildableItems.stream().filter(item -> item instanceof Shield).collect(Collectors.toList()));
        listsOfItems.add(BuildableItems.stream().filter(item -> item instanceof MidnightArmour).collect(Collectors.toList()));
        return listsOfItems;
    }

    /**
     * Converts to list
     * @param listsOfItems
     * @return list
     */
    private List <Object> convertToList(List<List<Object>> listsOfItems) {
        return listsOfItems.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * Gets rounds
     * @return the rounds
     */
    public List<RoundRecord> getRounds() {
        return rounds; 
    }

    /**
     * Gets initial player health
     * @return the initial player health
     */
    public double getInitialPlayerHealth() {
        return initialPlayerHealth;
    }

    /**
     * Gets initial enemy health
     * @return the initial enemy health
     */
    public double getInitialEnemyHealth() {
        return initialEnemyHealth;
    }

    /**
     * gets enemy
     * @return the enemy
     */
    public DynamicEntity getEnemy() {
        return enemy;
    }
}

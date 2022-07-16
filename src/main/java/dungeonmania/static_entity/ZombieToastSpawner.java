package dungeonmania.static_entity;

import dungeonmania.DungeonManiaController;
import dungeonmania.Entity;
import dungeonmania.dynamic_entity.Player;
import dungeonmania.dynamic_entity.Spider;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Position;

/**
 * Acts like spawn points for zombie toasts. Has following properties:
 *      - Spawns zombie toasts onto available cardinally adjacent squares to the spawner's location.
 *      - Spawners can be destroyed by a Player (that has a weapon) only if they are on a cardinally adjacent square to it.
 */
public class ZombieToastSpawner extends StaticEntity {
    DungeonManiaController dungeon;
    int spawnRate;
    int currentTick;
    int zombieAttack;
    int zombieHealth;

    public ZombieToastSpawner(DungeonManiaController dungeon, String id, Position xy, int spawnRate, int zombieAttack, int zombieHealth) {
        super(id, "zombie_toast_spawner", xy);
        this.dungeon = dungeon;
        currentTick = 0;
        this.spawnRate = spawnRate;
        this.zombieAttack = zombieAttack;
        this.zombieHealth = zombieHealth;
    }

    @Override
    public void interact(Player player) throws InvalidActionException {
        player.getPosition();
        // check cardinally adjacent
        if (!Position.isAdjacent(this.getPosition(), player.getPosition())) {
            throw new InvalidActionException("Not cardinally adjacent to spawner");
        }
        // check has weapon
        else if (!player.hasSword()) {
            throw new InvalidActionException("Doesn't have a weapon");
        }
        dungeon.removeEntity(this.getId());
    }
    @Override
    public String getType() {
        return "zombie_toast_spawner";
    }
    
    public void tick() {
        currentTick += 1;
        if (currentTick == spawnRate) {
            currentTick = 0;
            if (dungeon.checkStaticCollision(new Position(this.getPosition().getX() + 1, this.getPosition().getY())) == null) {
                dungeon.spawnToast(zombieAttack, zombieHealth, new Position(this.getPosition().getX() + 1, this.getPosition().getY()));
                return;
            }
            else if (dungeon.checkStaticCollision(new Position(this.getPosition().getX() - 1, this.getPosition().getY())) == null) {
                dungeon.spawnToast(zombieAttack, zombieHealth, new Position(this.getPosition().getX() - 1, this.getPosition().getY()));
                return;
            }
            else if (dungeon.checkStaticCollision(new Position(this.getPosition().getX(), this.getPosition().getY() + 1)) == null) {
                dungeon.spawnToast(zombieAttack, zombieHealth, new Position(this.getPosition().getX(), this.getPosition().getY() + 1));
                return;
            }
            else if (dungeon.checkStaticCollision(new Position(this.getPosition().getX(), this.getPosition().getY() - 1)) == null) {
                dungeon.spawnToast(zombieAttack, zombieHealth, new Position(this.getPosition().getX(), this.getPosition().getY() - 1));
                return;
            }
        }
    }
    public boolean collide(Entity entity) {
        return entity instanceof Spider;
    }

}

package dungeonmania.dynamic_entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dungeonmania.Entity;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

/**
 * Are among the enemy entities that WILL harm Player. Has following properties:
 *      - Spawn only at Zombie Toast spawners.
 *      - Move in RANDOM directions and are constricted to same conditions as Player movement, however CANNOT use portals.
 */
public class ZombieToast extends DynamicEntity {
    @Override
    public String getType() {
        return "zombie_toast";
    }

    public ZombieToast(String id, Position xy) {
        super(id, xy);
    }
    
    public void updatePos(Direction d, List<Entity> l) {
        List <Position> newPositions = new ArrayList<>();
        // Check up

        // Check down

        // Check left
        
        // Check right
        
        // All cardinal directions are invalid
        if (newPositions.size() == 0) {
            return;
        }
        Random rand = new Random();
        int result = rand.nextInt(newPositions.size());
        
        this.setPosition(newPositions.get(result));
    }
}

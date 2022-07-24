package dungeonmania.static_entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.Entity;
import dungeonmania.util.Position;

public class ActiveBomb extends StaticEntity{
    /**
     * ActiveBomb Constructor
     * @param id
     * @param xy
     */
    public ActiveBomb(String id, Position xy) {
        super(id, "bomb", xy);
    }
    
    /**
     * Collision detection
     * @param entity
     * @return boolean of collision status
     */
    @Override
    public boolean collide(Entity entity) {
        return false;
    }
    
    /**
     * Gets type
     * @return the type, i.e. "bomb"
     */
    @Override
    public String getType() {
        return "bomb";
    }

    /**
     * Explode the bomb
     * @param entities
     * @param config
     * @return update of current entities
     */
    public List<Entity> explode(List<Entity> entities, JSONObject config) {
        int radius = config.getInt("bomb_radius");
        List<Entity> toRemove = new ArrayList<>();
        toRemove.add(this);
        List<Position> positions = getPosition().getAdjacentPositions();
        List<Position> copy = new ArrayList<>();

        for (int i = 1; i < radius; i++ ) {
            for (Position position : positions) {
                List<Position> adjacentPositions = position.getAdjacentPositions();
                for (Position pos : adjacentPositions) {
                    if (!positions.contains(pos)) {
                        copy.add(pos);
                    }
                }
            }
            positions.addAll(copy);
        }
        for (Position position : positions) {
            for (Entity entity : entities) {
                if (!entity.getType().equals("player") && positions.contains(entity.getPosition())) {
                    toRemove.add(entity);
                }
            }
        }
        return toRemove;
    }
}
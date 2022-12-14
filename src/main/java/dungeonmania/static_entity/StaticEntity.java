package dungeonmania.static_entity;

import dungeonmania.Entity;
import dungeonmania.util.Position;

/**
 * Entities that DO NOT move in dungeon.
 */
public abstract class StaticEntity extends Entity {
    /**
     * StaticEntity Constructor
     * @param id
     * @param type
     * @param xy
     */
    public StaticEntity(String id, String type, Position xy) {
        super(id, type, xy);
    }

    public abstract boolean collide(Entity entity);

    public abstract String getType();
}

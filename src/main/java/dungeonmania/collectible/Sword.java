package dungeonmania.collectible;

import dungeonmania.util.Position;

/**
 * Are among the collectible entities and is stored as Player inventory. Has following properties:
 *      - Can be collected when Player moves onto the square it is on.
 *      - Used as a melee weapon during battles.
 *      - Damage dealt by the weapon is increased by an additive factor.
 *      - Has a specific durability limit and will deteriorate gradually after each battle. Once the limit is reached, 
 *          it is no longer useable.
 */
public class Sword extends Collectible {

    public Sword(String id, Position xy) {
        super(id, xy);
    }
    
}

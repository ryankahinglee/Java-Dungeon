package dungeonmania;

/**
 * Are among the collectible entities and is stored as Player inventory. Has following properties:
 *      - Can be collected when Player moves onto the square it is on.
 *      - When Player uses it, it is placed in current square Player is on and removed from inventory.
 *      - Once used it CANNOT be picked up again.
 *      - Will only detonate if placed on a cardinally adjacent square to an active switch, 
 *          if switch is NOT ACTIVE it will NOT detonate unless switch is activated later.
 *      - If detonated it will destroy all entities that are DIAGONALLY and CARDINALLY adjacent cells to it and will
 *          NOT harm the Player.
 */
public class Bomb extends Collectible {
    
}

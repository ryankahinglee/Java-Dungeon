package dungeonmania.static_entity;

/**
 * A door co-exists with a corresponding key. Has following properties:
 *      - If player reaches it and has the corresponding key in inventory, Player can use key to move through it.
 *      - Once it is opened, it remains opened.
 */
public class Door extends StaticEntity {

    @Override
    public String getType() {
        return "door";
    }
}
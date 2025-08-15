package nub.wi1helm.template.npc.actions;

import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;


public abstract class StaticMenuAction extends AbstractAction {

    private final Inventory inventory;

    // Static means that the inventory is created once and will remain the same.
    public StaticMenuAction(int id, long delayToNext, boolean requiresPlayerClick, Inventory inventory) {
        super(id, delayToNext, requiresPlayerClick);
        this.inventory = inventory;
    }

    @Override
    public void execute(Player player) {
        if (player != null) {
            player.openInventory(inventory);
        }
    }
}

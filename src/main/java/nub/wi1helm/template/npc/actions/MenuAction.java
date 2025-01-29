package nub.wi1helm.template.npc.actions;

import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import rip.snicon.compass.npc.AbstractAction;

public abstract class MenuAction extends AbstractAction {

    private final Inventory inventory;

    public MenuAction(int id, long delayToNext, boolean requiresPlayerClick, Inventory inventory) {
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

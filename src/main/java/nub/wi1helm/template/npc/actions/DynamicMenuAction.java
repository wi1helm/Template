package nub.wi1helm.template.npc.actions;

import net.minestom.server.entity.Player;
import nub.wi1helm.template.inventory.TemplateInventory;

public abstract class DynamicMenuAction extends AbstractAction {

    private final TemplateInventory inventory;


    // Dynamic means that everytime its run its recontructed. Works well when the gui needs to dynamicly update data on open.
    public DynamicMenuAction(int id, long delayToNext, boolean requiresPlayerClick, TemplateInventory inventory) {
        super(id, delayToNext, requiresPlayerClick);
        this.inventory = inventory;
    }

    @Override
    public void execute(Player player) {
        if (player != null) {
            player.openInventory(inventory.constructInventory(player));
        }
    }
}

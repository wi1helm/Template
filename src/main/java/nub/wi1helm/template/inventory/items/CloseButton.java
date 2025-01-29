package nub.wi1helm.template.inventory.items;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import nub.wi1helm.template.inventory.TemplateInventoryEvent;
import nub.wi1helm.template.inventory.TemplateItem;


public class CloseButton extends TemplateItem {

    public CloseButton() {
        super(Material.BARRIER);

        setName(MiniMessage.miniMessage().deserialize("<gray>» <red>Close</red> «</gray>"));
    }


    @Override
    protected void initialize() {

    }

    @Override
    protected void personalize(Player player) {

    }

    @Override
    public void onUse(TemplateInventoryEvent event) {
        event.getPlayer().closeInventory();
    }

    @Override
    public void onDrop(TemplateInventoryEvent event) {

    }
}
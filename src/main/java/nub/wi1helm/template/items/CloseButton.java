package nub.wi1helm.template.items;

import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import rip.snicon.compass.inventory.TemplateItem;
import rip.snicon.compass.utils.TextUtils;

public class CloseButton extends TemplateItem {

    public CloseButton() {
        super(Material.BARRIER);

        setName(TextUtils.convertStringToComponent("<gray>» <red>Close</red> «</gray>"));
    }


    @Override
    protected void initialize() {

    }

    @Override
    protected void personalize(Player player) {

    }

    @Override
    public void onUse(Player player) {
        player.closeInventory();
    }

    @Override
    public void onDrop(Player player) {

    }
}
package nub.wi1helm.template.items;

import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import rip.snicon.compass.inventory.TemplateItem;

public class BackgroundItem extends TemplateItem {

    public BackgroundItem() {
        super(Material.GRAY_STAINED_GLASS_PANE);

        hideTooltip();
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected void personalize(Player player) {

    }

    @Override
    public void onUse(Player player) {

    }

    @Override
    public void onDrop(Player player) {

    }
}
package nub.wi1helm.template.ai.modifiers;

import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import nub.wi1helm.template.ai.TempPNode;
import nub.wi1helm.template.ai.TempPNodeModifiers;
import nub.wi1helm.template.ai.actions.SwimAction;
import nub.wi1helm.template.ai.actions.UnsetAction;

public class SwimModifier implements TempPNodeModifiers {
    @Override
    public void modify(Entity entity, TempPNode node) {
        if (!(node.action() instanceof UnsetAction)) return;
        Instance instance = entity.getInstance();
        if (instance == null) return;

        int x = node.blockX();
        int y = node.blockY() - 1;
        int z = node.blockZ();
        if (instance.getBlock(x, y, z).equals(Block.WATER)) {
            node.setAction(new SwimAction());
        }
    }

    @Override
    public boolean valid(Entity entity, TempPNode node) {
        Instance instance = entity.getInstance();
        if (instance == null) return false;
        int x = node.blockX();
        int y = node.blockY() - 1;
        int z = node.blockZ();
        return instance.getBlock(x, y, z).equals(Block.WATER);
    }
}

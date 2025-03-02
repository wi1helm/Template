package nub.wi1helm.template.ai.modifiers;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import nub.wi1helm.template.ai.TempPNode;
import nub.wi1helm.template.ai.TempPNodeModifiers;
import nub.wi1helm.template.ai.actions.UnsetAction;
import nub.wi1helm.template.ai.actions.WalkAction;

public class WalkModifier implements TempPNodeModifiers {
    private static final double EPSILON = 1e-6;

    @Override
    public void modify(Entity entity, TempPNode node) {
        // Only process nodes with unset actions
        if (!(node.action() instanceof UnsetAction)) {
            return;
        }

        Instance instance = entity.getInstance();
        if (instance == null) {
            node.setAction(null);
            return;
        }
        node.setPoint(snapToBlockPoint(node.point()));
        if (!valid(entity,node)) {
            node.valid(false);
        }

        if (collidesAtPosition(entity, node.point())) {
            node.valid(false);
        }


        // Get reference point (parent node or entity position)
        Point referencePoint;
        TempPNode parent = node.parent();
        if (parent != null) {
            referencePoint = parent.point();
        } else {
            referencePoint = entity.getPosition();
        }
        referencePoint = snapToBlockPoint(referencePoint);
        // WalkModifier's UNIQUE CONDITION:
        // Apply only when:
        // 1. Node is at same Y level as reference point
        // 2. There's a solid block below the node
        double dy = node.point().y() - referencePoint.y();
        if (Math.abs(dy) <= EPSILON && valid(entity, node)) {
            node.valid(true);
            // Check if entity can move to this position without collision
            if (canMoveWithoutCollision(entity, referencePoint, node.point())) {
                node.setAction(new WalkAction());
                // Normal walking cost = distance
                double distance = referencePoint.distance(node.point());
                node.setCost(node.cost() + distance);
            } else {
                // If there's potential collision, increase the cost
                double distance = referencePoint.distance(node.point());
                node.setCost(node.cost() + distance + 5.0);
                // Still set it as a walk action, but it'll be more costly
                node.setAction(new WalkAction());
            }
        }
    }

    @Override
    public boolean valid(Entity entity, TempPNode node) {
        Instance instance = entity.getInstance();
        if (instance == null) return false;

        // Node is valid for walking if there's a solid block below it
        int x = node.blockX();
        int y = node.blockY() - 1;
        int z = node.blockZ();
        Block block = instance.getBlock(x, y, z);
        return block.isSolid();
    }
}
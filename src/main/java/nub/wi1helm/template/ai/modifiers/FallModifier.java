package nub.wi1helm.template.ai.modifiers;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import nub.wi1helm.template.ai.TempPNode;
import nub.wi1helm.template.ai.TempPNodeModifiers;
import nub.wi1helm.template.ai.actions.*;

public class FallModifier implements TempPNodeModifiers {
    private static final double MAX_FALL_DISTANCE = 10.0; // Maximum fall distance
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

        // Get reference point (parent node or entity position)
        Point referencePoint;
        TempPNode parent = node.parent();
        if (parent != null) {
            referencePoint = parent.point();

        } else {
            referencePoint = entity.getPosition();
        }
        referencePoint = snapToBlockPoint(referencePoint);


        // Calculate height difference
        double heightDifference = node.point().y() - referencePoint.y();

        // FallModifier's UNIQUE CONDITION:
        // Apply only when:
        // 1. Node is lower than reference point by up to MAX_FALL_DISTANCE
        // 2. Node has a solid block beneath it (checked in valid method)
        if (heightDifference < 0 && Math.abs(heightDifference) <= MAX_FALL_DISTANCE) {
            // Find the actual landing position after falling
            Point landingPosition = findLandingPosition(entity, node.point());
            if (landingPosition == null) {
                // No valid landing position found
                node.setAction(new DebugAction());
                return;
            }
            landingPosition = snapToBlockPoint(landingPosition);
            // Check for collisions at landing position
            if (!collidesAtPosition(entity, landingPosition)) {
                // Create an edge node at the same X,Z as landing but at the original Y height
                // This represents the initial fall point before landing
                Point edgePoint = landingPosition.withY(referencePoint.y());
                TempPNode edgeNode = new TempPNode(
                        edgePoint,
                        0,
                        heuristic(edgePoint, landingPosition),
                        new FallAction(),
                        parent
                );

                // Set the current node as the walking node after the fall
                node.setAction(new WalkAction());
                node.setParent(edgeNode);

                // Adjust cost based on fall distance - higher falls are more costly
                double fallCost = 5 + Math.abs(heightDifference) * 0.5;
                node.setCost(node.cost() + fallCost);

                // Make sure the node position is set to the landing position
                node.setPoint(landingPosition);
                node.valid(true);
            }
        }
    }

    /**
     * Find the actual landing position after falling
     */
    private Point findLandingPosition(Entity entity, Point startPoint) {
        Instance instance = entity.getInstance();
        if (instance == null) return null;

        int x = (int) Math.floor(startPoint.x());
        int z = (int) Math.floor(startPoint.z());
        int startY = (int) Math.floor(startPoint.y());

        // Look downward to find first solid block
        for (int y = startY - 1; y >= startY - MAX_FALL_DISTANCE; y--) {
            Block block = instance.getBlock(x, y, z);
            if (block.isSolid()) {
                // Return position on top of this block
                return startPoint.withY(y + 1);
            }
        }

        // No solid block found within range
        return null;
    }

    @Override
    public boolean valid(Entity entity, TempPNode node) {
        Instance instance = entity.getInstance();
        if (instance == null) return false;

        // Get reference point (parent node or entity position)
        Point referencePoint;
        TempPNode parent = node.parent();
        if (parent != null) {
            referencePoint = parent.point();
        } else {
            referencePoint = entity.getPosition();
        }
        referencePoint = snapToBlockPoint(referencePoint);

        // Check height difference
        double heightDifference = node.point().y() - referencePoint.y();
        if (heightDifference >= 0) {
            return false; // Not falling if we're going up or staying level
        }

        // Node is valid for falling if there's a solid block beneath it
        int x = node.blockX();
        int z = node.blockZ();

        // Check if there's a solid block within MAX_FALL_DISTANCE down
        for (int y = node.blockY() - 1; y >= node.blockY() - MAX_FALL_DISTANCE; y--) {
            Block block = instance.getBlock(x, y, z);
            // Make sure there's no block at entity's current Y level
            Block blockAtEntityY = instance.getBlock(x, referencePoint.blockY(), z);
            if (block.isSolid() && !blockAtEntityY.isSolid()) {
                return true;
            }
        }

        return false;
    }
}
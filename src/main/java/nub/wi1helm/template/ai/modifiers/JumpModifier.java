package nub.wi1helm.template.ai.modifiers;

import net.minestom.server.entity.Entity;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import nub.wi1helm.template.ai.TempPNode;
import nub.wi1helm.template.ai.TempPNodeModifiers;
import nub.wi1helm.template.ai.actions.JumpAction;
import nub.wi1helm.template.ai.actions.UnsetAction;
import nub.wi1helm.template.ai.actions.WalkAction;

public class JumpModifier implements TempPNodeModifiers {
    private static final double JUMP_HEIGHT = 1.25; // Maximum jumpable height in Minecraft (1 block + 1/4)
    private static final double EPSILON = 0.1;      // Small tolerance for height comparison
    private static final double DIAGONAL_JUMP_PENALTY = 3.0; // Penalty for diagonal jumps

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

        // Calculate height difference between node and reference
        double heightDifference = node.point().y() - referencePoint.y();

        // JumpModifier's UNIQUE CONDITION:
        // Apply only when:
        // 1. Node is higher than reference point by up to JUMP_HEIGHT
        // 2. There's a solid block below the node
        if (heightDifference > 0 && heightDifference <= JUMP_HEIGHT && valid(entity, node)) {
            // Check for collisions during the jump
            if (!collidesAtPosition(entity, node.point())) {
                // Create the jump node at the same X,Z as reference but with the target Y
                // This represents the initial jump point before walking
                Point jumpPoint = referencePoint;
                TempPNode jumpNode = new TempPNode(
                        jumpPoint,
                        0,
                        heuristic(jumpPoint, node.point()),
                        new JumpAction(),
                        parent
                );

                if (!valid(entity,jumpNode)) {
                    node.valid(false);
                }

                // Set the current node as a walk node after the jump
                node.setAction(new WalkAction());
                node.setParent(jumpNode);

                // Base jump cost (higher jumps cost more)
                double jumpCost = 1.0;

                // Check if this is a diagonal jump
                boolean isDiagonal = isDiagonalJump(referencePoint, node.point());
                if (isDiagonal) {
                    // Apply penalty for diagonal jumps
                    jumpCost += DIAGONAL_JUMP_PENALTY;
                }

                // Set the cost
                node.setCost(node.cost() + jumpCost);
                node.valid(true);
            }
        }
    }

    /**
     * Determines if the jump is diagonal (changing in both X and Z directions)
     */
    private boolean isDiagonalJump(Point start, Point end) {
        double dx = Math.abs(end.x() - start.x());
        double dz = Math.abs(end.z() - start.z());

        // If both X and Z change by more than a small threshold, it's diagonal
        return dx > EPSILON && dz > EPSILON;
    }

    @Override
    public boolean valid(Entity entity, TempPNode node) {
        Instance instance = entity.getInstance();
        if (instance == null) return false;

        // Check that there is a solid block beneath the node
        int x = node.blockX();
        int z = node.blockZ();
        int y = node.blockY() - 1;
        Block blockBelow = instance.getBlock(x, y, z);

        if (!blockBelow.isSolid()) {
            return false;
        }

        // Make sure there's space for the entity to jump
        // Check above current position and landing position
        for (int checkY = node.blockY(); checkY <= node.blockY() + 1; checkY++) {
            if (instance.getBlock(x, checkY, z).isSolid()) {
                return false; // Can't jump if there's a ceiling or obstacle
            }
        }

        return true;
    }
}
package nub.wi1helm.template.ai;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.collision.PhysicsResult;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public interface TempPNodeModifiers {

    /**
     * Modify the node based on specific conditions.
     * Each implementation should:
     * 1. Check if the action is already set, returning immediately if it is
     * 2. Validate the node is valid for the specific modifier
     * 3. Apply a specific action and adjust costs as needed
     *
     * @param entity The entity performing the path finding
     * @param node The node to modify
     */
    void modify(Entity entity, TempPNode node);

    /**
     * Check if a node is valid for this modifier.
     * This should implement the specific validation logic for each modifier type.
     *
     * @param entity The entity performing the path finding
     * @param node The node to validate
     * @return true if the node is valid for this modifier, false otherwise
     */
    boolean valid(Entity entity, TempPNode node);

    /**
     * Calculate the heuristic distance between two points.
     * Default is straight-line distance.
     *
     * @param node The current point
     * @param target The target point
     * @return The heuristic distance
     */
    default double heuristic(@NotNull Point node, @NotNull Point target) {
        return node.distance(target);
    }

    /**
     * Check if the entity would collide with any blocks at the given position.
     *
     * @param entity The entity to check collision for
     * @param point The point to check collision at
     * @return true if collision detected, false otherwise
     */
    default boolean collidesAtPosition(Entity entity, @NotNull Point point) {
        var iterator = entity.getBoundingBox().getBlocks(point);
        while (iterator.hasNext()) {
            var block = iterator.next();
            if (entity.getInstance().getBlock(block.blockX(), block.blockY(), block.blockZ(), Block.Getter.Condition.TYPE).isSolid()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the entity can move from start to end without collision.
     *
     * @param entity The entity to check movement for
     * @param start The starting point
     * @param end The ending point
     * @return true if movement is possible without collision, false otherwise
     */
    default boolean canMoveWithoutCollision(Entity entity, @NotNull Point start, @NotNull Point end) {
        final Point diff = end.sub(start);
        PhysicsResult res = CollisionUtils.handlePhysics(entity.getInstance(), entity.getBoundingBox(), Pos.fromPoint(start), Vec.fromPoint(diff), null, false);
        return !(res.collisionX() || res.collisionY() || res.collisionZ());
    }

    default Point snapToGrid(Point point) {
        // Make so all nodes are aligned to middle of blocks.
        return new Pos(Math.floor(point.x()) + 0.5, Math.floor(point.y()), Math.floor(point.z()) + 0.5);

    }
}
package nub.wi1helm.template.ai;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a single "action" on a node:
 * for example, jump, dash, or other logic
 * that triggers when the entity steps onto that node.
 */
public interface TempNodeAction {
    void trigger(Entity entity, @Nullable Point current, @Nullable Point target);
}

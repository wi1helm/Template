package nub.wi1helm.template.ai.actions;

import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.collision.PhysicsResult;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.utils.position.PositionUtils;
import nub.wi1helm.template.ai.TempNodeAction;
import org.jetbrains.annotations.Nullable;

public class FallAction implements TempNodeAction {
    @Override
    public void trigger(Entity entity, @Nullable Point current, @Nullable Point target) {
        // Validate input
        if (entity == null || current == null || target == null) return;

        // Get the current in-world position
        Pos position = entity.getPosition();

        // Compute horizontal differences only (ignore vertical differences)
        double dx = target.x() - position.x();
        double dz = target.z() - position.z();

        // Calculate horizontal distance (ignoring any vertical offset)
        double horizontalDistanceSq = dx * dx + dz * dz;
        if (horizontalDistanceSq < 1e-8) {
            // Already at the target horizontally
            return;
        }
        double horizontalDistance = Math.sqrt(horizontalDistanceSq);

        // Determine movement speed (only horizontal)
        double speed = movementSpeed(entity);
        if (horizontalDistance < speed) {
            speed = horizontalDistance;
        }

        // Calculate the horizontal movement vector
        double radians = Math.atan2(dz, dx);
        double speedX = Math.cos(radians) * speed;
        double speedZ = Math.sin(radians) * speed;

        // Set look direction based solely on horizontal movement (pitch = 0 for a flat look)
        float yaw = PositionUtils.getLookYaw(dx, dz);
        float pitch = 0.0f;

        // Apply physics for horizontal movement only; no vertical component is added here.
        PhysicsResult physicsResult = CollisionUtils.handlePhysics(entity, new Vec(speedX, 0.0, speedZ));

        // Update the entity's position and view, preserving the current y-level.
        Pos newPos = Pos.fromPoint(physicsResult.newPosition()).withView(yaw, pitch);
        entity.refreshPosition(newPos);
    }

    /**
     * Retrieve the entity's movement speed, using its attribute if available.
     */
    private double movementSpeed(Entity entity) {
        if (entity instanceof LivingEntity living) {
            return living.getAttributeValue(Attribute.MOVEMENT_SPEED);
        }
        return 0.1;
    }
}

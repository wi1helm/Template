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

public class SwimAction implements TempNodeAction {

    @Override
    public void trigger(Entity entity, @Nullable Point current, @Nullable Point target) {

        // Make sure we have valid points
        if (entity == null || current == null || target == null) return;

        // Get the actual in-world position (could differ slightly from 'current')
        Pos position = entity.getPosition();

        // Horizontal/vertical deltas from entity to target
        double dx = target.x() - position.x();
        double dy = target.y() - position.y();
        double dz = target.z() - position.z();

        // Movement speed for swimming (typically slower than walking)
        double speed = movementSpeed(entity) * 0.8;

        // Distance to target (including vertical component for swimming)
        double distSq = dx * dx + dy * dy + dz * dz;
        if (distSq < 1e-8) {
            // Already basically at the target
            return;
        }
        double distance = Math.sqrt(distSq);

        // Clamp speed so we don't overshoot
        if (distance < speed) {
            speed = distance;
        }

        // Calculate 3D direction vector
        double magnitude = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double dirX = dx / magnitude;
        double dirY = dy / magnitude;
        double dirZ = dz / magnitude;

        // Scale by speed
        double speedX = dirX * speed;
        double speedY = dirY * speed;
        double speedZ = dirZ * speed;

        // Calculate look direction
        float yaw = PositionUtils.getLookYaw(dx, dz);
        float pitch = PositionUtils.getLookPitch(dx, dy, dz);

        // Apply physics (including water resistance and collisions)
        PhysicsResult physicsResult = CollisionUtils.handlePhysics(
                entity, new Vec(speedX, speedY, speedZ));

        // Update entity position & view
        Pos newPos = Pos.fromPoint(physicsResult.newPosition()).withView(yaw, pitch);
        entity.refreshPosition(newPos);
    }

    /**
     * Retrieve the entity's movement speed or a default value.
     */
    private double movementSpeed(Entity entity) {
        if (entity instanceof LivingEntity living) {
            // For example, get actual speed attribute
            return living.getAttributeValue(Attribute.MOVEMENT_SPEED);
        }
        // Default if it's not a LivingEntity
        return 0.1;
    }
}
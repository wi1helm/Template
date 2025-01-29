
package nub.wi1helm.template.npc.goals;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.GoalSelector;

public final class LookAtPlayerGoal extends GoalSelector {
    private Entity target;
    private final double range;
    private final Pos defaultPos;

    public LookAtPlayerGoal(EntityCreature entityCreature, double range, Pos defaultPos) {
        super(entityCreature);
        this.range = range;
        this.defaultPos = defaultPos;
    }

    @Override
    public boolean shouldStart() {
        // Find the closest target within range
        target = findTarget();
        return target != null;
    }

    @Override
    public void start() {
        // No initialization needed for this goal
    }

    @Override
    public void tick(long time) {
        target = findTarget(); // Dynamically update the target

        // Check if the target is invalid or out of range
        if (target == null ||
                entityCreature.getDistanceSquared(target) > range * range ||
                entityCreature.getInstance() != target.getInstance()) {
            resetHeadRotation(); // Reset the rotation if no valid target
            target = null;
            return;
        }

        // Calculate the target's head position
        Pos targetHeadPosition = target.getPosition().add(0, target.getEyeHeight(), 0);

        // Adjust for the sitting pose
        if (entityCreature.getPose() == Entity.Pose.SITTING) {
            targetHeadPosition = targetHeadPosition.add(0, EntityType.ARMOR_STAND.registry().eyeHeight(), 0);
        }

        // Make the entity look at the adjusted position
        entityCreature.lookAt(targetHeadPosition);
    }



    @Override
    public boolean shouldEnd() {
        // End if there's no valid target
        return target == null;
    }

    @Override
    public void end() {
        // Reset the rotation when this goal ends
        resetHeadRotation();
    }

    private void resetHeadRotation() {
        // Reset the creature's rotation to its default view (facing forward)
        entityCreature.refreshPosition(defaultPos);
    }

    public Entity findTarget() {
        // Find the closest player within range
        return entityCreature.getInstance().getEntities()
                .stream()
                .filter(entity -> entity instanceof Player && entityCreature.getDistanceSquared(entity) <= range * range)
                .min((e1, e2) -> Double.compare(entityCreature.getDistanceSquared(e1), entityCreature.getDistanceSquared(e2)))
                .orElse(null);
    }
}
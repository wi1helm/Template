package nub.wi1helm.template.npc.goals;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
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

        // Adjust NPC head position based on sitting or standing
        double sittingYOffset = entityCreature.getPose() == EntityPose.SITTING ? entityCreature.getEntityType().height() +0.5 : 0.0;
        Pos targetHeadPosition = target.getPosition().add(0, target.getEyeHeight() + sittingYOffset, 0);

        // Make the entity look at the adjusted position
        entityCreature.lookAt(targetHeadPosition);
    }

    @Override
    public boolean shouldEnd() {
        // Stop looking if target is gone OR too far away
        return target == null || entityCreature.getDistanceSquared(target) > range * range;
    }

    @Override
    public void end() {
        // Reset the rotation when this goal ends
        resetHeadRotation();
    }

    private void resetHeadRotation() {
        // TODO fix the bug where sitting npcs dont reset looking to degautl
        entityCreature.teleport(new Pos(
                entityCreature.getPosition().x(),
                entityCreature.getPosition().y(),
                entityCreature.getPosition().z(),
                defaultPos.yaw(), // Restore default yaw
                defaultPos.pitch() // Restore default pitch
        ));
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

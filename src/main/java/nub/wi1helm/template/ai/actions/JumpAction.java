package nub.wi1helm.template.ai.actions;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import nub.wi1helm.template.ai.TempNodeAction;
import org.jetbrains.annotations.Nullable;

public class JumpAction implements TempNodeAction {
    @Override
    public void trigger(Entity entity, @Nullable Point current, @Nullable Point target) {
        if (entity.isOnGround()) {
            double height = target.y() - current.y();
            entity.setVelocity(new Vec((double)0.0F, (double)(4.0F * 2.5F), (double)0.0F));
        }

    }
}

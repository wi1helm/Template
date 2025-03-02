package nub.wi1helm.template.ai;

import net.minestom.server.entity.Entity;

import java.util.List;

public class TempPParameters {

    // The maximum distance that can be traveled
    public final double maxDistance;

    // The minimum distance to consider as “close” to the goal
    public final double minDistance;

    // Tolerance or variance for certain checks
    public final double variance;

    public final List<TempPNodeModifiers> modifiers;

    public TempPParameters(double maxDistance, double minDistance, double variance, List<TempPNodeModifiers> modifiers) {
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
        this.variance = variance;
        this.modifiers = modifiers;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public double getVariance() {
        return variance;
    }

    public List<TempPNodeModifiers> getModifiers() {
        return modifiers;
    }

    public void applyAllModifiersToNode(Entity entity, TempPNode node) {
        for (var modifier : modifiers) {
            modifier.modify(entity, node);
        }
    }

    // Validate that exactly one modifier considers this node valid.
    public boolean validForAllModifiers(Entity entity, TempPNode node) {
        int validCount = 0;
        for (TempPNodeModifiers modifier : modifiers) {
            if (modifier.valid(entity, node)) {
                validCount++;
            }
        }
        return validCount >= 1;
    }
}

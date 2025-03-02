package nub.wi1helm.template.ai;

import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import net.minestom.server.coordinate.Point;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import nub.wi1helm.template.ai.actions.UnsetAction;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A "centralized" A* path generator which:
 * - Applies cost/heuristic modifiers to each node immediately upon creation.
 * - Uses a typical A* relaxation approach with a bestCosts map instead of a closed set.
 * - Provides optional "variance" pruning.
 */
public final class TempPathGenerator {

    // Compare by cost + heuristic => classical A*
    private static final Comparator<TempPNode> NODE_COMPARATOR =
            Comparator.comparingDouble(n -> n.cost() + n.heuristic());

    private TempPathGenerator() {
        // Utility class
    }

    public static @NotNull TempPPath generate(@NotNull Entity entity,
                                              @NotNull Point start,
                                              @NotNull Point target,
                                              @NotNull TempPParameters parameters) {

        // Make so all nodes are aligned to middle of blocks.
        start = new Pos(Math.floor(start.x()) + 0.5, Math.floor(start.y()), Math.floor(start.z()) + 0.5);

        TempPPath path = new TempPPath();

        // Quick bailout if no instance
        final Instance instance = entity.getInstance();
        if (instance == null) {
            path.setState(TempPPath.State.INVALID);
            return path;
        }

        // 1) Compute stepSize as half the bounding box width
        final BoundingBox bb = entity.getBoundingBox();
        double stepSize = bb.width() / 2.0;
        if (stepSize < 1) {
            // Minimum horizontal range to explore
            stepSize = 1;
        }

        // 2) Distances & thresholds
        final double straightDistance = start.distance(target);
        final double closeDistance = Math.max(0.8, parameters.minDistance);
        final int maxNodeSize = (int) Math.floor(parameters.maxDistance * 10);

        // Keep track of best-so-far for fallback
        double bestDistanceToGoal = Double.MAX_VALUE;
        List<TempPNode> bestNodeSoFar = List.of();

        // Create the start node
        TempPNode startNode = new TempPNode(start, 0.0, straightDistance, new UnsetAction(), null);
        parameters.applyAllModifiersToNode(entity, startNode);

        // Priority queue for open nodes
        ObjectHeapPriorityQueue<TempPNode> openQueue = new ObjectHeapPriorityQueue<>(NODE_COMPARATOR);
        openQueue.enqueue(startNode);

        // bestCosts: store the best discovered cost for each position
        Map<Point, Double> bestCosts = new HashMap<>();
        bestCosts.put(startNode.point(), startNode.cost());

        // Final result node (if we succeed)
        TempPNode finalNode = null;

        // 3) A* main loop
        while (!openQueue.isEmpty() && bestCosts.size() < maxNodeSize) {
            // External termination?
            if (path.getState() == TempPPath.State.TERMINATING) {
                path.setState(TempPPath.State.TERMINATED);
                return path;
            }

            // Dequeue the node with the smallest (cost + heuristic)
            TempPNode current = openQueue.dequeue();

            // Skip if a cheaper path was found later
            Double knownCost = bestCosts.get(current.point());
            if (knownCost == null || knownCost < current.cost()) {
                continue;
            }

            // Check distance to goal
            double distToGoal = current.point().distance(target);
            if (distToGoal <= closeDistance) {
                finalNode = current;
                break;
            }

            // Update best so far
            if (distToGoal < bestDistanceToGoal) {
                bestDistanceToGoal = distToGoal;
                bestNodeSoFar = List.of(current);
            }

            // Optional pruning by variance
            double f = current.cost() + current.heuristic();
            if (f > straightDistance + parameters.variance) {
                continue;
            }

            // 4) Expand neighbors
            // stepIncrement for dx, dz = 0.5
            // dy in [-1..2] with increments of 1

            for (double dx = -stepSize; dx <= stepSize; dx++) {
                for (double dy = -1; dy <= 1; dy++) {
                    for (double dz = -stepSize; dz <= stepSize; dz++) {

                        // Skip the current position
                        if (dx == 0 && dy == 0 && dz == 0 || dx == 0 && dy == 1 && dz == 0 || dx == 0 && dy == -1 && dz == 0) {
                            continue;
                        }

                        // The neighbor's position
                        Point neighborPos = current.point().add(dx, dy, dz);

                        if (entity.getInstance().getBlock(neighborPos, Block.Getter.Condition.TYPE).isSolid()) {
                            continue;
                        }

                        if (neighborPos.distance(start) > parameters.maxDistance) {
                            continue;
                        }


                        // Calculate cost
                        double stepCost = Math.sqrt(dx*dx + dy*dy + dz*dz);
                        double newCost = current.cost() + stepCost;
                        double newHeuristic = neighborPos.distance(target);

                        // Create neighbor node
                        TempPNode neighbor = new TempPNode(neighborPos, newCost, newHeuristic, new UnsetAction(), current);

                        // Apply all modifiers
                        parameters.applyAllModifiersToNode(entity, neighbor);

                        if (!neighbor.valid()) {
                            continue;
                        }

                        double finalCost = neighbor.cost();
                        Double existingCost = bestCosts.get(neighborPos);
                        if (existingCost == null || finalCost < existingCost) {
                            bestCosts.put(neighborPos, finalCost);
                            openQueue.enqueue(neighbor);
                        }
                    }
                }
            }
        }

        // 5) Evaluate results
        if (finalNode != null) {
            rebuildPath(path, finalNode);
            path.setState(TempPPath.State.COMPUTED);
        } else if (!bestNodeSoFar.isEmpty()) {
            rebuildPath(path, bestNodeSoFar.getFirst());
            path.setState(TempPPath.State.BEST_EFFORT);
        } else {
            path.setState(TempPPath.State.INVALID);
        }

        return path;
    }

    /**
     * Rebuild path from end -> ... -> start, then reverse it.
     */
    private static void rebuildPath(@NotNull TempPPath path, @NotNull TempPNode endNode) {
        List<TempPNode> result = new ArrayList<>();
        TempPNode current = endNode;
        while (current != null) {
            result.add(current);
            current = current.parent();
        }
        Collections.reverse(result);
        path.getNodes().addAll(result);
    }
}

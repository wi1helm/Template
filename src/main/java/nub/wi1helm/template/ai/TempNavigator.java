package nub.wi1helm.template.ai;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.utils.chunk.ChunkUtils;
import nub.wi1helm.template.ai.actions.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Navigator is the entity's path logic.
 * It's responsible for requesting a path
 * and then following it via PathFollower.
 */
public final class TempNavigator {

    private Point target;
    private final Entity entity;
    private TempPParameters parameters;

    private TempPPath computingPath;  // Path is being computed
    private TempPPath computedPath;   // Path we are following

    public TempNavigator(@NotNull Entity entity) {
        this.entity = entity;
    }

    public synchronized boolean setPathTo(@Nullable Point point, @NotNull TempPParameters parameters) {
        Instance instance = this.entity.getInstance();

        if (point == null || instance == null) {
            return false;
        }

        WorldBorder worldBorder = instance.getWorldBorder();
        Chunk chunk = instance.getChunkAt(point);

        if (!worldBorder.inBounds(point) || !ChunkUtils.isLoaded(chunk)) {
            return false;
        }

        if (this.entity.getDistance(point) < parameters.minDistance || point.sameBlock(this.entity.getPosition())) {
            return false;
        }

        if (this.computingPath != null) {
            this.computingPath.setState(TempPPath.State.TERMINATED);
        }

        this.computingPath = TempPathGenerator.generate(this.entity, this.entity.getPosition(), point, parameters);
        this.target = point;
        this.parameters = parameters;
        return true;
    }

    public synchronized void tick(long time) {
        if (this.target == null || entity.isRemoved()) {
            return;
        }

        // Handle path computation completion
        if (this.computingPath != null && (this.computingPath.getState() == TempPPath.State.COMPUTED || this.computingPath.getState() == TempPPath.State.BEST_EFFORT)) {
            this.computedPath = this.computingPath;
            this.computingPath = null;
        }

        if (this.computedPath == null) {
            return;
        }

        // Initialize the path to be followed
        if (this.computedPath.getState() == TempPPath.State.COMPUTED || this.computedPath.getState() == TempPPath.State.BEST_EFFORT) {
            this.computedPath.setState(TempPPath.State.FOLLOWING);

            for (int i = 0; i < this.computedPath.getNodes().size(); ++i) {
                if (this.computedPath.getNodes().get(i).point().sameBlock(this.entity.getPosition())) {
                    this.computedPath.getNodes().subList(0, i).clear();
                    break;
                }
            }

            if (this.computedPath.getState() != TempPPath.State.FOLLOWING) {
                return;
            }

            if (this.entity.getPosition().distance(this.target) < this.parameters.minDistance) {
                this.computedPath = null;
                return;
            }

            TempPNode currentNode = this.computedPath.getCurrentNode();
            TempPNode nextNode = this.computedPath.getNextNode();

            if (currentNode == null) {
                if (this.computingPath == null || this.computingPath.getState() != TempPPath.State.CALCULATING) {
                    this.computingPath = TempPathGenerator.generate(this.entity, this.entity.getPosition(), target, parameters);
                }
                return;
            }

            if (nextNode == null) {
                this.computedPath.setState(TempPPath.State.INVALID);
                return;
            }

            Point currentTarget = currentNode.point();
            Point nextTarget = nextNode.point();

            if (nextNode.action() != null) {
                nextNode.action().trigger(entity, currentTarget, nextTarget);
                if (isAtPoint(currentTarget)) {
                    this.computedPath.advance();
                }
            }
        }
    }

    public void reset() {
        this.target = null;
        this.computedPath = null;
        this.computingPath = null;
    }

    public boolean isComplete() {
        if (this.computedPath == null) {
            return true;
        }
        return this.target == null || this.entity.getPosition().sameBlock(this.target);
    }

    public void drawPath(TempPPath path) {
        if (path == null) {
            return;
        }

        for (TempPNode node : path.getNodes()) {
            // Color-code by action type
            Particle particle = Particle.COMPOSTER; // Default

            if (node.action() != null) {
                if (node.action() instanceof WalkAction) {
                    particle = Particle.COMPOSTER;
                } else if (node.action() instanceof JumpAction) {
                    particle = Particle.TOTEM_OF_UNDYING;
                } else if (node.action() instanceof FallAction) {
                    particle = Particle.WAX_ON;
                } else if (node.action() instanceof SwimAction) {
                    particle = Particle.BUBBLE_POP;
                } else if (node.action() instanceof DebugAction) {
                    particle = Particle.FLAME;
                } else if (node.action() instanceof UnsetAction) {
                    particle = Particle.NOTE;
                }
            } else {
                // Null action node - highlight with a different particle
                particle = Particle.SMOKE;
            }

            ParticlePacket packet = new ParticlePacket(
                    particle,
                    node.point().x(),
                    node.point().y() + 0.5,
                    node.point().z(),
                    0.0F, 0.0F, 0.0F, 0.0F, 1
            );
            this.entity.sendPacketToViewers(packet);

            // Draw lines between connected nodes to visualize the path
            if (node.parent() != null) {
                drawLine(node.parent().point(), node.point(), Particle.END_ROD);
            }
        }
    }

    private void drawLine(Point start, Point end, Particle particle) {
        // Simple line drawing algorithm
        double steps = 5; // Number of particles in the line
        for (int i = 0; i <= steps; i++) {
            double t = i / steps;
            double x = start.x() + (end.x() - start.x()) * t;
            double y = start.y() + (end.y() - start.y()) * t + 0.5; // Slight Y offset for visibility
            double z = start.z() + (end.z() - start.z()) * t;

            ParticlePacket packet = new ParticlePacket(
                    particle, x, y, z, 0.0F, 0.0F, 0.0F, 0.0F, 1
            );
            this.entity.sendPacketToViewers(packet);
        }
    }

    public boolean isAtPoint(@NotNull Point point) {
        return this.entity.getPosition().sameBlock(point);
    }

    public TempPPath getComputedPath() {
        return computedPath;
    }
}

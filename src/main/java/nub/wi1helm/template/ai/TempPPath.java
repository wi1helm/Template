package nub.wi1helm.template.ai;

import net.minestom.server.coordinate.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TempPPath {

    private final List<TempPNode> pathNodes = new ArrayList<>();
    private int currentNodeIndex = 0;
    private final AtomicReference<TempPPath.State> state;

    public TempPPath() {
        this.state = new AtomicReference<>(State.CALCULATING);
    }

    public State getState() {
        return state.get();
    }

    public void setState(State newState) {
        this.state.set(newState);
    }

    public void addNode(TempPNode node) {
        this.pathNodes.add(node);
    }

    public List<TempPNode> getNodes() {
        return pathNodes;
    }

    public boolean isEmpty() {
        return pathNodes.isEmpty();
    }

    /**
     * @return The current node that we're targeting.
     */
    public TempPNode getCurrentNode() {
        if (currentNodeIndex < 0 || currentNodeIndex >= pathNodes.size()) {
            return null;
        }
        return pathNodes.get(currentNodeIndex);
    }

    public Point getCurrentNodePoint() {
        if (currentNodeIndex < 0 || currentNodeIndex >= pathNodes.size()) {
            return null;
        }
        return pathNodes.get(currentNodeIndex).point();
    }

    /**
     * @return The next node after the current node that we're targeting.
     */
    public TempPNode getNextNode() {
        if (currentNodeIndex < 0 ||
                currentNodeIndex >= pathNodes.size() - 1) {
            return null;
        }
        return pathNodes.get(currentNodeIndex + 1);
    }

    public Point getNextNodePoint() {
        if (currentNodeIndex < 0 ||
                currentNodeIndex >= pathNodes.size() - 1) {
            return null;
        }
        return pathNodes.get(currentNodeIndex + 1).point();
    }


    /**
     * Move to the next node in the path.
     */
    public void advance() {
        if (currentNodeIndex < pathNodes.size() - 1) {
            currentNodeIndex++;
        }
    }

    public static enum State {
        CALCULATING,
        FOLLOWING,
        STALE,
        TERMINATED,
        COMPUTED,
    }
}

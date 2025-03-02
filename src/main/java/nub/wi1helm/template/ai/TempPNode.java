package nub.wi1helm.template.ai;

import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

public class TempPNode {

    private double cost;       // "g" in A*
    private double heuristic;  // "h" in A*
    private TempPNode parent;
    private Point point;
    private int hashCode;
    private boolean valid = false;

    private TempNodeAction action;

    public TempPNode(Point point, double cost, double heuristic, TempNodeAction action, TempPNode parent) {
        this.point = point;
        this.cost = cost;
        this.heuristic = heuristic;
        this.parent = parent;
        this.action = action;

        // Use Cantor pairing for a unique hash based on block coords
        this.hashCode = cantor((int)Math.floor(point.x()),
                cantor((int)Math.floor(point.y()),
                        (int)Math.floor(point.z())));
    }

    public int blockX() { return (int)Math.floor(this.point.x()); }
    public int blockY() { return (int)Math.floor(this.point.y()); }
    public int blockZ() { return (int)Math.floor(this.point.z()); }

    @Internal
    public TempNodeAction action() {
        return this.action;
    }

    @Internal
    public void setAction(TempNodeAction newAction) {
        this.action = newAction;
    }

    @Internal
    public double cost() {
        return this.cost;
    }

    @Internal
    public double heuristic() {
        return this.heuristic;
    }

    @Internal
    public void setCost(double c) {
        if (c < 0) c = 0;
        this.cost = c;
    }

    public boolean valid() {
        return this.valid;
    }

    public void valid(boolean valid) {
        this.valid = valid;
    }

    @Internal
    public void setHeuristic(double h) {
        this.heuristic = h;
    }

    @Internal
    public @Nullable TempPNode parent() {
        return this.parent;
    }

    @Internal
    public void setParent(@Nullable TempPNode current) {
        this.parent = current;
    }

    @Internal
    public Point point() {
        return point;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public void setPoint(Point point) {
        this.point = point;
        this.hashCode = cantor((int)Math.floor(point.x()),
                cantor((int)Math.floor(point.y()),
                        (int)Math.floor(point.z())));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TempPNode other)) return false;
        return point.equals(other.point);
    }

    private static int cantor(int a, int b) {
        int ca = a >= 0 ? 2 * a : -2 * a - 1;
        int cb = b >= 0 ? 2 * b : -2 * b - 1;
        return (ca + cb + 1) * (ca + cb) / 2 + cb;
    }

    public String toString() {
        double var10000 = this.point.x();
        return "PNode{point=" + var10000 + ", " + this.point.y() + ", " + this.point.z() + ", d=" + (this.cost + this.heuristic) + ", type=" + this.action + "}";
    }
}

package nub.wi1helm.template.npc;

import net.minestom.server.entity.Player;

public abstract class AbstractAction {

    private final int id; // Unique ID or index of this action
    private final long delayToNext; // Delay before the next action
    private final boolean requiresPlayerClick;

    protected AbstractAction(int id, long delayToNext, boolean requiresPlayerClick) {
        this.id = id;
        this.delayToNext = delayToNext;
        this.requiresPlayerClick = requiresPlayerClick;
    }

    public int getId() {
        return id;
    }

    public long getDelayToNext() {
        return delayToNext;
    }

    public boolean requiresPlayerClick() {
        return requiresPlayerClick;
    }

    /**
     * Executes the logic for this action.
     *
     * @param player The player interacting with the NPC.
     */
    public abstract void execute(Player player);

    /**
     * Determines the next action dynamically.
     *
     * @param player      The player interacting with the NPC.
     * @param actionList The complete list of actions.
     * @return The next action to execute, or null if none.
     */
    public abstract AbstractAction determineNextAction(Player player, ActionList actionList);
}

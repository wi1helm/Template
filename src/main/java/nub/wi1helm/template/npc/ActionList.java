package nub.wi1helm.template.npc;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class    ActionList {
    private final Map<Integer, AbstractAction> actions = new HashMap<>();
    private AbstractAction currentAction;

    // Keep track of the scheduled task so we can cancel it if we “fast-forward”
    private Task scheduledTask;

    // Tracks last click to enforce any cooldown if your action uses it
    private long lastClickTime = 0;

    public ActionList(AbstractAction... actions) {
        // Create a map of ID -> Action
        this.actions.putAll(IntStream.range(0, actions.length)
                .boxed()
                .collect(Collectors.toMap(
                        i -> actions[i].getId(),
                        i -> actions[i]
                )));

        // If we have at least one action, pick the first as the starting action
        if (!this.actions.isEmpty()) {
            this.currentAction = this.actions.get(0);
        }
    }

    public ActionList() {
        // no-op, empty list
    }

    /**
     * This method is called whenever the next action is triggered.
     * (Either by a click, or because a previous action scheduled it.)
     */
    public void executeNext(Player player) {
        // If there is nothing left, we’re done
        if (currentAction == null) return;

        // Cancel any old scheduled task before we do anything
        if (scheduledTask != null) {
            scheduledTask.cancel();
            scheduledTask = null;
        }

        // We'll keep processing actions in a loop.
        // - For "auto" actions with 0 delay, we'll keep going until we
        //   reach either a click-required action or a scheduled delay action or no next action.
        while (true) {
            if (currentAction == null) {
                return;
            }

            long currentTime = System.currentTimeMillis();
            long delay = currentAction.getDelayToNext();

            // Check the click-cooldown if this action requires a click.
            // That means, if not enough time has passed, we ignore it.
            if (currentAction.requiresPlayerClick()) {
                if (currentTime - lastClickTime < delay) {
                    // We haven't waited long enough since the last click
                    // So just ignore this attempt
                    return;
                }
            }

            // Execute the current action
            currentAction.execute(player);
            lastClickTime = currentTime;

            // Determine the next action in the chain
            AbstractAction nextAction = currentAction.determineNextAction(player, this);
            if (nextAction == null) {
                // No next action, so stop entirely
                currentAction = null;
                return;
            }

            // Update our current action to the next one
            currentAction = nextAction;

            // If the new action requires a click, we break out of the loop
            // so the next click triggers it.
            if (currentAction.requiresPlayerClick()) {
                break;
            }

            // Otherwise, the action does NOT require a click:
            // 1. If delay == 0, we want to process it immediately (continue looping).
            // 2. If delay > 0, we schedule it and break the loop.
            long nextDelay = currentAction.getDelayToNext();
            if (nextDelay > 0) {
                scheduledTask = MinecraftServer.getSchedulerManager()
                        .buildTask(() -> executeNext(player))
                        .delay(nextDelay, TimeUnit.MILLISECONDS.toChronoUnit())
                        .schedule();
                break;
            }
            // If nextDelay == 0, we just loop around again
            // (which executes that action in the same method call).
        }
    }


    /**
     * Retrieve an action by its ID.
     */
    public AbstractAction getAction(int id) {
        return actions.get(id);
    }

    /**
     * Check if this ActionList has no actions.
     */
    public boolean isEmpty() {
        return actions.isEmpty();
    }

    /**
     * Static factory for an empty ActionList.
     */
    public static ActionList empty() {
        return new ActionList();
    }
}

package nub.wi1helm.template.npc;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ActionList {
    private final Map<Integer, AbstractAction> actions;
    private AbstractAction currentAction;
    private long lastClickTime = 0; // Tracks when the last click happened

    public ActionList(AbstractAction... actions) {
        this.actions = IntStream.range(0, actions.length)
                .boxed()
                .collect(Collectors.toMap(
                        i -> actions[i].getId(), // Use the ID of the action
                        i -> actions[i]
                ));

        if (!this.actions.isEmpty()) {
            this.currentAction = this.actions.get(0); // Start with the first action
        }
    }

    public void executeNext(Player player) {
        if (currentAction == null) return;

        long currentTime = System.currentTimeMillis();
        long delay = currentAction.getDelayToNext(); // Get delay for next action

        // If this action requires a click, enforce a cooldown
        if (currentAction.requiresPlayerClick()) {
            if (currentTime - lastClickTime < delay) {
                // Ignore extra clicks within the cooldown period
                return;
            }
        }

        // Execute the current action
        currentAction.execute(player);
        lastClickTime = currentTime; // Update last click time

        // Determine the next action
        AbstractAction nextAction = currentAction.determineNextAction(player, this);
        if (nextAction != null) {
            currentAction = nextAction;

            // Schedule the next action only if it doesn't require a player click
            if (!currentAction.requiresPlayerClick()) {
                MinecraftServer.getSchedulerManager()
                        .buildTask(() -> executeNext(player))
                        .delay(currentAction.getDelayToNext(), TimeUnit.MILLISECONDS.toChronoUnit())
                        .schedule();
            }
        } else {
            currentAction = null; // End of action list
        }
    }


    public AbstractAction getAction(int id) {
        return actions.get(id);
    }

    public boolean isEmpty() {
        return actions.isEmpty();
    }
}

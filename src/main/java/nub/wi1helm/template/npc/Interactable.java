// src/main/java/nub/wi1helm/template/npc/IHasActions.java (or in a new 'actions' subpackage)
package nub.wi1helm.template.npc;

import net.minestom.server.entity.Player;
import nub.wi1helm.template.npc.actions.ActionList;
import org.jetbrains.annotations.NotNull;

/**
 * An interface for entities that can have an associated ActionList
 * and respond to player interactions by executing actions.
 */
public interface Interactable {

    @NotNull ActionList getActionList();

    void setActionList(@NotNull ActionList actionList);

    default void onInteract(@NotNull Player player) {
        // Retrieve the ActionList
        ActionList actions = getActionList();

        // If the ActionList is empty, there's nothing to do
        if (actions.isEmpty()) {
            return;
        }

        // Execute the next action in the list
        actions.executeNext(player);
    }
}
package nub.wi1helm.template.npc.actions;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import rip.snicon.compass.npc.AbstractAction;

public abstract class DialogAction extends AbstractAction {

    private final Component message;

    protected DialogAction(int id, long delayToNext, boolean requiresPlayerClick, Component message) {
        super(id, delayToNext, requiresPlayerClick);
        this.message = message;
    }

    @Override
    public void execute(Player player) {
        if (player != null) {
            player.sendMessage(message);
        }
    }
}

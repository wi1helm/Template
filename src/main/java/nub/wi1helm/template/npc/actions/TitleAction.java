package nub.wi1helm.template.npc.actions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.entity.Player;

public abstract class TitleAction extends AbstractAction{

    private final Title title;

    protected TitleAction(int id, long delayToNext, boolean requiresPlayerClick, Title title) {
        super(id, delayToNext, requiresPlayerClick);
        this.title = title;
    }

    @Override
    public void execute(Player player) {
        if (player != null) {
            player.showTitle(title);
        }

    }
}

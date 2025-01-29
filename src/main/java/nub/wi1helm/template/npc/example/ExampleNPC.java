package nub.wi1helm.template.npc.example;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import rip.snicon.compass.instances.MysteryInstanceType;
import rip.snicon.compass.inventory.debug.DebugContainer;
import rip.snicon.compass.npc.*;
import rip.snicon.compass.npc.actions.MenuAction;
import rip.snicon.compass.npc.goals.LookAtPlayerGoal;
import rip.snicon.compass.utils.TextUtils;


public class ExampleNPC extends TemplateNPC {

    public ExampleNPC() {
        super(EntityType.PLAYER);
        setSpawnPosition(new Pos(27,9,30));
        setInstance(MysteryInstanceType.HUB.getInstance(), getSpawnPosition());
        setName(new TemplateText(TextUtils.convertStringToComponent("Nub"), TextUtils.convertStringToComponent("<green>FUck you</green>")));
        setSkinLayer(SkinLayer.NO_CAPE);

        setGoal(new LookAtPlayerGoal(this, 7, getSpawnPosition()));

    }

    @Override
    protected void personalize(Player player) {
        setSkin(player.getSkin());
        setActionList(new ActionList(
                new MenuAction(0, 5000, true, new DebugContainer().constructInventory(player)) {

                    @Override
                    public AbstractAction determineNextAction(Player player, ActionList actionList) {
                        return actionList.getAction(0);
                    }
                }
        ));
    }
}

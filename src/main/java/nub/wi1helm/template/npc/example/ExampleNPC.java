package nub.wi1helm.template.npc.example;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import nub.wi1helm.template.npc.*;
import nub.wi1helm.template.npc.actions.MenuAction;
import nub.wi1helm.template.npc.goals.LookAtPlayerGoal;


public class ExampleNPC extends TemplateNPC {

    public ExampleNPC() {
        super(EntityType.PLAYER);
        //setSpawnPosition(new Pos(27,9,30));
        //setInstance(MysteryInstanceType.HUB.getInstance(), getSpawnPosition());
        //setName(new TemplateText(Component.text("Hello"), Component.));
        //setSkinLayer(SkinLayer.NO_CAPE);

        //setGoal(new LookAtPlayerGoal(this, 7, getSpawnPosition()));

    }

    @Override
    protected void personalize(Player player) {
        //setSkin(player.getSkin());
        //setActionList(new ActionList(
        //        new MenuAction(0, 5000, true, new DebugContainer().constructInventory(player)) {
//
   //                  @Override
     //               public AbstractAction determineNextAction(Player player, ActionList actionList) {
       //                 return actionList.getAction(0);
         //           }
           //     }
        //));
    }
}

package nub.wi1helm.template.npc;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import nub.wi1helm.template.npc.hologram.TemplateTextNPC;

public interface Namable {
    default void setName(int rows ,Component... text) {
        if (this instanceof TemplateNPC npc) {
            TemplateTextNPC name = new TemplateTextNPC(rows) {
                @Override
                protected void personalize(Player player) {

                }
            };
            name.setText(text);

            npc.addPassenger(name);
        }
    }
}

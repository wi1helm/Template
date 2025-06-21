package nub.wi1helm.template.npc.entities;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.network.packet.server.SendablePacket;
import nub.wi1helm.template.npc.TemplateNPC;
import org.jetbrains.annotations.NotNull;


public class TemplateMountEntity extends Entity {
    public TemplateMountEntity() {
        super(EntityType.ARMOR_STAND);
        setAutoViewable(false);
        this.editEntityMeta(ArmorStandMeta.class, meta -> {
            meta.setInvisible(true);
            meta.setHasNoGravity(true);
            meta.setMarker(true); // Ensures it does not interfere with collisions
        });
    }

    @Override
    public void sendPacketToViewers(@NotNull SendablePacket packet) {
        // HEHE
    }

    public void addPassenger(@NotNull TemplateNPC entity, Player player) {



    }
}

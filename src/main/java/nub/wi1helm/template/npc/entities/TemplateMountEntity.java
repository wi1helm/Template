package nub.wi1helm.template.npc.entities;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.SetPassengersPacket;
import nub.wi1helm.template.npc.TemplateNPC;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

        if (entity.getVehicle() == null) {
            player.sendPacket(this.getEntityType().registry().spawnType().getSpawnPacket(this));
            player.sendPacket(this.getMetadataPacket());
            player.sendPacket(new SetPassengersPacket(this.getEntityId(), List.of(entity.getEntityId())));
            entity.setVehicle(this);
        } else {
            player.sendPacket(entity.getVehicle().getEntityType().registry().spawnType().getSpawnPacket(entity.getVehicle()));
            player.sendPacket(entity.getVehicle().getMetadataPacket());
            player.sendPacket(new SetPassengersPacket(entity.getVehicle().getEntityId(), List.of(entity.getEntityId())));
        }


    }
}

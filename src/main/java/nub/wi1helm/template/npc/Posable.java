package nub.wi1helm.template.npc;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityPose;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.EntityPositionPacket;
import net.minestom.server.network.packet.server.play.SetPassengersPacket;
import net.minestom.server.tag.Tag;
import nub.wi1helm.template.Template;
import nub.wi1helm.template.npc.entities.TemplateMountEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Posable {

    default void handlePose(TemplateNPC entity, Player player) {
        switch (entity.getPose()) {
            case SITTING -> handleSittingPose(entity, player);
            case SLEEPING -> handleLayingPose(entity, player);
            default -> handleDefaultPose();
        }
    }

    default void handleSittingPose(TemplateNPC entity, Player player) {
        TemplateMountEntity mount = new TemplateMountEntity();
        mount.setInstance(entity.getInstance(), entity.getPosition());
        mount.addPassenger(entity, player);

    }

    default void handleLayingPose(TemplateNPC entity, Player player) {
    }

    default void handleDefaultPose() {
    }
}

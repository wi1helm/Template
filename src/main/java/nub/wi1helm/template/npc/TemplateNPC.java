package nub.wi1helm.template.npc;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.SendablePacket;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class TemplateNPC extends Entity {


    public TemplateNPC(@NotNull EntityType entityType, Instance instance) {
        super(entityType);
        setInstance(instance);
        setAutoViewable(false);
    }

    public TemplateNPC(@NotNull EntityType entityType, Instance instance, Pos pos) {
        super(entityType);
        setInstance(instance, pos);
        setAutoViewable(false);
    }

    public abstract void personalize(Player player);

    public abstract Collection<SendablePacket> getNpcSpawnPackets(Player player);

    public abstract Collection<SendablePacket> getNpcDespawnPackets(Player player);

}
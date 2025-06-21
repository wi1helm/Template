package nub.wi1helm.template.npc.mob;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import nub.wi1helm.template.npc.TemplateNPC;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class TemplateMobNPC extends TemplateNPC {
    public TemplateMobNPC(@NotNull EntityType entityType, Instance instance) {
        super(entityType, instance);
        initTeam();
    }

    public TemplateMobNPC(@NotNull EntityType entityType, Instance instance, Pos pos) {
        super(entityType, instance, pos);
        initTeam();
    }

    private void initTeam() {
        Team team = MinecraftServer.getTeamManager().createBuilder("NPC").nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER).collisionRule(TeamsPacket.CollisionRule.NEVER).build();
        team.addMember(getUuid().toString());
    }

    @Override
    public Collection<SendablePacket> getNpcSpawnPackets(Player player) {
        personalize(player);
        return List.of(
                getSpawnPacket(),
                getMetadataPacket()

        );
    }

    @Override
    public Collection<SendablePacket> getNpcDespawnPackets(Player player) {
        return List.of(new DestroyEntitiesPacket(getEntityId()));
    }
}
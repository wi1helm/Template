package nub.wi1helm.template.npc.mob;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import nub.wi1helm.template.npc.Namable;
import nub.wi1helm.template.npc.Posable;
import nub.wi1helm.template.npc.TemplateNPC;
import org.jetbrains.annotations.NotNull;

public abstract class TemplateMobNPC extends TemplateNPC implements Posable, Namable {


    public TemplateMobNPC(@NotNull EntityType entityType) {
        super(entityType);

        Team team = MinecraftServer.getTeamManager().createBuilder("NPC").nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER).collisionRule(TeamsPacket.CollisionRule.NEVER).build();
        team.addMember(getUuid().toString());
    }

    // Implement


    @Override
    protected void onSpawn(Player player) {

    }

    @Override
    protected void onDespawn(Player player) {

    }

    // Overwrite
    @Override
    public void sendPacketToViewers(@NotNull SendablePacket packet) {
        // Here so methods
    }
}

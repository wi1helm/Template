package nub.wi1helm.template.npc.player;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import nub.wi1helm.template.npc.TemplateNPC;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class TemplatePlayerNPC extends TemplateNPC {

    private PlayerSkin skin;
    private SkinLayer skinLayer;


    public TemplatePlayerNPC(Instance instance) {
        super(EntityType.PLAYER, instance);
        initTeam();
    }

    public TemplatePlayerNPC(Instance instance, Pos pos) {
        super(EntityType.PLAYER, instance, pos);
        initTeam();
    }

    private void initTeam() {
        Team team = MinecraftServer.getTeamManager().createBuilder("NPC").nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER).collisionRule(TeamsPacket.CollisionRule.NEVER).build();
        team.addMember(getUsername());
    }

    @Override
    public Collection<SendablePacket> getNpcSpawnPackets(Player player) {
        addViewer(player);
        personalize(player);
        List<PlayerInfoUpdatePacket.Property> properties = (skin != null)
                ? List.of(new PlayerInfoUpdatePacket.Property("textures", skin.textures(), skin.signature()))
                : List.of();

        PlayerInfoUpdatePacket.Entry entry = new PlayerInfoUpdatePacket.Entry(
                this.getUuid(), this.getUsername(), properties, false, 0, GameMode.SURVIVAL, null, null,0
        );

        return List.of(
                new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry),
                getSpawnPacket(),
                getMetadataPacket()
        );
    }

    @Override
    public Collection<SendablePacket> getNpcDespawnPackets(Player player) {
        return List.of(
                new PlayerInfoRemovePacket(getUuid()),
                new DestroyEntitiesPacket(getEntityId())
        );
    }


    private String getUsername() {
        return this.getUuid().toString().substring(0,14) + " ?";
    }

    public void setSkin(PlayerSkin skin) {
        this.skin = skin;
    }

    public void setSkinLayer(SkinLayer skinLayer) {
        this.skinLayer = skinLayer;
        editEntityMeta(PlayerMeta.class, this.skinLayer::apply);
    }

    @Override
    public void sendPacketToViewersAndSelf(@NotNull SendablePacket packet) {
        // HEHE Do nothing cause hit fucking sucks
    }
}
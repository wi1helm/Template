package nub.wi1helm.template.npc.player;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import nub.wi1helm.template.Template;
import nub.wi1helm.template.npc.Namable;
import nub.wi1helm.template.npc.Posable;
import nub.wi1helm.template.npc.TemplateNPC;

import java.util.List;

public abstract class TemplatePlayerNPC extends TemplateNPC implements Posable, Namable {

    private PlayerSkin skin;
    private SkinLayer skinLayer = SkinLayer.NONE;

    public TemplatePlayerNPC() {
        super(EntityType.PLAYER);

        Team team = MinecraftServer.getTeamManager().createBuilder("NPC").nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER).collisionRule(TeamsPacket.CollisionRule.NEVER).build();
        team.addMember(getUsername());
    }

    @Override
    protected void onSpawn(Player player) {
        this.editEntityMeta(PlayerMeta.class, meta -> {
            this.getSkinLayer().apply(meta);
        });

        List<PlayerInfoUpdatePacket.Property> properties = (this.getSkin() != null)
                ? List.of(new PlayerInfoUpdatePacket.Property("textures", this.getSkin().textures(), this.getSkin().signature()))
                : List.of();

        PlayerInfoUpdatePacket.Entry entry = new PlayerInfoUpdatePacket.Entry(
                this.getUuid(), this.getUsername(), properties, false, 0, GameMode.SURVIVAL, null, null,0
        );

        player.sendPacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry));

        super.spawn(player);

    }

    @Override
    protected void onDespawn(Player player) {
        player.sendPacket(new PlayerInfoRemovePacket(this.getUuid()));
        super.despawn(player);
    }

    // Setters
    public void setSkinLayer(SkinLayer skinLayer) {
        this.skinLayer = skinLayer;
    }
    public void setSkin(PlayerSkin skin) {
        this.skin = skin;
    }

    // Getters
    public SkinLayer getSkinLayer() {
        return this.skinLayer;
    }
    public PlayerSkin getSkin() {
        return this.skin;
    }
    private String getUsername() {
        return this.getUuid().toString().substring(0,14) + " ?";
    }
}

package nub.wi1helm.template.npc;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class TemplateNPC extends EntityCreature {


    private TemplateText name = TemplateText.empty();
    private SpawnStrategy spawnStrategy = SpawnStrategy.STANDING;
    private Pos spawnPosition = new Pos(0,0,0);
    private PlayerSkin skin;
    private ActionList actionList = ActionList.empty();
    private Boolean exist = false;
    private Integer viewDistance = 32;
    private SkinLayer skinLayer = SkinLayer.NONE;
    private GoalSelector goal;
    // Finals
    private final String identifier;


    public TemplateNPC(@NotNull EntityType entityType) {
        super(entityType);
        this.identifier = UUID.randomUUID().toString().substring(0,16);
        setTag(Tag.String("uuid"), identifier);
        TemplateNPCHandler.registerNPC(this);

        Team team = MinecraftServer.getTeamManager().createBuilder("NPC").nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER).collisionRule(TeamsPacket.CollisionRule.NEVER).build();

        team.addMember(identifier);
    }

    abstract protected void personalize(Player player);



    // Name Methods

    public TemplateText getName() {
        return this.name;
    }

    public void setName(TemplateText name) {
        this.name = name;
    }

    protected void updateName(TemplateText name) {
        this.name = name;

        name.getText().forEach((integer, entity) -> entity.getViewers().forEach(player -> player.sendPacket(entity.getMetadataPacket())));

    }
    protected void updateRow(Integer row, Component text) {
        this.name.setRow(row, text);

        name.getText().forEach((integer, entity) -> entity.getViewers().forEach(player -> player.sendPacket(entity.getMetadataPacket())));
    }

    // Spawn & Despawn Methods

    protected void setSpawnStrategy(SpawnStrategy spawnStrategy){
        this.spawnStrategy = spawnStrategy;
    }

    public void spawn(Player player) {
        if (!shouldSpawn(player)) return; // Ensure spawn only happens if needed
        this.exist = true;
        this.personalize(player);
        this.spawnStrategy.spawn(this, player);

    }

    private Boolean shouldSpawn(Player player) {
        Pos playerPosition = player.getPosition();
        return playerPosition.distance(this.getSpawnPosition()) <= viewDistance && !exist;
    }

    public void despawn(Player player) {
        if (!shouldDespawn(player)) return; // Ensure despawn only happens if needed
        this.exist = false;
        this.spawnStrategy.despawn(this, player);
    }

    private Boolean shouldDespawn(Player player) {
        Pos playerPosition = player.getPosition();
        return playerPosition.distance(this.getSpawnPosition()) > viewDistance && exist;
    }


    // Spawn Position Methods

    public Pos getSpawnPosition() {
        return spawnPosition;
    }

    public void setSpawnPosition(Pos spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    // Identifier Methods

    public String getIdentifier() {
        return this.identifier;
    }

    public PlayerSkin getSkin() {
        return skin;
    }

    public void setSkin(PlayerSkin skin) {
        this.skin = skin;
    }

    public Boolean doExist() {
        return exist;
    }

    public Integer getViewDistance() {
        return viewDistance;
    }

    public void setViewDistance(Integer viewDistance) {
        this.viewDistance = viewDistance;
    }
// Action Methods

    public final void onInteract(Player player) {
        if (getActionList().isEmpty()) return;
        getActionList().executeNext(player);
    }

    public ActionList getActionList() {
        return actionList;
    }

    public void setActionList(ActionList actionList) {
        this.actionList = actionList;
    }

    public void setSkinLayer(SkinLayer skinLayer) {
        this.skinLayer = skinLayer;
    }

    public SkinLayer getSkinLayer() {
        return skinLayer;
    }

    public GoalSelector getGoal() {
        return goal;
    }

    public void setGoal(GoalSelector goal) {
        this.goal = goal;
    }
}


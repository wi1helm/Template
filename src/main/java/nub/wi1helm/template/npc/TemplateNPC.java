package nub.wi1helm.template.npc;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.utils.chunk.ChunkUtils;
import nub.wi1helm.template.npc.actions.ActionList;
import nub.wi1helm.template.npc.hologram.TemplateTextNPC;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class TemplateNPC extends Entity {

    private Integer viewDistance = 10;
    private ActionList actionList = ActionList.empty();
    // Todo See if goal can be changed for something else later on.
    private GoalSelector goal;

    public TemplateNPC(@NotNull EntityType entityType) {
        super(entityType);
        setAutoViewable(false);
        TemplateNPCHandler.registerNPC(this);
    }

    public void check(Player player) {
        this.personalize(player);

        if (inView(player) && inLoadedChunk(player)) {
            // In view try to spawn
            // If player already is a viewer return
            if (isViewer(player)) return;
            onSpawn(player);
            spawn(player);
            return;
        }
        // if not in view try to despawn
        // if player already doesnt see entity return
        if (!isViewer(player)) return;
        despawn(player);
        onDespawn(player);
    }

    protected void spawn(Player player) {

        this.addViewer(player);

        this.updateNewViewer(player);

        if (this instanceof Posable) {
            ((Posable) this).handlePose(this, player);
        }

        if (this instanceof Namable) {
            this.getPassengers().forEach(entity -> {
                if (entity instanceof TemplateTextNPC text) {
                    text.spawn(player);
                }
            });
        }
    }

    protected void despawn(Player player) {

        this.removeViewer(player);
        this.updateOldViewer(player);
        if (this.getVehicle() == null) return;
        this.getVehicle().updateOldViewer(player);
        this.getVehicle().remove();
    }

    protected abstract void onSpawn(Player player);
    protected abstract void onDespawn(Player player);



    public void interact(Player player) {
        if (getActionList().isEmpty()) return;
        getActionList().executeNext(player);
    }

    abstract protected void personalize(Player player);


    public boolean inView(Player player) {
        return this.getDistance(player) <= getViewDistance();
    }

    public boolean inLoadedChunk(Player player) {
        // Todo fix this so its removed when the chunk is not renderd
        return true;
    }

    @Override
    public boolean isActive() {
        return super.isActive();
    }

    // Setters
    public void setViewDistance(Integer distance) {
        this.viewDistance = distance;
    }
    public void setActionList(ActionList actionList) {
        this.actionList = actionList;
    }
    public void setVehicle(Entity entity) {
        this.vehicle = entity;
    }


    // Getters
    public Integer getViewDistance() {return this.viewDistance;}
    public ActionList getActionList() {
        return actionList;
    }


    // Overwrite

    @Override
    public void sendPacketToViewers(@NotNull SendablePacket packet) {
        // Here so methods
    }


}

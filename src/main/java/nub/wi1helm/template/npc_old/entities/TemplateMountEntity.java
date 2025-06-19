package nub.wi1helm.template.npc_old.entities;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class TemplateMountEntity extends Entity {
    public TemplateMountEntity() {
        super(EntityType.ARMOR_STAND);

        this.editEntityMeta(ArmorStandMeta.class, meta -> {
            meta.setInvisible(true);
            meta.setHasNoGravity(true);
            meta.setMarker(true); // Ensures it does not interfere with collisions
        });
    }

    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos pos){
        this.instance = instance;
        this.position = pos;
        return null;
    }


}

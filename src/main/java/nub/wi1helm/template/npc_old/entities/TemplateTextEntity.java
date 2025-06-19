package nub.wi1helm.template.npc_old.entities;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class TemplateTextEntity extends Entity {
    public TemplateTextEntity(Component component) {
        super(EntityType.TEXT_DISPLAY);

        this.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setText(component);
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            meta.setHasNoGravity(true);
        });
    }

    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos pos){
        this.instance = instance;
        this.position = pos;
        return null;
    }
}

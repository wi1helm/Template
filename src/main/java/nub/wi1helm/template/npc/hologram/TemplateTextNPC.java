package nub.wi1helm.template.npc.hologram;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.network.packet.server.play.SetPassengersPacket;
import nub.wi1helm.template.npc.TemplateNPC;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class TemplateTextNPC extends TemplateNPC {

    private final int rows;
    private float spacing = 0.3F;
    private float offset = 1;
    private List<Entity> entities;

    public TemplateTextNPC(int rows) {
        super(EntityType.INTERACTION);
        this.rows = rows;
        this.entities = new ArrayList<>((rows*2) + 1);

        this.editEntityMeta(InteractionMeta.class, meta -> {
            meta.setWidth(0.1F);
            meta.setHasNoGravity(true);
            meta.setHeight(0.1F);
            // Height will be set in setText, so no need to set it here
        });

        Entity offset = new Entity(EntityType.INTERACTION);
        offset.editEntityMeta(InteractionMeta.class, meta -> {
            meta.setWidth(0.1F);
            meta.setHasNoGravity(true);
            meta.setHeight(this.offset);
            // Height will be set in setText, so no need to set it here
        });
        entities.add(offset);

        // Initialize spacers
        for (int i = 1; i < (2*rows + 1); i++) {

            Entity textDisplay = new Entity(EntityType.TEXT_DISPLAY);
            textDisplay.editEntityMeta(TextDisplayMeta.class, meta -> {
                meta.setHasNoGravity(true);
                meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            });
            // Text will be set in setText, so no need to set it here
            entities.add(textDisplay);



            Entity spacer = new Entity(EntityType.INTERACTION);
            spacer.editEntityMeta(InteractionMeta.class, meta -> {
                meta.setWidth(0.1F);
                meta.setHasNoGravity(true);
                // Height will be set in setText, so no need to set it here
            });
            entities.add(spacer);
        }
    }

    public void setText(Component... text) {
        if (text.length != rows) {
            throw new IllegalArgumentException("Text length must be equal to the number of rows: " + rows);
        }

        for (int i = 0; i < rows; i++) {
            Entity textEntity = entities.get(i * 2 + 1);
            Component content = text[i];
            textEntity.editEntityMeta(TextDisplayMeta.class, meta -> meta.setText(content));
        }

        for (int i = 1; i < entities.size(); i += 2) {
            Entity spacer = entities.get(i - 1);
            spacer.editEntityMeta(InteractionMeta.class, meta -> meta.setHeight(spacing));
        }
        entities.getFirst().editEntityMeta(InteractionMeta.class, meta -> meta.setHeight(offset));
    }

    @Override
    protected void onSpawn(Player player) {
        Entity previousEntity = this;

        for (Entity entity : entities) {
            player.sendMessage(previousEntity.toString());
            entity.setInstance(getInstance(), getPosition());
            player.sendPacket(entity.getEntityType().registry().spawnType().getSpawnPacket(entity));
            player.sendPacket(entity.getMetadataPacket());
            player.sendPacket(new SetPassengersPacket(previousEntity.getEntityId(), List.of(entity.getEntityId())));

            previousEntity = entity;
        }
    }


    @Override
    protected void onDespawn(Player player) {
        for (Entity entity : entities) {
            entity.remove();
            entity.updateOldViewer(player);
        }

    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }
}

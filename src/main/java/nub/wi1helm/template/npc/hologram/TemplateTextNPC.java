package nub.wi1helm.template.npc.hologram;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.SetPassengersPacket;
import net.minestom.server.network.packet.server.play.SpawnEntityPacket;
import nub.wi1helm.template.npc.TemplateNPC;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class TemplateTextNPC extends TemplateNPC {

    List<Component> text = new ArrayList<>();
    List<Entity> entities = new ArrayList<>();
    Float offset = 0.2F; // Initial offset from the base interaction entity
    Float spacing = 0.3F; // Spacing between text lines

    public TemplateTextNPC(Instance instance, Component... text) {
        super(EntityType.INTERACTION, instance);
        this.text = List.of(text); // Initialize text
        Init(); // Initialize base entity meta
        Chain(); // Build the hologram chain
    }

    public TemplateTextNPC(Instance instance, Pos pos, Component... text) {
        super(EntityType.INTERACTION, instance, pos);
        this.text = List.of(text); // Initialize text
        Init(); // Initialize base entity meta
        Chain(); // Build the hologram chain
    }

    public TemplateTextNPC(Instance instance, Pos pos, Float offset, Float spacing, Component... text) {
        super(EntityType.INTERACTION, instance, pos);
        this.text = List.of(text); // Initialize text
        this.offset = offset;
        this.spacing = spacing;
        Init(); // Initialize base entity meta
        Chain(); // Build the hologram chain
    }


    private void Init() {
        editEntityMeta(InteractionMeta.class, meta -> {
            meta.setHeight(offset); // Set initial offset as the base interaction's height
            meta.setWidth(0.1F);
            meta.setHasNoGravity(true);
            meta.setResponse(false);
        });
        // Ensure the base NPC entity is not auto-viewable for manual packet control
        this.setAutoViewable(false);
    }

    private void Chain() {
        entities.clear(); // Clear existing components before rebuilding

        Entity currentVehicle = this; // The base TemplateTextNPC is the first vehicle

        for (int i = 0; i < text.size(); i++) {
            Component line = text.get(i);

            // Create and add the Text Display entity
            Entity display = createDisplay(instance, line);
            entities.add(display);
            display.setInstance(instance, Pos.ZERO); // Position relative to vehicle
            currentVehicle.addPassenger(display);
            currentVehicle = display;

            Entity spacer = createSpacer(instance);
            entities.add(spacer);
            spacer.setInstance(instance, Pos.ZERO); // Position relative to text display
            currentVehicle.addPassenger(spacer);
            currentVehicle = spacer; // Spacer becomes the vehicle for the next line

        }
    }

    private @NotNull Entity createDisplay(@NotNull Instance instance, @NotNull Component text) {
        Entity display = new Entity(EntityType.TEXT_DISPLAY);
        display.setInstance(instance);
        display.setAutoViewable(false);

        display.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setText(text);
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            meta.setBackgroundColor(0);
            meta.setHasNoGravity(true);
        });

        return display;
    }

    private @NotNull Entity createSpacer(@NotNull Instance instance) {
        Entity spacer = new Entity(EntityType.INTERACTION);
        spacer.setInstance(instance);
        spacer.setAutoViewable(false);
        spacer.editEntityMeta(InteractionMeta.class, meta -> {
            meta.setHeight(spacing);
            meta.setWidth(0.2F);
            meta.setHasNoGravity(true);
            meta.setResponse(false);
        });
        return spacer;
    }

    @Override
    public Collection<SendablePacket> getNpcSpawnPackets(Player player) {
        List<SendablePacket> packets = new ArrayList<>();

        // 1. Add spawn and metadata packets for the base NPC
        addEntitySpawnPackets(packets, this);

        // 2. Add spawn and metadata packets for all hologram components (spacers and text displays)
        for (Entity component : entities) {
            addEntitySpawnPackets(packets, component);
        }

        // 3. Send SetPassengersPacket for each link in the chain
        // The base NPC is the vehicle for the first component in 'entities'
        if (!entities.isEmpty()) {
            // The first entity in your 'entities' list (the first spacer) is a passenger of the base NPC.
            packets.add(new SetPassengersPacket(this.getEntityId(), List.of(entities.getFirst().getEntityId())));

            // Now, iterate through the rest of the chain.
            // Each component is a vehicle for the next component.
            for (int i = 0; i < entities.size() - 1; i++) {
                Entity currentVehicle = entities.get(i);
                Entity nextPassenger = entities.get(i + 1);
                packets.add(new SetPassengersPacket(currentVehicle.getEntityId(), List.of(nextPassenger.getEntityId())));
            }
        }

        return packets;
    }

    // Helper to generate SpawnEntityPacket and EntityMetaDataPacket for any entity
    private void addEntitySpawnPackets(List<SendablePacket> packets, Entity entity) {
        packets.add(new SpawnEntityPacket(
                entity.getEntityId(),
                entity.getUuid(),
                entity.getEntityType().id(),
                entity.getPosition(),
                entity.getPosition().yaw(),
                0,
                (short) 0,
                (short) 0,
                (short) 0
        ));
        packets.add(entity.getMetadataPacket());
    }

    @Override
    public Collection<SendablePacket> getNpcDespawnPackets(Player player) {
        List<SendablePacket> packets = new ArrayList<>();

        // Collect all entity IDs that need to be despawned
        List<Integer> entityIdsToDestroy = new ArrayList<>();
        entityIdsToDestroy.add(this.getEntityId()); // Include the base TemplateTextNPC
        for (Entity component : entities) {
            entityIdsToDestroy.add(component.getEntityId()); // Include all hologram components
        }

        packets.add(new DestroyEntitiesPacket(entityIdsToDestroy));

        return packets;
    }

    @Override
    public void personalize(@NotNull Player player) {
        // No default personalization logic
    }
}
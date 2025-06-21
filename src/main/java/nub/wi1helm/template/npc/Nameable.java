package nub.wi1helm.template.npc;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.SetPassengersPacket;
import nub.wi1helm.template.npc.hologram.TemplateTextNPC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public interface Nameable {

    // A static map to keep track of holograms attached to each Nameable entity.
    ConcurrentMap<Entity, TemplateTextNPC> attachedHolograms = new ConcurrentHashMap<>();

    default void setName(@NotNull Instance instance, @NotNull Component... lines) {
        Entity self = (Entity) this;

        // Check if there's an existing hologram attached to this entity
        TemplateTextNPC existingHologram = attachedHolograms.get(self);

        if (existingHologram != null) {
            // If it exists, detach it and remove it server-side.
            // This is crucial for cleanup before attaching a new one.
            self.removePassenger(existingHologram);
            existingHologram.remove(); // Removes server-side entity tracking and calls its components' remove()
            attachedHolograms.remove(self);
        }

        // Create a new TemplateTextNPC (hologram base)
        TemplateTextNPC newHologram = new TemplateTextNPC(instance, self.getPosition(), lines) {
            @Override
            public void personalize(@NotNull Player player) {
                // This specific personalize method for the attached hologram
                // can remain empty as its primary function is to display text
                // and follow the parent entity.
            }
        };

        // Attach the new hologram as a passenger to this entity (server-side)
        self.addPassenger(newHologram);

        // Spawn the new hologram's entities server-side
        newHologram.spawn();

        // Store the new hologram in the map for future reference
        attachedHolograms.put(self, newHologram);
    }

    default void setName(@NotNull Instance instance, Float offset, Float spacing, @NotNull Component... lines) {
        Entity self = (Entity) this;

        // Check if there's an existing hologram attached to this entity
        TemplateTextNPC existingHologram = attachedHolograms.get(self);

        if (existingHologram != null) {
            // If it exists, detach it and remove it server-side.
            // This is crucial for cleanup before attaching a new one.
            self.removePassenger(existingHologram);
            existingHologram.remove(); // Removes server-side entity tracking and calls its components' remove()
            attachedHolograms.remove(self);
        }

        // Create a new TemplateTextNPC (hologram base)
        TemplateTextNPC newHologram = new TemplateTextNPC(instance, self.getPosition(), offset, spacing, lines) {
            @Override
            public void personalize(@NotNull Player player) {
                // This specific personalize method for the attached hologram
                // can remain empty as its primary function is to display text
                // and follow the parent entity.
            }
        };

        // Attach the new hologram as a passenger to this entity (server-side)
        self.addPassenger(newHologram);

        // Spawn the new hologram's entities server-side
        newHologram.spawn();

        // Store the new hologram in the map for future reference
        attachedHolograms.put(self, newHologram);
    }


    /**
     * Removes the name hologram attached to this entity, if one exists.
     * This method handles server-side detachment and removal.
     * Packet generation for despawn is handled by getNameHologramDespawnPackets.
     */
    default void removeName() {
        Entity self = (Entity) this;
        TemplateTextNPC hologram = attachedHolograms.remove(self);
        if (hologram != null) {
            self.removePassenger(hologram); // Detach server-side
            hologram.remove(); // Remove hologram entities server-side
        }
    }

    /**
     * Retrieves the necessary packets to spawn the attached name hologram for a specific player.
     * This method should be called by the implementing entity's own packet-sending logic (e.g., getSpawnPackets).
     *
     * @param player The player to send the packets to.
     * @return A collection of packets to spawn the name hologram for the player.
     */
    default @NotNull Collection<SendablePacket> getNameSpawnPackets(@NotNull Player player) {
        Entity self = (Entity) this;
        TemplateTextNPC hologram = attachedHolograms.get(self);
        List<SendablePacket> packets = new ArrayList<>();

        if (hologram != null) {
            // Get all the internal spawn packets for the hologram (its base and text/spacer entities)
            packets.addAll(hologram.getNpcSpawnPackets(player));

            // Also, send the SetPassengersPacket to link the main entity (self) with the hologram.
            // This is crucial because the hologram is a passenger of 'self'.
            // Only add if the passenger relationship actually exists server-side.
            if (self.getPassengers().contains(hologram)) {
                packets.add(new SetPassengersPacket(self.getEntityId(), List.of(hologram.getEntityId())));
            }
        }
        return packets;
    }

    /**
     * Retrieves the necessary packets to despawn the attached name hologram for a specific player.
     * This method should be called by the implementing entity's own packet-sending logic (e.g., getDespawnPackets).
     *
     * @param player The player to send the packets to.
     * @return A collection of packets to despawn the name hologram for the player.
     */
    default @NotNull Collection<SendablePacket> getNameDespawnPackets(@NotNull Player player) {
        Entity self = (Entity) this;
        TemplateTextNPC hologram = attachedHolograms.get(self);
        if (hologram != null) {
            // Delegate to the TemplateTextNPC's despawn packet generation.
            // This handles destroying all its component entities.
            return hologram.getNpcDespawnPackets(player);
        }
        return List.of();
    }
}
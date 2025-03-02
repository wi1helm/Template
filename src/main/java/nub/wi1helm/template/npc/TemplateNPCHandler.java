package nub.wi1helm.template.npc;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TemplateNPCHandler {
    private static final Map<String, TemplateNPC> npcRegistry = new HashMap<>();

    /**
     * Registers an NPC in the registry.
     *
     * @param npc The NPC to register.
     */
    public static void registerNPC(TemplateNPC npc) {
        npcRegistry.put(npc.getIdentifier(), npc);
    }

    /**
     * Retrieves an NPC by its identifier.
     *
     * @param identifier The identifier of the NPC.
     * @return The NPC instance, or null if not found.
     */
    public static TemplateNPC getNPCByIdentifier(String identifier) {
        return npcRegistry.get(identifier);
    }

    /**
     * Retrieves all registered NPCs.
     *
     * @return A collection of all registered NPCs.
     */
    public static Collection<TemplateNPC> getAllNPCs() {
        return npcRegistry.values();
    }

    /**
     * Initializes the event listeners for spawning, despawning, interaction, and attack handling.
     */
    public static void initialize() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerMoveEvent.class, e -> handleSpawnDespawn(e.getPlayer()));
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, e -> handleSpawnDespawn(e.getPlayer()));
        MinecraftServer.getGlobalEventHandler().addListener(PlayerEntityInteractEvent.class, e -> {
            if (e.getHand() == PlayerHand.MAIN) handleInteraction(e.getPlayer(), e.getTarget());
        });
        MinecraftServer.getGlobalEventHandler().addListener(EntityAttackEvent.class, event -> {
            // e.getEntity() is the *attacker*
            // e.getTarget() is what is being attacked (the target)
            Entity attacker = event.getEntity();
            Entity target = event.getTarget();

            // If we only care when a *player* is attacking something:
            if (attacker instanceof Player player) {
                handleInteraction(player, target);
            }
        });
    }

    /**
     * Handles spawning and despawning NPCs based on player position.
     *
     * @param player The player triggering the event.
     */
    private static void handleSpawnDespawn(Player player) {
        for (TemplateNPC npc : getAllNPCs()) {
            npc.spawn(player);
            npc.despawn(player);
        }
    }

    /**
     * Handles player interactions with NPCs.
     *
     * @param player The player interacting with the NPC.
     * @param target The entity being interacted with.
     */
    private static void handleInteraction(Player player, Entity target) {
        if (target instanceof TemplateNPC npc) {
            npc.onInteract(player);
        }
    }
}

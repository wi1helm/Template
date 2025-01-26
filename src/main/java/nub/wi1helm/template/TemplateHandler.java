package nub.wi1helm.template;

import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.MinecraftServer;

import java.util.*;

public class TemplateHandler {
    private static final Map<UUID, TemplateItem> templateRegistry = new HashMap<>();

    public static void registerTemplate(UUID identifier, TemplateItem template) {
        templateRegistry.put(identifier, template);
    }

    public static TemplateItem getTemplateByIdentifier(UUID identifier) {
        return templateRegistry.get(identifier);
    }

    public static Collection<TemplateItem> getAllRegisteredTemplates() {
        return templateRegistry.values();
    }

    public static void initialize() {
        MinecraftServer.getGlobalEventHandler().addListener(InventoryPreClickEvent.class, e -> {
            TemplateInventoryEvent event = new TemplateInventoryEvent(
                    e.getPlayer(),
                    e.getInventory(),
                    e.getClickedItem(),
                    e.getClickType(),
                    null,
                    e.getSlot()
            );

            handleCombinedEvent(event);
            if (event.isCancelled()) {
                e.setCancelled(true);
            }
        });

        MinecraftServer.getGlobalEventHandler().addListener(PlayerUseItemEvent.class, e -> {
            TemplateInventoryEvent event = new TemplateInventoryEvent(
                    e.getPlayer(),
                    null,
                    e.getItemStack(),
                    null,
                    e.getHand(),
                    -1
            );

            handleCombinedEvent(event);
            if (event.isCancelled()) {
                e.setCancelled(true);
            }
        });

        MinecraftServer.getGlobalEventHandler().addListener(ItemDropEvent.class, e -> {
            TemplateInventoryEvent combinedEvent = new TemplateInventoryEvent(
                    e.getPlayer(),
                    null,
                    e.getItemStack(),
                    null,
                    null,
                    -1
            );

            handleCombinedEvent(combinedEvent);
            if (combinedEvent.isCancelled()) {
                e.setCancelled(true);
            }
        });
    }

    private static void handleCombinedEvent(TemplateInventoryEvent event) {
        event.setCancelled(true);
        if (event.getItemStack().hasTag(Tag.UUID("uuid"))) {
            UUID itemUUID = event.getItemStack().getTag(Tag.UUID("uuid"));
            TemplateItem templateItem = getTemplateByIdentifier(itemUUID);

            if (templateItem != null) {
                if (event.getClickType() == null) {
                    templateItem.onDrop(event);
                }

                templateItem.onUse(event);
            }
        }
    }


}
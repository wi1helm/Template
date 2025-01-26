package nub.wi1helm.template;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        MinecraftServer.getGlobalEventHandler().addListener(InventoryPreClickEvent.class, event -> {
            event.setCancelled(true);

            Player player = event.getPlayer();
            ItemStack itemStack = event.getClickedItem();
            if (itemStack.hasTag(Tag.UUID("uuid"))) {
                UUID itemUUID = itemStack.getTag(Tag.UUID("uuid"));
                TemplateItem templateItem = getTemplateByIdentifier(itemUUID);

                if (templateItem != null) {
                    templateItem.onUse(player);
                }
            }
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerUseItemEvent.class, event -> {
            event.setCancelled(true);

            Player player = event.getPlayer();
            ItemStack itemStack = event.getItemStack();
            if (itemStack.hasTag(Tag.UUID("uuid"))) {
                UUID itemUUID = itemStack.getTag(Tag.UUID("uuid"));
                TemplateItem templateItem = getTemplateByIdentifier(itemUUID);

                if (templateItem != null) {
                    templateItem.onUse(player);
                }
            }
        });

        MinecraftServer.getGlobalEventHandler().addListener(ItemDropEvent.class, event -> {
            event.setCancelled(true);

            Player player = event.getPlayer();
            ItemStack itemStack = event.getItemStack();
            if (itemStack.hasTag(Tag.UUID("uuid"))) {
                UUID itemUUID = itemStack.getTag(Tag.UUID("uuid"));
                TemplateItem templateItem = getTemplateByIdentifier(itemUUID);

                if (templateItem != null) {
                    templateItem.onDrop(player);
                }
            }
        });

    }
}
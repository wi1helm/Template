// File: TemplateInventory.java
package nub.wi1helm.template.inventory;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class TemplateInventory {
    private final Component title;
    private final InventoryType type;
    private final Map<Integer, TemplateItem> items = new HashMap<>();

    public TemplateInventory(Component name, InventoryType type) {
        this.title = name;
        this.type = type;
        initialize();
    }

    protected abstract void initialize();

    protected abstract void personalize(Player player);

    public Component getName() {
        return title;
    }

    public InventoryType getType() {
        return type;
    }

    public void setItem(int slot, TemplateItem item) {
        items.put(slot, item);
    }

    public void fillInventory(TemplateItem filler) {
        for (int i = 0; i < type.getSize(); i++) { // Use the type's slot count
            items.putIfAbsent(i, filler);
        }
    }

    public Inventory constructInventory(Player player) {
        Inventory inventory = new Inventory(type, title);
        construct(player, inventory::setItemStack, inventory);
        return inventory;
    }

    public void constructPlayerInventory(Player player) {
        PlayerInventory inventory = player.getInventory();
        construct(player, inventory::setItemStack, inventory);
    }

    private void construct(Player player, BiConsumer<Integer, ItemStack> applyItem, AbstractInventory hostInventory) {
        personalize(player);

        for (Map.Entry<Integer, TemplateItem> entry : items.entrySet()) {
            int slot = entry.getKey();
            TemplateItem item = entry.getValue();
            if (item != null) {
                item.setHostInventory(hostInventory); // Set host inventory
                item.setHostSlot(slot); // Set slot in the host inventory
                applyItem.accept(slot, item.constructItemStack(player));
            }
        }
    }

    public Map<Integer, TemplateItem> getItems() {
        return items;
    }
}

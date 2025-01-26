package nub.wi1helm.template;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.event.trait.ItemEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TemplateInventoryEvent implements InventoryEvent, ItemEvent, PlayerInstanceEvent, CancellableEvent {
    private final Inventory inventory;
    private final ItemStack itemStack;
    private final Player player;
    private final ClickType clickType;
    private final Player.Hand hand;
    private final int slot;
    private boolean cancelled;

    public TemplateInventoryEvent(@NotNull Player player, @Nullable Inventory inventory, @NotNull ItemStack itemStack,
                                  @Nullable ClickType clickType, @Nullable Player.Hand hand, int slot) {
        this.player = player;
        this.inventory = inventory;
        this.itemStack = itemStack;
        this.clickType = clickType;
        this.hand = hand;
        this.slot = slot;
    }

    @Nullable
    public Inventory getInventory() {
        return inventory;
    }

    @NotNull
    public ItemStack getItemStack() {
        return itemStack;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @Nullable
    public ClickType getClickType() {
        return clickType;
    }

    @Nullable
    public Player.Hand getHand() {
        return hand;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

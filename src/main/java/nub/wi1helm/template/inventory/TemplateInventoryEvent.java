package nub.wi1helm.template.inventory;

import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.event.trait.ItemEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TemplateInventoryEvent implements InventoryEvent, ItemEvent, PlayerInstanceEvent, CancellableEvent {
    private final AbstractInventory inventory;
    private final ItemStack itemStack;
    private final Player player;
    private final ClickType clickType;
    private final PlayerHand hand;
    private final int slot;
    private boolean cancelled;

    public TemplateInventoryEvent(@NotNull Player player, @Nullable AbstractInventory inventory, @NotNull ItemStack itemStack,
                                  @Nullable ClickType clickType, @Nullable PlayerHand hand, int slot) {
        this.player = player;
        this.inventory = inventory;
        this.itemStack = itemStack;
        this.clickType = clickType;
        this.hand = hand;
        this.slot = slot;
    }

    @Nullable
    public AbstractInventory getInventory() {
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
    public PlayerHand getHand() {
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

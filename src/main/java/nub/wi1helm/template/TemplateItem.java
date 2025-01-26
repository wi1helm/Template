package nub.wi1helm.template;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.CustomData;
import net.minestom.server.item.component.DyedItemColor;
import net.minestom.server.item.component.HeadProfile;
import net.minestom.server.tag.Tag;

import java.util.*;

public abstract class TemplateItem {
    private final UUID identifier;

    private Material material;
    private Component name;
    private List<Component> lore;
    private int stackCount = 1;
    private boolean glint;
    private Integer modelData;
    private DyedItemColor dyeColor;
    private boolean hideTooltip;
    private PlayerSkin skin;
    private List<CustomData> customData;

    private AbstractInventory hostInventory;
    private int hostSlot;

    public TemplateItem(Material material) {
        this.material = material;
        this.identifier = UUID.randomUUID();

        TemplateHandler.registerTemplate(identifier, this);

        initialize();
    }

    public ItemStack constructItemStack(Player player) {

        personalize(player);

        ItemStack.Builder builder = ItemStack.builder(this.material);

        if (name != null) builder.set(ItemComponent.ITEM_NAME, name);
        if (lore != null) builder.set(ItemComponent.LORE, lore);
        if (stackCount > 0) builder.amount(stackCount);
        if (glint) builder.set(ItemComponent.ENCHANTMENT_GLINT_OVERRIDE, true);
        if (modelData != null) builder.set(ItemComponent.CUSTOM_MODEL_DATA, modelData);
        if (dyeColor != null) builder.set(ItemComponent.DYED_COLOR, dyeColor);
        if (hideTooltip) builder.set(ItemComponent.HIDE_TOOLTIP);
        if (skin != null && material.equals(Material.PLAYER_HEAD)) builder.set(ItemComponent.PROFILE, new HeadProfile(skin));
        if (identifier != null) builder.setTag(Tag.UUID("uuid"),identifier);
        return builder.build();
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setName(Component name) {
        this.name = name;
    }

    public void setLore(List<Component> lore) {
        this.lore = lore;
    }

    public void setStackCount(int count) {
        this.stackCount = count;
    }

    public void setGlint(boolean glint) {
        this.glint = glint;
    }

    public void setModelData(Integer modelData) {
        this.modelData = modelData;
    }

    public void setDyeColor(DyedItemColor dyeColor) {
        this.dyeColor = dyeColor;
    }

    public void hideTooltip() {
        this.hideTooltip = true;
    }

    public void setSkin(PlayerSkin skin) {
        this.skin = skin;
    }

    public AbstractInventory getHostInventory() {
        return hostInventory;
    }

    public void setHostInventory(AbstractInventory hostInventory) {
        this.hostInventory = hostInventory;
    }

    public int getHostSlot() {
        return hostSlot;
    }

    public void setHostSlot(int hostSlot) {
        this.hostSlot = hostSlot;
    }

    protected abstract void initialize();

    protected abstract void personalize(Player player);

    public abstract void onUse(TemplateInventoryEvent event);

    public abstract void onDrop(TemplateInventoryEvent event);

    public UUID getIdentifier() {
        return identifier;
    }
}
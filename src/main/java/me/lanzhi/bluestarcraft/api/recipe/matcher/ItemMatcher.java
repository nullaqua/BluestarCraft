package me.lanzhi.bluestarcraft.api.recipe.matcher;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public interface ItemMatcher extends ConfigurationSerializable, Cloneable
{
    boolean match(ItemStack itemStack);

    public ItemMatcher clone();
}

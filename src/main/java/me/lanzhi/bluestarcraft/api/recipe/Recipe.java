package me.lanzhi.bluestarcraft.api.recipe;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public interface Recipe extends ConfigurationSerializable,Cloneable
{
    String getName();

    boolean needSave();

    boolean match(CraftInventory inventory);

    default ItemStack getResult(CraftInventory inventory)
    {
        if (match(inventory))
        {
            return getResult();
        }
        return null;
    }

    ItemStack getResult();
}

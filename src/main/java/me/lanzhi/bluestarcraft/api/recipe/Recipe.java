package me.lanzhi.bluestarcraft.api.recipe;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Recipe extends ConfigurationSerializable,Cloneable
{
    String getName();

    boolean needSave();

    boolean match(CraftInventory inventory);

    default List<ItemStack> getResult(CraftInventory inventory)
    {
        if (match(inventory))
        {
            return getResult();
        }
        return null;
    }

    List<ItemStack> getResult();
}

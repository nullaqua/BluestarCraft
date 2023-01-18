package me.lanzhi.bluestarcraft.api.recipe;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Recipe extends ConfigurationSerializable, Cloneable
{
    String getName();

    boolean needSave();

    List<ItemStack> getResult(HumanEntity player,CraftInventory inventory);

    Recipe clone();
}

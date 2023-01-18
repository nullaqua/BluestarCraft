package me.lanzhi.bluestarcraft.api.recipe;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class AbstractRecipe implements DisplayableRecipe
{
    @Override
    public List<ItemStack> getResult(HumanEntity ignoredPlayer,CraftInventory inventory)
    {
        if (match(inventory))
        {
            return getResult();
        }
        return null;
    }

    protected abstract boolean match(CraftInventory inventory);

    protected abstract List<ItemStack> getResult();

    @Override
    public abstract AbstractRecipe clone();
}
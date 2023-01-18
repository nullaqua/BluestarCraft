package me.lanzhi.bluestarcraft.api.recipe;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface DisplayableRecipe extends Recipe
{
    List<DisplayInfo> displays(Player player);

    public static final class DisplayInfo
    {
        private final CraftInventory inventory;
        private final List<ItemStack> result;

        public DisplayInfo(CraftInventory inventory,List<ItemStack> result)
        {
            this.inventory=inventory;
            this.result=new ArrayList<>(result);
        }

        public CraftInventory inventory()
        {
            return inventory;
        }

        public List<ItemStack> result()
        {
            return result;
        }
    }
}
package me.lanzhi.bluestarcraft.api.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lanzhi
 */
public final class CraftInventory implements Cloneable
{
    private final static List<Integer> craftItem=Arrays.asList(0,1,2,3,4,9,10,11,12,13,18,19,20,21,22,27,28,29,30,31,36,37,38,39,40);
    private List<ItemStack> itemStacks=new ArrayList<>();

    public CraftInventory(Inventory inventory)
    {
        for (int i: craftItem)
        {
            ItemStack itemStack=inventory.getItem(i);
            if (itemStack==null)
            {
                itemStack=new ItemStack(Material.AIR);
            }
            else
            {
                itemStack=itemStack.clone();
            }
            itemStack.setAmount(1);
            itemStacks.add(itemStack);
        }
    }

    @NotNull
    public ItemStack getItem(int index)
    {
        return itemStacks.get(index).clone();
    }

    @NotNull
    public ItemStack getItem(int x,int y)
    {
        return itemStacks.get(x*5+y).clone();
    }

    public ItemStack[] getItems()
    {
        List<ItemStack>list=new ArrayList<>();
        for (ItemStack itemStack:itemStacks)
        {
            list.add(itemStack.clone());
        }
        return list.toArray(new ItemStack[0]);
    }

    @Override
    public CraftInventory clone()
    {
        CraftInventory clone;
        try
        {
            clone=(CraftInventory) super.clone();
            clone.itemStacks=new ArrayList<>();
            for (ItemStack itemStack: itemStacks)
            {
                clone.itemStacks.add(itemStack.clone());
            }
        }
        catch (CloneNotSupportedException e)
        {
            return null;
        }
        return clone;
    }
}

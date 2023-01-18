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
    private final static List<Integer> craftItem;

    static
    {
        //0-4,9-13,18-22,27-31,36-40
        craftItem=new ArrayList<>();
        for (int i=0;i<5;i++)
        {
            for (int j=0;j<5;j++)
            {
                craftItem.add(i*9+j);
            }
        }
    }

    public CraftInventory(ItemStack... itemStacks)
    {
        this.itemStacks=new ArrayList<>();
        this.itemStacks.addAll(Arrays.asList(itemStacks));
        if (this.itemStacks.size()<25)
        {
            for (int i=this.itemStacks.size();i<25;i++)
            {
                this.itemStacks.add(new ItemStack(Material.AIR));
            }
        }
        if (this.itemStacks.size()>25)
        {
            this.itemStacks=this.itemStacks.subList(0,25);
        }
    }

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

    public CraftInventory(List<ItemStack> itemStacks)
    {
        this.itemStacks=new ArrayList<>();
        this.itemStacks.addAll(itemStacks);
        if (this.itemStacks.size()<25)
        {
            for (int i=this.itemStacks.size();i<25;i++)
            {
                this.itemStacks.add(new ItemStack(Material.AIR));
            }
        }
        if (this.itemStacks.size()>25)
        {
            this.itemStacks=this.itemStacks.subList(0,25);
        }
    }

    public static List<Integer> craftItem()
    {
        return craftItem;
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
        List<ItemStack> list=new ArrayList<>();
        for (ItemStack itemStack: itemStacks)
        {
            list.add(itemStack.clone());
        }
        return list.toArray(new ItemStack[0]);
    }

    @Override
    public CraftInventory clone()
    {
        return new CraftInventory(itemStacks.toArray(new ItemStack[0]));
    }

}

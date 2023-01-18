package me.lanzhi.bluestarcraft.api.recipe.matcher;

import me.lanzhi.bluestarapi.config.AutoSerialize;
import me.lanzhi.bluestarapi.config.SerializeAs;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 一个精确匹配器,即需要物品类型和nbt都相同
 *
 * @author Lanzhi
 */
@SerializeAs("BluestarCraft.ExactMatcher")
public final class ExactMatcher implements DisplayableItemMatcher, AutoSerialize
{
    private final ArrayList<ItemStack> itemStacks=new ArrayList<>();

    public ExactMatcher()
    {
    }

    /**
     * 匹配多个物品
     * @param itemStacks 匹配列表
     */
    public ExactMatcher(Collection<ItemStack> itemStacks)
    {
        for (ItemStack itemStack: itemStacks)
        {
            this.itemStacks.add(itemStack.clone());
        }
    }

    /**
     * 匹配多个物品
     * @param itemStacks 匹配列表
     */
    public ExactMatcher(ItemStack... itemStacks)
    {
        for (ItemStack itemStack: itemStacks)
        {
            this.itemStacks.add(itemStack.clone());
        }
    }

    @Override
    public boolean match(ItemStack itemStack)
    {
        for (ItemStack item: this.itemStacks)
        {
            if (itemStack.isSimilar(item))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public ExactMatcher clone()
    {
        return new ExactMatcher(itemStacks.toArray(new ItemStack[0]));
    }

    @Override
    public ItemStack get()
    {
        return itemStacks.get(0);
    }
}

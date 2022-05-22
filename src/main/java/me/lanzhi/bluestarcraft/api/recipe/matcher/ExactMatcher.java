package me.lanzhi.bluestarcraft.api.recipe.matcher;

import me.lanzhi.bluestarapi.Api.config.AutoSerializeInterface;
import me.lanzhi.bluestarapi.Api.config.SerializeAs;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@SerializeAs("BluestarCraft.ExactMatcher")
public final class ExactMatcher implements ItemMatcher, AutoSerializeInterface
{
    private final ArrayList<ItemStack> itemStacks=new ArrayList<>();

    public ExactMatcher()
    {
    }

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
}

package me.lanzhi.bluestarcraft.api.recipe.matcher;

import me.lanzhi.bluestarcraft.api.BluestarCraft;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public interface ItemMatcher extends ConfigurationSerializable, Cloneable
{
    boolean match(ItemStack itemStack);

    public ItemMatcher clone();

    ItemStack get();

    static EmptyMatcher EMPTY_MATCHER=new EmptyMatcher();

    final class EmptyMatcher implements ItemMatcher
    {
        private EmptyMatcher()
        {
        }
        @Override
        public boolean match(ItemStack itemStack)
        {
            return itemStack==null||itemStack.getType()==null||BluestarCraft.getPlugin().isAir(itemStack);
        }

        @Override
        public ItemMatcher clone()
        {
            return EMPTY_MATCHER;
        }

        @Override
        public ItemStack get()
        {
            return null;
        }

        @NotNull
        @Override
        public Map<String, Object> serialize()
        {
            return new HashMap<>();
        }

        @NotNull
        public static EmptyMatcher deserialize(Map<String, Object> map)
        {
            return EMPTY_MATCHER;
        }
    }
}

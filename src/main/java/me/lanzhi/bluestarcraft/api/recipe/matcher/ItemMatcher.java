package me.lanzhi.bluestarcraft.api.recipe.matcher;

import me.lanzhi.bluestarcraft.api.BluestarCraft;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个物品匹配器,用于匹配物品
 *
 * @author Lanzhi
 */
public interface ItemMatcher extends ConfigurationSerializable, Cloneable
{

    /**
     * 一个物品是否可被此匹配器匹配
     * @param itemStack 物品
     * @return 是否匹配
     */
    boolean match(ItemStack itemStack);

    public ItemMatcher clone();

    static EmptyMatcher EMPTY_MATCHER=new EmptyMatcher();

    final class EmptyMatcher implements DisplayableItemMatcher
    {
        ChatColor

        private EmptyMatcher()
        {
        }

        @Override
        public boolean match(ItemStack itemStack)
        {
            return itemStack==null||BluestarCraft.getPlugin().isAir(itemStack);
        }

        @Override
        public ItemMatcher clone()
        {
            return EMPTY_MATCHER;
        }

        @Override
        public ItemStack get()
        {
            return new ItemStack(Material.AIR);
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

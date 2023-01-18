package me.lanzhi.bluestarcraft.api.recipe.matcher;

import org.bukkit.inventory.ItemStack;

/**
 * 一个支持在配方查询书中展示的物品匹配器
 *
 * @author Lanzhi
 */
public interface DisplayableItemMatcher extends ItemMatcher
{
    /**
     * 获取一个用于展示的物品
     *
     * @return 用于展示的物品
     */
    ItemStack get();
}

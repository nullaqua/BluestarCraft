package me.lanzhi.bluestarcraft.api.recipe.matcher;

import org.bukkit.inventory.RecipeChoice;

/**
 * @author Lanzhi
 */
public interface ItemMatcherToBukkitAble extends ItemMatcher
{
    public Object toBukkit();
}

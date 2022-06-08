package me.lanzhi.bluestarcraft.api.recipe;

/**
 * @author Lanzhi
 */
public interface RecipeToBukkitAble extends Recipe
{
    public org.bukkit.inventory.Recipe toBukkit();
}

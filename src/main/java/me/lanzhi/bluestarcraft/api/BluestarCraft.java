package me.lanzhi.bluestarcraft.api;

import me.lanzhi.bluestarcraft.BluestarCraftPlugin;
import me.lanzhi.bluestarcraft.api.recipe.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class BluestarCraft
{
    private static BluestarCraftPlugin plugin;

    public static void addRecipe(@NotNull Recipe recipe)
    {
        plugin.getBluestarCraftManager().addRecipe(recipe);
    }

    public static boolean containsRecipe(@Nullable Recipe recipe)
    {
        return plugin.getBluestarCraftManager().containsRecipe(recipe);
    }

    public static boolean containsRecipe(@Nullable String name)
    {
        return plugin.getBluestarCraftManager().containsRecipe(name);
    }

    public static boolean removeRecipe(@NotNull Recipe recipe)
    {
        return plugin.getBluestarCraftManager().removeRecipe(recipe);
    }

    public static Recipe removeRecipe(@NotNull String name)
    {
        return plugin.getBluestarCraftManager().removeRecipe(name);
    }

    @NotNull
    public static List<Recipe> getRecipes()
    {
        return plugin.getBluestarCraftManager().getRecipes();
    }

    @NotNull
    public static List<String> getRecipeNames()
    {
        return plugin.getBluestarCraftManager().getRecipeNames();
    }

    public static void openCraftGui(@NotNull Player player)
    {
        plugin.getBluestarCraftManager().openCraftGui(player);
    }

    public static void setPlugin(BluestarCraftPlugin plugin)
    {
        if (BluestarCraft.plugin!=null)
        {
            return;
        }
        BluestarCraft.plugin=plugin;
    }

    public static BluestarCraftPlugin getPlugin()
    {
        return plugin;
    }

    private static double getVersion()
    {
        return Double.parseDouble(Bukkit.getBukkitVersion().substring(2));
    }
}

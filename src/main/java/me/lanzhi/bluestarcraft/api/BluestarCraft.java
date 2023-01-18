package me.lanzhi.bluestarcraft.api;

import me.lanzhi.bluestarcraft.BluestarCraftPlugin;
import me.lanzhi.bluestarcraft.api.recipe.DisplayableRecipe;
import me.lanzhi.bluestarcraft.api.recipe.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class BluestarCraft
{
    private static BluestarCraftPlugin plugin;

    /**
     * 注册一个合成配方
     *
     * @param recipe 配方
     */
    public static void addRecipe(@NotNull Recipe recipe)
    {
        plugin.getBluestarCraftManager().addRecipe(recipe);
    }

    /**
     * 注册一个合成表,并使其在UI中显示
     */
    public static void addDisplayableRecipe(@NotNull DisplayableRecipe recipe)
    {
        plugin.getBluestarCraftManager().addDisplayableRecipe(recipe);
    }

    public static void removeDisplayableRecipe(@NotNull DisplayableRecipe recipe)
    {
        plugin.getBluestarCraftManager().removeDisplayableRecipe(recipe.getName());
    }

    /**
     * 查询一个配方是否已经注册
     *
     * @param recipe 配方
     * @return 是否已经注册
     */
    public static boolean containsRecipe(@Nullable Recipe recipe)
    {
        return plugin.getBluestarCraftManager().containsRecipe(recipe);
    }

    /**
     * 查询一个配方是否已经注册
     *
     * @param name 配方名称
     * @return 是否已经注册
     */
    public static boolean containsRecipe(@Nullable String name)
    {
        return plugin.getBluestarCraftManager().containsRecipe(name);
    }

    /**
     * 删除一个配方
     * 如果配方不存在则不会有任何操作
     *
     * @param recipe 配方
     * @return 是否成功删除
     */
    public static boolean removeRecipe(@NotNull Recipe recipe)
    {
        return plugin.getBluestarCraftManager().removeRecipe(recipe);
    }

    /**
     * 删除一个配方
     * 如果配方不存在则不会有任何操作
     *
     * @param name 配方名称
     * @return 是否成功删除
     */
    public static Recipe removeRecipe(@NotNull String name)
    {
        return plugin.getBluestarCraftManager().removeRecipe(name);
    }

    /**
     * 获取所有已经注册的配方
     *
     * @return 所有已经注册的配方
     */
    @NotNull
    public static List<Recipe> getRecipes()
    {
        return plugin.getBluestarCraftManager().getRecipes();
    }

    /**
     * 通过名称获取配方
     *
     * @param name 配方名称
     * @return 配方
     */
    public static Recipe getRecipe(@NotNull String name)
    {
        return plugin.getBluestarCraftManager().getRecipe(name);
    }

    /**
     * 获取所有已经注册的配方的名称
     *
     * @return 所有已经注册的配方的名称
     */
    @NotNull
    public static List<String> getRecipeNames()
    {
        return plugin.getBluestarCraftManager().getRecipeNames();
    }

    /**
     * 为一个玩家打开合成台
     *
     * @param player 玩家
     */
    public static void openCraftGui(@NotNull Player player)
    {
        plugin.getBluestarCraftManager().openCraftGui(player);
    }

    /**
     * 为一个玩家打开合成表
     *
     * @param player 玩家
     */
    public static void openRecipesBook(@NotNull Player player)
    {
        plugin.getBluestarCraftManager().openRecipesGui(player);
    }

    /**
     * 获取插件实例
     *
     * @return 插件实例
     */
    public static BluestarCraftPlugin getPlugin()
    {
        return plugin;
    }

    /**
     * 内部使用
     *
     * @param plugin 插件
     */
    public static void setPlugin(BluestarCraftPlugin plugin)
    {
        if (BluestarCraft.plugin!=null)
        {
            return;
        }
        BluestarCraft.plugin=plugin;
    }

    /**
     * 获取服务器版本
     * 例如: 1.16.5返回16.5
     *
     * @return 服务器版本
     */
    private static double getVersion()
    {
        return Double.parseDouble(Bukkit.getBukkitVersion().substring(2));
    }
}

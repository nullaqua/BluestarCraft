package me.lanzhi.bluestarcraft;

import de.tr7zw.nbtapi.NBTItem;
import me.lanzhi.bluestarapi.api.config.AutoSerialize;
import me.lanzhi.bluestarapi.api.config.YamlFile;
import me.lanzhi.bluestarcraft.api.BluestarCraft;
import me.lanzhi.bluestarcraft.api.recipe.Recipe;
import me.lanzhi.bluestarcraft.api.recipe.ShapelessRecipe;
import me.lanzhi.bluestarcraft.api.recipe.matcher.ExactMatcher;
import me.lanzhi.bluestarcraft.api.recipe.matcher.MaterialMatcher;
import me.lanzhi.bluestarcraft.commands.craftCommand;
import me.lanzhi.bluestarcraft.commands.opencraftCommand;
import me.lanzhi.bluestarcraft.listeners.CraftGuiListener;
import me.lanzhi.bluestarcraft.listeners.CraftTableListener;
import me.lanzhi.bluestarcraft.listeners.RegisterListener;
import me.lanzhi.bluestarcraft.managers.BluestarCraftManager;
import me.lanzhi.bluestarcraft.managers.Metrics;
import me.lanzhi.bluestarcraft.managers.checkUpdata;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BluestarCraftPlugin extends JavaPlugin
{
    private BluestarCraftManager bluestarCraftManager;
    private YamlFile recipes;
    private YamlFile data;
    private YamlFile lang;
    private double version;
    private BukkitTask task;

    @Override
    public void onEnable()
    {
        version=Double.parseDouble(Bukkit.getBukkitVersion().split("-")[0].substring(2));
        info("识别到MInecraft版本: 1."+version);
        this.bluestarCraftManager=new BluestarCraftManager(this);

        getCommand("bluestarcraft").setExecutor(new craftCommand(this));
        getCommand("opencraft").setExecutor(new opencraftCommand(this));
        Bukkit.getPluginManager().registerEvents(new CraftGuiListener(this),this);
        Bukkit.getPluginManager().registerEvents(new CraftTableListener(this),this);
        Bukkit.getPluginManager().registerEvents(new RegisterListener(this),this);

        BluestarCraft.setPlugin(this);

        AutoSerialize.registerClass(ExactMatcher.class);
        AutoSerialize.registerClass(MaterialMatcher.class);
        AutoSerialize.registerClass(ShapelessRecipe.class);
        AutoSerialize.registerClass(me.lanzhi.bluestarcraft.api.recipe.ShapedRecipe.class);

        this.saveResource("recipe.yml",false);
        this.saveResource("config.yml",false);
        this.saveResource("lang/en.yml",false);
        this.saveResource("lang/zh_cn.yml",false);

        recipes=YamlFile.loadYamlFile(new File(getDataFolder(),"recipe.yml"));
        data=YamlFile.loadYamlFile(new File(getDataFolder(),"data.yml"));
        lang=YamlFile.loadYamlFile(new File(getDataFolder(),"lang/"+YamlFile.loadYamlFile(
                new File(getDataFolder(),"config.yml")).getString("lang")+".yml"));

        NBTItem nbtItem;
        if (version>=13)
        {
            nbtItem=new NBTItem(new ItemStack(Material.CRAFTING_TABLE));
        }
        else
        {
            nbtItem=new NBTItem(new ItemStack(Material.getMaterial("WORKBENCH")));
        }
        nbtItem.setBoolean("BluestarCraft.Table",true);
        ItemMeta meta=nbtItem.getItem().getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+lang.getString("crafting_table"));
        nbtItem.getItem().setItemMeta(meta);
        if (version>=15)
        {
            Bukkit.removeRecipe(new NamespacedKey(this,"crafttable"));
        }
        ShapedRecipe recipe;
        if (version>=12)
        {
            recipe=new ShapedRecipe(new NamespacedKey(this,"crafttable"),nbtItem.getItem());
        }
        else
        {
            recipe=new ShapedRecipe(nbtItem.getItem());
        }
        recipe.shape(" a ","aba"," a ");
        recipe.setIngredient('a',Material.DIAMOND);
        if (version>=13)
        {
            recipe.setIngredient('b',Material.CRAFTING_TABLE);
        }
        else
        {
            recipe.setIngredient('b',Material.getMaterial("WORKBENCH"));
        }
        Bukkit.addRecipe(recipe);

        List<?> list=data.getList("recipe");
        if (list==null)
        {
            list=new ArrayList<>();
        }
        for (Object o: list)
        {
            if (!(o instanceof Recipe))
            {
                continue;
            }
            bluestarCraftManager.addRecipe((Recipe) o);
        }
        loadRecipes();
        new Metrics(this);
        task=new checkUpdata(this).runTaskTimerAsynchronously(this,0,72000);
        info("BluestarCraft"+lang.getString("enable"));
    }

    @Override
    public void onDisable()
    {
        task.cancel();
        for (Inventory inventory: bluestarCraftManager.getInventories())
        {
            for (HumanEntity entity: inventory.getViewers())
            {
                entity.closeInventory();
                bluestarCraftManager.closeCraft(entity,inventory);
            }
        }
        for (Inventory inventory: bluestarCraftManager.getRegisters())
        {
            for (HumanEntity entity: inventory.getViewers())
            {
                entity.closeInventory();
            }
        }
        if (version>=15)
        {
            Bukkit.removeRecipe(new NamespacedKey(this,"crafttable"));
        }
        List<Recipe> recipes=new ArrayList<>();
        for (Recipe recipe: bluestarCraftManager.getRecipes())
        {
            if (recipe.needSave())
            {
                recipes.add(recipe);
            }
            Bukkit.removeRecipe(new NamespacedKey(this,recipe.getName()));
        }
        data.set("recipe",recipes);
        data.save();
        info("BluestarCraft"+lang.getString("disable"));
    }

    public BluestarCraftManager getBluestarCraftManager()
    {
        return bluestarCraftManager;
    }

    private void loadRecipes()
    {
        ConfigurationSection shaped=recipes.getConfigurationSection("shaped");
        ConfigurationSection shapeless=recipes.getConfigurationSection("shapeless");
        if (shaped!=null)
        {
            for (String key: shaped.getKeys(false))
            {
                me.lanzhi.bluestarcraft.api.recipe.ShapedRecipe recipe=new me.lanzhi.bluestarcraft.api.recipe.ShapedRecipe(
                        key,new ArrayList<>(Collections.singletonList(
                        new ItemStack(Material.matchMaterial(shaped.getString(key+".result")),
                                      shaped.getInt(key+".amount")
                        ))),shaped.getStringList(key+".shape").toArray(new String[0]));
                for (String s: shaped.getConfigurationSection(key+".ingredient").getKeys(false))
                {
                    recipe.setIngredient(s.charAt(0),Material.matchMaterial(shaped.getString(key+".ingredient."+s)));
                }
                bluestarCraftManager.addRecipe(recipe);
            }
        }
        if (shapeless!=null)
        {
            for (String key: shapeless.getKeys(false))
            {
                ShapelessRecipe recipe=new ShapelessRecipe(key,new ArrayList<>(Collections.singletonList(
                        new ItemStack(Material.matchMaterial(shapeless.getString(key+".result")),
                                      shapeless.getInt(key+".amount")))));
                for (String s: shapeless.getConfigurationSection(key+".ingredient").getKeys(false))
                {
                    recipe.addMaterial(Material.matchMaterial(s),shapeless.getInt(key+".ingredient."+s));
                }
                bluestarCraftManager.addRecipe(recipe);
            }
        }
    }

    public YamlFile getLang()
    {
        return lang;
    }

    public boolean isAir(ItemStack itemStack)
    {
        if (version>=14)
        {
            return itemStack.getType().isAir();
        }
        else
        {
            return itemStack.getType()==Material.AIR;
        }
    }

    public double getVersion()
    {
        return version;
    }

    public void info(String message)
    {
        Bukkit.getLogger().info(message);
    }
}

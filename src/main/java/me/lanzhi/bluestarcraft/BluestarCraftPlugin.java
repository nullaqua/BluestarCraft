package me.lanzhi.bluestarcraft;

import me.lanzhi.bluestarapi.config.AutoSerialize;
import me.lanzhi.bluestarapi.config.YamlFile;
import me.lanzhi.bluestarapi.nbt.NBTItem;
import me.lanzhi.bluestarcraft.api.BluestarCraft;
import me.lanzhi.bluestarcraft.api.recipe.Recipe;
import me.lanzhi.bluestarcraft.api.recipe.ShapelessRecipe;
import me.lanzhi.bluestarcraft.api.recipe.matcher.ExactMatcher;
import me.lanzhi.bluestarcraft.api.recipe.matcher.MaterialMatcher;
import me.lanzhi.bluestarcraft.commands.CraftCommand;
import me.lanzhi.bluestarcraft.commands.OpenBookCommand;
import me.lanzhi.bluestarcraft.commands.OpenCraftCommand;
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
import java.util.Objects;

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

        Objects.requireNonNull(getCommand("bluestarcraft")).setExecutor(new CraftCommand(this));
        Objects.requireNonNull(getCommand("opencraft")).setExecutor(new OpenCraftCommand(this));
        Objects.requireNonNull(getCommand("recipebook")).setExecutor(new OpenBookCommand(this));
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
        lang=YamlFile.loadYamlFile(new File(getDataFolder(),
                                            "lang/"+
                                            YamlFile.loadYamlFile(new File(getDataFolder(),"config.yml"))
                                                    .getString("lang")+
                                            ".yml"),true);

        NBTItem nbtItem;
        if (version>=13)
        {
            nbtItem=new NBTItem(new ItemStack(Material.CRAFTING_TABLE));
        }
        else
        {
            nbtItem=new NBTItem(new ItemStack(Objects.requireNonNull(Material.getMaterial("WORKBENCH"))));
        }
        nbtItem.setBoolean("BluestarCraft.Table",true);
        ItemMeta meta=nbtItem.getItem().getItemMeta();
        assert meta!=null;
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
            recipe.setIngredient('b',Objects.requireNonNull(Material.getMaterial("WORKBENCH")));
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

    private void loadRecipes()
    {
        ConfigurationSection shaped=recipes.getConfigurationSection("shaped");
        ConfigurationSection shapeless=recipes.getConfigurationSection("shapeless");
        if (shaped!=null)
        {
            for (String key: shaped.getKeys(false))
            {
                me.lanzhi.bluestarcraft.api.recipe.ShapedRecipe recipe=
                        new me.lanzhi.bluestarcraft.api.recipe.ShapedRecipe(
                        key,
                        getResult(shaped,key),
                        shaped.getStringList(key+".shape").toArray(new String[0]));
                ConfigurationSection ingredients=shaped.getConfigurationSection(key+".ingredients");
                assert ingredients!=null;
                for (String s: ingredients.getKeys(false))
                {
                    String ingredient=shaped.getString(key+".ingredients."+s);
                    assert ingredient!=null;
                    recipe.setIngredient(s.charAt(0),Material.matchMaterial(ingredient));
                }
                bluestarCraftManager.addRecipe(recipe);
            }
        }
        if (shapeless!=null)
        {
            for (String key: shapeless.getKeys(false))
            {
                ShapelessRecipe recipe=new ShapelessRecipe(key,getResult(shapeless,key));
                ConfigurationSection ingredients=shapeless.getConfigurationSection(key+".ingredients");
                assert ingredients!=null;
                for (String s: ingredients.getKeys(false))
                {
                    recipe.addMaterial(Material.matchMaterial(s),shapeless.getInt(key+".ingredients."+s));
                }
                bluestarCraftManager.addRecipe(recipe);
            }
        }
        for (var x: data.getStringList("book"))
        {
            System.out.println(x);
            bluestarCraftManager.addDisplayableRecipe(x);
        }
    }

    public BluestarCraftManager getBluestarCraftManager()
    {
        return bluestarCraftManager;
    }

    private static List<ItemStack> getResult(ConfigurationSection shapeless,String key)
    {
        var result=shapeless.getString(key+".result");
        assert result!=null;
        var resultItem=Material.matchMaterial(result);
        assert resultItem!=null;
        var tmp=new ItemStack(resultItem,shapeless.getInt(key+".amount"));
        var list=Collections.singletonList(tmp);
        return new ArrayList<>(list);
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
        data.set("book",bluestarCraftManager.getDisplayableRecipes());
        data.save();
        info("BluestarCraft"+lang.getString("disable"));
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

package me.lanzhi.bluestarcraft.managers;

import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import me.lanzhi.bluestarcraft.BluestarCraftPlugin;
import me.lanzhi.bluestarcraft.api.recipe.CraftInventory;
import me.lanzhi.bluestarcraft.api.recipe.Recipe;
import me.lanzhi.bluestarcraft.api.recipe.ShapedRecipe;
import me.lanzhi.bluestarcraft.api.recipe.ShapelessRecipe;
import me.lanzhi.bluestarcraft.api.recipe.matcher.ExactMatcher;
import me.lanzhi.bluestarcraft.api.recipe.matcher.ItemMatcher;
import me.lanzhi.bluestarcraft.api.recipe.matcher.MaterialMatcher;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class BluestarCraftManager
{
    public final List<Integer> empty=Arrays.asList(5,6,7,8,14,15,16,17,23,26,32,33,34,35,41,42,43,44);
    public final List<Integer> craftItem=Arrays.asList(0,1,2,3,4,9,10,11,12,13,18,19,20,21,22,27,28,29,30,31,36,37,38,
                                                       39,40
                                                      );
    public final ItemStack Close;
    public final ItemStack Empty;
    public final ItemStack EmptyAns;
    public final ItemStack Register;
    private final List<Inventory> inventories=new ArrayList<>();
    private final Map<Inventory, RecipeData> registers=new HashMap<>();
    private final Map<String, Recipe> recipes=new HashMap<>();
    private final BluestarCraftPlugin plugin;

    public BluestarCraftManager(BluestarCraftPlugin plugin)
    {
        this.plugin=plugin;
        ItemMeta itemMeta;
        if (plugin.getVersion()>=13)
        {
            Close=new ItemStack(Material.RED_STAINED_GLASS_PANE);
            Empty=new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            Register=new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        }
        else
        {
            Close=new ItemStack(Material.matchMaterial("STAINED_GLASS_PANE"));
            Close.setDurability((short) 14);
            Empty=new ItemStack(Material.matchMaterial("STAINED_GLASS_PANE"));
            Empty.setDurability((short) 15);
            Register=new ItemStack(Material.matchMaterial("STAINED_GLASS_PANE"));
            Register.setDurability((short) 13);
        }
        itemMeta=Close.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED+"点击关闭");
        Close.setItemMeta(itemMeta);
        itemMeta=Empty.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY+"|-高级工作台-|");
        Empty.setItemMeta(itemMeta);
        EmptyAns=new ItemStack(Material.BARRIER);
        itemMeta=EmptyAns.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED+"错误配方");
        EmptyAns.setItemMeta(itemMeta);
        itemMeta=Register.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN+"点击注册合成表");
        Register.setItemMeta(itemMeta);
        new Updata().runTaskTimer(plugin,0,1);
    }

    public void addInventory(Inventory inventory)
    {
        inventories.add(inventory);
    }

    public boolean containsInventory(Inventory inventory)
    {
        return inventories.contains(inventory);
    }

    public boolean removeInventory(Inventory inventory)
    {
        return inventories.remove(inventory);
    }

    public void register(Inventory in)
    {
        RecipeData data=registers.remove(in);
        if (data==null)
        {
            return;
        }
        CraftInventory inventory=new CraftInventory(in);
        ItemStack item=in.getItem(24);
        if (item==null||plugin.isAir(item))
        {
            return;
        }
        if (data.shape)
        {
            List<String> list=new ArrayList<>();
            Map<Character, ItemMatcher> map=new HashMap<>();
            for (int i=0;i<5;i++)
            {
                StringBuilder stringBuilder=new StringBuilder();
                for (int j=0;j<5;j++)
                {
                    ItemStack itemStack=inventory.getItem(i,j);
                    if (plugin.isAir(itemStack))
                    {
                        stringBuilder.append(' ');
                    }
                    else
                    {
                        stringBuilder.append((char) (i*5+j+'a'));
                        if (data.exact)
                        {
                            map.put((char) (i*5+j+'a'),new ExactMatcher(itemStack));
                        }
                        else
                        {
                            map.put((char) (i*5+j+'a'),new MaterialMatcher(itemStack.getType()));
                        }
                    }
                }
                list.add(stringBuilder.toString());
            }
            ShapedRecipe recipe=new ShapedRecipe(data.name,item,true,list.toArray(new String[0]));
            for (Map.Entry<Character, ItemMatcher> entry: map.entrySet())
            {
                recipe.setIngredient(entry.getKey(),entry.getValue());
            }
            addRecipe(recipe);
            return;
        }
        ShapelessRecipe recipe=new ShapelessRecipe(data.name,item,true);
        for (ItemStack itemStack: inventory.getItems())
        {
            if (itemStack==null||plugin.isAir(itemStack))
            {
                continue;
            }
            recipe.addMaterial(itemStack.getType(),1);
        }
        addRecipe(recipe);
    }

    public void addRegister(Inventory inventory,RecipeData data)
    {
        registers.put(inventory,data);
    }

    public boolean containsRegister(Inventory inventory)
    {
        return registers.containsKey(inventory);
    }

    public void removeRegister(Inventory inventory)
    {
        registers.remove(inventory);
    }

    public void addRecipe(@NotNull Recipe recipe)
    {
        System.out.println("注册合成表: "+recipe.getName());
        recipes.put(recipe.getName(),recipe);
    }

    public boolean containsRecipe(Recipe recipe)
    {
        return recipes.containsValue(recipe);
    }

    public boolean containsRecipe(String name)
    {
        return recipes.containsKey(name);
    }

    public boolean removeRecipe(@NotNull Recipe recipe)
    {
        System.out.println("移除合成表: "+recipe.getName());
        return recipes.values().remove(recipe);
    }

    public Recipe removeRecipe(String name)
    {
        System.out.println("移除合成表: "+name);
        return recipes.remove(name);
    }

    public void openCraftGui(@NotNull Player player)
    {
        player.openInventory(getCraftGui(player));
    }

    @NotNull
    private Inventory getCraftGui(Player player)
    {
        Inventory inventory=Bukkit.createInventory(player,5*9,""+plugin.getLang().getString("craft_gui_titel"));
        for (int i: plugin.getBluestarCraftManager().empty)
        {
            inventory.setItem(i,plugin.getBluestarCraftManager().Empty);
        }
        inventory.setItem(24,plugin.getBluestarCraftManager().EmptyAns);
        inventory.setItem(25,plugin.getBluestarCraftManager().Close);
        plugin.getBluestarCraftManager().addInventory(inventory);
        return inventory;
    }

    public void openRegisterGui(@NotNull Player player,@NotNull RecipeData data)
    {
        player.openInventory(getRegisterGui(player,data));
    }

    @NotNull
    private Inventory getRegisterGui(@NotNull Player player,@NotNull RecipeData data)
    {
        Inventory inventory=Bukkit.createInventory(player,5*9,""+plugin.getLang().getString("register_gui_titel"));
        for (int i: plugin.getBluestarCraftManager().empty)
        {
            inventory.setItem(i,plugin.getBluestarCraftManager().Empty);
        }
        inventory.setItem(33,plugin.getBluestarCraftManager().Register);
        inventory.setItem(25,plugin.getBluestarCraftManager().Close);
        plugin.getBluestarCraftManager().addRegister(inventory,data);
        return inventory;
    }

    public List<Inventory> getInventories()
    {
        return inventories;
    }


    public @NotNull List<Inventory> getRegisters()
    {
        return new ArrayList<>(registers.keySet());
    }

    public List<String> getRecipeNames()
    {
        return new ArrayList<>(recipes.keySet());
    }

    public List<Recipe> getRecipes()
    {
        return new ArrayList<>(recipes.values());
    }

    public boolean isBluestarCraftTable(ItemStack itemStack)
    {
        if (itemStack==null||plugin.isAir(itemStack))
        {
            return false;
        }
        return new NBTItem(itemStack).getBoolean("BluestarCraft.Table");
    }

    public void give(Entity owner,ItemStack item)
    {
        if (item==null||plugin.isAir(item)||item.getAmount()==0)
        {
            return;
        }
        int cnt=item.getAmount();
        Location location=owner.getLocation();
        World world=owner.getLocation().getWorld();
        if (world==null)
        {
            return;
        }
        while (cnt>0)
        {
            item.setAmount(Math.min(cnt,item.getMaxStackSize()));
            NBTEntity entity=new NBTEntity(world.dropItem(location,item));
            if (plugin.getVersion()>=16)
            {
                entity.setUUID("Owner",owner.getUniqueId());
            }
            else
            {
                entity.setString("Owner",owner.getName());
            }
            entity.setInteger("PickupDelay",0);
            cnt-=item.getAmount();
        }
    }

    public void closeCraft(HumanEntity player,Inventory inventory)
    {
        for (int i: craftItem)
        {
            ItemStack itemStack=inventory.getItem(i);
            if (itemStack==null||plugin.isAir(itemStack))
            {
                continue;
            }
            this.give(player,itemStack);
        }
    }

    @Nullable
    public ItemStack make(Inventory inventory,boolean max)
    {
        for (Recipe recipe: recipes.values())
        {
            ItemStack itemStack=recipe.getResult(new CraftInventory(inventory));
            if (itemStack!=null)
            {
                itemStack=itemStack.clone();
            }
            else
            {
                continue;
            }
            if (!plugin.isAir(itemStack))
            {
                if (max)
                {
                    int cnt=64;
                    for (int i: craftItem)
                    {
                        ItemStack item=inventory.getItem(i);
                        if (item!=null&&!plugin.isAir(item))
                        {
                            cnt=Math.min(cnt,item.getAmount());
                        }
                    }
                    for (int i: craftItem)
                    {
                        ItemStack itemStack1=inventory.getItem(i);
                        if (itemStack1!=null&&!plugin.isAir(itemStack1))
                        {
                            itemStack1.setAmount(itemStack1.getAmount()-cnt);
                        }
                        inventory.setItem(i,itemStack1);
                    }
                    itemStack.setAmount(itemStack.getAmount()*cnt);
                    return itemStack;
                }
                else
                {
                    for (int i: craftItem)
                    {
                        ItemStack itemStack1=inventory.getItem(i);
                        if (itemStack1!=null&&!plugin.isAir(itemStack1))
                        {
                            itemStack1.setAmount(itemStack1.getAmount()-1);
                        }
                        inventory.setItem(i,itemStack1);
                    }
                    return itemStack;
                }
            }
        }
        return null;
    }

    public void updata(Inventory inventory)
    {
        ItemStack itemStack=null;
        for (Recipe recipe: recipes.values())
        {
            itemStack=recipe.getResult(new CraftInventory(inventory));
            if (itemStack!=null&&!plugin.isAir(itemStack))
            {
                break;
            }
        }
        if (itemStack!=null&&!plugin.isAir(itemStack))
        {
            NBTItem item=new NBTItem(itemStack);
            if (plugin.getVersion()>=16)
            {
                item.setUUID("BluestarCraft.block_replication",UUID.randomUUID());
            }
            else
            {
                item.setString("BluestarCraft.block_replication",UUID.randomUUID().toString());
            }
            inventory.setItem(24,item.getItem());
        }
        else
        {
            inventory.setItem(24,EmptyAns);
        }
    }

    public static class RecipeData
    {
        final String name;
        final boolean shape;
        final boolean exact;

        public RecipeData(String name,boolean shape,boolean exact)
        {
            this.name=name;
            this.shape=shape;
            this.exact=exact;
        }
    }

    private class Updata extends BukkitRunnable
    {
        @Override
        public void run()
        {
            for (Inventory inventory: inventories)
            {
                updata(inventory);
            }
        }
    }
}

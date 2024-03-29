package me.lanzhi.bluestarcraft.managers;

import me.lanzhi.bluestarapi.nbt.NBTEntity;
import me.lanzhi.bluestarapi.nbt.NBTItem;
import me.lanzhi.bluestarapi.player.gui.ChestGui;
import me.lanzhi.bluestarapi.player.gui.GuiItem;
import me.lanzhi.bluestarcraft.BluestarCraftPlugin;
import me.lanzhi.bluestarcraft.api.recipe.*;
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
    public final List<Integer> empty=Arrays.asList(5,7,8,14,16,17,23,32,34,35,41,43,44);
    public final List<Integer> ansItems=Arrays.asList(6,15,24,33,42);
    public final List<Integer> craftItem=CraftInventory.craftItem();
    public final Integer Yes=25;
    public final Integer No=26;
    public final ItemStack Close;
    public final ItemStack Empty;
    public final ItemStack EmptyAns;
    public final ItemStack Register;
    public final ItemStack GetAns;
    public final ItemStack LastPage;
    public final ItemStack NextPage;
    private final List<Inventory> inventories=new ArrayList<>();
    private final Map<Inventory,RecipeData> registers=new HashMap<>();
    private final Map<String,Recipe> recipes=new HashMap<>();
    private final List<String> displayableRecipes=new ArrayList<>();
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
            GetAns=new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            LastPage=new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
            NextPage=new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        }
        else
        {
            Close=new ItemStack(Objects.requireNonNull(Material.matchMaterial("STAINED_GLASS_PANE")));
            Close.setDurability((short) 14);
            Empty=new ItemStack(Objects.requireNonNull(Material.matchMaterial("STAINED_GLASS_PANE")));
            Empty.setDurability((short) 15);
            Register=new ItemStack(Objects.requireNonNull(Material.matchMaterial("STAINED_GLASS_PANE")));
            Register.setDurability((short) 13);
            GetAns=new ItemStack(Objects.requireNonNull(Material.matchMaterial("STAINED_GLASS_PANE")));
            GetAns.setDurability((short) 13);
            LastPage=new ItemStack(Objects.requireNonNull(Material.matchMaterial("STAINED_GLASS_PANE")));
            LastPage.setDurability((short) 3);
            NextPage=new ItemStack(Objects.requireNonNull(Material.matchMaterial("STAINED_GLASS_PANE")));
            NextPage.setDurability((short) 3);
        }
        itemMeta=Close.getItemMeta();
        assert itemMeta!=null;
        itemMeta.setDisplayName(ChatColor.RED+"点击关闭");
        Close.setItemMeta(itemMeta);
        itemMeta=Empty.getItemMeta();
        assert itemMeta!=null;
        itemMeta.setDisplayName(ChatColor.GRAY+"|-高级工作台-|");
        Empty.setItemMeta(itemMeta);
        EmptyAns=new ItemStack(Material.BARRIER);
        itemMeta=EmptyAns.getItemMeta();
        assert itemMeta!=null;
        itemMeta.setDisplayName(ChatColor.RED+"错误配方");
        EmptyAns.setItemMeta(itemMeta);
        itemMeta=Register.getItemMeta();
        assert itemMeta!=null;
        itemMeta.setDisplayName(ChatColor.GREEN+"点击注册合成表");
        Register.setItemMeta(itemMeta);
        itemMeta=GetAns.getItemMeta();
        assert itemMeta!=null;
        itemMeta.setDisplayName(ChatColor.GREEN+"合成");
        GetAns.setItemMeta(itemMeta);
        itemMeta=LastPage.getItemMeta();
        assert itemMeta!=null;
        itemMeta.setDisplayName(ChatColor.AQUA+"上一页");
        LastPage.setItemMeta(itemMeta);
        itemMeta=NextPage.getItemMeta();
        assert itemMeta!=null;
        itemMeta.setDisplayName(ChatColor.AQUA+"下一页");
        NextPage.setItemMeta(itemMeta);
        new Update().runTaskTimer(plugin,0,1);
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
        List<ItemStack> itemStacks=new ArrayList<>();
        boolean flag=false;
        for (Integer integer: ansItems)
        {
            itemStacks.add(in.getItem(integer));
            if (in.getItem(integer)!=null&&!plugin.isAir(in.getItem(integer)))
            {
                flag=true;
            }
        }
        if (!flag)
        {
            return;
        }
        if (data.shape)
        {
            List<String> list=new ArrayList<>();
            Map<Character,ItemMatcher> map=new HashMap<>();
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
            ShapedRecipe recipe=new ShapedRecipe(data.name,itemStacks,true,list.toArray(new String[0]));
            for (Map.Entry<Character,ItemMatcher> entry: map.entrySet())
            {
                recipe.setIngredient(entry.getKey(),entry.getValue());
            }
            addRecipe(recipe);
            return;
        }
        ShapelessRecipe recipe=new ShapelessRecipe(data.name,itemStacks,true);
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

    public Recipe getRecipe(String name)
    {
        return recipes.get(name);
    }

    public List<String> getDisplayableRecipes()
    {
        return new ArrayList<>(displayableRecipes);
    }

    public DisplayableRecipe getDisplayableRecipe(String name)
    {
        if (!displayableRecipes.contains(name))
        {
            return null;
        }
        var x=recipes.get(name);
        if (x instanceof DisplayableRecipe)
        {
            return (DisplayableRecipe) x;
        }
        return null;
    }

    public void removeDisplayableRecipe(String name)
    {
        displayableRecipes.remove(name);
    }

    public void addRecipe(@NotNull Recipe recipe)
    {
        plugin.info("注册合成表: "+recipe.getName());
        recipes.put(recipe.getName(),recipe);
    }

    public void addDisplayableRecipe(@NotNull String recipe)
    {
        if (!displayableRecipes.contains(recipe))
        {
            displayableRecipes.add(recipe);
        }
    }

    public void addDisplayableRecipe(@NotNull DisplayableRecipe recipe)
    {
        displayableRecipes.add(recipe.getName());
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
        plugin.info("移除合成表: "+recipe.getName());
        displayableRecipes.remove(recipe.getName());
        return recipes.values().remove(recipe);
    }

    public Recipe removeRecipe(String name)
    {
        plugin.info("移除合成表: "+name);
        displayableRecipes.remove(name);
        return recipes.remove(name);
    }

    public void openCraftGui(@NotNull Player player)
    {
        player.openInventory(getCraftGui(player));
    }

    public void openRecipesGui(@NotNull Player player)
    {
        if (displayableRecipes.size()==0)
        {
            player.sendMessage(ChatColor.RED+"没有可用的合成表");
            return;
        }
        new RecipesBook(player).open();
    }

    @NotNull
    private Inventory getCraftGui(Player player)
    {
        Inventory inventory=Bukkit.createInventory(player,5*9,""+plugin.getLang().getString("craft_gui_title"));
        for (int i: plugin.getBluestarCraftManager().empty)
        {
            inventory.setItem(i,plugin.getBluestarCraftManager().Empty);
        }
        for (int i: plugin.getBluestarCraftManager().ansItems)
        {
            inventory.setItem(i,plugin.getBluestarCraftManager().EmptyAns);
        }
        inventory.setItem(No,plugin.getBluestarCraftManager().Close);
        inventory.setItem(plugin.getBluestarCraftManager().Yes,plugin.getBluestarCraftManager().GetAns);
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
        inventory.setItem(plugin.getBluestarCraftManager().Yes,plugin.getBluestarCraftManager().Register);
        inventory.setItem(No,plugin.getBluestarCraftManager().Close);
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
    public List<ItemStack> make(Inventory inventory,boolean max)
    {
        for (Recipe recipe: recipes.values())
        {
            List<ItemStack> itemStack_=recipe.getResult(inventory.getViewers().get(0),new CraftInventory(inventory));
            List<ItemStack> itemStacks=new ArrayList<>();
            if (itemStack_!=null&&!itemStack_.isEmpty())
            {
                for (ItemStack itemStack: itemStack_)
                {
                    itemStack=itemStack==null?new ItemStack(Material.AIR):itemStack;
                    itemStacks.add(itemStack.clone());
                }
            }
            else
            {
                continue;
            }
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
                for (ItemStack itemStack: itemStacks)
                {
                    if (itemStack!=null&&!plugin.isAir(itemStack))
                    {
                        itemStack.setAmount(itemStack.getAmount()*cnt);
                    }
                }
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
            }
            return itemStacks;
        }
        return null;
    }

    public void updata(Inventory inventory)
    {
        List<ItemStack> itemStack_=null;
        for (Recipe recipe: recipes.values())
        {
            itemStack_=recipe.getResult(inventory.getViewers().get(0),new CraftInventory(inventory));
            if (itemStack_!=null&&!itemStack_.isEmpty())
            {
                break;
            }
        }
        if (itemStack_!=null&&!itemStack_.isEmpty())
        {
            List<ItemStack> itemStacks=new ArrayList<>();
            for (ItemStack itemStack: itemStack_)
            {
                if (itemStack==null||plugin.isAir(itemStack))
                {
                    itemStacks.add(new ItemStack(Material.AIR));
                    continue;
                }
                NBTItem item=new NBTItem(itemStack.clone());
                if (plugin.getVersion()>=16)
                {
                    item.setUUID("BluestarCraft.block_replication",UUID.randomUUID());
                }
                else
                {
                    item.setString("BluestarCraft.block_replication",UUID.randomUUID().toString());
                }
                itemStacks.add(item.getItem());
            }
            for (int i=0;i<5;i++)
            {
                if (!plugin.isAir(itemStacks.get(i)))
                {
                    inventory.setItem(ansItems.get(i),itemStacks.get(i));
                }
                else
                {
                    inventory.setItem(ansItems.get(i),EmptyAns);
                }
            }
        }
        else
        {
            for (int i: ansItems)
            {
                inventory.setItem(i,EmptyAns);
            }
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

    private class RecipesBook
    {
        private final Player player;
        private final ChestGui gui;
        private List<DisplayableRecipe.DisplayInfo> list=Collections.emptyList();
        private int x=-1, y=0;
        private RecipesBook(Player player)
        {
            this.player=player;

            gui=ChestGui.builder(plugin)
                    .title(plugin.getLang().getString("recipes_book_gui_title"))
                    .emptyItem(new GuiItem().setItem(Empty))
                    .setSize(6)
                    .prohibitAnyClick(true)
                    .make(player);
            nextPage();
            setPage();
        }

        public void open()
        {
            gui.open();
        }

        public void nextPage()
        {
            if (y+1>=list.size())
            {
                if (x+1<displayableRecipes.size())
                {
                    x++;
                    y=0;
                    list=getDisplayableRecipe(displayableRecipes.get(x)).displays(player);
                }
            }
            else
            {
                y++;
            }
        }

        public void lastPage()
        {
            if (y<1)
            {
                if (x>0)
                {
                    x--;
                    list=getDisplayableRecipe(displayableRecipes.get(x)).displays(player);
                    y=list.size()-1;
                }
            }
            else
            {
                y--;
            }
        }

        public void setPage()
        {
            gui.clearItems();
            DisplayableRecipe.DisplayInfo info=list.get(y);
            int i1=0;
            for (var i: craftItem)
            {
                int x=i/9, y=i%9;
                gui.setItem(x,y,new GuiItem().setItem(info.inventory().getItem(i1++)));
            }
            i1=0;
            for (var i: ansItems)
            {
                int x=i/9, y=i%9;
                gui.setItem(x,y,new GuiItem().setItem(info.result().get(i1++)));
            }
            gui.setItem(5,4,new GuiItem().setItem(Close).setOnClick((gui1,clickType)->GuiItem.Response.close()));
            gui.setItem(5,3,new GuiItem().setItem(LastPage).setOnClick((gui1,clickType)->
                                                                       {
                                                                           lastPage();
                                                                           setPage();
                                                                           return GuiItem.Response.nothing();
                                                                       }));
            gui.setItem(5,5,new GuiItem().setItem(NextPage).setOnClick((gui1,clickType)->
                                                                       {
                                                                           nextPage();
                                                                           setPage();
                                                                           return GuiItem.Response.nothing();
                                                                       }));
            gui.paint();
        }
    }

    private class Update extends BukkitRunnable
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

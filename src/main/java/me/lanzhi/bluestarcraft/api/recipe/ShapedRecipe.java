package me.lanzhi.bluestarcraft.api.recipe;

import me.lanzhi.bluestarapi.Api.config.AutoSerializeInterface;
import me.lanzhi.bluestarapi.Api.config.SerializeAs;
import me.lanzhi.bluestarapi.Api.config.SpecialSerialize;
import me.lanzhi.bluestarcraft.api.recipe.matcher.ItemMatcher;
import me.lanzhi.bluestarcraft.api.recipe.matcher.MaterialMatcher;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SerializeAs("BluestarCraft.ShapedRecipe")
public final class ShapedRecipe implements Recipe, AutoSerializeInterface
{
    private final String name;
    private ItemStack result;
    private List<String> lines;
    @SpecialSerialize
    private final boolean save;
    @SpecialSerialize(serialize="serialize", deserialize="deserialize")
    private Map<Character, ItemMatcher> ingredient=new HashMap<>();

    public ShapedRecipe()
    {
        result=null;
        lines=null;
        name=null;
        save=true;
    }

    public ShapedRecipe(String name,ItemStack result,String... lines)
    {
        this(name,result,false,lines);
    }

    public ShapedRecipe(String name,ItemStack result,boolean needSave,String... lines)
    {
        this.save=needSave;
        this.name=name;
        this.result=result;
        this.lines=new ArrayList<>(Arrays.asList(lines));
        int s=0;
        while (s<this.lines.size()&&this.lines.get(s).replace(" ","").isEmpty())
        {
            this.lines.remove(s);
        }
        s=this.lines.size()-1;
        while (s>=0&&this.lines.get(s).replace(" ","").isEmpty())
        {
            this.lines.remove(s);
            s--;
        }
        while (this.lines.size()<5)
        {
            this.lines.add("     ");
        }
        while (true)
        {
            boolean flag=true;
            for (String string: this.lines)
            {
                if (!(string.startsWith(" ")||string.isEmpty()))
                {
                    flag=false;
                    break;
                }
            }
            if (!flag)
            {
                break;
            }
            for (int i=0;i<this.lines.size();i++)
            {
                if (!this.lines.get(i).isEmpty())
                {
                    this.lines.add(i,this.lines.remove(i).substring(1));
                }
            }
        }
        for (int i=0;i<this.lines.size();i++)
        {
            while (this.lines.get(i).length()<5)
            {
                this.lines.add(i,this.lines.remove(i)+" ");
            }
        }
        setIngredient(' ',MaterialMatcher.EMPTY_MATCHER);
    }

    public static Map<String, ItemMatcher> serialize(Map<Character, ItemMatcher> map)
    {
        Map<String, ItemMatcher> map1=new HashMap<>();
        for (Map.Entry<Character, ItemMatcher> entry: map.entrySet())
        {
            map1.put(entry.getKey()+"",entry.getValue());
        }
        map1.remove(" ");
        return map1;
    }

    public static Map<Character, ItemMatcher> deserialize(Map<String, ItemMatcher> map)
    {
        Map<Character, ItemMatcher> map1=new HashMap<>();
        for (Map.Entry<String, ItemMatcher> entry: map.entrySet())
        {
            map1.put(entry.getKey().charAt(0),entry.getValue());
        }
        map1.put(' ',MaterialMatcher.EMPTY_MATCHER);
        return map1;
    }

    public void setIngredient(char s,Material material)
    {
        setIngredient(s,new MaterialMatcher(material));
    }

    public void setIngredient(char s,ItemMatcher itemMatcher)
    {
        ingredient.put(s,itemMatcher);
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean needSave()
    {
        return save;
    }

    @Override
    public boolean match(CraftInventory inventory)
    {
        List<ItemStack> items=new ArrayList<>(Arrays.asList(inventory.getItems().clone()));
        for (int k=0;k<5&&!items.isEmpty();k++)
        {
            boolean flag=true;
            for (int i=0;i<5;i++)
            {
                if (!MaterialMatcher.EMPTY_MATCHER.match(items.get(i)))
                {
                    flag=false;
                    break;
                }
            }
            if (!flag)
            {
                break;
            }
            items.subList(0,5).clear();
            for (int i=0;i<5;i++)
            {
                items.add(null);
            }
        }
        for (int k=0;k<5;k++)
        {
            boolean flag=true;
            for (int i=0;i<items.size();i+=5)
            {
                if (!MaterialMatcher.EMPTY_MATCHER.match(items.get(i)))
                {
                    flag=false;
                    break;
                }
            }
            if (!flag)
            {
                break;
            }
            for (int i=0;i<items.size();i+=5)
            {
                items.remove(i);
                items.add(i+4,new ItemStack(Material.AIR));
            }
        }
        boolean flag=true;
        for (int i=0;i<5;i++)
        {
            for (int j=0;j<5;j++)
            {
                flag=flag&&ingredient.get(this.lines.get(i).toCharArray()[j]).match(items.get(i*5+j));
            }
        }
        return flag;
    }

    @Override
    public ItemStack getResult()
    {
        return result.clone();
    }

    @Override
    public ShapedRecipe clone()
    {
        ShapedRecipe clone;
        try
        {
            clone=(ShapedRecipe) super.clone();
            clone.result=this.result.clone();
            clone.lines=new ArrayList<>(this.lines);
            clone.ingredient=new HashMap<>(this.ingredient);
            return clone;
        }
        catch (CloneNotSupportedException e)
        {
            return null;
        }
    }
}

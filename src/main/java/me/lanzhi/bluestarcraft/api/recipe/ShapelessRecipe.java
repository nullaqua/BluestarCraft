package me.lanzhi.bluestarcraft.api.recipe;

import me.lanzhi.bluestarapi.Api.config.AutoSerializeInterface;
import me.lanzhi.bluestarapi.Api.config.SerializeAs;
import me.lanzhi.bluestarapi.Api.config.SpecialSerialize;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SerializeAs("BluestarCraft.ShapelessRecipe")
public final class ShapelessRecipe implements Recipe, AutoSerializeInterface
{
    private final String name;
    @SpecialSerialize
    private final boolean save;
    @SpecialSerialize(serialize="serialize", deserialize="deserialize")
    private Map<Material, Integer> map=new HashMap<>();
    private ItemStack result;

    public ShapelessRecipe()
    {
        result=null;
        name=null;
        save=true;
    }

    public ShapelessRecipe(String name,ItemStack result,boolean needSave)
    {
        this.save=needSave;
        this.name=name;
        this.result=result;
    }

    public ShapelessRecipe(String name,ItemStack result)
    {
        this.save=false;
        this.name=name;
        this.result=result;
    }

    public static Map<String, Integer> serialize(Map<Material, Integer> map)
    {
        Map<String, Integer> map1=new HashMap<>();
        for (Map.Entry<Material, Integer> entry: map.entrySet())
        {
            map1.put(entry.getKey().name(),entry.getValue());
        }
        return map1;
    }

    public static Map<Material, Integer> deserialize(Map<String, Integer> map)
    {
        Map<Material, Integer> map1=new HashMap<>();
        for (Map.Entry<String, Integer> entry: map.entrySet())
        {
            map1.put(Material.getMaterial(entry.getKey()),entry.getValue());
        }
        return map1;
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

    public void setMaterial(Material material,int cnt)
    {
        map.put(material,cnt);
    }

    public void addMaterial(Material material,int cnt)
    {
        Integer x=map.get(material);
        if (x!=null)
        {
            x+=cnt;
            map.put(material,x);
        }
        else
        {
            map.put(material,cnt);
        }
    }

    public void removeMaterial(Material material,int cnt)
    {
        addMaterial(material,-cnt);
    }

    @Override
    public boolean match(CraftInventory inventory)
    {
        Map<Material, Integer> map1=new HashMap<>();
        for (ItemStack itemStack: inventory.getItems())
        {
            if (itemStack==null||itemStack.getType().isAir())
            {
                continue;
            }
            Integer cnt=map1.get(itemStack.getType());
            if (cnt!=null)
            {
                cnt++;
            }
            else
            {
                cnt=1;
            }
            map1.put(itemStack.getType(),cnt);
        }
        for (Material material: map.keySet())
        {
            if (Objects.equals(map1.get(material),map.get(material)))
            {
                map1.remove(material);
            }
            else
            {
                return false;
            }
        }
        return map1.isEmpty();
    }

    @Override
    public ItemStack getResult()
    {
        return result;
    }

    @Override
    public ShapelessRecipe clone()
    {
        ShapelessRecipe clone;
        try
        {
            clone=(ShapelessRecipe) super.clone();
            clone.result=this.result.clone();
            clone.map=new HashMap<>(map);
            return clone;
        }
        catch (CloneNotSupportedException e)
        {
            return null;
        }
    }
}

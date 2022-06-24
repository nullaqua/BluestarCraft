package me.lanzhi.bluestarcraft.api.recipe;

import me.lanzhi.bluestarapi.Api.config.AutoSerializeInterface;
import me.lanzhi.bluestarapi.Api.config.SerializeAs;
import me.lanzhi.bluestarapi.Api.config.SpecialSerialize;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SerializeAs("BluestarCraft.ShapelessRecipe")
public final class ShapelessRecipe implements Recipe, AutoSerializeInterface
{
    private final String name;
    @SpecialSerialize
    private final boolean save;
    @SpecialSerialize(serialize="serialize", deserialize="deserialize")
    private Map<Material, Integer> map=new HashMap<>();
    private ItemStack result, result1, result2, result3, result4;

    public ShapelessRecipe()
    {
        result=null;
        name=null;
        save=true;
    }

    public ShapelessRecipe(String name,List<ItemStack> result,boolean needSave)
    {
        this.save=needSave;
        this.name=name;
        switch (result.size())
        {
            case 5:
            {
                result4=result.get(4);
            }
            case 4:
            {
                result3=result.get(3);
            }
            case 3:
            {
                result2=result.get(2);
            }
            case 2:
            {
                result1=result.get(1);
            }
            case 1:
            {
                this.result=result.get(0);
            }
            default:
            {
            }
        }
    }

    public ShapelessRecipe(String name,List<ItemStack> result)
    {
        this(name,result,false);
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
            if (itemStack==null||itemStack.getType()==Material.AIR)
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
    public List<ItemStack> getResult()
    {
        return new ArrayList<>(Arrays.asList(result,result1,result2,result3,result4));
    }

    @Override
    public ShapelessRecipe clone()
    {
        ShapelessRecipe clone;
        try
        {
            clone=(ShapelessRecipe) super.clone();
            clone.result=result;
            clone.result1=result1;
            clone.result2=result2;
            clone.result3=result3;
            clone.result4=result4;
            clone.map=new HashMap<>(map);
            return clone;
        }
        catch (CloneNotSupportedException e)
        {
            return null;
        }
    }
}

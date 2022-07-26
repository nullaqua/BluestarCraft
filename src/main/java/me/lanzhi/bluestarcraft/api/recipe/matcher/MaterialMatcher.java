package me.lanzhi.bluestarcraft.api.recipe.matcher;

import me.lanzhi.bluestarapi.api.config.AutoSerializeInterface;
import me.lanzhi.bluestarapi.api.config.SerializeAs;
import me.lanzhi.bluestarapi.api.config.SpecialSerialize;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SerializeAs("BluestarCraft.MaterialMatcher")
public final class MaterialMatcher implements ItemMatcher, AutoSerializeInterface
{
    @SpecialSerialize(serialize="serialize", deserialize="deserialize")
    private List<Material> materials;

    public MaterialMatcher()
    {
        materials=null;
    }

    public MaterialMatcher(Material... materials)
    {
        this.materials=Arrays.asList(materials.clone());
    }

    public MaterialMatcher(Collection<Material>materials)
    {
        this.materials=new ArrayList<>(materials);
    }

    public static List<String> serialize(List<Material> materials)
    {
        List<String> list=new ArrayList<>();
        for (Material material: materials)
        {
            list.add(material.name());
        }
        return list;
    }

    public static List<Material> deserialize(List<String> list)
    {
        List<Material> materials=new ArrayList<>();
        for (String s: list)
        {
            materials.add(Material.getMaterial(s));
        }
        return materials;
    }

    @Override
    public boolean match(ItemStack itemStack)
    {
        Material material=null;
        if (itemStack!=null)
        {
            material=itemStack.getType();
        }
        return materials.contains(material);
    }

    @Override
    public MaterialMatcher clone()
    {
        return new MaterialMatcher(materials.toArray(new Material[0]));
    }

    @Override
    public ItemStack get()
    {
        return new ItemStack(materials.get(0));
    }
}

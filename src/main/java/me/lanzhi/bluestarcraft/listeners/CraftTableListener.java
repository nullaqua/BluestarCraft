package me.lanzhi.bluestarcraft.listeners;

import me.lanzhi.bluestarcraft.BluestarCraftPlugin;
import me.lanzhi.bluestarcraft.api.BluestarCraft;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public final class CraftTableListener implements Listener
{
    private final BluestarCraftPlugin plugin;

    public CraftTableListener(BluestarCraftPlugin plugin)
    {
        this.plugin=plugin;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onOpenCraft(InventoryOpenEvent event)
    {
        if (event.isCancelled()||event.getInventory().getType()!=InventoryType.WORKBENCH||!(event.getPlayer() instanceof Player))
        {
            return;
        }
        ItemStack itemStack;
        if (plugin.getVersion()>=9)
        {
            itemStack=event.getPlayer().getInventory().getItemInMainHand();
            if (plugin.isAir(itemStack))
            {
                itemStack=event.getPlayer().getInventory().getItemInOffHand();
            }
        }
        else
        {
            itemStack=event.getPlayer().getInventory().getItemInHand();
        }
        if (plugin.getBluestarCraftManager().isBluestarCraftTable(itemStack))
        {
            event.setCancelled(true);
            plugin.getBluestarCraftManager().openCraftGui((Player) event.getPlayer());
        }
    }
    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerPlaceBlock(BlockPlaceEvent event)
    {
        if (plugin.getBluestarCraftManager().isBluestarCraftTable(event.getItemInHand()))
        {
            event.setCancelled(true);
        }
    }
}

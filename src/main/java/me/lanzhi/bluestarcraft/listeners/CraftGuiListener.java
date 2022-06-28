package me.lanzhi.bluestarcraft.listeners;

import me.lanzhi.bluestarcraft.BluestarCraftPlugin;
import me.lanzhi.bluestarcraft.managers.BluestarCraftManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class CraftGuiListener implements Listener
{
    private final BluestarCraftManager manager;
    private final BluestarCraftPlugin plugin;

    public CraftGuiListener(BluestarCraftPlugin plugin)
    {
        manager=plugin.getBluestarCraftManager();
        this.plugin=plugin;
    }

    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void onInventoryClick(InventoryClickEvent event)
    {
        if (!manager.containsInventory(event.getInventory()))
        {
            return;
        }
        if (manager.containsInventory(event.getClickedInventory()))
        {
            if (manager.empty.contains(event.getSlot())||manager.ansItems.contains(event.getSlot()))
            {
                event.setCancelled(true);
                return;
            }
            if (event.getSlot()==plugin.getBluestarCraftManager().No)
            {
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                return;
            }
            if (event.getSlot()==plugin.getBluestarCraftManager().Yes)
            {
                event.setCancelled(true);
                List<ItemStack>itemStacks=manager.make(event.getInventory(),event.isShiftClick());
                if (itemStacks==null)
                {
                    return;
                }
                for (ItemStack itemStack:itemStacks)
                {
                    manager.give(event.getWhoClicked(),itemStack);
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if (manager.removeInventory(event.getInventory()))
        {
            manager.closeCraft(event.getPlayer(),event.getInventory());
        }
    }
}

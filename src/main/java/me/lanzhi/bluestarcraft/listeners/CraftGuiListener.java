package me.lanzhi.bluestarcraft.listeners;

import me.lanzhi.bluestarcraft.BluestarCraftPlugin;
import me.lanzhi.bluestarcraft.managers.BluestarCraftManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public final class CraftGuiListener implements Listener
{
    private final BluestarCraftManager manager;
    private final BluestarCraftPlugin plugin;

    public CraftGuiListener(BluestarCraftPlugin plugin)
    {
        manager=plugin.getBluestarCraftManager();
        this.plugin=plugin;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event)
    {
        if (event.isCancelled())
        {
            return;
        }
        if (!manager.containsInventory(event.getInventory()))
        {
            return;
        }
        if (manager.containsInventory(event.getClickedInventory()))
        {
            ItemStack cursor=event.getCursor();
            ItemStack current=event.getCurrentItem();
            if (cursor!=null&&!plugin.isAir(cursor)&&event.getSlot()==24)
            {
                event.setCancelled(true);
                return;
            }
            if (current!=null&&!plugin.isAir(current))
            {
                if (manager.empty.contains(event.getSlot()))
                {
                    event.setCancelled(true);
                    return;
                }
                if (event.getSlot()==25)
                {
                    event.setCancelled(true);
                    event.getWhoClicked().closeInventory();
                    return;
                }
            }
            if (event.getSlot()==24)
            {
                if (event.isShiftClick())
                {
                    manager.give(event.getWhoClicked(),manager.make(event.getInventory(),true));
                    event.setCancelled(true);
                }
                else
                {
                    event.setCursor(manager.make(event.getInventory(),false));
                    event.setCancelled(true);
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

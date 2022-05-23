package me.lanzhi.bluestarcraft.listeners;

import me.lanzhi.bluestarcraft.BluestarCraftPlugin;
import me.lanzhi.bluestarcraft.managers.BluestarCraftManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class RegisterListener implements Listener
{

    private final BluestarCraftManager manager;
    private final BluestarCraftPlugin plugin;

    public RegisterListener(BluestarCraftPlugin plugin)
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
        if (!manager.containsRegister(event.getClickedInventory()))
        {
            return;
        }
        if (event.getSlot()==25)
        {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            return;
        }
        if (manager.empty.contains(event.getSlot()))
        {
            event.setCancelled(true);
        }
        if (event.getSlot()==33)
        {
            event.setCancelled(true);
            manager.register(event.getInventory());
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(""+plugin.getLang().getString("successfully_registered"));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event)
    {
        manager.removeRegister(event.getInventory());
    }
}

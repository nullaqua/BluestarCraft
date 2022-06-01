package me.lanzhi.bluestarcraft.commands;

import me.lanzhi.bluestarcraft.BluestarCraftPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class opencraftCommand implements CommandExecutor
{
    private BluestarCraftPlugin plugin;
    public opencraftCommand(BluestarCraftPlugin plugin)
    {
        this.plugin=plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,@NotNull String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED+plugin.getLang().getString("only_players_enter"));
            return true;
        }
        Player player=(Player) sender;
        plugin.getBluestarCraftManager().openCraftGui(player);
        return false;
    }
}

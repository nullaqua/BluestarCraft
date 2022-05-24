package me.lanzhi.bluestarcraft.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class test implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender,Command command,String label,String[] args)
    {
        Player player=(Player) sender;
        Location location=player.getLocation();
        location.setY(location.getY()-1);
        System.out.println(location.getBlock().getType().getId());
        System.out.println(location.getBlock().getType().name());
        System.out.println(location.getBlock().getType());
        return false;
    }
}

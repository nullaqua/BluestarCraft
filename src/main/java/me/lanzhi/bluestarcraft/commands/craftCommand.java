package me.lanzhi.bluestarcraft.commands;

import me.lanzhi.bluestarcraft.BluestarCraftPlugin;
import me.lanzhi.bluestarcraft.managers.BluestarCraftManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class craftCommand implements CommandExecutor, TabExecutor
{
    private final BluestarCraftManager manager;
    private final BluestarCraftPlugin plugin;

    public craftCommand(BluestarCraftPlugin plugin)
    {
        this.plugin=plugin;
        manager=plugin.getBluestarCraftManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,@NotNull String[] args)
    {
        if (!(sender instanceof Player)&&Arrays.asList("open","register").contains(args[0]))
        {
            sender.sendMessage("仅允许玩家输入");
            return true;
        }
        switch (args[0])
        {
            case "open" -> manager.openCraftGui((Player) sender);
            case "list" -> sender.sendMessage(manager.getRecipeNames().toArray(new String[0]));
            case "register" -> {
                if (args.length<3)
                {
                    sender.sendMessage("格式错误");
                    return true;
                }
                String name=args[1];
                boolean shape="shaped".equals(args[2]);
                boolean exact=shape&&"exact".equals(args[3]);
                plugin.getBluestarCraftManager().openRegisterGui((Player) sender,
                                                                 new BluestarCraftManager.RecipeData(name,shape,exact));
            }
            case "delete" ->
                    {
                        manager.removeRecipe(args[1]);
                        sender.sendMessage(ChatColor.GREEN+"已删除");
                    }
            default -> sender.sendMessage("格式错误");
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,@NotNull Command command,@NotNull String alias,@NotNull String[] args)
    {
        if (args.length==1)
        {
            return Arrays.asList("open","register","list","delete");
        }
        if ("register".equals(args[0]))
        {
            switch (args.length)
            {
                case 2 -> {
                    return Collections.singletonList("name");
                }
                case 3 -> {
                    return Arrays.asList("shaped","shapeless");
                }
                case 4 -> {
                    return Arrays.asList("exact","material");
                }
                default -> {
                    return Collections.emptyList();
                }
            }
        }
        if ("delete".equals(args[0]))
        {
            return manager.getRecipeNames();
        }
        return Collections.emptyList();
    }
}

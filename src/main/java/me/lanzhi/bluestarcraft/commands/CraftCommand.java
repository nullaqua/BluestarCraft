package me.lanzhi.bluestarcraft.commands;

import me.lanzhi.bluestarcraft.BluestarCraftPlugin;
import me.lanzhi.bluestarcraft.api.recipe.DisplayableRecipe;
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

public class CraftCommand implements CommandExecutor, TabExecutor
{
    private final BluestarCraftManager manager;
    private final BluestarCraftPlugin plugin;

    public CraftCommand(BluestarCraftPlugin plugin)
    {
        this.plugin=plugin;
        manager=plugin.getBluestarCraftManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,
                             @NotNull String[] args)
    {
        if (!(sender instanceof Player)&&Arrays.asList("open","register","book").contains(args[0]))
        {
            sender.sendMessage(ChatColor.RED+plugin.getLang().getString("only_players_enter"));
            return true;
        }
        switch (args[0])
        {
            case "open":
            {
                assert sender instanceof Player;
                manager.openCraftGui((Player) sender);
                return true;
            }
            case "list":
            {
                sender.sendMessage(manager.getRecipeNames().toArray(new String[0]));
                return true;
            }
            case "register":
            {
                if (args.length<4)
                {
                    sender.sendMessage(ChatColor.RED+plugin.getLang().getString("unknown_command"));
                    return true;
                }
                String name=args[1];
                boolean shape="shaped".equals(args[2]);
                boolean exact="exact".equals(args[3]);
                assert sender instanceof Player;
                plugin.getBluestarCraftManager()
                      .openRegisterGui((Player) sender,new BluestarCraftManager.RecipeData(name,shape,exact));
                return true;
            }
            case "book":
            {
                if (args.length<2)
                {
                    sender.sendMessage(ChatColor.RED+plugin.getLang().getString("unknown_command"));
                    return true;
                }
                switch (args[1])
                {
                    case "add":
                    {
                        if (args.length<3)
                        {
                            sender.sendMessage(ChatColor.RED+plugin.getLang().getString("unknown_command"));
                            return true;
                        }
                        String name=args[2];
                        var x=manager.getRecipe(name);
                        if (!(x instanceof DisplayableRecipe))
                        {
                            sender.sendMessage(ChatColor.RED+plugin.getLang().getString("unknown_displayable_recipe"));
                            return true;
                        }
                        manager.addDisplayableRecipe((DisplayableRecipe) x);
                        sender.sendMessage(ChatColor.GREEN+plugin.getLang().getString("successfully_registered"));
                        return true;
                    }
                    case "list":
                    {
                        sender.sendMessage(manager.getDisplayableRecipes().toArray(new String[0]));
                        return true;
                    }
                    case "remove":
                    {
                        if (args.length<3)
                        {
                            sender.sendMessage(ChatColor.RED+plugin.getLang().getString("unknown_command"));
                            return true;
                        }
                        String name=args[2];
                        manager.removeDisplayableRecipe(name);
                        sender.sendMessage(ChatColor.GREEN+plugin.getLang().getString("successfully_deleted"));
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.RED+plugin.getLang().getString("unknown_displayable_recipe"));
                return true;
            }
            case "delete":
            {
                if (args.length<2)
                {
                    sender.sendMessage(ChatColor.RED+plugin.getLang().getString("unknown_command"));
                    return true;
                }
                manager.removeRecipe(args[1]);
                sender.sendMessage(ChatColor.GREEN+plugin.getLang().getString("successfully_deleted"));
                return true;
            }
            default:
            {
                sender.sendMessage(ChatColor.RED+plugin.getLang().getString("unknown_command"));
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,@NotNull Command command,@NotNull String alias,
                                      @NotNull String[] args)
    {
        if (args.length==1)
        {
            return Arrays.asList("open","register","list","delete","book");
        }
        if ("register".equals(args[0]))
        {
            switch (args.length)
            {
                case 2:
                {
                    return Collections.singletonList("name");
                }
                case 3:
                {
                    return Arrays.asList("shaped","shapeless");
                }
                case 4:
                {
                    return Arrays.asList("exact","material");
                }
                default:
                {
                    return Collections.emptyList();
                }
            }
        }
        if ("delete".equals(args[0]))
        {
            return manager.getRecipeNames();
        }
        if ("book".equals(args[0]))
        {
            if (args.length==2)
            {
                return Arrays.asList("add","remove","list");
            }
            if ("add".equals(args[1]))
            {
                return manager.getRecipeNames();
            }
            if ("remove".equals(args[1]))
            {
                return manager.getDisplayableRecipes();
            }
        }
        return Collections.emptyList();
    }
}

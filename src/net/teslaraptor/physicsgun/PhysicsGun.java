package net.teslaraptor.physicsgun;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class PhysicsGun extends JavaPlugin {
    
    public static final ItemStack physicsGun;
    static {
        physicsGun = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = physicsGun.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Physics Gun");
        physicsGun.setItemMeta(meta);
    }
    
    public static boolean isPhysicsGun(ItemStack item) {
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return (item.getItemMeta().getDisplayName().equalsIgnoreCase(physicsGun.getItemMeta().getDisplayName()));
        }
        return false;
    }
    
    public static String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "PhysicsGun" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;
    public static HashMap<Player, SelectedBlock> selectedBlocks = new HashMap<>();
    public static int maxDistance = 20;
    
    private EventsListener listener;
    
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        try {
            PhysicsGun.maxDistance = (int)this.getConfig().get("physicsgun.maxDistance");
        } catch (Exception e) {
            Bukkit.getLogger().warning(ChatColor.stripColor(prefix) + "Invalid number provided for physicsgun.maxDistance. Using default value.");
        }
        
        listener = new EventsListener(this);
        Bukkit.getPluginManager().registerEvents(listener, this);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("physicsgun")) {
            if (args.length > 0) {
                String playerName = args[0];
                Player player = Bukkit.getPlayer(playerName);
                if (player != null) {
                    player.getInventory().addItem(PhysicsGun.physicsGun);
                    sender.sendMessage(PhysicsGun.prefix + "Added item to " + playerName + "'s inventory!");
                    return true;
                } else {
                    sender.sendMessage(PhysicsGun.prefix + "Player could not be found!");
                    return true;
                }
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    player.getInventory().addItem(PhysicsGun.physicsGun);
                    player.sendMessage(PhysicsGun.prefix + "Added physics gun to your inventory!");
                    return true;
                } else {
                    sender.sendMessage(PhysicsGun.prefix + "Console must provide username!");
                    return false;
                }
            }
        }
        
        return false;
    }
    
}
package net.teslaraptor.physicsgun;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventsListener implements Listener {

    private PhysicsGun plugin;

    public EventsListener(PhysicsGun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && PhysicsGun.isPhysicsGun(event.getItem()) && event.getPlayer().hasPermission("physicsgun.user")) {
            Player player = event.getPlayer();
            if (PhysicsGun.selectedBlocks.containsKey(player)) {
                SelectedBlock sBlock = PhysicsGun.selectedBlocks.get(player);
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    PhysicsGun.selectedBlocks.remove(player);
                    event.setCancelled(true);
                    return;
                } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    sBlock.getCurrentLocation().getBlock().setType(Material.AIR);
                    FallingBlock fallingSand = sBlock.getCurrentLocation().getWorld().spawnFallingBlock(sBlock.getCurrentLocation(), sBlock.getBlockType(), sBlock.getBlockData());
                    fallingSand.setVelocity(sBlock.getOwner().getEyeLocation().getDirection().multiply(2));
                    PhysicsGun.selectedBlocks.remove(player);
                    event.setCancelled(true);
                    return;
                }
            } else {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (event.getClickedBlock() != null) {
                        Block b = event.getClickedBlock();
                        FallingBlock fallingSand = b.getLocation().getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
                        fallingSand.setDropItem(false);
                        fallingSand.setVelocity(player.getEyeLocation().getDirection().multiply(2));
                        b.setType(Material.AIR);
                        event.setCancelled(true);
                        return;
                    }
                }
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    List<Block> blocks = player.getLineOfSight(null, PhysicsGun.maxDistance);
                    Block selectedBlock = null;
                    for (Block block : blocks) {
                        if (block.getType() != Material.AIR) {
                            boolean hasOwner = false;
                            for (SelectedBlock curSBlock : PhysicsGun.selectedBlocks.values()) {
                                if (block.getLocation().equals(curSBlock.getCurrentLocation())) {
                                    hasOwner = true;
                                }
                            }
                            if (!hasOwner) {
                                selectedBlock = block;
                                break;
                            }
                        }
                    }
                    if (selectedBlock != null) {
                        SelectedBlock sBlock = new SelectedBlock(player, selectedBlock.getLocation(), selectedBlock.getType(), selectedBlock.getData());
                        PhysicsGun.selectedBlocks.put(player, sBlock);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (PhysicsGun.selectedBlocks.containsKey(event.getPlayer())) {
            SelectedBlock sBlock = PhysicsGun.selectedBlocks.get(event.getPlayer());
            sBlock.updateBlock();
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            if (PhysicsGun.selectedBlocks.containsKey((Player) event.getWhoClicked())) {
                if (PhysicsGun.isPhysicsGun(event.getCurrentItem())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        if (PhysicsGun.selectedBlocks.containsKey(event.getPlayer())) {
            event.setCancelled(true);
            if (!(event.getNewSlot() == 1 || event.getNewSlot() == 2)) {
                return;
            }
            int difference = (event.getNewSlot() == 1) ? 1 : -1;
            SelectedBlock sBlock = PhysicsGun.selectedBlocks.get(event.getPlayer());
            double distance = sBlock.getBlockDistance();
            double newDistance = (distance - difference) * (distance - difference);
            sBlock.setBlockDistanceSq(newDistance);
            sBlock.updateBlock();
        }
    }

}

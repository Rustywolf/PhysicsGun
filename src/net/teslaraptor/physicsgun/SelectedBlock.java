package net.teslaraptor.physicsgun;

import java.util.HashSet;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SelectedBlock {
    
    private final Player owner;
    private Location currentLocation;
    private Material blockType;
    private byte blockData;
    private double blockDistanceSq;
    
    public SelectedBlock(Player owner, Location currentLocation, Material blockType, byte blockData) {
        this.owner = owner;
        this.currentLocation = currentLocation;
        this.blockType = blockType;
        this.blockData = blockData;
        this.blockDistanceSq = owner.getLocation().distanceSquared(currentLocation);
    }
    
    public Player getOwner() {
        return this.owner;
    }
    
    public Location getCurrentLocation() {
        return this.currentLocation;
    }
    
    public void moveBlock(Location newLoc) {
        this.currentLocation.getBlock().setType(Material.AIR);
        this.currentLocation = newLoc;
        this.currentLocation.getBlock().setType(blockType);
        this.currentLocation.getBlock().setData(blockData);
    }
    
    public Material getBlockType() {
        return this.blockType;
    }
    
    public void setBlockType(Material blockType) {
        this.blockType = blockType;
    }
    
    public byte getBlockData() {
        return this.blockData;
    }
    
    public void setBlockData(byte data) {
        this.blockData = data;
    }
    
    public double getBlockDistance() {
        return Math.sqrt(blockDistanceSq);
    }
    
    public double getBlockDistanceSq() {
        return this.blockDistanceSq;
    }
    
    public void setBlockDistanceSq(double blockDistance) {
        if (blockDistance > PhysicsGun.maxDistance*PhysicsGun.maxDistance) {
            blockDistance = PhysicsGun.maxDistance*PhysicsGun.maxDistance;
        }
        
        if (blockDistance < 1) {
            blockDistance = 1;
        }
        
        this.blockDistanceSq = blockDistance;
    }
    
    public void updateBlock() {
        HashSet<Byte> set = new HashSet<>();
        set.add((byte)Material.AIR.getId());
        set.add((byte)this.getBlockType().getId());
        List<Block> blocks = this.getOwner().getLineOfSight(set, PhysicsGun.maxDistance);
        Block prevBlock = blocks.get(0);
        for (Block block : blocks) {
            if (block.getType() != Material.AIR && !block.getLocation().equals(this.getCurrentLocation())) {
                break;
            }

            if (block.getLocation().distanceSquared(this.getOwner().getLocation()) > this.getBlockDistanceSq()) {
                break;
            }

            prevBlock = block;
        }

        this.moveBlock(prevBlock.getLocation());
    }
    
}

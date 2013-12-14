/*
 * BlockProtectionListener.java
 * 
 * PrisonMine
 * Copyright (C) 2013 bitWolfy <http://www.wolvencraft.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.wolvencraft.prison.mines.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.material.MaterialData;

import com.wolvencraft.prison.mines.PrisonMine;
import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.mines.util.constants.BlacklistState;
import com.wolvencraft.prison.mines.util.constants.ProtectionType;
import com.wolvencraft.prison.mines.util.data.Blacklist;
import java.util.ArrayList;
import java.util.List;

public class BlockProtectionListener implements Listener {
    
    public BlockProtectionListener(PrisonMine plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Message.debug("| + BlockProtectionListener Initialized");
    }
    public List<Block> blocks = new ArrayList<>();

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockbreakLow(BlockBreakEvent event) {
        Block b = event.getBlock();
        for (Mine mine : PrisonMine.getStaticMines()) {
            if (mine.getRegion().isLocationInRegion(b.getLocation())) {
                event.setCancelled(true);
                blocks.add(event.getBlock());
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockbreakHigh(BlockBreakEvent event) {
        Block b = event.getBlock();
        if (blocks.contains(b)) {
            event.setCancelled(false);
            blocks.remove(b);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Message.debug("BlockPlaceEvent caught");
        
        Player player = event.getPlayer();
        
        if(player.hasPermission("prison.mine.bypass.place")) {
                Message.debug("The player has bypass permission");
            return;
        }

        Message.debug("Retrieving the region list...");
        
        Block b = event.getBlock();
        String errorString = PrisonMine.getLanguage().PROTECTION_PLACE;
        errorString = errorString.replaceAll("<BLOCK>", b.getType().name().toLowerCase().replace("_", " "));
        
        for(Mine mine : PrisonMine.getStaticMines()) {
            Message.debug("Checking mine " + mine.getId());
            
            if(!mine.getProtectionRegion().isLocationInRegion(b.getLocation())) continue;
            
            Message.debug("Location is in the mine protection region");
            
            if(!player.hasPermission("prison.mine.protection.place." + mine.getId()) && !player.hasPermission("prison.mine.protection.place")) {
                Message.debug("Player " + event.getPlayer().getName() + " does not have permission to place blocks in the mine");
                Message.sendFormattedError(player, errorString);
                event.setCancelled(true);
                continue;
            }
                
            if(!mine.getProtection().contains(ProtectionType.BLOCK_PLACE)) {
                Message.debug("Mine has no block placement protection enabled");
                return;
            }
                
            Message.debug("Mine has a block placement protection enabled");
            Blacklist placeBlacklist = mine.getPlaceBlacklist();
            if(!placeBlacklist.getState().equals(BlacklistState.DISABLED)) {
                Message.debug("Block placement blacklist detected");
                boolean found = false;
                for(MaterialData block : placeBlacklist.getBlocks()) {
                    if(block.getItemType().equals(b.getType())) {
                        found = true;
                        break;
                    }
                }
                
                if((placeBlacklist.getState().equals(BlacklistState.BLACKLIST) && found) || (placeBlacklist.getState().equals(BlacklistState.WHITELIST) && !found)) {
                    Message.debug("Player " + player.getName() + " placed a black/whitelisted block in the mine (" + b.getType().name() + ")!");
                    Message.sendFormattedError(player, errorString);
                    event.setCancelled(true);
                    return;
                }
            }
            else {
                Message.debug("No block placement blacklist detected");
                Message.sendFormattedError(player, errorString);
                event.setCancelled(true);
            }
            Message.debug("All checks passed, player is allowed to break blocks");
            return;
        }
        Message.debug("Placed block was not in the mine region");
        return;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if(event.isCancelled()) return;
        Message.debug("BucketEmptyEvent caught");
        
        Player player = event.getPlayer();
        
        if(player.hasPermission("prison.mine.bypass.place")) {
            Message.debug("The player has a permission to bypass the protection. Aborting . . .");
            return;
        }

        Message.debug("Retrieving the region list...");

        for (Mine mine : PrisonMine.getStaticMines()) {
            Message.debug("Checking mine " + mine.getId());
            
            if(!mine.getProtectionRegion().isLocationInRegion(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) continue;
            
            if(!player.hasPermission("prison.mine.protection.place." + mine.getId()) && !player.hasPermission("prison.mine.protection.place")) {
                Message.debug("Player " + event.getPlayer().getName() + " does not have permission to empty buckets in the mine");
                Message.sendFormattedError(player, "You are not allowed to empty buckets in this area");
                event.setCancelled(true);
                  return;
            }
            
            if (!mine.getProtection().contains(ProtectionType.BLOCK_PLACE)) {
                Message.debug("The mine doesn't have placement protection enabled, skipping rest of check...");
                continue;
            }

            Message.debug("Mine has a block placement protection enabled");
            if(!mine.getPlaceBlacklist().getState().equals(BlacklistState.DISABLED)) {
                Message.debug("Block placement blacklist detected");
                boolean found = false;
                for(MaterialData block : mine.getPlaceBlacklist().getBlocks()) {
                    if(block.getItemType().equals(event.getBucket())) {
                        found = true;
                        break;
                    }
                }
                
                if((mine.getPlaceBlacklist().getState().equals(BlacklistState.WHITELIST) && !found) || (!mine.getPlaceBlacklist().getState().equals(BlacklistState.BLACKLIST) && found)) {
                    Message.debug("Player " + player.getName() + " broke a black/whitelisted block in the mine!");
                    Message.sendFormattedError(player, "You are not allowed to empty buckets in the mine");
                    event.setCancelled(true);
                    return;
                }
            }
            else {
                Message.debug("No block placement blacklist detected");
                Message.sendFormattedError(player, "You are not allowed to empty buckets in the mine");
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBucketFill(PlayerBucketFillEvent event) {
        if(event.isCancelled()) return;
        Message.debug("BucketFillEvent caught");
        
        Player player = event.getPlayer();
        
        if(player.hasPermission("prison.mine.bypass.break")) {
            Message.debug("The player has a permission to bypass the protection. Aborting . . .");
            return;
        }

        Message.debug("Retrieving the region list...");
        
        for (Mine mine : PrisonMine.getStaticMines()) {
            Message.debug("Checking mine " + mine.getId());
            
            if(!mine.getProtectionRegion().isLocationInRegion(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) continue;
            
            if(!player.hasPermission("prison.mine.protection.break." + mine.getId()) && !player.hasPermission("prison.mine.protection.break")) {
                Message.debug("Player " + event.getPlayer().getName() + " does not have permission to fill buckets in the mine");
                Message.sendFormattedError(player, "You are not allowed to fill buckets in this area");
                event.setCancelled(true);
                  return;
            }
            
            if (!mine.getProtection().contains(ProtectionType.BLOCK_BREAK)) {
                Message.debug("The mine doesn't have breaking protection enabled, skipping rest of check...");
                continue;
            }

            Message.debug("Mine has a block breaking protection enabled");
            if(!mine.getBreakBlacklist().getState().equals(BlacklistState.DISABLED)) {
                Message.debug("Block breaking blacklist detected");
                boolean found = false;
                for(MaterialData block : mine.getBreakBlacklist().getBlocks()) {
                    if(block.getItemType().equals(event.getBucket())) {
                        found = true;
                        break;
                    }
                }
                
                if((mine.getBreakBlacklist().getState().equals(BlacklistState.WHITELIST) && !found) || (!mine.getBreakBlacklist().getState().equals(BlacklistState.BLACKLIST) && found)) {
                    Message.debug("Player " + player.getName() + " broke a black/whitelisted block in the mine!");
                    Message.sendFormattedError(player, "You are not allowed to fill buckets in the mine");
                    event.setCancelled(true);
                    return;
                }
            }
            else {
                Message.debug("No block breaking blacklist detected");
                Message.sendFormattedError(player, "You are not allowed to fill buckets in the mine");
                event.setCancelled(true);
            }
        }
    }
}

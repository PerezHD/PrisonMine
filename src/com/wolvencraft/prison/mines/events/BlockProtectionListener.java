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

import com.sk89q.worldedit.Vector;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import com.wolvencraft.prison.mines.PrisonMine;
import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.util.Message;
import java.util.HashSet;
import org.bukkit.ChatColor;

public class BlockProtectionListener implements Listener {
    
    public BlockProtectionListener(PrisonMine plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Message.debug("| + BlockProtectionListener Initialized");
    }

    public HashSet<Vector> blocks = new HashSet<>();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockbreakLow(BlockBreakEvent event) {
        Block b = event.getBlock();
        for (Mine mine : PrisonMine.getStaticMines()) {
            if (!mine.getRegion().isLocationInRegion(b.getLocation())) {
                continue;
            }
            event.setCancelled(true);
            blocks.add(new Vector(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ()));
            break;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockbreakHigh(BlockBreakEvent event) {
        Vector vec = new Vector(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ());
        if (!blocks.contains(vec)) {
            return;
        }

        event.setCancelled(false);
        blocks.remove(vec);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (player.hasPermission("prison.mine.bypass.place")) {
            return;
        }
        
        for (Mine mine : PrisonMine.getStaticMines()) {
            if (!mine.getRegion().isLocationInRegion(event.getBlock().getLocation())) {
                continue;
            }
            player.sendMessage(ChatColor.YELLOW + "You cannot place blocks in a mine.");
            event.setCancelled(true);
            break;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        
        if (player.hasPermission("prison.mine.bypass.place")) {
            return;
        }
        
        for (Mine mine : PrisonMine.getStaticMines()) {
            if (!mine.getRegion().isLocationInRegion(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) {
                continue;
            }
            player.sendMessage(ChatColor.YELLOW + "You cannot place blocks in a mine.");
            event.setCancelled(true);
            break;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        
        if (player.hasPermission("prison.mine.bypass.place")) {
            return;
        }
        
        for (Mine mine : PrisonMine.getStaticMines()) {
            if (!mine.getRegion().isLocationInRegion(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) {
                continue;
            }
            player.sendMessage(ChatColor.YELLOW + "You cannot place blocks in a mine.");
            event.setCancelled(true);
            break;
        }
    }
}

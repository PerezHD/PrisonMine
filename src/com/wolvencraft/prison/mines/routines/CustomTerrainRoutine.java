/*
 * CustomTerrainRoutine.java
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

package com.wolvencraft.prison.mines.routines;

import java.util.ConcurrentModificationException;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.mines.util.constants.BlacklistState;
import com.wolvencraft.prison.mines.util.constants.MineFlag;
import com.wolvencraft.prison.mines.util.data.RandomBlock;
import com.wolvencraft.prison.util.Util;

public class CustomTerrainRoutine {    
    public static boolean run(Mine mine) {
        RandomBlock pattern = new RandomBlock(mine.getBlocks());
        Location one = mine.getRegion().getMinimum();
        Location two = mine.getRegion().getMaximum();
        World world = mine.getWorld();
        
        BlacklistState blState = mine.getBlacklist().getState();
        MaterialData borderBlock = Util.getBlock(mine.getFlag(MineFlag.SurfaceOre).getOption());
        
        if(blState.equals(BlacklistState.DISABLED)) {
            for (int x = one.getBlockX(); x <= two.getBlockX(); x++) {
                for (int y = one.getBlockY(); y <= two.getBlockY(); y++) {
                    for (int z = one.getBlockZ(); z <= two.getBlockZ(); z++) {
                        
                        try {
                            Block original = world.getBlockAt(x, y, z);
                            MaterialData newBlock;
                            if((Math.abs(one.getBlockX() - x) < 3 || Math.abs(two.getBlockX() - x) < 3) ||
                                    (Math.abs(one.getBlockY() - y) < 3 || Math.abs(two.getBlockY() - y) < 3) ||
                                        (Math.abs(one.getBlockZ() - z) < 3 || Math.abs(two.getBlockZ() - z) < 3)) { newBlock = borderBlock; }
                            else { newBlock = pattern.next(); }
                            original.setTypeIdAndData(newBlock.getItemTypeId(), newBlock.getData(), false);
                        }
                        catch (ConcurrentModificationException cme) { Message.log(Level.SEVERE, "An error has occurred while running a generator [ConcurrentModificationException]"); continue; }
                        catch (Exception ex) { Message.log(Level.SEVERE, "An error has occurred while running a generator [Exception]"); continue; }
                        catch (IllegalAccessError iae) { Message.log(Level.SEVERE, "An error has occurred while running a generator [IllegalAccessError]"); continue; }
                        catch (Error iae) { Message.log(Level.SEVERE, "An error has occurred while running a generator [Error]"); continue; }
                    }
                }
            }
            Message.debug("| Reset complete! BlacklistState.DISABLED");
            return true;
        } else if(blState.equals(BlacklistState.BLACKLIST)) {
            for (int x = one.getBlockX(); x <= two.getBlockX(); x++) {
                for (int y = one.getBlockY(); y <= two.getBlockY(); y++) {
                    for (int z = one.getBlockZ(); z <= two.getBlockZ(); z++) {
                        try { 
                            Block original = world.getBlockAt(x, y, z);
                            MaterialData newBlock;
                            if((Math.abs(one.getBlockX() - x) < 3 || Math.abs(two.getBlockX() - x) < 3) ||
                                (Math.abs(one.getBlockY() - y) < 3 || Math.abs(two.getBlockY() - y) < 3) ||
                                (Math.abs(one.getBlockZ() - z) < 3 || Math.abs(two.getBlockZ() - z) < 3)) { newBlock = borderBlock; }
                            else { newBlock = pattern.next(); }
                            if(!mine.getBlacklist().getBlocks().contains(original.getState().getData()))
                                original.setTypeIdAndData(newBlock.getItemTypeId(), newBlock.getData(), false);
                        }
                        catch (ConcurrentModificationException cme) { Message.log(Level.SEVERE, "An error has occurred while running a generator [ConcurrentModificationException]"); continue; }
                        catch (Exception ex) { Message.log(Level.SEVERE, "An error has occurred while running a generator [Exception]"); continue; }
                        catch (IllegalAccessError iae) { Message.log(Level.SEVERE, "An error has occurred while running a generator [IllegalAccessError]"); continue; }
                        catch (Error iae) { Message.log(Level.SEVERE, "An error has occurred while running a generator [Error]"); continue; }
                    }
                }
            }
            Message.debug("| Reset complete! BlacklistState.BLACKLIST");
            return true;
        } else if(blState.equals(BlacklistState.WHITELIST)) {
            for (int x = one.getBlockX(); x <= two.getBlockX(); x++) {
                for (int y = one.getBlockY(); y <= two.getBlockY(); y++) {
                    for (int z = one.getBlockZ(); z <= two.getBlockZ(); z++) {
                        try { 
                            Block original = world.getBlockAt(x, y, z);
                            MaterialData newBlock;
                            if((Math.abs(one.getBlockX() - x) < 3 || Math.abs(two.getBlockX() - x) < 3) ||
                                    (Math.abs(one.getBlockY() - y) < 3 || Math.abs(two.getBlockY() - y) < 3) ||
                                    (Math.abs(one.getBlockZ() - z) < 3 || Math.abs(two.getBlockZ() - z) < 3)) { newBlock = borderBlock; }
                            else { newBlock = pattern.next(); }
                            if(mine.getBlacklist().getBlocks().contains(original.getState().getData()))
                                original.setTypeIdAndData(newBlock.getItemTypeId(), newBlock.getData(), false);
                        }
                        catch (ConcurrentModificationException cme) { Message.log(Level.SEVERE, "An error has occurred while running a generator [ConcurrentModificationException]"); continue; }
                        catch (Exception ex) { Message.log(Level.SEVERE, "An error has occurred while running a generator [Exception]"); continue; }
                        catch (IllegalAccessError iae) { Message.log(Level.SEVERE, "An error has occurred while running a generator [IllegalAccessError]"); continue; }
                        catch (Error iae) { Message.log(Level.SEVERE, "An error has occurred while running a generator [Error]"); continue; }
                    }
                }
             }
            Message.debug("| Reset complete! BlacklistState.WHITELIST");
            return true;
        } else {
            Message.log(Level.SEVERE, "Unknown blacklist state! Aborting.");
            Message.log(Level.SEVERE, "Abortion not legal in your state due to religious nutjobs");
        }
        
        return false;
    }
}
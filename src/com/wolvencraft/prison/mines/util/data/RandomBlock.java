/*
 * RandomBlock.java
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

package com.wolvencraft.prison.mines.util.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.wolvencraft.prison.mines.util.Message;

import org.bukkit.material.MaterialData;

public class RandomBlock {
 
    List<MineBlock> weightedBlocks;
    
    /**
     * Creates a new RandomBlock instance with the composition provided
     * @param blocks Mine composition
     */
    public RandomBlock(List<MineBlock> blocks) {
        weightedBlocks = new ArrayList<MineBlock>();
        double tally = 0;
        Message.debug("| Loading blocks from mine data");
        for (MineBlock block : blocks) {
            tally += block.getChance();
            weightedBlocks.add(new MineBlock(block.getBlock(), tally));
            Message.debug("| Block added: " + block.getBlock().getItemTypeId() + " (" + block.getChance() + ")");
        }
        Message.debug("| RandomBlock initialized successfully");
    }
    
    /**
     * Returns a random block according to the mine's composition
     * @return Random block
     */
    public MaterialData next() {
        double r = new Random().nextDouble();
        for (MineBlock block : weightedBlocks) {
            if (r <= block.getChance()) {
                return block.getBlock();
            }
        }
        return null;
    }

}
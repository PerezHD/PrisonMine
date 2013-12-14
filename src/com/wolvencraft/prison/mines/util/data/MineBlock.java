/*
 * MineBlock.java
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

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("MineBlock")
public class MineBlock implements ConfigurationSerializable {

    private MaterialData block;
    private double chance;

    public MineBlock(MaterialData block, double chance) {
        this.block = block;
        this.chance = chance;
    }

    public MineBlock(Map<String, Object> me) {
        chance = (Double) me.get("chance");
        block = new MaterialData((Integer) me.get("blockId"), ((Integer) me.get("blockData")).byteValue());
    }

    public Map<String, Object> serialize() {
        Map<String, Object> me = new HashMap<String, Object>();
        me.put("chance", chance);
        me.put("blockId", block.getItemTypeId());
        me.put("blockData", block.getData());
        return me;
    }
    
    public MaterialData getBlock()     { return block; }
    public double getChance()         { return chance; }
    
    public void setChance(double chance) { this.chance = chance; }

    public MineBlock clone() { return new MineBlock(block, chance); }
}
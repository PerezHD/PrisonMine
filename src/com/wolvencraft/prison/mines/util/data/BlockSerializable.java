/*
 * BlockSerializable.java
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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.material.MaterialData;

@SerializableAs("BlockSerializable")
public class BlockSerializable implements ConfigurationSerializable {
    int blockId;
    byte data;
    
    public BlockSerializable(MaterialData block) {
        blockId = block.getItemTypeId();
        data = block.getData();
    }
    
    public BlockSerializable(Map<String, Object> map) {
        blockId = ((Integer) map.get("blockId")).intValue();
        data = ((Integer) map.get("data")).byteValue();
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("blockId", blockId);
        map.put("data", data);
        return map;
    }
    
    public MaterialData toMaterialData() {
        MaterialData block = new MaterialData(blockId);
        block.setData(data);
        return block;
    }
}

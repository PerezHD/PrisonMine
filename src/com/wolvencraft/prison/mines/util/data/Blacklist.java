/*
 * Blacklist.java
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.material.MaterialData;

import com.wolvencraft.prison.mines.util.constants.BlacklistState;
import com.wolvencraft.prison.util.Message;

@SerializableAs("Blacklist")
public class Blacklist implements ConfigurationSerializable {
    private BlacklistState type;
    private List<MaterialData> blocks;
    
    public Blacklist() {
        type = BlacklistState.DISABLED;
        blocks = new ArrayList<MaterialData>();
    }
    
    @SuppressWarnings("unchecked")
    public Blacklist(Map<String, Object> me) {
        type = BlacklistState.DISABLED;
        if(me.containsKey("enabled") && ((Boolean) me.get("enabled"))) type = BlacklistState.BLACKLIST;
        if(me.containsKey("whitelist") && ((Boolean) me.get("whitelist"))) type = BlacklistState.WHITELIST;
        if(me.containsKey("type")) type = BlacklistState.get((Integer) me.get("type"));
        
        Map<Integer, Byte> materials = (Map<Integer, Byte>) me.get("blocks");
        blocks = new ArrayList<MaterialData>();
        for(Map.Entry<Integer, Byte> entry : materials.entrySet()) {
            try {
                blocks.add(new MaterialData(Material.getMaterial(entry.getKey().intValue()), entry.getValue().byteValue()));
            } catch (ClassCastException cce) {
                blocks.add(new MaterialData(Material.getMaterial(entry.getKey().intValue())));
            }
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> me = new HashMap<String, Object>();
        me.put("type", type.getId());
        Map<Integer, Byte> materials = new HashMap<Integer, Byte>();
        for(MaterialData block : blocks) {
            materials.put(block.getItemTypeId(), block.getData());
        }
        me.put("blocks", materials);
        return me;
    }
    
    public BlacklistState getState()     { return type; }
    
    public List<MaterialData> getBlocks() { 
        List<MaterialData> tempBlocks = new ArrayList<MaterialData>();
        for(MaterialData block : blocks) tempBlocks.add(block);
        return tempBlocks;
    }
    
    public void setState(BlacklistState type) { this.type = type; }
    
    public void setBlocks(List<MaterialData> newBlocks) {
        blocks.clear();
        for(MaterialData block : newBlocks) {
            blocks.add(block);
            Message.debug("Block added to the blacklist = " + block.getItemType());
        }
    }
}

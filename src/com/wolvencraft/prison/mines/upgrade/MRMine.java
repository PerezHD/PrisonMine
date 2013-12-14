/*
 * MRMine.java
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

package com.wolvencraft.prison.mines.upgrade;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.mines.util.constants.ProtectionType;
import com.wolvencraft.prison.mines.util.data.Blacklist;
import com.wolvencraft.prison.mines.util.data.MineBlock;
import com.wolvencraft.prison.mines.util.data.SimpleLoc;
import com.wolvencraft.prison.region.PrisonRegion;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
 
/**
 * Here lies the mine object code as written by me and jjkoletar in MineReset 2.0.1<br />
 * Please, contact me if you have any objections to me using this code for my purposes.
 * @author bitWolfy, jjkoletar
 */

@SerializableAs("MRMine")
public class MRMine implements ConfigurationSerializable, Listener {
    private Location one;
    private Location two;
    private World world;
    private Location tpPoint;
    private String name;
    private String displayName;
    private String parent;
    private List<MineBlock> blocks;
    private String generator;
    private Blacklist blacklist;
    private boolean silent;
    private boolean automatic;
    private int automaticSeconds;
    private long nextAutomaticResetTick;
    private boolean cooldownEnabled;
    private int cooldownSeconds;
    private boolean warned;
    private List<Integer> warningTimes;
    private List<ProtectionType> enabledProtection;
    private Location protOne;
    private Location protTwo;
    private Blacklist breakBlacklist;
    private Blacklist placeBlacklist;
     
    private int blocksLeft;
 
    /**
     * Deserialize a mine from its YML form
     * @param me Bukkitian map of strings to objects. <b>Incorrect object types for values are not tolerated by the code!</b>
     */
    @SuppressWarnings("unchecked")
    public MRMine(Map<String, Object> me) {
        String worldString = (String) me.get("world");
        world = Bukkit.getServer().getWorld(worldString);
        if(world == null) world = Bukkit.getServer().getWorld(UUID.fromString(worldString));
        if(world == null) throw new IllegalArgumentException("Mine file contains an invalid world!");
        one = ((Vector) me.get("one")).toLocation(world);
        two = ((Vector) me.get("two")).toLocation(world);
        tpPoint = ((SimpleLoc) me.get("tpPoint")).toLocation();
        displayName = (String) me.get("displayName");
        name = (String) me.get("name");
        parent = (String) me.get("parent");
        blacklist = (Blacklist) me.get("blacklist");
        generator = (String) me.get("generator");
        silent = (Boolean) me.get("silent");
        automatic = (Boolean) me.get("automatic");
        automaticSeconds = (Integer) me.get("automaticResetTime");
        if(me.containsKey("nextAutomaticResetTick")) nextAutomaticResetTick = ((Integer) me.get("nextAutomaticResetTick")).longValue();
        else nextAutomaticResetTick = automaticSeconds * 20;
        cooldownEnabled = (Boolean) me.get("cooldownEnabled");
        cooldownSeconds = (Integer) me.get("cooldownSeconds");
        warned = (Boolean) me.get("isWarned");
        warningTimes = (List<Integer>) me.get("warningTimes");
        blocks = (List<MineBlock>) me.get("blocks");
        List<String> names = (List<String>) me.get("protectionTypes");
        enabledProtection = new ArrayList<ProtectionType>();
        for(String name : names)
            enabledProtection.add(ProtectionType.valueOf(name));
        protOne = ((Vector) me.get("protOne")).toLocation(world);
        protTwo = ((Vector) me.get("protTwo")).toLocation(world);
        breakBlacklist = (Blacklist) me.get("breakBlacklist");
        placeBlacklist = (Blacklist) me.get("placeBlacklist");
        
        if(me.containsKey("blocksLeft")) blocksLeft = ((Integer) me.get("blocksLeft")).intValue();
    }
 
    public Map<String, Object> serialize() {
        Map<String, Object> me = new HashMap<String, Object>();
        me.put("one", one.toVector());
        me.put("two", two.toVector());
        me.put("world", world.getUID().toString());
        me.put("tpPoint", new SimpleLoc(tpPoint));
        me.put("displayName", displayName);
        me.put("name", name);
        me.put("parent", parent);
        me.put("blacklist", blacklist);
        me.put("generator", generator);
        me.put("silent", silent);
        me.put("automatic", automatic);
        me.put("automaticResetTime", automaticSeconds);
        me.put("nextAutomaticResetTick", nextAutomaticResetTick);
        me.put("cooldownEnabled", cooldownEnabled);
        me.put("cooldownSeconds", cooldownSeconds);
        me.put("isWarned", warned);
        me.put("warningTimes", warningTimes);
        List<String> names = new ArrayList<String>();
        for(ProtectionType prot : enabledProtection)
            names.add(prot.name());
        me.put("protectionTypes", names);
        me.put("protOne", protOne.toVector());
        me.put("protTwo", protTwo.toVector());
        me.put("blocks", blocks);
        me.put("breakBlacklist", breakBlacklist);
        me.put("placeBlacklist", placeBlacklist);
        me.put("blocksLeft", blocksLeft);
        return me;
    }
     
    public Mine importMine() {
        PrisonRegion region = new PrisonRegion(one, two);
        
        Mine mine = new Mine(name, displayName, parent, region, world, tpPoint, blocks, blacklist, cooldownEnabled, cooldownSeconds, silent, warned, warningTimes, enabledProtection, breakBlacklist, placeBlacklist);
        
        if(automatic) {
            mine.setAutomaticReset(true);
            mine.setResetPeriod(automaticSeconds);
        }
        
        mine.saveFile();
        Message.log("Imported mine from MineReset: " + name);
        return mine;
    }
}
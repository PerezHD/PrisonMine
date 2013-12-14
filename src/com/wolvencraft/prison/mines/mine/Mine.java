/*
 * Mine.java
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

package com.wolvencraft.prison.mines.mine;

import com.wolvencraft.prison.region.PrisonRegion;
import com.wolvencraft.prison.region.PrisonSelection;
import com.wolvencraft.prison.mines.PrisonMine;
import com.wolvencraft.prison.mines.routines.CustomTerrainRoutine;
//import com.wolvencraft.prison.mines.routines.RandomFastTerrainRoutine;
import com.wolvencraft.prison.mines.routines.RandomTerrainRoutine;
import com.wolvencraft.prison.mines.triggers.BaseTrigger;
import com.wolvencraft.prison.mines.triggers.CompositionTrigger;
import com.wolvencraft.prison.mines.triggers.TimeTrigger;
import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.mines.util.Util;
//import com.wolvencraft.prison.mines.util.constants.DisplaySignType;
import com.wolvencraft.prison.mines.util.constants.MineFlag;
import com.wolvencraft.prison.mines.util.constants.ProtectionType;
import com.wolvencraft.prison.mines.util.constants.ResetTrigger;
import com.wolvencraft.prison.mines.util.data.Blacklist;
import com.wolvencraft.prison.mines.util.data.MineBlock;
import com.wolvencraft.prison.mines.util.data.SimpleLoc;
import com.wolvencraft.prison.mines.util.flags.BaseFlag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandException;
//import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.MaterialData;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * A virtual representation of a cuboid region that is being reset during the runtime
 * @author bitWolfy
 *
 */
@SerializableAs("pMine")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class Mine implements ConfigurationSerializable {
    private String id;
    private String name;

    private String parent;
    
    private PrisonRegion region;
    private World world;
    private Location tpPoint;
    
    private List<MineBlock> blocks;
    private Blacklist blacklist;
    
    private List<BaseTrigger> resetTriggers;
    private List<BaseFlag> flags;
    
    private boolean cooldownEnabled;
    private int cooldownPeriod;
    private long cooldownEndsIn;
    
    private List<Integer> warningTimes;
    
    private List<ProtectionType> protection;
    private PrisonRegion protectionRegion; 
    private Blacklist breakBlacklist;
    private Blacklist placeBlacklist;
    
    private int totalBlocks;
    private int blocksLeft;
    
    private String lastResetBy;

    /**
     * Standard constructor for new mines
     * @param id Mine ID, specified by player. This will be the name of the mine configuration file.
     * @param region Mine region
     * @param world World where the mine is located
     * @param tpPoint Mine warp location4
     */
    public Mine(String id, PrisonRegion region, World world, Location tpPoint) {
        this.id = id;
        name = "";
        
        parent = null;
        
        this.region = region;
        this.world = world;
        this.tpPoint = tpPoint;
        
        blocks = new ArrayList<MineBlock>();
        blocks.add(new MineBlock(new MaterialData(Material.AIR), 1.0));
        blacklist = new Blacklist();
        
        resetTriggers = new ArrayList<BaseTrigger>();
        
        cooldownEnabled = false;
        cooldownPeriod = 0;
        cooldownEndsIn = 0;
        
        warningTimes = new ArrayList<Integer>();
        
        flags = new ArrayList<BaseFlag>();
        
        protection = new ArrayList<ProtectionType>();
        protectionRegion = region.clone();
        breakBlacklist = new Blacklist();
        placeBlacklist = new Blacklist();
        
        totalBlocks = blocksLeft = region.getBlockCount();
        
        lastResetBy = "None";
    }
    
    /**
     * Full constructor. Should only be used to import mines of old format.
     * @param id
     * @param name
     * @param parent
     * @param region
     * @param world
     * @param tpPoint
     * @param blocks
     * @param blockReplaceBlacklist
     * @param cooldownEnabled
     * @param cooldownPeriod
     * @param silent
     * @param warned
     * @param warningTimes
     * @param enabledProtection
     * @param breakBlacklist
     * @param placeBlacklist
     */
    public Mine(String id, String name, String parent, PrisonRegion region, World world, Location tpPoint, List<MineBlock> blocks, Blacklist blockReplaceBlacklist, boolean cooldownEnabled, int cooldownPeriod, boolean silent, boolean warned, List<Integer> warningTimes, List<ProtectionType> enabledProtection, Blacklist breakBlacklist, Blacklist placeBlacklist) {
        this.id = id;
        this.name = name;

        this.parent = parent;
        
        this.region = region;
        this.world = world;
        this.tpPoint = tpPoint;
        
        this.blocks = blocks;
        this.blacklist = blockReplaceBlacklist;
        
        this.resetTriggers = new ArrayList<BaseTrigger>();
        this.flags = new ArrayList<BaseFlag>();
        
        this.cooldownEnabled = cooldownEnabled;
        this.cooldownPeriod = cooldownPeriod;
        this.cooldownEndsIn = cooldownPeriod * 20;
        
        if(silent) flags.add(MineFlag.Silent.dispatch());
        
        this.warningTimes = warningTimes;
        
        this.protection = enabledProtection;
        this.protectionRegion = region.clone(); 
        this.breakBlacklist = breakBlacklist;
        this.placeBlacklist = placeBlacklist;
        
        this.totalBlocks = region.getBlockCount();
        this.blocksLeft = region.getBlockCount();

        this.lastResetBy = "None";
    }
    
    /**
     * Constructor for deserialization from a map
     * @param map Map to deserialize from
     */
    @SuppressWarnings("unchecked")
    public Mine(Map<String, Object> map) {
        id = (String) map.get("id");
        name = (String) map.get("name");
        
        parent = (String) map.get("parent");
        
        region = (PrisonRegion) map.get("region");
        world = Bukkit.getWorld((String) map.get("world"));
        tpPoint = ((SimpleLoc) map.get("tpPoint")).toLocation();
        
        blocks = (List<MineBlock>) map.get("blocks");
        blacklist = (Blacklist) map.get("blockReplaceBlacklist");
        
        resetTriggers = (List<BaseTrigger>) map.get("resetTriggers");

        cooldownEnabled = ((Boolean) map.get("cooldownEnabled")).booleanValue();
        cooldownPeriod = ((Integer) map.get("cooldownPeriod")).intValue();
        cooldownEndsIn = 0;
        
        warningTimes = (List<Integer>) map.get("warningTimes");
        
        flags = MineFlag.toMineFlagList((List<String>) map.get("flags"));
        
        if(map.containsValue("silent") && !hasFlag(MineFlag.Silent) && ((Boolean) map.get("silent")).booleanValue()) flags.add(MineFlag.Silent.dispatch());
        
        protection = ProtectionType.toProtectionList((List<String>) map.get("enabledProtection"));
        protectionRegion = (PrisonRegion) map.get("protectionRegion");
        breakBlacklist = (Blacklist) map.get("breakBlacklist");
        placeBlacklist = (Blacklist) map.get("placeBlacklist");
        
        totalBlocks = region.getBlockCount();
        blocksLeft = ((Integer) map.get("blocksLeft")).intValue();
        
        lastResetBy = "None";
    }
    
    /**
     * Serialization method for mine data storage
     * @return Serialization map
     */
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        map.put("name", name);
        
        map.put("parent", parent);
        
        map.put("region", region);
        map.put("world", world.getName());
        map.put("tpPoint", new SimpleLoc(tpPoint));
        
        map.put("blocks", blocks);
        map.put("blockReplaceBlacklist", blacklist);
        
        map.put("resetTriggers", resetTriggers);
        
        map.put("cooldownEnabled", cooldownEnabled);
        map.put("cooldownPeriod", cooldownPeriod);
        
        map.put("warningTimes", warningTimes);
        
        map.put("flags", MineFlag.toStringList(flags));
        
        map.put("enabledProtection", ProtectionType.toStringList(protection));
        map.put("protectionRegion", protectionRegion);
        map.put("breakBlacklist", breakBlacklist);
        map.put("placeBlacklist", placeBlacklist);
        
        map.put("blocksLeft", blocksLeft);
        return map;
    }
    
    /**
     * Reset the mine according
     * @return <b>true</b> if successful, <b>false</b> if not
     */
    public boolean reset() {
        if(PrisonMine.getSettings().PLAYERS_TP_ON_RESET) {
            try { removePlayers(); }
            catch(ConcurrentModificationException cme) { Message.log(Level.WARNING, "An error occured while removing players from the mine"); }
        }
        
        if(hasFlag(MineFlag.CommandBeforeFlag)) {
            for(BaseFlag flag : flags) {
                if(!flag.getName().equals("commandbefore")) continue;
                try { Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), flag.getOption()); }
                catch (CommandException ex) { }
            }
        }
        
        if(hasFlag(MineFlag.ResetSound)) {
            String soundName = getFlag(MineFlag.ResetSound).getOption();
            if(Util.soundExists(soundName)) {
                for (Player player : Util.getNearbyPlayers(tpPoint, 32)) {
                    player.playSound(tpPoint, Util.getSound(soundName), 1, 0);
                }
            }
        }
        
        /*
        for(DisplaySign sign : PrisonMine.getStaticSigns()) {
            if(!sign.getParent().equals(id) || !sign.getType().equals(DisplaySignType.Output) || sign.getAttachedBlock() == null) continue;
            
            Block torch = sign.getAttachedBlock().getRelative(sign.getAttachedBlockFace());
            if(torch == null || !torch.getType().equals(Material.REDSTONE_TORCH_OFF)) continue;
            torch.setType(Material.REDSTONE_TORCH_ON);
        }
        */
        boolean result = true;
        if(hasFlag(MineFlag.SurfaceOre)) result = CustomTerrainRoutine.run(this);
        else {
                result = RandomTerrainRoutine.run(this);
        }
        
        if(hasFlag(MineFlag.CommandAfterFlag)) {
            for(BaseFlag flag : flags) {
                if(!flag.getName().equals("commandafter")) continue;
                try { Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), flag.getOption()); }
                catch (CommandException ex) { }
            }
        }
        
        return result;
    }
    
    /**
     * Teleport all the players from the region that is being reset
     * @return <b>true</b> if successful, <b>false</b> if not
     * @throws ConcurrentModificationException Exception is thrown if a player is already being teleported
     */
    private boolean removePlayers() throws ConcurrentModificationException {
        for (Player p : Util.getStaticPlayers(world)) {
            if (region.isLocationInRegion(p.getLocation())) {
                p.teleport(tpPoint, PlayerTeleportEvent.TeleportCause.PLUGIN);
                Message.sendFormattedSuccess(p, PrisonMine.getLanguage().MISC_TELEPORT, true, this);
            }
        }
        return true;
    }
    
    public String getName()                     { if(name.equalsIgnoreCase("")) return id; else return name; }
    public boolean hasParent()                  { return (parent != null); }
    public Mine getSuperParent()                { return getSuperParent(this); }
    public int getCooldownEndsIn()              { return (int)(cooldownEndsIn / 20); }
    public List<MineBlock> getBlocks()          { return new ArrayList<MineBlock>(blocks); }
    public List<Integer> getWarningTimes()      { return new ArrayList<Integer>(warningTimes); }
    
    public void setRegion(PrisonSelection sel)  { this.region = null; this.region = new PrisonRegion(sel); }
    public void updateCooldown(long ticks)      { cooldownEndsIn -= ticks; }
    public void resetCooldown()                 { cooldownEndsIn = cooldownPeriod * 20; }
    
    public void addBlock(MineBlock block) {
        blocks.add(block);
    }
    
    public void addBlock(MaterialData material, double percent) {
        blocks.add(new MineBlock(material, percent));
    }
    
    public void removeBlock(MineBlock block) {
        blocks.remove(block);
    }
    
    public void removeBlock(MaterialData material) {
        blocks.remove(getBlock(material));
    }
    
    public MineBlock getBlock(MaterialData block) {
        if(block == null) return null;
        for(MineBlock thisBlock : blocks) { if(thisBlock.getBlock().equals(block)) return thisBlock; }
        return null;
    }
    
    public MineBlock getMostCommonBlock() {
        MineBlock mostCommon = blocks.get(0);
        for(MineBlock curBlock : blocks) {
            if(curBlock.getChance() > mostCommon.getChance()) mostCommon = curBlock;
        }
        return mostCommon;
    }
    
    public boolean hasWarnings() {
        return !warningTimes.isEmpty();
    }
    
    public boolean hasWarningTime(Integer time) {
        return warningTimes.contains(time);
    }
    
    public void addWarningTime(Integer time) {
        warningTimes.add(time);
    }
    
    public void removeWarningTime(Integer time) {
        warningTimes.remove(time);
    }
    
    /**
     * Returns the instance of the trigger with the specified ID, or <b>null</b> if it is not present
     * @param triggerId ID of the requested trigger
     * @return Instance of the trigger, or <b>null</b> if it is not present
     */
    private BaseTrigger getTrigger(ResetTrigger triggerId) {
        for(BaseTrigger trigger : resetTriggers) {
            if(trigger.getId().equals(triggerId)) return trigger;
        }
        return null;
    }
    
    /**
     * Determines if the mine is reset by the timer.<br />
     * Works by iterating through the list of triggers to determine if the TimeTrigger is present
     * @return <b>true</b> if the TimeTrigger is present, <b>false</b> otherwise
     */
    public boolean getAutomaticReset() {
        return (getTrigger(ResetTrigger.TIME) != null);
    }
    
    /**
     * Returns the reset period (i.e. how often should the mine reset) of the mine, in seconds
     * @return <b>int</b> reset period of the mine, or <b>-1</b> if the trigger is absent
     */
    public int getResetPeriod() {
        if(getTrigger(ResetTrigger.TIME) == null) return -1;
        return ((TimeTrigger)(getTrigger(ResetTrigger.TIME))).getPeriod(); }
    
    /**
     * Returns the reset period (i.e. how often should the mine reset) of the mine's <b>superparent</b>, in seconds
     * @return <b>int</b> reset period of the mine's superparent, or <b>-1</b> if the trigger is absent
     */
    public int getResetPeriodSafe() {
        if(parent == null) return getResetPeriod();
        return get(parent).getResetPeriodSafe();
    }
    
    /**
     * Returns the time until the mine resets, in seconds
     * @return <b>int</b> time until the reset period ends
     */
    public int getResetsIn() {
        if(getTrigger(ResetTrigger.TIME) == null) return -1;
        return ((TimeTrigger)(getTrigger(ResetTrigger.TIME))).getNext();
    }
    
    /**
     * Returns the time until the mine's <b>superparent</b> resets, in seconds
     * @return <b>int</b> time until the reset period ends
     */
    public int getResetsInSafe() {
        if(parent == null) return getResetsIn();
        return get(parent).getResetsInSafe();
    }
    
    /**
     * Toggles whether the mine should be reset by the timer
     * @param state Should the mine be reset by the timer?
     * @return <b>false</b> if an error has occurred, <b>true</b> otherwise.
     */
    public boolean setAutomaticReset(boolean state) {
        if(state) {
            if(getTrigger(ResetTrigger.TIME) != null) return false;
            resetTriggers.add(new TimeTrigger(this, PrisonMine.getSettings().DEFAULTTIME));
        } else {
            if(getTrigger(ResetTrigger.TIME) == null) return false;
            getTrigger(ResetTrigger.TIME).cancel();
            resetTriggers.remove(getTrigger(ResetTrigger.TIME));
        }
        return true;
    }
    
    /**
     * Sets the reset period to the value specified
     * @param period Time, in seconds, between resets
     * @return <b>false</b> if the TimeTrigger is not present, <b>true</b> otherwise
     */
    public boolean setResetPeriod(int period) {
        if(getTrigger(ResetTrigger.TIME) == null) return false;
        ((TimeTrigger)(getTrigger(ResetTrigger.TIME))).setPeriod(period);
        return true;
    }
    
    /**
     * Resets the timer to the default value.<br />
     * This method should only be used to reset the timer if the mine was reset manually
     * @return <b>false</b> if the TimeTrigger is not present, <b>true</b> otherwise.
     */
    public boolean resetTimer() {
        if(getTrigger(ResetTrigger.TIME) == null) return false;
        ((TimeTrigger)getTrigger(ResetTrigger.TIME)).resetTimer();
        return true;
    }
    
    /**
     * Determines if the mine is reset by the composition.<br />
     * Works by iterating through the list of triggers to determine if the CompositionTrigger is present
     * @return <b>true</b> if the CompositionTrigger is present, <b>false</b> otherwise
     */
    public boolean getCompositionReset() {
        return (getTrigger(ResetTrigger.COMPOSITION) != null);
    }
    
    /**
     * Returns the total number of blocks in the mine.<br />
     * Calling this method triggers the immediate recount of blocks in the mine.<br />
     * CompositionTrigger does not have to be active in order to count the blocks.
     * @return <b>int</b> number of blocks
     */
    public int getTotalBlocks() {
        totalBlocks = region.getBlockCount();
        return totalBlocks;
    }
    
    /**
     * Returns the total number of blocks in the mine.<br />
     * CompositionTrigger does not have to be active in order to count the blocks.
     * @return <b>int</b> number of blocks
     */
    public int getTotalBlocksSafe() {
        return totalBlocks;
    }
    
    /**
     * Returns the number of non-air blocks remaining in the mine. This number might not reflect how full the mine is due to flooding, for example.<br />
     * Calling this method triggers the immediate recount of blocks in the mine.<br />
     * CompositionTrigger does not have to be active in order to count the blocks.
     * @return <b>int</b> number of non-air blocks in the mine
     */
    public int getBlocksLeft() {
        blocksLeft = region.getBlockCountSolid();
        return blocksLeft;
    }
    
    /**
     * Returns the number of non-air blocks remaining in the mine. This number might not reflect how full the mine is due to flooding, for example.<br />
     * CompositionTrigger does not have to be active in order to count the blocks.
     * @return <b>int</b> number of non-air blocks in the mine
     */
    public int getBlocksLeftSafe() {
        return blocksLeft;
    } 
    
    /**
     * Performs the following calculation and returns the result:<br />
     * (<b>getBlocksLeft()</b> / <b>getTotalBlocks()</b>) * 100
     * @return <b>double</b> Percent of the mine that is taken up by solid blocks
     */
    public double getCurrentPercent() {
        return ((double) getBlocksLeft() / (double) getTotalBlocks()) * 100;
    }
    
    /**
     * Returns the percent at which the mine shall be reset via CompositionTrigger
     * @return <b>double</b> Percentage required for the reset
     */
    public double getRequiredPercent() {
        if(getTrigger(ResetTrigger.COMPOSITION) == null) return -1;
        return ((CompositionTrigger)(getTrigger(ResetTrigger.COMPOSITION))).getPercent() * 100;
    }
    
    /**
     * Toggles whether the mine should be reset due to its composition
     * @param state Should the mine be reset because of its composition
     * @return <b>false</b> if an error has occurred, <b>true</b> otherwise.
     */
    public boolean setCompositionReset(boolean state) {
        if(state) {
            if(getCompositionReset()) return false;
            resetTriggers.add(new CompositionTrigger(this, 0));
        }
        else {
            if(!getCompositionReset()) return false;
            getTrigger(ResetTrigger.COMPOSITION).cancel();
            resetTriggers.remove(getTrigger(ResetTrigger.COMPOSITION));
        }
        return true;
    }
    
    /**
     * Sets the required percent to the value specified
     * @param Percent at which the mine should be reset
     * @return <b>false</b> if the CompositionTrigger is not present, <b>true</b> otherwise
     */
    public boolean setCompositionPercent(double percent) {
        if(getTrigger(ResetTrigger.COMPOSITION) == null) return false;
        ((CompositionTrigger)(getTrigger(ResetTrigger.COMPOSITION))).setPercent(percent);
        return true;
    }
    
    public List<BaseFlag> getAllFlags() {
        List<BaseFlag> localFlags = new ArrayList<BaseFlag>();
        for(BaseFlag flag : flags) localFlags.add(flag);
        return localFlags;
    }
    
    public List<BaseFlag> getFlagsByType(MineFlag flag) {
        List<BaseFlag> demFlags = new ArrayList<BaseFlag>();
        for(BaseFlag testFlag : flags) {
            if(testFlag.getName().equalsIgnoreCase(flag.getAlias())) demFlags.add(testFlag);
        }
        return demFlags;
    }
    
    public BaseFlag getFlag(MineFlag flag) {
        for(BaseFlag testFlag : flags) {
            if(testFlag.getName().equalsIgnoreCase(flag.getAlias())) return testFlag;
        }
        return null;
    }
    
    public BaseFlag getFlag(MineFlag flag, String option) {
        for(BaseFlag testFlag : flags) {
            if(testFlag.getName().equalsIgnoreCase(flag.getAlias()) && testFlag.getOption().equalsIgnoreCase(option)) return testFlag;
        }
        return null;
    }
    
    public boolean hasFlag(MineFlag flag) {
        for(BaseFlag testFlag : flags) {
            if(testFlag.getName().equalsIgnoreCase(flag.getAlias())) return true;
        }
        return false;
    }
    
    public boolean hasFlag(MineFlag flag, String option) {
        for(BaseFlag testFlag : flags) {
            if(testFlag.getName().equalsIgnoreCase(flag.getAlias()) && testFlag.getOption().equalsIgnoreCase(option)) return true;
        }
        return false;
    }
    
    public void addFlag(MineFlag flag, String option) {
        BaseFlag flagObj = flag.dispatch();
        flagObj.setOption(option);
        flags.add(flagObj);
    }
    
    public void addFlag(MineFlag flag) {
        flags.add(flag.dispatch());
    }
    
    public void removeFlag(MineFlag flag, String option) {
        BaseFlag flagToRemove = getFlag(flag, option);
        flags.remove(flagToRemove);
    }
    
    public void removeFlag(MineFlag flag) {
        BaseFlag flagToRemove = getFlag(flag);
        flags.remove(flagToRemove);
    }
    
    public List<Mine> getChildren() {
        List<Mine> children = new ArrayList<Mine>();
        for(Mine mine : PrisonMine.getStaticMines()) {
            if(mine.hasParent() && mine.getParent().equalsIgnoreCase(getId())) { children.add(mine); }
        }
        return children;
    }
    
    public List<String> getBlocksSorted() {
        List<String> finalList = new ArrayList<String>(blocks.size());
        
        MineBlock tempBlock;
        for(int j = blocks.size(); j > 0; j--) {
            for(int i = 0; i < (j - 1); i++) {
                if(blocks.get(i + 1).getChance() > blocks.get(i).getChance()) {
                    tempBlock = blocks.get(i).clone();
                    blocks.set(i, blocks.get(i + 1).clone());
                    blocks.set(i + 1, tempBlock.clone());
                }
                
            }
        }
        
        for(MineBlock block : blocks) {
            String blockName = block.getBlock().getItemType().toString().toLowerCase().replace("_", " ");
            if(block.getBlock().getData() != 0) {
                String[] tempBlockName = {block.getBlock().getItemTypeId() + "", block.getBlock().getData() + ""};
                blockName = Util.parseMetadata(tempBlockName, true) + " " + blockName;
            }
            String blockWeight = Util.formatPercent(block.getChance());
            
            if(!blockWeight.equalsIgnoreCase("0.0%"))
                finalList.add(ChatColor.WHITE + blockWeight + " " + ChatColor.GREEN + blockName + ChatColor.WHITE);
        }
        
        return finalList;
    }
    
    private static Mine getSuperParent(Mine curMine) {
        if(!curMine.hasParent()) return curMine;
        return getSuperParent(get(curMine.getParent()));
    }
    
    public static Mine get(String id) {
        for(Mine curMine : PrisonMine.getStaticMines()) {
            if(curMine.getId().equalsIgnoreCase(id)) return curMine;
        }
        return null;
    }
    
    /**
     * Saves the mine data to file.
     * @return <b>true</b> if the save was successful, <b>false</b> if an error occurred
     */
    public boolean saveFile() {
        File mineFile = new File(new File(PrisonMine.getInstance().getDataFolder(), "mines"), id + ".pmine.yml");
        FileConfiguration mineConf =  YamlConfiguration.loadConfiguration(mineFile);
        mineConf.set("mine", this);
        try {
            mineConf.save(mineFile);
        } catch (IOException e) {
            Message.log(Level.SEVERE, "Unable to serialize mine '" + id + "'!");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * Deletes the mine data file.<br />
     * <b>Warning:</b> invoking this method will not remove the mine from the list of active mines
     * @return <b>true</b> if the deletion was successful, <b>false</b> if an error occurred
     */
    public boolean deleteFile() {
        File mineFolder = new File(PrisonMine.getInstance().getDataFolder(), "mines");
        if(!mineFolder.exists() || !mineFolder.isDirectory()) return false;
        
        File[] mineFiles = mineFolder.listFiles(new FileFilter() {
            public boolean accept(File file) { return file.getName().contains(".pmine.yml"); }
        });
        
        for(File mineFile : mineFiles) {
            if(mineFile.getName().equals(id + ".pmine.yml")) {
                return mineFile.delete();
            }
        }
        
        return false;
    }
}

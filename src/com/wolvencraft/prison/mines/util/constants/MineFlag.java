/*
 * MineFlag.java
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

package com.wolvencraft.prison.mines.util.constants;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.mines.util.flags.*;

@Getter(AccessLevel.PUBLIC)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum MineFlag {
    
    CommandAfterFlag    (CommandAfterFlag.class, "commandafter", true, true, true),
    CommandBeforeFlag   (CommandBeforeFlag.class, "commandbefore", true, true, true),
    MoneyReward         (MoneyRewardFlag.class, "moneyreward", true, false),
    MoneyRewardPlus     (MoneyRewardPlusFlag.class, "moneyrewardpluss", true, false),
    NoExpDrop           (NoExpDropFlag.class, "noexpdrop", false, false),
    NoHungerLoss        (NoHungerLossFlag.class, "nohungerloss", false, false),
    NoPlayerDamage      (NoPlayerDamageFlag.class, "noplayerdamage", false, false),
    NoToolDamage        (NoToolDamageFlag.class, "notooldamage", false, false),
    PlayerEffect        (PlayerEffectFlag.class, "playereffect", true, true),
    ResetSound          (ResetSoundFlag.class, "resetsound", true, false),
    Silent              (SilentFlag.class, "silent", false, false),
    SuperTools          (SuperToolsFlag.class, "supertools", false, false),
    SurfaceOre          (SurfaceOreFlag.class, "surfaceore", true, false),
    ToolReplace         (ToolReplaceFlag.class, "toolreplace", false, false);

    
    @Getter(AccessLevel.NONE) Class<?> clazz;
    
    String alias;
    boolean parameterized;
    boolean multiWord;
    boolean duplicateAware;
    
    MineFlag(Class<?> clazz, String alias, boolean parameterized, boolean duplicateAware) {
        this.clazz = clazz;
        this.alias = alias;
        this.parameterized = parameterized;
        this.multiWord = false;
        this.duplicateAware = duplicateAware;
    }
    
    public BaseFlag dispatch() {
        try{
            return (BaseFlag) clazz.newInstance();
        } catch (InstantiationException e) {
            Message.log(Level.SEVERE, "Error while instantiating a command! InstantiationException");
            return null;
        } catch (IllegalAccessException e) {
            Message.log(Level.SEVERE, "Error while instantiating a command! IllegalAccessException");
            return null;
        }
    }
    
    public boolean isOptionValid (String option) { return this.dispatch().isOptionValid(option); }
    
    public static MineFlag get(String alias) {
        for(MineFlag flag : MineFlag.values()) {
            if(flag.getAlias().equalsIgnoreCase(alias)) return flag;
        }
        return null;
    }
    
    /**
     * Converts the list of Protection constants to a list of their aliases. This method is used during the serialization of Mine objects
     * @param source List of Protection constants
     * @return List of Strings
     */
    public static List<String> toStringList(List<BaseFlag> source) {
        List<String> list = new ArrayList<String>();
        for(BaseFlag flag : source) {
            if(MineFlag.get(flag.getName()).isParameterized()) list.add(flag.getName() + "," + flag.getOption());
            else list.add(flag.getName());
        }
        return list;
    }
    
    /**
     * Converts the list of String protection aliases back to the list of Protection constants. This method is used during the deserialization of Mine objects.
     * @param source List of String protection aliases
     * @return List of Protection constants
     */
    public static List<BaseFlag> toMineFlagList(List<String> source) {
        List<BaseFlag> list = new ArrayList<BaseFlag>();
        for(String string : source) {
            String[] parts = string.split(",");
            if(parts.length == 1) { 
                list.add(get(string).dispatch());
            } else {
                Message.debug("'" + parts[0] + "' :: '" + parts[1] + "'");
                BaseFlag flag = get(parts[0]).dispatch();
                flag.setOption(parts[1]);
                list.add(flag);
            }
        }
        return list;
    }
}

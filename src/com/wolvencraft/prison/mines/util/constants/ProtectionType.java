/*
 * Protection.java
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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the three types of protection that can be enabled for a mine. The types are:<br /><br />
 * <b>PVP</b><br />
 * <b>BLOCK_PLACE</b><br />
 * <b>BLOCK_BREAK</b><br />
 * @author bitWolfy
 *
 */

@Getter(AccessLevel.PUBLIC)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum ProtectionType {
    PVP         ("PVP"),
    BLOCK_PLACE ("BLOCK_PLACE"),
    BLOCK_BREAK ("BLOCK_BREAK");
    
    private String alias;
    
    /**
     * Returns the enum constant based on its alias
     * @param alias Alias to test
     * @return <b>Protection</b>, or <b>null</b> if there isn't one
     */
    public static ProtectionType get(String alias) {
        for(ProtectionType prot : values()) {
            if(prot.getAlias().equalsIgnoreCase(alias)) return prot;
        }
        return null;
    }
    
    /**
     * Converts the list of Protection constants to a list of their aliases. This method is used during the serialization of Mine objects
     * @param source List of Protection constants
     * @return List of Strings
     */
    public static List<String> toStringList(List<ProtectionType> source) {
        List<String> list = new ArrayList<String>();
        for(ProtectionType prot : source) {
            list.add(prot.getAlias());
        }
        return list;
    }
    
    /**
     * Converts the list of String protection aliases back to the list of Protection constants. This method is used during the deserialization of Mine objects.
     * @param source List of String protection aliases
     * @return List of Protection constants
     */
    public static List<ProtectionType> toProtectionList(List<String> source) {
        List<ProtectionType> list = new ArrayList<ProtectionType>();
        for(String string : source) {
            list.add(get(string));
        }
        return list;
    }
}

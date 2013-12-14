/*
 * DrawingTools.java
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

package com.wolvencraft.prison.mines.util;

public enum DrawingTools {
    CornerTopLeft('+'),
    CornerTopRight('+'),
    CornerBottomLeft('+'),
    CornerBottomRight('+'),
    LineHorizontal('-'),
    LineVertical('|'),
    WhiteSpace(' '),
    Color('\u00A7');
    
    DrawingTools(char character) {
        this.character = character;
    }
    
    char character;
    
    @Override
    public String toString() { return character + ""; }
    
    public char toChar() { return character; }
    
    public static String getAllCharacters() {
        String all = "";
        for(DrawingTools tool : DrawingTools.values()) all += tool.toString();
        return all;
    }
    
    public static boolean isPresent(char ch) {
        for(DrawingTools tool : DrawingTools.values()) {
            if(tool.toString().equals(ch)) return true;
        }
        return false;
    }
    
    public static int getTrueLength(String str) {
        boolean skipNext = false;
        int length = 0;
        
        for(char ch : str.toCharArray()) {
            if(skipNext) {
                skipNext = false;
                continue;
            }
            
            if(ch == '&' || ch == DrawingTools.Color.toChar()) {
                skipNext = true;
                continue;
            }
            
            length++;
        }
        return length;
    }
}

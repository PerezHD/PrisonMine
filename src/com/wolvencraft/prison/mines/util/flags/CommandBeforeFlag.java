package com.wolvencraft.prison.mines.util.flags;

public class CommandBeforeFlag implements BaseFlag {
    
    String option;
    
    @Override
    public String getName() {
        return "commandbefore";
    }

    @Override
    public String getOption() {
        return option;
    }

    @Override
    public void setOption(String option) {
        if(option.startsWith("/")) option = option.substring(1);
        this.option = option;
    }

    @Override
    public boolean isOptionValid(String option) {
        return true;
    }

}

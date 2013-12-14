package com.wolvencraft.prison.mines.util.flags;

public class CommandAfterFlag implements BaseFlag {

    String option;
    
    @Override
    public String getName() {
        return "commandafter";
    }

    @Override
    public String getOption() {
        return option;
    }

    @Override
    public void setOption(String option) {
        this.option = option;
    }

    @Override
    public boolean isOptionValid(String option) {
        return true;
    }

}

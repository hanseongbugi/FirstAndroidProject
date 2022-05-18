package org.techtown.blockgame;

public class Data {
    private int backgroundId;
    private String sound;

    public void setBackground(int bgId){
        backgroundId=bgId;
    }
    public void setSound(String sound){
        this.sound=sound;
    }
    public int getBackground(){
        return backgroundId;
    }
    public String getSound(){
        return sound;
    }

}


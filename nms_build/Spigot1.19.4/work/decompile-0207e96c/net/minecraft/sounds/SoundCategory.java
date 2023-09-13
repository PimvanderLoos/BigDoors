package net.minecraft.sounds;

public enum SoundCategory {

    MASTER("master"), MUSIC("music"), RECORDS("record"), WEATHER("weather"), BLOCKS("block"), HOSTILE("hostile"), NEUTRAL("neutral"), PLAYERS("player"), AMBIENT("ambient"), VOICE("voice");

    private final String name;

    private SoundCategory(String s) {
        this.name = s;
    }

    public String getName() {
        return this.name;
    }
}

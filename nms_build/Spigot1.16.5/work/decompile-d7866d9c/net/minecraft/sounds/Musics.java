package net.minecraft.sounds;

public class Musics {

    public static final Music a = new Music(SoundEffects.MUSIC_MENU, 20, 600, true);
    public static final Music b = new Music(SoundEffects.MUSIC_CREATIVE, 12000, 24000, false);
    public static final Music c = new Music(SoundEffects.MUSIC_CREDITS, 0, 0, true);
    public static final Music d = new Music(SoundEffects.MUSIC_DRAGON, 0, 0, true);
    public static final Music e = new Music(SoundEffects.MUSIC_END, 6000, 24000, true);
    public static final Music f = a(SoundEffects.MUSIC_UNDER_WATER);
    public static final Music g = a(SoundEffects.MUSIC_GAME);

    public static Music a(SoundEffect soundeffect) {
        return new Music(soundeffect, 12000, 24000, false);
    }
}

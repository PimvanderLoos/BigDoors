package net.minecraft.tags;

import net.minecraft.core.IRegistry;
import net.minecraft.world.level.gameevent.GameEvent;

public class GameEventTags {

    protected static final TagUtil<GameEvent> HELPER = TagStatic.a(IRegistry.GAME_EVENT_REGISTRY, "tags/game_events");
    public static final Tag.e<GameEvent> VIBRATIONS = a("vibrations");
    public static final Tag.e<GameEvent> IGNORE_VIBRATIONS_SNEAKING = a("ignore_vibrations_sneaking");

    public GameEventTags() {}

    private static Tag.e<GameEvent> a(String s) {
        return GameEventTags.HELPER.a(s);
    }

    public static Tags<GameEvent> a() {
        return GameEventTags.HELPER.b();
    }
}

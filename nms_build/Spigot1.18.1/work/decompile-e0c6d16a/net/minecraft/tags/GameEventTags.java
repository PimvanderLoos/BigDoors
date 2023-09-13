package net.minecraft.tags;

import net.minecraft.core.IRegistry;
import net.minecraft.world.level.gameevent.GameEvent;

public class GameEventTags {

    protected static final TagUtil<GameEvent> HELPER = TagStatic.create(IRegistry.GAME_EVENT_REGISTRY, "tags/game_events");
    public static final Tag.e<GameEvent> VIBRATIONS = bind("vibrations");
    public static final Tag.e<GameEvent> IGNORE_VIBRATIONS_SNEAKING = bind("ignore_vibrations_sneaking");

    public GameEventTags() {}

    private static Tag.e<GameEvent> bind(String s) {
        return GameEventTags.HELPER.bind(s);
    }

    public static Tags<GameEvent> getAllTags() {
        return GameEventTags.HELPER.getAllTags();
    }
}

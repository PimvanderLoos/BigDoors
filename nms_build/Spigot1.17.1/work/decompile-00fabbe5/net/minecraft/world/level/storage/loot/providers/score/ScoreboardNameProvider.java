package net.minecraft.world.level.storage.loot.providers.score;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;

public interface ScoreboardNameProvider {

    @Nullable
    String a(LootTableInfo loottableinfo);

    LootScoreProviderType a();

    Set<LootContextParameter<?>> b();
}

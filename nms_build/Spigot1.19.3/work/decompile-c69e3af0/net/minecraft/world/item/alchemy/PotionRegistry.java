package net.minecraft.world.item.alchemy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.effect.MobEffect;

public class PotionRegistry {

    @Nullable
    private final String name;
    private final ImmutableList<MobEffect> effects;

    public static PotionRegistry byName(String s) {
        return (PotionRegistry) BuiltInRegistries.POTION.get(MinecraftKey.tryParse(s));
    }

    public PotionRegistry(MobEffect... amobeffect) {
        this((String) null, amobeffect);
    }

    public PotionRegistry(@Nullable String s, MobEffect... amobeffect) {
        this.name = s;
        this.effects = ImmutableList.copyOf(amobeffect);
    }

    public String getName(String s) {
        return s + (this.name == null ? BuiltInRegistries.POTION.getKey(this).getPath() : this.name);
    }

    public List<MobEffect> getEffects() {
        return this.effects;
    }

    public boolean hasInstantEffects() {
        if (!this.effects.isEmpty()) {
            UnmodifiableIterator unmodifiableiterator = this.effects.iterator();

            while (unmodifiableiterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) unmodifiableiterator.next();

                if (mobeffect.getEffect().isInstantenous()) {
                    return true;
                }
            }
        }

        return false;
    }
}

package net.minecraft.world.damagesource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DamageType(String msgId, DamageScaling scaling, float exhaustion, DamageEffects effects, DeathMessageType deathMessageType) {

    public static final Codec<DamageType> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.STRING.fieldOf("message_id").forGetter(DamageType::msgId), DamageScaling.CODEC.fieldOf("scaling").forGetter(DamageType::scaling), Codec.FLOAT.fieldOf("exhaustion").forGetter(DamageType::exhaustion), DamageEffects.CODEC.optionalFieldOf("effects", DamageEffects.HURT).forGetter(DamageType::effects), DeathMessageType.CODEC.optionalFieldOf("death_message_type", DeathMessageType.DEFAULT).forGetter(DamageType::deathMessageType)).apply(instance, DamageType::new);
    });

    public DamageType(String s, DamageScaling damagescaling, float f) {
        this(s, damagescaling, f, DamageEffects.HURT, DeathMessageType.DEFAULT);
    }

    public DamageType(String s, DamageScaling damagescaling, float f, DamageEffects damageeffects) {
        this(s, damagescaling, f, damageeffects, DeathMessageType.DEFAULT);
    }

    public DamageType(String s, float f, DamageEffects damageeffects) {
        this(s, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, f, damageeffects);
    }

    public DamageType(String s, float f) {
        this(s, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, f);
    }
}

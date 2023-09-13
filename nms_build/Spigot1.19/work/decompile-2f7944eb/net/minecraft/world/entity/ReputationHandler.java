package net.minecraft.world.entity;

import net.minecraft.world.entity.ai.village.ReputationEvent;

public interface ReputationHandler {

    void onReputationEventFrom(ReputationEvent reputationevent, Entity entity);
}

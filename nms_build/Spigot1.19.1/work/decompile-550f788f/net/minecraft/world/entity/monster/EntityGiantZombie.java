package net.minecraft.world.entity.monster;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;

public class EntityGiantZombie extends EntityMonster {

    public EntityGiantZombie(EntityTypes<? extends EntityGiantZombie> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return 10.440001F;
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.MAX_HEALTH, 100.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.5D).add(GenericAttributes.ATTACK_DAMAGE, 50.0D);
    }

    @Override
    public float getWalkTargetValue(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getPathfindingCostFromLightLevels(blockposition);
    }
}

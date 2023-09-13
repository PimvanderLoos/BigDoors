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
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 10.440001F;
    }

    public static AttributeProvider.Builder m() {
        return EntityMonster.eR().a(GenericAttributes.MAX_HEALTH, 100.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.5D).a(GenericAttributes.ATTACK_DAMAGE, 50.0D);
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.y(blockposition) - 0.5F;
    }
}

package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.entity.animal.EntityBee;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.animal.EntityChicken;
import net.minecraft.world.entity.animal.EntityCow;
import net.minecraft.world.entity.animal.EntityDolphin;
import net.minecraft.world.entity.animal.EntityFish;
import net.minecraft.world.entity.animal.EntityFox;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.animal.EntityOcelot;
import net.minecraft.world.entity.animal.EntityPanda;
import net.minecraft.world.entity.animal.EntityParrot;
import net.minecraft.world.entity.animal.EntityPig;
import net.minecraft.world.entity.animal.EntityPolarBear;
import net.minecraft.world.entity.animal.EntityRabbit;
import net.minecraft.world.entity.animal.EntitySheep;
import net.minecraft.world.entity.animal.EntitySnowman;
import net.minecraft.world.entity.animal.EntitySquid;
import net.minecraft.world.entity.animal.EntityTurtle;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.animal.horse.EntityHorseAbstract;
import net.minecraft.world.entity.animal.horse.EntityHorseChestedAbstract;
import net.minecraft.world.entity.animal.horse.EntityHorseSkeleton;
import net.minecraft.world.entity.animal.horse.EntityHorseZombie;
import net.minecraft.world.entity.animal.horse.EntityLlama;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.monster.EntityBlaze;
import net.minecraft.world.entity.monster.EntityCaveSpider;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.monster.EntityEnderman;
import net.minecraft.world.entity.monster.EntityEndermite;
import net.minecraft.world.entity.monster.EntityEvoker;
import net.minecraft.world.entity.monster.EntityGhast;
import net.minecraft.world.entity.monster.EntityGiantZombie;
import net.minecraft.world.entity.monster.EntityGuardian;
import net.minecraft.world.entity.monster.EntityGuardianElder;
import net.minecraft.world.entity.monster.EntityIllagerIllusioner;
import net.minecraft.world.entity.monster.EntityMagmaCube;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.EntityPigZombie;
import net.minecraft.world.entity.monster.EntityPillager;
import net.minecraft.world.entity.monster.EntityRavager;
import net.minecraft.world.entity.monster.EntityShulker;
import net.minecraft.world.entity.monster.EntitySilverfish;
import net.minecraft.world.entity.monster.EntitySkeletonAbstract;
import net.minecraft.world.entity.monster.EntitySpider;
import net.minecraft.world.entity.monster.EntityStrider;
import net.minecraft.world.entity.monster.EntityVex;
import net.minecraft.world.entity.monster.EntityVindicator;
import net.minecraft.world.entity.monster.EntityWitch;
import net.minecraft.world.entity.monster.EntityZoglin;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglinBrute;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeDefaults {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<EntityTypes<? extends EntityLiving>, AttributeProvider> b = ImmutableMap.builder().put(EntityTypes.ARMOR_STAND, EntityLiving.cL().a()).put(EntityTypes.BAT, EntityBat.m().a()).put(EntityTypes.BEE, EntityBee.eZ().a()).put(EntityTypes.BLAZE, EntityBlaze.m().a()).put(EntityTypes.CAT, EntityCat.fa().a()).put(EntityTypes.CAVE_SPIDER, EntityCaveSpider.m().a()).put(EntityTypes.CHICKEN, EntityChicken.eK().a()).put(EntityTypes.COD, EntityFish.m().a()).put(EntityTypes.COW, EntityCow.eK().a()).put(EntityTypes.CREEPER, EntityCreeper.m().a()).put(EntityTypes.DOLPHIN, EntityDolphin.eM().a()).put(EntityTypes.DONKEY, EntityHorseChestedAbstract.eL().a()).put(EntityTypes.DROWNED, EntityZombie.eS().a()).put(EntityTypes.ELDER_GUARDIAN, EntityGuardianElder.m().a()).put(EntityTypes.ENDERMAN, EntityEnderman.m().a()).put(EntityTypes.ENDERMITE, EntityEndermite.m().a()).put(EntityTypes.ENDER_DRAGON, EntityEnderDragon.m().a()).put(EntityTypes.EVOKER, EntityEvoker.eK().a()).put(EntityTypes.FOX, EntityFox.eK().a()).put(EntityTypes.GHAST, EntityGhast.eJ().a()).put(EntityTypes.GIANT, EntityGiantZombie.m().a()).put(EntityTypes.GUARDIAN, EntityGuardian.eM().a()).put(EntityTypes.HOGLIN, EntityHoglin.eK().a()).put(EntityTypes.HORSE, EntityHorseAbstract.fi().a()).put(EntityTypes.HUSK, EntityZombie.eS().a()).put(EntityTypes.ILLUSIONER, EntityIllagerIllusioner.eK().a()).put(EntityTypes.IRON_GOLEM, EntityIronGolem.m().a()).put(EntityTypes.LLAMA, EntityLlama.fw().a()).put(EntityTypes.MAGMA_CUBE, EntityMagmaCube.m().a()).put(EntityTypes.MOOSHROOM, EntityCow.eK().a()).put(EntityTypes.MULE, EntityHorseChestedAbstract.eL().a()).put(EntityTypes.OCELOT, EntityOcelot.eK().a()).put(EntityTypes.PANDA, EntityPanda.eY().a()).put(EntityTypes.PARROT, EntityParrot.eU().a()).put(EntityTypes.PHANTOM, EntityMonster.eR().a()).put(EntityTypes.PIG, EntityPig.eK().a()).put(EntityTypes.PIGLIN, EntityPiglin.eT().a()).put(EntityTypes.PIGLIN_BRUTE, EntityPiglinBrute.eS().a()).put(EntityTypes.PILLAGER, EntityPillager.eK().a()).put(EntityTypes.PLAYER, EntityHuman.ep().a()).put(EntityTypes.POLAR_BEAR, EntityPolarBear.eK().a()).put(EntityTypes.PUFFERFISH, EntityFish.m().a()).put(EntityTypes.RABBIT, EntityRabbit.eL().a()).put(EntityTypes.RAVAGER, EntityRavager.m().a()).put(EntityTypes.SALMON, EntityFish.m().a()).put(EntityTypes.SHEEP, EntitySheep.eK().a()).put(EntityTypes.SHULKER, EntityShulker.m().a()).put(EntityTypes.SILVERFISH, EntitySilverfish.m().a()).put(EntityTypes.SKELETON, EntitySkeletonAbstract.m().a()).put(EntityTypes.SKELETON_HORSE, EntityHorseSkeleton.eL().a()).put(EntityTypes.SLIME, EntityMonster.eR().a()).put(EntityTypes.SNOW_GOLEM, EntitySnowman.m().a()).put(EntityTypes.SPIDER, EntitySpider.eK().a()).put(EntityTypes.SQUID, EntitySquid.m().a()).put(EntityTypes.STRAY, EntitySkeletonAbstract.m().a()).put(EntityTypes.STRIDER, EntityStrider.eM().a()).put(EntityTypes.TRADER_LLAMA, EntityLlama.fw().a()).put(EntityTypes.TROPICAL_FISH, EntityFish.m().a()).put(EntityTypes.TURTLE, EntityTurtle.eM().a()).put(EntityTypes.VEX, EntityVex.m().a()).put(EntityTypes.VILLAGER, EntityVillager.eY().a()).put(EntityTypes.VINDICATOR, EntityVindicator.eK().a()).put(EntityTypes.WANDERING_TRADER, EntityInsentient.p().a()).put(EntityTypes.WITCH, EntityWitch.eK().a()).put(EntityTypes.WITHER, EntityWither.eK().a()).put(EntityTypes.WITHER_SKELETON, EntitySkeletonAbstract.m().a()).put(EntityTypes.WOLF, EntityWolf.eU().a()).put(EntityTypes.ZOGLIN, EntityZoglin.m().a()).put(EntityTypes.ZOMBIE, EntityZombie.eS().a()).put(EntityTypes.ZOMBIE_HORSE, EntityHorseZombie.eL().a()).put(EntityTypes.ZOMBIE_VILLAGER, EntityZombie.eS().a()).put(EntityTypes.ZOMBIFIED_PIGLIN, EntityPigZombie.eW().a()).build();

    public static AttributeProvider a(EntityTypes<? extends EntityLiving> entitytypes) {
        return (AttributeProvider) AttributeDefaults.b.get(entitytypes);
    }

    public static boolean b(EntityTypes<?> entitytypes) {
        return AttributeDefaults.b.containsKey(entitytypes);
    }

    public static void a() {
        Stream stream = IRegistry.ENTITY_TYPE.g().filter((entitytypes) -> {
            return entitytypes.e() != EnumCreatureType.MISC;
        }).filter((entitytypes) -> {
            return !b(entitytypes);
        });
        RegistryBlocks registryblocks = IRegistry.ENTITY_TYPE;

        registryblocks.getClass();
        stream.map(registryblocks::getKey).forEach((minecraftkey) -> {
            if (SharedConstants.d) {
                throw new IllegalStateException("Entity " + minecraftkey + " has no attributes");
            } else {
                AttributeDefaults.LOGGER.error("Entity {} has no attributes", minecraftkey);
            }
        });
    }
}

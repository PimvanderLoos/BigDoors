package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeDefaults {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<EntityTypes<? extends EntityLiving>, AttributeProvider> b = ImmutableMap.builder().put(EntityTypes.ARMOR_STAND, EntityLiving.cK().a()).put(EntityTypes.BAT, EntityBat.m().a()).put(EntityTypes.BEE, EntityBee.fa().a()).put(EntityTypes.BLAZE, EntityBlaze.m().a()).put(EntityTypes.CAT, EntityCat.fb().a()).put(EntityTypes.CAVE_SPIDER, EntityCaveSpider.m().a()).put(EntityTypes.CHICKEN, EntityChicken.eL().a()).put(EntityTypes.COD, EntityFish.m().a()).put(EntityTypes.COW, EntityCow.eL().a()).put(EntityTypes.CREEPER, EntityCreeper.m().a()).put(EntityTypes.DOLPHIN, EntityDolphin.eN().a()).put(EntityTypes.DONKEY, EntityHorseChestedAbstract.eM().a()).put(EntityTypes.DROWNED, EntityZombie.eT().a()).put(EntityTypes.ELDER_GUARDIAN, EntityGuardianElder.m().a()).put(EntityTypes.ENDERMAN, EntityEnderman.m().a()).put(EntityTypes.ENDERMITE, EntityEndermite.m().a()).put(EntityTypes.ENDER_DRAGON, EntityEnderDragon.m().a()).put(EntityTypes.EVOKER, EntityEvoker.eL().a()).put(EntityTypes.FOX, EntityFox.eL().a()).put(EntityTypes.GHAST, EntityGhast.eK().a()).put(EntityTypes.GIANT, EntityGiantZombie.m().a()).put(EntityTypes.GUARDIAN, EntityGuardian.eN().a()).put(EntityTypes.HOGLIN, EntityHoglin.eL().a()).put(EntityTypes.HORSE, EntityHorseAbstract.fj().a()).put(EntityTypes.HUSK, EntityZombie.eT().a()).put(EntityTypes.ILLUSIONER, EntityIllagerIllusioner.eL().a()).put(EntityTypes.IRON_GOLEM, EntityIronGolem.m().a()).put(EntityTypes.LLAMA, EntityLlama.fx().a()).put(EntityTypes.MAGMA_CUBE, EntityMagmaCube.m().a()).put(EntityTypes.MOOSHROOM, EntityCow.eL().a()).put(EntityTypes.MULE, EntityHorseChestedAbstract.eM().a()).put(EntityTypes.OCELOT, EntityOcelot.eL().a()).put(EntityTypes.PANDA, EntityPanda.eZ().a()).put(EntityTypes.PARROT, EntityParrot.eV().a()).put(EntityTypes.PHANTOM, EntityMonster.eS().a()).put(EntityTypes.PIG, EntityPig.eL().a()).put(EntityTypes.PIGLIN, EntityPiglin.eL().a()).put(EntityTypes.PILLAGER, EntityPillager.eL().a()).put(EntityTypes.PLAYER, EntityHuman.eo().a()).put(EntityTypes.POLAR_BEAR, EntityPolarBear.eL().a()).put(EntityTypes.PUFFERFISH, EntityFish.m().a()).put(EntityTypes.RABBIT, EntityRabbit.eM().a()).put(EntityTypes.RAVAGER, EntityRavager.m().a()).put(EntityTypes.SALMON, EntityFish.m().a()).put(EntityTypes.SHEEP, EntitySheep.eL().a()).put(EntityTypes.SHULKER, EntityShulker.m().a()).put(EntityTypes.SILVERFISH, EntitySilverfish.m().a()).put(EntityTypes.SKELETON, EntitySkeletonAbstract.m().a()).put(EntityTypes.SKELETON_HORSE, EntityHorseSkeleton.eM().a()).put(EntityTypes.SLIME, EntityMonster.eS().a()).put(EntityTypes.SNOW_GOLEM, EntitySnowman.m().a()).put(EntityTypes.SPIDER, EntitySpider.eL().a()).put(EntityTypes.SQUID, EntitySquid.m().a()).put(EntityTypes.STRAY, EntitySkeletonAbstract.m().a()).put(EntityTypes.STRIDER, EntityStrider.eN().a()).put(EntityTypes.TRADER_LLAMA, EntityLlama.fx().a()).put(EntityTypes.TROPICAL_FISH, EntityFish.m().a()).put(EntityTypes.TURTLE, EntityTurtle.eN().a()).put(EntityTypes.VEX, EntityVex.m().a()).put(EntityTypes.VILLAGER, EntityVillager.eX().a()).put(EntityTypes.VINDICATOR, EntityVindicator.eL().a()).put(EntityTypes.WANDERING_TRADER, EntityInsentient.p().a()).put(EntityTypes.WITCH, EntityWitch.eL().a()).put(EntityTypes.WITHER, EntityWither.eL().a()).put(EntityTypes.WITHER_SKELETON, EntitySkeletonAbstract.m().a()).put(EntityTypes.WOLF, EntityWolf.eV().a()).put(EntityTypes.ZOGLIN, EntityZoglin.m().a()).put(EntityTypes.ZOMBIE, EntityZombie.eT().a()).put(EntityTypes.ZOMBIE_HORSE, EntityHorseZombie.eM().a()).put(EntityTypes.ZOMBIE_VILLAGER, EntityZombie.eT().a()).put(EntityTypes.ZOMBIFIED_PIGLIN, EntityPigZombie.eX().a()).build();

    public static AttributeProvider a(EntityTypes<? extends EntityLiving> entitytypes) {
        return (AttributeProvider) AttributeDefaults.b.get(entitytypes);
    }

    public static boolean b(EntityTypes<?> entitytypes) {
        return AttributeDefaults.b.containsKey(entitytypes);
    }

    public static void a() {
        Stream stream = IRegistry.ENTITY_TYPE.e().filter((entitytypes) -> {
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

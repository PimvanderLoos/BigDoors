package net.minecraft.server;

public class DataConverterRegistry {

    private static void a(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterEquipment()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.BLOCK_ENTITY, (IDataConverter) (new DataConverterSignText()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ITEM_INSTANCE, (IDataConverter) (new DataConverterMaterialId()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ITEM_INSTANCE, (IDataConverter) (new DataConverterPotionId()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ITEM_INSTANCE, (IDataConverter) (new DataConverterSpawnEgg()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterMinecart()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.BLOCK_ENTITY, (IDataConverter) (new DataConverterMobSpawner()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterUUID()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterHealth()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterSaddle()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterHanging()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterDropChances()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterRiding()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterArmorStand()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ITEM_INSTANCE, (IDataConverter) (new DataConverterBook()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ITEM_INSTANCE, (IDataConverter) (new DataConverterCookedFish()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterZombie()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.OPTIONS, (IDataConverter) (new DataConverterVBO()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterGuardian()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterSkeleton()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterZombieType()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterHorse()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.BLOCK_ENTITY, (IDataConverter) (new DataConverterTileEntity()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterEntity()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ITEM_INSTANCE, (IDataConverter) (new DataConverterBanner()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ITEM_INSTANCE, (IDataConverter) (new DataConverterPotionWater()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ENTITY, (IDataConverter) (new DataConverterShulker()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ITEM_INSTANCE, (IDataConverter) (new DataConverterShulkerBoxItem()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.BLOCK_ENTITY, (IDataConverter) (new DataConverterShulkerBoxBlock()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.OPTIONS, (IDataConverter) (new DataConverterLang()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ITEM_INSTANCE, (IDataConverter) (new DataConverterTotem()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.CHUNK, (IDataConverter) (new DataConverterBedBlock()));
        dataconvertermanager.a((DataConverterType) DataConverterTypes.ITEM_INSTANCE, (IDataConverter) (new DataConverterBedItem()));
    }

    public static DataConverterManager a() {
        DataConverterManager dataconvertermanager = new DataConverterManager(1343);

        WorldData.a(dataconvertermanager);
        EntityPlayer.a(dataconvertermanager);
        EntityHuman.c(dataconvertermanager);
        ChunkRegionLoader.a(dataconvertermanager);
        ItemStack.a(dataconvertermanager);
        DefinedStructure.a(dataconvertermanager);
        Entity.b(dataconvertermanager);
        EntityArmorStand.a(dataconvertermanager);
        EntityArrow.a(dataconvertermanager);
        EntityBat.a(dataconvertermanager);
        EntityBlaze.a(dataconvertermanager);
        EntityCaveSpider.a(dataconvertermanager);
        EntityChicken.a(dataconvertermanager);
        EntityCow.a(dataconvertermanager);
        EntityCreeper.a(dataconvertermanager);
        EntityHorseDonkey.a(dataconvertermanager);
        EntityDragonFireball.a(dataconvertermanager);
        EntityGuardianElder.a(dataconvertermanager);
        EntityEnderDragon.a(dataconvertermanager);
        EntityEnderman.a(dataconvertermanager);
        EntityEndermite.a(dataconvertermanager);
        EntityEvoker.a(dataconvertermanager);
        EntityFallingBlock.a(dataconvertermanager);
        EntityFireworks.a(dataconvertermanager);
        EntityGhast.a(dataconvertermanager);
        EntityGiantZombie.a(dataconvertermanager);
        EntityGuardian.c(dataconvertermanager);
        EntityHorse.a(dataconvertermanager);
        EntityZombieHusk.a(dataconvertermanager);
        EntityItem.a(dataconvertermanager);
        EntityItemFrame.a(dataconvertermanager);
        EntityLargeFireball.a(dataconvertermanager);
        EntityMagmaCube.a(dataconvertermanager);
        EntityMinecartChest.a(dataconvertermanager);
        EntityMinecartCommandBlock.a(dataconvertermanager);
        EntityMinecartFurnace.a(dataconvertermanager);
        EntityMinecartHopper.a(dataconvertermanager);
        EntityMinecartRideable.a(dataconvertermanager);
        EntityMinecartMobSpawner.a(dataconvertermanager);
        EntityMinecartTNT.a(dataconvertermanager);
        EntityHorseMule.a(dataconvertermanager);
        EntityMushroomCow.c(dataconvertermanager);
        EntityOcelot.a(dataconvertermanager);
        EntityPig.a(dataconvertermanager);
        EntityPigZombie.a(dataconvertermanager);
        EntityRabbit.a(dataconvertermanager);
        EntitySheep.a(dataconvertermanager);
        EntityShulker.a(dataconvertermanager);
        EntitySilverfish.a(dataconvertermanager);
        EntitySkeleton.a(dataconvertermanager);
        EntityHorseSkeleton.a(dataconvertermanager);
        EntitySlime.c(dataconvertermanager);
        EntitySmallFireball.a(dataconvertermanager);
        EntitySnowman.a(dataconvertermanager);
        EntitySnowball.a(dataconvertermanager);
        EntitySpectralArrow.c(dataconvertermanager);
        EntitySpider.c(dataconvertermanager);
        EntitySquid.a(dataconvertermanager);
        EntitySkeletonStray.a(dataconvertermanager);
        EntityEgg.a(dataconvertermanager);
        EntityEnderPearl.a(dataconvertermanager);
        EntityThrownExpBottle.a(dataconvertermanager);
        EntityPotion.a(dataconvertermanager);
        EntityTippedArrow.c(dataconvertermanager);
        EntityVex.a(dataconvertermanager);
        EntityVillager.a(dataconvertermanager);
        EntityIronGolem.a(dataconvertermanager);
        EntityVindicator.a(dataconvertermanager);
        EntityWitch.a(dataconvertermanager);
        EntityWither.a(dataconvertermanager);
        EntitySkeletonWither.a(dataconvertermanager);
        EntityWitherSkull.a(dataconvertermanager);
        EntityWolf.a(dataconvertermanager);
        EntityZombie.c(dataconvertermanager);
        EntityHorseZombie.a(dataconvertermanager);
        EntityZombieVillager.a(dataconvertermanager);
        TileEntityPiston.a(dataconvertermanager);
        TileEntityFlowerPot.a(dataconvertermanager);
        TileEntityFurnace.a(dataconvertermanager);
        TileEntityChest.a(dataconvertermanager);
        TileEntityDispenser.a(dataconvertermanager);
        TileEntityDropper.b(dataconvertermanager);
        TileEntityBrewingStand.a(dataconvertermanager);
        TileEntityHopper.a(dataconvertermanager);
        BlockJukeBox.a(dataconvertermanager);
        TileEntityMobSpawner.a(dataconvertermanager);
        TileEntityShulkerBox.a(dataconvertermanager);
        a(dataconvertermanager);
        return dataconvertermanager;
    }

    public static NBTTagCompound a(DataConverter dataconverter, NBTTagCompound nbttagcompound, int i, String s) {
        if (nbttagcompound.hasKeyOfType(s, 10)) {
            nbttagcompound.set(s, dataconverter.a(DataConverterTypes.ITEM_INSTANCE, nbttagcompound.getCompound(s), i));
        }

        return nbttagcompound;
    }

    public static NBTTagCompound b(DataConverter dataconverter, NBTTagCompound nbttagcompound, int i, String s) {
        if (nbttagcompound.hasKeyOfType(s, 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList(s, 10);

            for (int j = 0; j < nbttaglist.size(); ++j) {
                nbttaglist.a(j, dataconverter.a(DataConverterTypes.ITEM_INSTANCE, nbttaglist.get(j), i));
            }
        }

        return nbttagcompound;
    }
}

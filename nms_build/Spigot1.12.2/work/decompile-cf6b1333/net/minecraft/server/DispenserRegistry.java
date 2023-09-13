package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import java.io.File;
import java.io.PrintStream;
import java.util.Random;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DispenserRegistry {

    public static final PrintStream a = System.out;
    private static boolean c;
    public static boolean b;
    private static final Logger d = LogManager.getLogger();

    public static boolean a() {
        return DispenserRegistry.c;
    }

    static void b() {
        BlockDispenser.REGISTRY.a(Items.ARROW, new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                EntityTippedArrow entitytippedarrow = new EntityTippedArrow(world, iposition.getX(), iposition.getY(), iposition.getZ());

                entitytippedarrow.fromPlayer = EntityArrow.PickupStatus.ALLOWED;
                return entitytippedarrow;
            }
        });
        BlockDispenser.REGISTRY.a(Items.TIPPED_ARROW, new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                EntityTippedArrow entitytippedarrow = new EntityTippedArrow(world, iposition.getX(), iposition.getY(), iposition.getZ());

                entitytippedarrow.a(itemstack);
                entitytippedarrow.fromPlayer = EntityArrow.PickupStatus.ALLOWED;
                return entitytippedarrow;
            }
        });
        BlockDispenser.REGISTRY.a(Items.SPECTRAL_ARROW, new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                EntitySpectralArrow entityspectralarrow = new EntitySpectralArrow(world, iposition.getX(), iposition.getY(), iposition.getZ());

                entityspectralarrow.fromPlayer = EntityArrow.PickupStatus.ALLOWED;
                return entityspectralarrow;
            }
        });
        BlockDispenser.REGISTRY.a(Items.EGG, new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                return new EntityEgg(world, iposition.getX(), iposition.getY(), iposition.getZ());
            }
        });
        BlockDispenser.REGISTRY.a(Items.SNOWBALL, new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                return new EntitySnowball(world, iposition.getX(), iposition.getY(), iposition.getZ());
            }
        });
        BlockDispenser.REGISTRY.a(Items.EXPERIENCE_BOTTLE, new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                return new EntityThrownExpBottle(world, iposition.getX(), iposition.getY(), iposition.getZ());
            }

            protected float a() {
                return super.a() * 0.5F;
            }

            protected float getPower() {
                return super.getPower() * 1.25F;
            }
        });
        BlockDispenser.REGISTRY.a(Items.SPLASH_POTION, new IDispenseBehavior() {
            public ItemStack a(ISourceBlock isourceblock, final ItemStack itemstack) {
                return (new DispenseBehaviorProjectile() {
                    protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                        return new EntityPotion(world, iposition.getX(), iposition.getY(), iposition.getZ(), itemstack1.cloneItemStack());
                    }

                    protected float a() {
                        return super.a() * 0.5F;
                    }

                    protected float getPower() {
                        return super.getPower() * 1.25F;
                    }
                }).a(isourceblock, itemstack);
            }
        });
        BlockDispenser.REGISTRY.a(Items.LINGERING_POTION, new IDispenseBehavior() {
            public ItemStack a(ISourceBlock isourceblock, final ItemStack itemstack) {
                return (new DispenseBehaviorProjectile() {
                    protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                        return new EntityPotion(world, iposition.getX(), iposition.getY(), iposition.getZ(), itemstack1.cloneItemStack());
                    }

                    protected float a() {
                        return super.a() * 0.5F;
                    }

                    protected float getPower() {
                        return super.getPower() * 1.25F;
                    }
                }).a(isourceblock, itemstack);
            }
        });
        BlockDispenser.REGISTRY.a(Items.SPAWN_EGG, new DispenseBehaviorItem() {
            public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
                double d0 = isourceblock.getX() + (double) enumdirection.getAdjacentX();
                double d1 = (double) ((float) (isourceblock.getBlockPosition().getY() + enumdirection.getAdjacentY()) + 0.2F);
                double d2 = isourceblock.getZ() + (double) enumdirection.getAdjacentZ();
                Entity entity = ItemMonsterEgg.a(isourceblock.getWorld(), ItemMonsterEgg.h(itemstack), d0, d1, d2);

                if (entity instanceof EntityLiving && itemstack.hasName()) {
                    entity.setCustomName(itemstack.getName());
                }

                ItemMonsterEgg.a(isourceblock.getWorld(), (EntityHuman) null, itemstack, entity);
                itemstack.subtract(1);
                return itemstack;
            }
        });
        BlockDispenser.REGISTRY.a(Items.FIREWORKS, new DispenseBehaviorItem() {
            public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
                double d0 = isourceblock.getX() + (double) enumdirection.getAdjacentX();
                double d1 = (double) ((float) isourceblock.getBlockPosition().getY() + 0.2F);
                double d2 = isourceblock.getZ() + (double) enumdirection.getAdjacentZ();
                EntityFireworks entityfireworks = new EntityFireworks(isourceblock.getWorld(), d0, d1, d2, itemstack);

                isourceblock.getWorld().addEntity(entityfireworks);
                itemstack.subtract(1);
                return itemstack;
            }

            protected void a(ISourceBlock isourceblock) {
                isourceblock.getWorld().triggerEffect(1004, isourceblock.getBlockPosition(), 0);
            }
        });
        BlockDispenser.REGISTRY.a(Items.FIRE_CHARGE, new DispenseBehaviorItem() {
            public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
                IPosition iposition = BlockDispenser.a(isourceblock);
                double d0 = iposition.getX() + (double) ((float) enumdirection.getAdjacentX() * 0.3F);
                double d1 = iposition.getY() + (double) ((float) enumdirection.getAdjacentY() * 0.3F);
                double d2 = iposition.getZ() + (double) ((float) enumdirection.getAdjacentZ() * 0.3F);
                World world = isourceblock.getWorld();
                Random random = world.random;
                double d3 = random.nextGaussian() * 0.05D + (double) enumdirection.getAdjacentX();
                double d4 = random.nextGaussian() * 0.05D + (double) enumdirection.getAdjacentY();
                double d5 = random.nextGaussian() * 0.05D + (double) enumdirection.getAdjacentZ();

                world.addEntity(new EntitySmallFireball(world, d0, d1, d2, d3, d4, d5));
                itemstack.subtract(1);
                return itemstack;
            }

            protected void a(ISourceBlock isourceblock) {
                isourceblock.getWorld().triggerEffect(1018, isourceblock.getBlockPosition(), 0);
            }
        });
        BlockDispenser.REGISTRY.a(Items.aH, new DispenserRegistry.a(EntityBoat.EnumBoatType.OAK));
        BlockDispenser.REGISTRY.a(Items.aI, new DispenserRegistry.a(EntityBoat.EnumBoatType.SPRUCE));
        BlockDispenser.REGISTRY.a(Items.aJ, new DispenserRegistry.a(EntityBoat.EnumBoatType.BIRCH));
        BlockDispenser.REGISTRY.a(Items.aK, new DispenserRegistry.a(EntityBoat.EnumBoatType.JUNGLE));
        BlockDispenser.REGISTRY.a(Items.aM, new DispenserRegistry.a(EntityBoat.EnumBoatType.DARK_OAK));
        BlockDispenser.REGISTRY.a(Items.aL, new DispenserRegistry.a(EntityBoat.EnumBoatType.ACACIA));
        DispenseBehaviorItem dispensebehavioritem = new DispenseBehaviorItem() {
            private final DispenseBehaviorItem b = new DispenseBehaviorItem();

            public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                ItemBucket itembucket = (ItemBucket) itemstack.getItem();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));

                return itembucket.a((EntityHuman) null, isourceblock.getWorld(), blockposition) ? new ItemStack(Items.BUCKET) : this.b.a(isourceblock, itemstack);
            }
        };

        BlockDispenser.REGISTRY.a(Items.LAVA_BUCKET, dispensebehavioritem);
        BlockDispenser.REGISTRY.a(Items.WATER_BUCKET, dispensebehavioritem);
        BlockDispenser.REGISTRY.a(Items.BUCKET, new DispenseBehaviorItem() {
            private final DispenseBehaviorItem b = new DispenseBehaviorItem();

            public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));
                IBlockData iblockdata = world.getType(blockposition);
                Block block = iblockdata.getBlock();
                Material material = iblockdata.getMaterial();
                Item item;

                if (Material.WATER.equals(material) && block instanceof BlockFluids && ((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue() == 0) {
                    item = Items.WATER_BUCKET;
                } else {
                    if (!Material.LAVA.equals(material) || !(block instanceof BlockFluids) || ((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue() != 0) {
                        return super.b(isourceblock, itemstack);
                    }

                    item = Items.LAVA_BUCKET;
                }

                world.setAir(blockposition);
                itemstack.subtract(1);
                if (itemstack.isEmpty()) {
                    return new ItemStack(item);
                } else {
                    if (((TileEntityDispenser) isourceblock.getTileEntity()).addItem(new ItemStack(item)) < 0) {
                        this.b.a(isourceblock, new ItemStack(item));
                    }

                    return itemstack;
                }
            }
        });
        BlockDispenser.REGISTRY.a(Items.FLINT_AND_STEEL, new DispenserRegistry.b() {
            protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();

                this.b = true;
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));

                if (world.isEmpty(blockposition)) {
                    world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
                    if (itemstack.isDamaged(1, world.random, (EntityPlayer) null)) {
                        itemstack.setCount(0);
                    }
                } else if (world.getType(blockposition).getBlock() == Blocks.TNT) {
                    Blocks.TNT.postBreak(world, blockposition, Blocks.TNT.getBlockData().set(BlockTNT.EXPLODE, Boolean.valueOf(true)));
                    world.setAir(blockposition);
                } else {
                    this.b = false;
                }

                return itemstack;
            }
        });
        BlockDispenser.REGISTRY.a(Items.DYE, new DispenserRegistry.b() {
            protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                this.b = true;
                if (EnumColor.WHITE == EnumColor.fromInvColorIndex(itemstack.getData())) {
                    World world = isourceblock.getWorld();
                    BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));

                    if (ItemDye.a(itemstack, world, blockposition)) {
                        if (!world.isClientSide) {
                            world.triggerEffect(2005, blockposition, 0);
                        }
                    } else {
                        this.b = false;
                    }

                    return itemstack;
                } else {
                    return super.b(isourceblock, itemstack);
                }
            }
        });
        BlockDispenser.REGISTRY.a(Item.getItemOf(Blocks.TNT), new DispenseBehaviorItem() {
            protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));
                EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, (EntityLiving) null);

                world.addEntity(entitytntprimed);
                world.a((EntityHuman) null, entitytntprimed.locX, entitytntprimed.locY, entitytntprimed.locZ, SoundEffects.hW, SoundCategory.BLOCKS, 1.0F, 1.0F);
                itemstack.subtract(1);
                return itemstack;
            }
        });
        BlockDispenser.REGISTRY.a(Items.SKULL, new DispenserRegistry.b() {
            protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
                BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);
                BlockSkull blockskull = Blocks.SKULL;

                this.b = true;
                if (world.isEmpty(blockposition) && blockskull.b(world, blockposition, itemstack)) {
                    if (!world.isClientSide) {
                        world.setTypeAndData(blockposition, blockskull.getBlockData().set(BlockSkull.FACING, EnumDirection.UP), 3);
                        TileEntity tileentity = world.getTileEntity(blockposition);

                        if (tileentity instanceof TileEntitySkull) {
                            if (itemstack.getData() == 3) {
                                GameProfile gameprofile = null;

                                if (itemstack.hasTag()) {
                                    NBTTagCompound nbttagcompound = itemstack.getTag();

                                    if (nbttagcompound.hasKeyOfType("SkullOwner", 10)) {
                                        gameprofile = GameProfileSerializer.deserialize(nbttagcompound.getCompound("SkullOwner"));
                                    } else if (nbttagcompound.hasKeyOfType("SkullOwner", 8)) {
                                        String s = nbttagcompound.getString("SkullOwner");

                                        if (!UtilColor.b(s)) {
                                            gameprofile = new GameProfile((UUID) null, s);
                                        }
                                    }
                                }

                                ((TileEntitySkull) tileentity).setGameProfile(gameprofile);
                            } else {
                                ((TileEntitySkull) tileentity).setSkullType(itemstack.getData());
                            }

                            ((TileEntitySkull) tileentity).setRotation(enumdirection.opposite().get2DRotationValue() * 4);
                            Blocks.SKULL.a(world, blockposition, (TileEntitySkull) tileentity);
                        }

                        itemstack.subtract(1);
                    }
                } else if (ItemArmor.a(isourceblock, itemstack).isEmpty()) {
                    this.b = false;
                }

                return itemstack;
            }
        });
        BlockDispenser.REGISTRY.a(Item.getItemOf(Blocks.PUMPKIN), new DispenserRegistry.b() {
            protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));
                BlockPumpkin blockpumpkin = (BlockPumpkin) Blocks.PUMPKIN;

                this.b = true;
                if (world.isEmpty(blockposition) && blockpumpkin.b(world, blockposition)) {
                    if (!world.isClientSide) {
                        world.setTypeAndData(blockposition, blockpumpkin.getBlockData(), 3);
                    }

                    itemstack.subtract(1);
                } else {
                    ItemStack itemstack1 = ItemArmor.a(isourceblock, itemstack);

                    if (itemstack1.isEmpty()) {
                        this.b = false;
                    }
                }

                return itemstack;
            }
        });
        EnumColor[] aenumcolor = EnumColor.values();
        int i = aenumcolor.length;

        for (int j = 0; j < i; ++j) {
            EnumColor enumcolor = aenumcolor[j];

            BlockDispenser.REGISTRY.a(Item.getItemOf(BlockShulkerBox.a(enumcolor)), new DispenserRegistry.c(null));
        }

    }

    public static void c() {
        if (!DispenserRegistry.c) {
            DispenserRegistry.c = true;
            d();
            SoundEffect.b();
            Block.w();
            BlockFire.e();
            MobEffectList.k();
            Enchantment.g();
            Item.t();
            PotionRegistry.b();
            PotionBrewer.a();
            EntityTypes.c();
            BiomeBase.q();
            b();
            if (!CraftingManager.init()) {
                DispenserRegistry.b = true;
                DispenserRegistry.d.error("Errors with built-in recipes!");
            }

            StatisticList.a();
            if (DispenserRegistry.d.isDebugEnabled()) {
                if ((new AdvancementDataWorld((File) null)).b()) {
                    DispenserRegistry.b = true;
                    DispenserRegistry.d.error("Errors with built-in advancements!");
                }

                if (!LootTables.b()) {
                    DispenserRegistry.b = true;
                    DispenserRegistry.d.error("Errors with built-in loot tables");
                }
            }

        }
    }

    private static void d() {
        if (DispenserRegistry.d.isDebugEnabled()) {
            System.setErr(new DebugOutputStream("STDERR", System.err));
            System.setOut(new DebugOutputStream("STDOUT", DispenserRegistry.a));
        } else {
            System.setErr(new RedirectStream("STDERR", System.err));
            System.setOut(new RedirectStream("STDOUT", DispenserRegistry.a));
        }

    }

    static class c extends DispenserRegistry.b {

        private c() {}

        protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
            Block block = Block.asBlock(itemstack.getItem());
            World world = isourceblock.getWorld();
            EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
            BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);

            this.b = world.a(block, blockposition, false, EnumDirection.DOWN, (Entity) null);
            if (this.b) {
                EnumDirection enumdirection1 = world.isEmpty(blockposition.down()) ? enumdirection : EnumDirection.UP;
                IBlockData iblockdata = block.getBlockData().set(BlockShulkerBox.a, enumdirection1);

                world.setTypeUpdate(blockposition, iblockdata);
                TileEntity tileentity = world.getTileEntity(blockposition);
                ItemStack itemstack1 = itemstack.cloneAndSubtract(1);

                if (itemstack1.hasTag()) {
                    ((TileEntityShulkerBox) tileentity).e(itemstack1.getTag().getCompound("BlockEntityTag"));
                }

                if (itemstack1.hasName()) {
                    ((TileEntityShulkerBox) tileentity).setCustomName(itemstack1.getName());
                }

                world.updateAdjacentComparators(blockposition, iblockdata.getBlock());
            }

            return itemstack;
        }

        c(Object object) {
            this();
        }
    }

    public abstract static class b extends DispenseBehaviorItem {

        protected boolean b = true;

        public b() {}

        protected void a(ISourceBlock isourceblock) {
            isourceblock.getWorld().triggerEffect(this.b ? 1000 : 1001, isourceblock.getBlockPosition(), 0);
        }
    }

    public static class a extends DispenseBehaviorItem {

        private final DispenseBehaviorItem b = new DispenseBehaviorItem();
        private final EntityBoat.EnumBoatType c;

        public a(EntityBoat.EnumBoatType entityboat_enumboattype) {
            this.c = entityboat_enumboattype;
        }

        public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
            EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
            World world = isourceblock.getWorld();
            double d0 = isourceblock.getX() + (double) ((float) enumdirection.getAdjacentX() * 1.125F);
            double d1 = isourceblock.getY() + (double) ((float) enumdirection.getAdjacentY() * 1.125F);
            double d2 = isourceblock.getZ() + (double) ((float) enumdirection.getAdjacentZ() * 1.125F);
            BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);
            Material material = world.getType(blockposition).getMaterial();
            double d3;

            if (Material.WATER.equals(material)) {
                d3 = 1.0D;
            } else {
                if (!Material.AIR.equals(material) || !Material.WATER.equals(world.getType(blockposition.down()).getMaterial())) {
                    return this.b.a(isourceblock, itemstack);
                }

                d3 = 0.0D;
            }

            EntityBoat entityboat = new EntityBoat(world, d0, d1 + d3, d2);

            entityboat.setType(this.c);
            entityboat.yaw = enumdirection.l();
            world.addEntity(entityboat);
            itemstack.subtract(1);
            return itemstack;
        }

        protected void a(ISourceBlock isourceblock) {
            isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
        }
    }
}

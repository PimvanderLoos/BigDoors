package net.minecraft.server;

public class PathfinderGoalVillagerFarm extends PathfinderGoalGotoTarget {

    private final EntityVillager c;
    private boolean d;
    private boolean e;
    private int f;

    public PathfinderGoalVillagerFarm(EntityVillager entityvillager, double d0) {
        super(entityvillager, d0, 16);
        this.c = entityvillager;
    }

    public boolean a() {
        if (this.a <= 0) {
            if (!this.c.world.getGameRules().getBoolean("mobGriefing")) {
                return false;
            }

            this.f = -1;
            this.d = this.c.du();
            this.e = this.c.dt();
        }

        return super.a();
    }

    public boolean b() {
        return this.f >= 0 && super.b();
    }

    public void e() {
        super.e();
        this.c.getControllerLook().a((double) this.b.getX() + 0.5D, (double) (this.b.getY() + 1), (double) this.b.getZ() + 0.5D, 10.0F, (float) this.c.N());
        if (this.f()) {
            World world = this.c.world;
            BlockPosition blockposition = this.b.up();
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (this.f == 0 && block instanceof BlockCrops && ((BlockCrops) block).z(iblockdata)) {
                world.setAir(blockposition, true);
            } else if (this.f == 1 && iblockdata.getMaterial() == Material.AIR) {
                InventorySubcontainer inventorysubcontainer = this.c.dq();

                for (int i = 0; i < inventorysubcontainer.getSize(); ++i) {
                    ItemStack itemstack = inventorysubcontainer.getItem(i);
                    boolean flag = false;

                    if (!itemstack.isEmpty()) {
                        if (itemstack.getItem() == Items.WHEAT_SEEDS) {
                            world.setTypeAndData(blockposition, Blocks.WHEAT.getBlockData(), 3);
                            flag = true;
                        } else if (itemstack.getItem() == Items.POTATO) {
                            world.setTypeAndData(blockposition, Blocks.POTATOES.getBlockData(), 3);
                            flag = true;
                        } else if (itemstack.getItem() == Items.CARROT) {
                            world.setTypeAndData(blockposition, Blocks.CARROTS.getBlockData(), 3);
                            flag = true;
                        } else if (itemstack.getItem() == Items.BEETROOT_SEEDS) {
                            world.setTypeAndData(blockposition, Blocks.BEETROOT.getBlockData(), 3);
                            flag = true;
                        }
                    }

                    if (flag) {
                        itemstack.subtract(1);
                        if (itemstack.isEmpty()) {
                            inventorysubcontainer.setItem(i, ItemStack.a);
                        }
                        break;
                    }
                }
            }

            this.f = -1;
            this.a = 10;
        }

    }

    protected boolean a(World world, BlockPosition blockposition) {
        Block block = world.getType(blockposition).getBlock();

        if (block == Blocks.FARMLAND) {
            blockposition = blockposition.up();
            IBlockData iblockdata = world.getType(blockposition);

            block = iblockdata.getBlock();
            if (block instanceof BlockCrops && ((BlockCrops) block).z(iblockdata) && this.e && (this.f == 0 || this.f < 0)) {
                this.f = 0;
                return true;
            }

            if (iblockdata.getMaterial() == Material.AIR && this.d && (this.f == 1 || this.f < 0)) {
                this.f = 1;
                return true;
            }
        }

        return false;
    }
}

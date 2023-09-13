package net.minecraft.server;

public class EntityMinecartChest extends EntityMinecartContainer {

    public EntityMinecartChest(World world) {
        super(EntityTypes.CHEST_MINECART, world);
    }

    public EntityMinecartChest(World world, double d0, double d1, double d2) {
        super(EntityTypes.CHEST_MINECART, d0, d1, d2, world);
    }

    public void a(DamageSource damagesource) {
        super.a(damagesource);
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.a((IMaterial) Blocks.CHEST);
        }

    }

    public int getSize() {
        return 27;
    }

    public EntityMinecartAbstract.EnumMinecartType v() {
        return EntityMinecartAbstract.EnumMinecartType.CHEST;
    }

    public IBlockData z() {
        return (IBlockData) Blocks.CHEST.getBlockData().set(BlockChest.FACING, EnumDirection.NORTH);
    }

    public int B() {
        return 8;
    }

    public String getContainerName() {
        return "minecraft:chest";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        this.f(entityhuman);
        return new ContainerChest(playerinventory, this, entityhuman);
    }
}

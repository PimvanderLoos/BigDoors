package net.minecraft.server;

public class EntityLlamaSpit extends IProjectile {

    public EntityLlamaSpit(EntityTypes<? extends EntityLlamaSpit> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityLlamaSpit(World world, EntityLlama entityllama) {
        this(EntityTypes.LLAMA_SPIT, world);
        super.setShooter(entityllama);
        this.setPosition(entityllama.locX() - (double) (entityllama.getWidth() + 1.0F) * 0.5D * (double) MathHelper.sin(entityllama.aH * 0.017453292F), entityllama.getHeadY() - 0.10000000149011612D, entityllama.locZ() + (double) (entityllama.getWidth() + 1.0F) * 0.5D * (double) MathHelper.cos(entityllama.aH * 0.017453292F));
    }

    @Override
    public void tick() {
        super.tick();
        Vec3D vec3d = this.getMot();
        MovingObjectPosition movingobjectposition = ProjectileHelper.a(this, this::a, RayTrace.BlockCollisionOption.OUTLINE);

        if (movingobjectposition != null) {
            this.a(movingobjectposition);
        }

        double d0 = this.locX() + vec3d.x;
        double d1 = this.locY() + vec3d.y;
        double d2 = this.locZ() + vec3d.z;

        this.x();
        float f = 0.99F;
        float f1 = 0.06F;

        if (this.world.a(this.getBoundingBox()).noneMatch(BlockBase.BlockData::isAir)) {
            this.die();
        } else if (this.aD()) {
            this.die();
        } else {
            this.setMot(vec3d.a(0.9900000095367432D));
            if (!this.isNoGravity()) {
                this.setMot(this.getMot().add(0.0D, -0.05999999865889549D, 0.0D));
            }

            this.setPosition(d0, d1, d2);
        }
    }

    @Override
    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        super.a(movingobjectpositionentity);
        Entity entity = this.getShooter();

        if (entity instanceof EntityLiving) {
            movingobjectpositionentity.getEntity().damageEntity(DamageSource.a((Entity) this, (EntityLiving) entity).c(), 1.0F);
        }

    }

    @Override
    protected void a(MovingObjectPositionBlock movingobjectpositionblock) {
        super.a(movingobjectpositionblock);
        if (!this.world.isClientSide) {
            this.die();
        }

    }

    @Override
    protected void initDatawatcher() {}

    @Override
    public Packet<?> O() {
        return new PacketPlayOutSpawnEntity(this);
    }
}

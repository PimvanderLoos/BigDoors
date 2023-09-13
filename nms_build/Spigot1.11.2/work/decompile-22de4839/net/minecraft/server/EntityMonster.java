package net.minecraft.server;

public abstract class EntityMonster extends EntityCreature implements IMonster {

    public EntityMonster(World world) {
        super(world);
        this.b_ = 5;
    }

    public SoundCategory bC() {
        return SoundCategory.HOSTILE;
    }

    public void n() {
        this.cd();
        float f = this.e(1.0F);

        if (f > 0.5F) {
            this.ticksFarFromPlayer += 2;
        }

        super.n();
    }

    public void A_() {
        super.A_();
        if (!this.world.isClientSide && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.die();
        }

    }

    protected SoundEffect aa() {
        return SoundEffects.cR;
    }

    protected SoundEffect ab() {
        return SoundEffects.cQ;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        return this.isInvulnerable(damagesource) ? false : super.damageEntity(damagesource, f);
    }

    protected SoundEffect bW() {
        return SoundEffects.cO;
    }

    protected SoundEffect bX() {
        return SoundEffects.cN;
    }

    protected SoundEffect e(int i) {
        return i > 4 ? SoundEffects.cM : SoundEffects.cP;
    }

    public boolean B(Entity entity) {
        float f = (float) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
        int i = 0;

        if (entity instanceof EntityLiving) {
            f += EnchantmentManager.a(this.getItemInMainHand(), ((EntityLiving) entity).getMonsterType());
            i += EnchantmentManager.b((EntityLiving) this);
        }

        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), f);

        if (flag) {
            if (i > 0 && entity instanceof EntityLiving) {
                ((EntityLiving) entity).a(this, (float) i * 0.5F, (double) MathHelper.sin(this.yaw * 0.017453292F), (double) (-MathHelper.cos(this.yaw * 0.017453292F)));
                this.motX *= 0.6D;
                this.motZ *= 0.6D;
            }

            int j = EnchantmentManager.getFireAspectEnchantmentLevel(this);

            if (j > 0) {
                entity.setOnFire(j * 4);
            }

            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;
                ItemStack itemstack = this.getItemInMainHand();
                ItemStack itemstack1 = entityhuman.isHandRaised() ? entityhuman.cB() : ItemStack.a;

                if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem() instanceof ItemAxe && itemstack1.getItem() == Items.SHIELD) {
                    float f1 = 0.25F + (float) EnchantmentManager.getDigSpeedEnchantmentLevel(this) * 0.05F;

                    if (this.random.nextFloat() < f1) {
                        entityhuman.di().a(Items.SHIELD, 100);
                        this.world.broadcastEntityEffect(entityhuman, (byte) 30);
                    }
                }
            }

            this.a((EntityLiving) this, entity);
        }

        return flag;
    }

    public float a(BlockPosition blockposition) {
        return 0.5F - this.world.n(blockposition);
    }

    protected boolean r_() {
        BlockPosition blockposition = new BlockPosition(this.locX, this.getBoundingBox().b, this.locZ);

        if (this.world.getBrightness(EnumSkyBlock.SKY, blockposition) > this.random.nextInt(32)) {
            return false;
        } else {
            int i = this.world.getLightLevel(blockposition);

            if (this.world.V()) {
                int j = this.world.af();

                this.world.c(10);
                i = this.world.getLightLevel(blockposition);
                this.world.c(j);
            }

            return i <= this.random.nextInt(8);
        }
    }

    public boolean cM() {
        return this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.r_() && super.cM();
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
    }

    protected boolean isDropExperience() {
        return true;
    }
}

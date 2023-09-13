package net.minecraft.server;

public abstract class EntityMonster extends EntityCreature implements IMonster {

    public EntityMonster(World world) {
        super(world);
        this.b_ = 5;
    }

    public SoundCategory bK() {
        return SoundCategory.HOSTILE;
    }

    public void n() {
        this.cl();
        float f = this.aw();

        if (f > 0.5F) {
            this.ticksFarFromPlayer += 2;
        }

        super.n();
    }

    public void B_() {
        super.B_();
        if (!this.world.isClientSide && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.die();
        }

    }

    protected SoundEffect ae() {
        return SoundEffects.cX;
    }

    protected SoundEffect af() {
        return SoundEffects.cW;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        return this.isInvulnerable(damagesource) ? false : super.damageEntity(damagesource, f);
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.cU;
    }

    protected SoundEffect cf() {
        return SoundEffects.cT;
    }

    protected SoundEffect e(int i) {
        return i > 4 ? SoundEffects.cS : SoundEffects.cV;
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
                ItemStack itemstack1 = entityhuman.isHandRaised() ? entityhuman.cJ() : ItemStack.a;

                if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem() instanceof ItemAxe && itemstack1.getItem() == Items.SHIELD) {
                    float f1 = 0.25F + (float) EnchantmentManager.getDigSpeedEnchantmentLevel(this) * 0.05F;

                    if (this.random.nextFloat() < f1) {
                        entityhuman.getCooldownTracker().a(Items.SHIELD, 100);
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

    protected boolean s_() {
        BlockPosition blockposition = new BlockPosition(this.locX, this.getBoundingBox().b, this.locZ);

        if (this.world.getBrightness(EnumSkyBlock.SKY, blockposition) > this.random.nextInt(32)) {
            return false;
        } else {
            int i = this.world.getLightLevel(blockposition);

            if (this.world.X()) {
                int j = this.world.ah();

                this.world.c(10);
                i = this.world.getLightLevel(blockposition);
                this.world.c(j);
            }

            return i <= this.random.nextInt(8);
        }
    }

    public boolean P() {
        return this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.s_() && super.P();
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
    }

    protected boolean isDropExperience() {
        return true;
    }

    public boolean c(EntityHuman entityhuman) {
        return true;
    }
}

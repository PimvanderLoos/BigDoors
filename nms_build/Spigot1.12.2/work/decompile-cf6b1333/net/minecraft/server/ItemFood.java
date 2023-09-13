package net.minecraft.server;

public class ItemFood extends Item {

    public final int a;
    private final int b;
    private final float c;
    private final boolean d;
    private boolean e;
    private MobEffect f;
    private float n;

    public ItemFood(int i, float f, boolean flag) {
        this.a = 32;
        this.b = i;
        this.d = flag;
        this.c = f;
        this.b(CreativeModeTab.h);
    }

    public ItemFood(int i, boolean flag) {
        this(i, 0.6F, flag);
    }

    public ItemStack a(ItemStack itemstack, World world, EntityLiving entityliving) {
        if (entityliving instanceof EntityHuman) {
            EntityHuman entityhuman = (EntityHuman) entityliving;

            entityhuman.getFoodData().a(this, itemstack);
            world.a((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.fD, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            this.a(itemstack, world, entityhuman);
            entityhuman.b(StatisticList.b((Item) this));
            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.y.a((EntityPlayer) entityhuman, itemstack);
            }
        }

        itemstack.subtract(1);
        return itemstack;
    }

    protected void a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (!world.isClientSide && this.f != null && world.random.nextFloat() < this.n) {
            entityhuman.addEffect(new MobEffect(this.f));
        }

    }

    public int e(ItemStack itemstack) {
        return 32;
    }

    public EnumAnimation f(ItemStack itemstack) {
        return EnumAnimation.EAT;
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (entityhuman.n(this.e)) {
            entityhuman.c(enumhand);
            return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
        } else {
            return new InteractionResultWrapper(EnumInteractionResult.FAIL, itemstack);
        }
    }

    public int getNutrition(ItemStack itemstack) {
        return this.b;
    }

    public float getSaturationModifier(ItemStack itemstack) {
        return this.c;
    }

    public boolean g() {
        return this.d;
    }

    public ItemFood a(MobEffect mobeffect, float f) {
        this.f = mobeffect;
        this.n = f;
        return this;
    }

    public ItemFood h() {
        this.e = true;
        return this;
    }
}

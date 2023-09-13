package net.minecraft.world.level.material;

public final class Material {

    public static final Material AIR = (new Material.a(MaterialMapColor.NONE)).noCollider().notSolidBlocking().nonSolid().replaceable().build();
    public static final Material STRUCTURAL_AIR = (new Material.a(MaterialMapColor.NONE)).noCollider().notSolidBlocking().nonSolid().replaceable().build();
    public static final Material PORTAL = (new Material.a(MaterialMapColor.NONE)).noCollider().notSolidBlocking().nonSolid().notPushable().build();
    public static final Material CLOTH_DECORATION = (new Material.a(MaterialMapColor.WOOL)).noCollider().notSolidBlocking().nonSolid().flammable().build();
    public static final Material PLANT = (new Material.a(MaterialMapColor.PLANT)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
    public static final Material WATER_PLANT = (new Material.a(MaterialMapColor.WATER)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
    public static final Material REPLACEABLE_PLANT = (new Material.a(MaterialMapColor.PLANT)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().flammable().build();
    public static final Material REPLACEABLE_FIREPROOF_PLANT = (new Material.a(MaterialMapColor.PLANT)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
    public static final Material REPLACEABLE_WATER_PLANT = (new Material.a(MaterialMapColor.WATER)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
    public static final Material WATER = (new Material.a(MaterialMapColor.WATER)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
    public static final Material BUBBLE_COLUMN = (new Material.a(MaterialMapColor.WATER)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
    public static final Material LAVA = (new Material.a(MaterialMapColor.FIRE)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
    public static final Material TOP_SNOW = (new Material.a(MaterialMapColor.SNOW)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
    public static final Material FIRE = (new Material.a(MaterialMapColor.NONE)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
    public static final Material DECORATION = (new Material.a(MaterialMapColor.NONE)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
    public static final Material WEB = (new Material.a(MaterialMapColor.WOOL)).noCollider().notSolidBlocking().destroyOnPush().build();
    public static final Material SCULK = (new Material.a(MaterialMapColor.COLOR_BLACK)).build();
    public static final Material BUILDABLE_GLASS = (new Material.a(MaterialMapColor.NONE)).build();
    public static final Material CLAY = (new Material.a(MaterialMapColor.CLAY)).build();
    public static final Material DIRT = (new Material.a(MaterialMapColor.DIRT)).build();
    public static final Material GRASS = (new Material.a(MaterialMapColor.GRASS)).build();
    public static final Material ICE_SOLID = (new Material.a(MaterialMapColor.ICE)).build();
    public static final Material SAND = (new Material.a(MaterialMapColor.SAND)).build();
    public static final Material SPONGE = (new Material.a(MaterialMapColor.COLOR_YELLOW)).build();
    public static final Material SHULKER_SHELL = (new Material.a(MaterialMapColor.COLOR_PURPLE)).build();
    public static final Material WOOD = (new Material.a(MaterialMapColor.WOOD)).flammable().build();
    public static final Material NETHER_WOOD = (new Material.a(MaterialMapColor.WOOD)).build();
    public static final Material BAMBOO_SAPLING = (new Material.a(MaterialMapColor.WOOD)).flammable().destroyOnPush().noCollider().build();
    public static final Material BAMBOO = (new Material.a(MaterialMapColor.WOOD)).flammable().destroyOnPush().build();
    public static final Material WOOL = (new Material.a(MaterialMapColor.WOOL)).flammable().build();
    public static final Material EXPLOSIVE = (new Material.a(MaterialMapColor.FIRE)).flammable().notSolidBlocking().build();
    public static final Material LEAVES = (new Material.a(MaterialMapColor.PLANT)).flammable().notSolidBlocking().destroyOnPush().build();
    public static final Material GLASS = (new Material.a(MaterialMapColor.NONE)).notSolidBlocking().build();
    public static final Material ICE = (new Material.a(MaterialMapColor.ICE)).notSolidBlocking().build();
    public static final Material CACTUS = (new Material.a(MaterialMapColor.PLANT)).notSolidBlocking().destroyOnPush().build();
    public static final Material STONE = (new Material.a(MaterialMapColor.STONE)).build();
    public static final Material METAL = (new Material.a(MaterialMapColor.METAL)).build();
    public static final Material SNOW = (new Material.a(MaterialMapColor.SNOW)).build();
    public static final Material HEAVY_METAL = (new Material.a(MaterialMapColor.METAL)).notPushable().build();
    public static final Material BARRIER = (new Material.a(MaterialMapColor.NONE)).notPushable().build();
    public static final Material PISTON = (new Material.a(MaterialMapColor.STONE)).notPushable().build();
    public static final Material MOSS = (new Material.a(MaterialMapColor.PLANT)).destroyOnPush().build();
    public static final Material VEGETABLE = (new Material.a(MaterialMapColor.PLANT)).destroyOnPush().build();
    public static final Material EGG = (new Material.a(MaterialMapColor.PLANT)).destroyOnPush().build();
    public static final Material CAKE = (new Material.a(MaterialMapColor.NONE)).destroyOnPush().build();
    public static final Material AMETHYST = (new Material.a(MaterialMapColor.COLOR_PURPLE)).build();
    public static final Material POWDER_SNOW = (new Material.a(MaterialMapColor.SNOW)).nonSolid().noCollider().build();
    private final MaterialMapColor color;
    private final EnumPistonReaction pushReaction;
    private final boolean blocksMotion;
    private final boolean flammable;
    private final boolean liquid;
    private final boolean solidBlocking;
    private final boolean replaceable;
    private final boolean solid;

    public Material(MaterialMapColor materialmapcolor, boolean flag, boolean flag1, boolean flag2, boolean flag3, boolean flag4, boolean flag5, EnumPistonReaction enumpistonreaction) {
        this.color = materialmapcolor;
        this.liquid = flag;
        this.solid = flag1;
        this.blocksMotion = flag2;
        this.solidBlocking = flag3;
        this.flammable = flag4;
        this.replaceable = flag5;
        this.pushReaction = enumpistonreaction;
    }

    public boolean isLiquid() {
        return this.liquid;
    }

    public boolean isSolid() {
        return this.solid;
    }

    public boolean blocksMotion() {
        return this.blocksMotion;
    }

    public boolean isFlammable() {
        return this.flammable;
    }

    public boolean isReplaceable() {
        return this.replaceable;
    }

    public boolean isSolidBlocking() {
        return this.solidBlocking;
    }

    public EnumPistonReaction getPushReaction() {
        return this.pushReaction;
    }

    public MaterialMapColor getColor() {
        return this.color;
    }

    public static class a {

        private EnumPistonReaction pushReaction;
        private boolean blocksMotion;
        private boolean flammable;
        private boolean liquid;
        private boolean replaceable;
        private boolean solid;
        private final MaterialMapColor color;
        private boolean solidBlocking;

        public a(MaterialMapColor materialmapcolor) {
            this.pushReaction = EnumPistonReaction.NORMAL;
            this.blocksMotion = true;
            this.solid = true;
            this.solidBlocking = true;
            this.color = materialmapcolor;
        }

        public Material.a liquid() {
            this.liquid = true;
            return this;
        }

        public Material.a nonSolid() {
            this.solid = false;
            return this;
        }

        public Material.a noCollider() {
            this.blocksMotion = false;
            return this;
        }

        Material.a notSolidBlocking() {
            this.solidBlocking = false;
            return this;
        }

        protected Material.a flammable() {
            this.flammable = true;
            return this;
        }

        public Material.a replaceable() {
            this.replaceable = true;
            return this;
        }

        protected Material.a destroyOnPush() {
            this.pushReaction = EnumPistonReaction.DESTROY;
            return this;
        }

        protected Material.a notPushable() {
            this.pushReaction = EnumPistonReaction.BLOCK;
            return this;
        }

        public Material build() {
            return new Material(this.color, this.liquid, this.solid, this.blocksMotion, this.solidBlocking, this.flammable, this.replaceable, this.pushReaction);
        }
    }
}

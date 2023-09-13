package net.minecraft.world.level.material;

public final class Material {

    public static final Material AIR = (new Material.a(MaterialMapColor.NONE)).c().i().b().e().h();
    public static final Material STRUCTURAL_AIR = (new Material.a(MaterialMapColor.NONE)).c().i().b().e().h();
    public static final Material PORTAL = (new Material.a(MaterialMapColor.NONE)).c().i().b().g().h();
    public static final Material CLOTH_DECORATION = (new Material.a(MaterialMapColor.WOOL)).c().i().b().d().h();
    public static final Material PLANT = (new Material.a(MaterialMapColor.PLANT)).c().i().b().f().h();
    public static final Material WATER_PLANT = (new Material.a(MaterialMapColor.WATER)).c().i().b().f().h();
    public static final Material REPLACEABLE_PLANT = (new Material.a(MaterialMapColor.PLANT)).c().i().b().f().e().d().h();
    public static final Material REPLACEABLE_FIREPROOF_PLANT = (new Material.a(MaterialMapColor.PLANT)).c().i().b().f().e().h();
    public static final Material REPLACEABLE_WATER_PLANT = (new Material.a(MaterialMapColor.WATER)).c().i().b().f().e().h();
    public static final Material WATER = (new Material.a(MaterialMapColor.WATER)).c().i().b().f().e().a().h();
    public static final Material BUBBLE_COLUMN = (new Material.a(MaterialMapColor.WATER)).c().i().b().f().e().a().h();
    public static final Material LAVA = (new Material.a(MaterialMapColor.FIRE)).c().i().b().f().e().a().h();
    public static final Material TOP_SNOW = (new Material.a(MaterialMapColor.SNOW)).c().i().b().f().e().h();
    public static final Material FIRE = (new Material.a(MaterialMapColor.NONE)).c().i().b().f().e().h();
    public static final Material DECORATION = (new Material.a(MaterialMapColor.NONE)).c().i().b().f().h();
    public static final Material WEB = (new Material.a(MaterialMapColor.WOOL)).c().i().f().h();
    public static final Material SCULK = (new Material.a(MaterialMapColor.COLOR_BLACK)).h();
    public static final Material BUILDABLE_GLASS = (new Material.a(MaterialMapColor.NONE)).h();
    public static final Material CLAY = (new Material.a(MaterialMapColor.CLAY)).h();
    public static final Material DIRT = (new Material.a(MaterialMapColor.DIRT)).h();
    public static final Material GRASS = (new Material.a(MaterialMapColor.GRASS)).h();
    public static final Material ICE_SOLID = (new Material.a(MaterialMapColor.ICE)).h();
    public static final Material SAND = (new Material.a(MaterialMapColor.SAND)).h();
    public static final Material SPONGE = (new Material.a(MaterialMapColor.COLOR_YELLOW)).h();
    public static final Material SHULKER_SHELL = (new Material.a(MaterialMapColor.COLOR_PURPLE)).h();
    public static final Material WOOD = (new Material.a(MaterialMapColor.WOOD)).d().h();
    public static final Material NETHER_WOOD = (new Material.a(MaterialMapColor.WOOD)).h();
    public static final Material BAMBOO_SAPLING = (new Material.a(MaterialMapColor.WOOD)).d().f().c().h();
    public static final Material BAMBOO = (new Material.a(MaterialMapColor.WOOD)).d().f().h();
    public static final Material WOOL = (new Material.a(MaterialMapColor.WOOL)).d().h();
    public static final Material EXPLOSIVE = (new Material.a(MaterialMapColor.FIRE)).d().i().h();
    public static final Material LEAVES = (new Material.a(MaterialMapColor.PLANT)).d().i().f().h();
    public static final Material GLASS = (new Material.a(MaterialMapColor.NONE)).i().h();
    public static final Material ICE = (new Material.a(MaterialMapColor.ICE)).i().h();
    public static final Material CACTUS = (new Material.a(MaterialMapColor.PLANT)).i().f().h();
    public static final Material STONE = (new Material.a(MaterialMapColor.STONE)).h();
    public static final Material METAL = (new Material.a(MaterialMapColor.METAL)).h();
    public static final Material SNOW = (new Material.a(MaterialMapColor.SNOW)).h();
    public static final Material HEAVY_METAL = (new Material.a(MaterialMapColor.METAL)).g().h();
    public static final Material BARRIER = (new Material.a(MaterialMapColor.NONE)).g().h();
    public static final Material PISTON = (new Material.a(MaterialMapColor.STONE)).g().h();
    public static final Material MOSS = (new Material.a(MaterialMapColor.PLANT)).f().h();
    public static final Material VEGETABLE = (new Material.a(MaterialMapColor.PLANT)).f().h();
    public static final Material EGG = (new Material.a(MaterialMapColor.PLANT)).f().h();
    public static final Material CAKE = (new Material.a(MaterialMapColor.NONE)).f().h();
    public static final Material AMETHYST = (new Material.a(MaterialMapColor.COLOR_PURPLE)).h();
    public static final Material POWDER_SNOW = (new Material.a(MaterialMapColor.SNOW)).b().c().h();
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

    public boolean isBuildable() {
        return this.solid;
    }

    public boolean isSolid() {
        return this.blocksMotion;
    }

    public boolean isBurnable() {
        return this.flammable;
    }

    public boolean isReplaceable() {
        return this.replaceable;
    }

    public boolean f() {
        return this.solidBlocking;
    }

    public EnumPistonReaction getPushReaction() {
        return this.pushReaction;
    }

    public MaterialMapColor h() {
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

        public Material.a a() {
            this.liquid = true;
            return this;
        }

        public Material.a b() {
            this.solid = false;
            return this;
        }

        public Material.a c() {
            this.blocksMotion = false;
            return this;
        }

        Material.a i() {
            this.solidBlocking = false;
            return this;
        }

        protected Material.a d() {
            this.flammable = true;
            return this;
        }

        public Material.a e() {
            this.replaceable = true;
            return this;
        }

        protected Material.a f() {
            this.pushReaction = EnumPistonReaction.DESTROY;
            return this;
        }

        protected Material.a g() {
            this.pushReaction = EnumPistonReaction.BLOCK;
            return this;
        }

        public Material h() {
            return new Material(this.color, this.liquid, this.solid, this.blocksMotion, this.solidBlocking, this.flammable, this.replaceable, this.pushReaction);
        }
    }
}

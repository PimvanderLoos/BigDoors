package net.minecraft.server;

public class DebugReportFluidTags extends DebugReportTags<FluidType> {

    public DebugReportFluidTags(DebugReportGenerator debugreportgenerator) {
        super(debugreportgenerator, FluidType.c);
    }

    protected void b() {
        this.a(TagsFluid.a).a((Object[]) (new FluidType[] { FluidTypes.c, FluidTypes.b}));
        this.a(TagsFluid.b).a((Object[]) (new FluidType[] { FluidTypes.e, FluidTypes.d}));
    }

    protected java.nio.file.Path a(MinecraftKey minecraftkey) {
        return this.b.b().resolve("data/" + minecraftkey.b() + "/tags/fluids/" + minecraftkey.getKey() + ".json");
    }

    public String a() {
        return "Fluid Tags";
    }

    protected void a(Tags<FluidType> tags) {
        TagsFluid.a(tags);
    }
}

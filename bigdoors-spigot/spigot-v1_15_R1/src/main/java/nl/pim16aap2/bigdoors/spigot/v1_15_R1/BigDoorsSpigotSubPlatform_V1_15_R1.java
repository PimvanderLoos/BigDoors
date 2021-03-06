package nl.pim16aap2.bigdoors.spigot.v1_15_R1;

import lombok.Getter;
import nl.pim16aap2.bigdoors.annotations.Initializer;
import nl.pim16aap2.bigdoors.api.IBlockAnalyzer;
import nl.pim16aap2.bigdoors.api.factories.IAnimatedBlockFactory;
import nl.pim16aap2.bigdoors.managers.AnimatedBlockHookManager;
import nl.pim16aap2.bigdoors.spigot.util.api.IBigDoorsSpigotSubPlatform;
import nl.pim16aap2.bigdoors.spigot.util.api.IGlowingBlockFactory;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class BigDoorsSpigotSubPlatform_V1_15_R1 implements IBigDoorsSpigotSubPlatform
{
    private static final String VERSION = "v1_15_R1";

    @Getter
    private IAnimatedBlockFactory animatedBlockFactory;

    @Getter
    private IBlockAnalyzer blockAnalyzer;

    @Getter
    private IGlowingBlockFactory glowingBlockFactory;

    private final AnimatedBlockHookManager animatedBlockHookManager;

    @Inject
    public BigDoorsSpigotSubPlatform_V1_15_R1(AnimatedBlockHookManager animatedBlockHookManager)
    {
        this.animatedBlockHookManager = animatedBlockHookManager;
    }

    @Override
    public String getVersion()
    {
        return VERSION;
    }

    @Override
    @Initializer
    public void init(JavaPlugin plugin)
    {
        animatedBlockFactory = new AnimatedBlockFactory_V1_15_R1(animatedBlockHookManager);
        blockAnalyzer = new nl.pim16aap2.bigdoors.spigot.v1_15_R1.BlockAnalyzer_V1_15_R1();
        glowingBlockFactory = new GlowingBlock_V1_15_R1.Factory();
    }
}

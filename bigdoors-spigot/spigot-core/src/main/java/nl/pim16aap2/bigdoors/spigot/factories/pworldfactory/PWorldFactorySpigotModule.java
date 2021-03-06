package nl.pim16aap2.bigdoors.spigot.factories.pworldfactory;

import dagger.Binds;
import dagger.Module;
import nl.pim16aap2.bigdoors.api.factories.IPWorldFactory;

import javax.inject.Singleton;

@Module
public interface PWorldFactorySpigotModule
{
    @Binds
    @Singleton
    IPWorldFactory getPWorldFactory(PWorldFactorySpigot factory);
}

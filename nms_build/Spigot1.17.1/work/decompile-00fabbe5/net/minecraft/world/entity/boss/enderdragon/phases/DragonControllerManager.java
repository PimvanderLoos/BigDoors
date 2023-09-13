package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DragonControllerManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private final EntityEnderDragon dragon;
    private final IDragonController[] phases = new IDragonController[DragonControllerPhase.c()];
    private IDragonController currentPhase;

    public DragonControllerManager(EntityEnderDragon entityenderdragon) {
        this.dragon = entityenderdragon;
        this.setControllerPhase(DragonControllerPhase.HOVERING);
    }

    public void setControllerPhase(DragonControllerPhase<?> dragoncontrollerphase) {
        if (this.currentPhase == null || dragoncontrollerphase != this.currentPhase.getControllerPhase()) {
            if (this.currentPhase != null) {
                this.currentPhase.e();
            }

            this.currentPhase = this.b(dragoncontrollerphase);
            if (!this.dragon.level.isClientSide) {
                this.dragon.getDataWatcher().set(EntityEnderDragon.DATA_PHASE, dragoncontrollerphase.b());
            }

            DragonControllerManager.LOGGER.debug("Dragon is now in phase {} on the {}", dragoncontrollerphase, this.dragon.level.isClientSide ? "client" : "server");
            this.currentPhase.d();
        }
    }

    public IDragonController a() {
        return this.currentPhase;
    }

    public <T extends IDragonController> T b(DragonControllerPhase<T> dragoncontrollerphase) {
        int i = dragoncontrollerphase.b();

        if (this.phases[i] == null) {
            this.phases[i] = dragoncontrollerphase.a(this.dragon);
        }

        return this.phases[i];
    }
}

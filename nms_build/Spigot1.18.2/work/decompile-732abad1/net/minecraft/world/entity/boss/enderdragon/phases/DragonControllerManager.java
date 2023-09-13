package net.minecraft.world.entity.boss.enderdragon.phases;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import org.slf4j.Logger;

public class DragonControllerManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final EntityEnderDragon dragon;
    private final IDragonController[] phases = new IDragonController[DragonControllerPhase.getCount()];
    @Nullable
    private IDragonController currentPhase;

    public DragonControllerManager(EntityEnderDragon entityenderdragon) {
        this.dragon = entityenderdragon;
        this.setPhase(DragonControllerPhase.HOVERING);
    }

    public void setPhase(DragonControllerPhase<?> dragoncontrollerphase) {
        if (this.currentPhase == null || dragoncontrollerphase != this.currentPhase.getPhase()) {
            if (this.currentPhase != null) {
                this.currentPhase.end();
            }

            this.currentPhase = this.getPhase(dragoncontrollerphase);
            if (!this.dragon.level.isClientSide) {
                this.dragon.getEntityData().set(EntityEnderDragon.DATA_PHASE, dragoncontrollerphase.getId());
            }

            DragonControllerManager.LOGGER.debug("Dragon is now in phase {} on the {}", dragoncontrollerphase, this.dragon.level.isClientSide ? "client" : "server");
            this.currentPhase.begin();
        }
    }

    public IDragonController getCurrentPhase() {
        return this.currentPhase;
    }

    public <T extends IDragonController> T getPhase(DragonControllerPhase<T> dragoncontrollerphase) {
        int i = dragoncontrollerphase.getId();

        if (this.phases[i] == null) {
            this.phases[i] = dragoncontrollerphase.createInstance(this.dragon);
        }

        return this.phases[i];
    }
}

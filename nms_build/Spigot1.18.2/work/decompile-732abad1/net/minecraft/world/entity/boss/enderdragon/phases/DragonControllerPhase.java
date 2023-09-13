package net.minecraft.world.entity.boss.enderdragon.phases;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;

public class DragonControllerPhase<T extends IDragonController> {

    private static DragonControllerPhase<?>[] phases = new DragonControllerPhase[0];
    public static final DragonControllerPhase<DragonControllerHold> HOLDING_PATTERN = create(DragonControllerHold.class, "HoldingPattern");
    public static final DragonControllerPhase<DragonControllerStrafe> STRAFE_PLAYER = create(DragonControllerStrafe.class, "StrafePlayer");
    public static final DragonControllerPhase<DragonControllerLandingFly> LANDING_APPROACH = create(DragonControllerLandingFly.class, "LandingApproach");
    public static final DragonControllerPhase<DragonControllerLanding> LANDING = create(DragonControllerLanding.class, "Landing");
    public static final DragonControllerPhase<DragonControllerFly> TAKEOFF = create(DragonControllerFly.class, "Takeoff");
    public static final DragonControllerPhase<DragonControllerLandedFlame> SITTING_FLAMING = create(DragonControllerLandedFlame.class, "SittingFlaming");
    public static final DragonControllerPhase<DragonControllerLandedSearch> SITTING_SCANNING = create(DragonControllerLandedSearch.class, "SittingScanning");
    public static final DragonControllerPhase<DragonControllerLandedAttack> SITTING_ATTACKING = create(DragonControllerLandedAttack.class, "SittingAttacking");
    public static final DragonControllerPhase<DragonControllerCharge> CHARGING_PLAYER = create(DragonControllerCharge.class, "ChargingPlayer");
    public static final DragonControllerPhase<DragonControllerDying> DYING = create(DragonControllerDying.class, "Dying");
    public static final DragonControllerPhase<DragonControllerHover> HOVERING = create(DragonControllerHover.class, "Hover");
    private final Class<? extends IDragonController> instanceClass;
    private final int id;
    private final String name;

    private DragonControllerPhase(int i, Class<? extends IDragonController> oclass, String s) {
        this.id = i;
        this.instanceClass = oclass;
        this.name = s;
    }

    public IDragonController createInstance(EntityEnderDragon entityenderdragon) {
        try {
            Constructor<? extends IDragonController> constructor = this.getConstructor();

            return (IDragonController) constructor.newInstance(entityenderdragon);
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }

    protected Constructor<? extends IDragonController> getConstructor() throws NoSuchMethodException {
        return this.instanceClass.getConstructor(EntityEnderDragon.class);
    }

    public int getId() {
        return this.id;
    }

    public String toString() {
        return this.name + " (#" + this.id + ")";
    }

    public static DragonControllerPhase<?> getById(int i) {
        return i >= 0 && i < DragonControllerPhase.phases.length ? DragonControllerPhase.phases[i] : DragonControllerPhase.HOLDING_PATTERN;
    }

    public static int getCount() {
        return DragonControllerPhase.phases.length;
    }

    private static <T extends IDragonController> DragonControllerPhase<T> create(Class<T> oclass, String s) {
        DragonControllerPhase<T> dragoncontrollerphase = new DragonControllerPhase<>(DragonControllerPhase.phases.length, oclass, s);

        DragonControllerPhase.phases = (DragonControllerPhase[]) Arrays.copyOf(DragonControllerPhase.phases, DragonControllerPhase.phases.length + 1);
        DragonControllerPhase.phases[dragoncontrollerphase.getId()] = dragoncontrollerphase;
        return dragoncontrollerphase;
    }
}

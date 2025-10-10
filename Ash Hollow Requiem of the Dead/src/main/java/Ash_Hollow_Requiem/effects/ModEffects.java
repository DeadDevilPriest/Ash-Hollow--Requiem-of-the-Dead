package Ash_Hollow_Requiem.effects;

import Ash_Hollow_Requiem.Ash_Hollow;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
        public static final DeferredRegister<MobEffect> EFFECTS =
                DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Ash_Hollow.MODID);

        public static final RegistryObject<MobEffect> BLEEDING =
                EFFECTS.register("bleeding", BleedingEffect::new);

        public static final RegistryObject<MobEffect> TRAUMA = 
                EFFECTS.register("trauma", TraumaEffect::new);

        public static final RegistryObject<MobEffect> NECROTIC = 
                EFFECTS.register("necrotic", NecroticEffect::new);

        public static final RegistryObject<MobEffect> TOXIN = 
                EFFECTS.register("toxin", ToxinEffect::new);

        public static final RegistryObject<MobEffect> ELECTRO = 
                EFFECTS.register("electro", ElectroEffect::new);

        public static final RegistryObject<MobEffect> Radiation = 
                EFFECTS.register("radiation", RadiationEffect::new);

        public static final RegistryObject<MobEffect> CORRUPTION = 
                EFFECTS.register("corruption", CorruptionEffect::new);

        public static final RegistryObject<MobEffect> INSANITY = 
                EFFECTS.register("insanity", InsanityEffect::new);

        public static final RegistryObject<MobEffect> FROST = 
                EFFECTS.register("frost", FrostEffect::new);

        public static final RegistryObject<MobEffect> VOID = 
                EFFECTS.register("void", VoidEffect::new);
}

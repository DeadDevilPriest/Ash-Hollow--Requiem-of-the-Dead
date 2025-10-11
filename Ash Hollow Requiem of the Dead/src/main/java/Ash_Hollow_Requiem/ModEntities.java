package Ash_Hollow_Requiem;


import Ash_Hollow_Requiem.Ash_Hollow;
import Ash_Hollow_Requiem.HarpoonEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Ash_Hollow.MODID);

    public static final RegistryObject<EntityType<HarpoonEntity>> HARPOON =
            ENTITIES.register("harpoon",
                    () -> EntityType.Builder.<HarpoonEntity>of(HarpoonEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build("harpoon"));
}

package Ash_Hollow_Requiem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static Ash_Hollow_Requiem.Ash_Hollow.MODID;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER.comment("Whether to log the dirt block on common setup").define("logDirtBlock", true);

    private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER.comment("A magic number").defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER.comment("What you want the introduction message to be for the magic number").define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER.comment("A list of items to log on common setup.").defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    // Dark Vision settings
    private static final ForgeConfigSpec.DoubleValue DARK_VISION_GAMMA = BUILDER
            .comment("Gamma value when Dark Vision is active (lower = darker)")
            .defineInRange("darkVisionGamma", 0.0, 0.0, 1.0);
    
    private static final ForgeConfigSpec.DoubleValue DARK_VISION_SATURATION = BUILDER
            .comment("Saturation level when Dark Vision is active (0.0 = no color, 1.0 = full color)")
            .defineInRange("darkVisionSaturation", 0.0, 0.0, 1.0);
    
    private static final ForgeConfigSpec.BooleanValue DARK_VISION_SHOW_HOSTILES = BUILDER
            .comment("Whether hostile mobs should remain colored in red when Dark Vision is active")
            .define("darkVisionShowHostiles", true);
    
    private static final ForgeConfigSpec.BooleanValue DARK_VISION_SHOW_CLUES = BUILDER
            .comment("Whether clue objects should remain colored when Dark Vision is active")
            .define("darkVisionShowClues", true);
            
    // Spotlight settings
    private static final ForgeConfigSpec.IntValue SPOTLIGHT_RADIUS = BUILDER
            .comment("Radius of the spherical spotlight in Dark Vision mode (in blocks)")
            .defineInRange("spotlightRadius", 5, 1, 20);
            
    private static final ForgeConfigSpec.DoubleValue SPOTLIGHT_INTENSITY = BUILDER
            .comment("Brightness of the spotlight (0.0-1.0)")
            .defineInRange("spotlightIntensity", 0.7, 0.1, 1.0);
            
    private static final ForgeConfigSpec.BooleanValue SPOTLIGHT_USE_3D = BUILDER
            .comment("Whether to use true 3D object for spotlight (uses mesh instead of shader)")
            .define("spotlight3D", true);
            
    private static final ForgeConfigSpec.BooleanValue SPOTLIGHT_INVERT = BUILDER
            .comment("Whether to invert the spotlight (light outside, dark inside)")
            .define("spotlightInvert", false);
            
    private static final ForgeConfigSpec.IntValue SPOTLIGHT_RESOLUTION = BUILDER
            .comment("Resolution of the 3D sphere (higher = smoother but more performance impact)")
            .defineInRange("spotlightResolution", 16, 8, 32);

    private static final ForgeConfigSpec.BooleanValue SPOTLIGHT_HYBRID_MODE = BUILDER
            .comment("Enable both shader and 3D spotlight effects together (may impact performance)")
            .define("spotlightHybridMode", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean logDirtBlock;
    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;
    
    // Dark Vision settings
    public static double darkVisionGamma;
    public static double darkVisionSaturation;
    public static boolean darkVisionShowHostiles;
    public static boolean darkVisionShowClues;
    
    // Spotlight settings
    public static int spotlightRadius;
    public static double spotlightIntensity;
    public static boolean spotlight3D;
    public static boolean spotlightInvert;
    public static int spotlightResolution;
    public static boolean spotlightHybridMode;

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(ResourceLocation.tryParse(itemName));
    }
    
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        logDirtBlock = LOG_DIRT_BLOCK.get();
        magicNumber = MAGIC_NUMBER.get();
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
    
        // convert the list of strings into a set of items
        items = ITEM_STRINGS.get().stream()
            .map(itemName -> ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(itemName)))
    .collect(Collectors.toSet());
        
        // Load Dark Vision settings
        darkVisionGamma = DARK_VISION_GAMMA.get();
        darkVisionSaturation = DARK_VISION_SATURATION.get();
        darkVisionShowHostiles = DARK_VISION_SHOW_HOSTILES.get();
        darkVisionShowClues = DARK_VISION_SHOW_CLUES.get();
        
        // Load Spotlight settings
        spotlightRadius = SPOTLIGHT_RADIUS.get();
        spotlightIntensity = SPOTLIGHT_INTENSITY.get();
        spotlight3D = SPOTLIGHT_USE_3D.get();
        spotlightInvert = SPOTLIGHT_INVERT.get();
        spotlightResolution = SPOTLIGHT_RESOLUTION.get();
        spotlightHybridMode = SPOTLIGHT_HYBRID_MODE.get();
    }
}


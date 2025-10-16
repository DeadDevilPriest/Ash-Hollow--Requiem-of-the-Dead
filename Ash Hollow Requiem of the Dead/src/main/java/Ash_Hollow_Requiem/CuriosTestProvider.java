package Ash_Hollow_Requiem;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.data.provider.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

public class CuriosTestProvider extends CuriosDataProvider {

    public CuriosTestProvider(String modId, PackOutput output,
                              ExistingFileHelper fileHelper,
                              CompletableFuture<HolderLookup.Provider> registries) {
        super(modId, output, fileHelper, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
        // Generation code here
    }
}

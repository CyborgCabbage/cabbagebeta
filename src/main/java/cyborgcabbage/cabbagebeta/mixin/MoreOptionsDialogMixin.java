package cyborgcabbage.cabbagebeta.mixin;

import cyborgcabbage.cabbagebeta.CabbageBeta;
import cyborgcabbage.cabbagebeta.gui.CustomizeBetaLevelScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;


@Mixin(MoreOptionsDialog.class)
public abstract class MoreOptionsDialogMixin {

    @Shadow private Optional<RegistryEntry<WorldPreset>> presetEntry;

    @Shadow private GeneratorOptionsHolder generatorOptionsHolder;

    @Shadow private ButtonWidget customizeTypeButton;

    @Inject(method="method_28087", at=@At(value="HEAD"))
    private void inject(MinecraftClient client, CreateWorldScreen parent, ButtonWidget button, CallbackInfo ci){
        Optional<RegistryKey<WorldPreset>> presetOptional = this.presetEntry.flatMap(RegistryEntry::getKey);
        if(presetOptional.equals(Optional.of(CabbageBeta.BETA_PRESET))){
            client.setScreen(new CustomizeBetaLevelScreen(parent, this.generatorOptionsHolder));
        }
    }

    @Inject(method="setVisible",at=@At("RETURN"))
    private void inject2(boolean visible, CallbackInfo ci) {
        Optional<RegistryKey<WorldPreset>> presetOptional = this.presetEntry.flatMap(RegistryEntry::getKey);
        if (presetOptional.equals(Optional.of(CabbageBeta.BETA_PRESET))) {
            this.customizeTypeButton.visible = visible;
        }
    }
}

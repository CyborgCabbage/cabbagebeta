package cyborgcabbage.cabbagebeta.gui;

import cyborgcabbage.cabbagebeta.gen.BetaPreset;
import cyborgcabbage.cabbagebeta.gen.BetaProperties;
import cyborgcabbage.cabbagebeta.gen.beta.BetaChunkGenerator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.structure.StructureSet;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GeneratorOptions;

import java.util.ArrayList;
import java.util.List;

public class CustomizeBetaLevelScreen extends Screen {
    private final CreateWorldScreen parent;
    private GeneratorOptionsHolder generatorOptionsHolder;
    private CyclingButtonWidget<BetaPreset> betaPresetButton;

    private SimpleOption<Boolean> useFullHeightOption = SimpleOption.ofBoolean("createWorld.customize.beta.use_full_height", SimpleOption.constantTooltip(Text.translatable("createWorld.customize.beta.use_full_height.tip")), false);
    private SimpleOption<Integer> seaLevelOption = new SimpleOption<>("createWorld.customize.beta.sea_level", SimpleOption.constantTooltip(Text.translatable("createWorld.customize.beta.sea_level.tip")), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), 64, a -> {});
    private TextFieldWidget factorOption;
    private SimpleOption<Integer> groundLevelOption = new SimpleOption<>("createWorld.customize.beta.ground_level", SimpleOption.constantTooltip(Text.translatable("createWorld.customize.beta.ground_level.tip")), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), 68, a -> {});
    private SimpleOption<Integer> caveLavaLevelOption = new SimpleOption<>("createWorld.customize.beta.cave_lava_level", SimpleOption.constantTooltip(Text.translatable("createWorld.customize.beta.cave_lava_level.tip")), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), 10, a -> {});
    private TextFieldWidget mixingOption;
    private SimpleOption<Boolean> fixesOption = SimpleOption.ofBoolean("createWorld.customize.beta.fixes", SimpleOption.constantTooltip(Text.translatable("createWorld.customize.beta.fixes.tip")), false);
    private SimpleOption<Boolean> substituteBiomesOption = SimpleOption.ofBoolean("createWorld.customize.beta.substitute_biomes", SimpleOption.constantTooltip(Text.translatable("createWorld.custom.beta.substitute_biomes.tip")), false);

    private List<ClickableWidget> propertyWidgets = new ArrayList<>();

    private BetaProperties customProperties = null;

    private BetaPreset currentPreset = null;

    public CustomizeBetaLevelScreen(CreateWorldScreen parent, GeneratorOptionsHolder generatorOptionsHolder) {
        super(Text.translatable("createWorld.customize.beta.title"));
        this.parent = parent;
        this.generatorOptionsHolder = generatorOptionsHolder;
    }

    @Override
    public void tick() {
        super.tick();
        factorOption.tick();
        //UPDATE CUSTOM PROPERTIES
        if(getPreset() == BetaPreset.CUSTOM){
            customProperties = propertiesFromOptions();
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        //INITIAL PRESET
        if(currentPreset == null) {
            currentPreset = BetaPreset.CUSTOM;
            if (generatorOptionsHolder.generatorOptions().getChunkGenerator() instanceof BetaChunkGenerator gen) {
                for (BetaPreset value : BetaPreset.values()) {
                    if (value.getProperties().match(gen.getBetaProperties())) {
                        currentPreset = value;
                        break;
                    }
                }
            } else {
                currentPreset = BetaPreset.FAITHFUL;
            }
        }
        //CUSTOM PROPERTIES
        if(customProperties == null) {
            if (generatorOptionsHolder.generatorOptions().getChunkGenerator() instanceof BetaChunkGenerator gen && currentPreset == BetaPreset.CUSTOM) {
                customProperties = gen.getBetaProperties().clone();
            } else {
                customProperties = BetaPreset.CUSTOM.getProperties().clone();
            }
        }
        //BETA PRESET
        this.betaPresetButton = this.addDrawableChild(CyclingButtonWidget.builder(BetaPreset::getTranslatableName)
                .values(BetaPreset.values())
                .initially(currentPreset)
                .build(this.width / 2 - 75, 30, 150, 20, Text.translatable("createWorld.customize.beta"), (button, betaPreset) -> presetInit(betaPreset)));
        factorOption = new TextFieldWidget(textRenderer, this.width / 2 - 75, 0, 150, 20, Text.translatable("createWorld.customize.beta.factor"));
        mixingOption = new TextFieldWidget(textRenderer, this.width / 2 - 75, 0, 150, 20, Text.translatable("createWorld.customize.beta.mixing"));
        //SET VALUES
        presetInit(currentPreset);
        //CONFIRM
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, button -> {
            this.parent.moreOptionsDialog.apply(createModifier(propertiesFromOptions()));
            this.client.setScreen(this.parent);
        }));
        //CANCEL
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)));
    }

    private void presetInit(BetaPreset betaPreset) {
        //SET AS PRESET
        currentPreset = betaPreset;
        //COPY VALUES INTO OPTIONS
        BetaProperties initial;
        if(betaPreset != BetaPreset.CUSTOM) {
            initial = betaPreset.getProperties();
        }else{
            initial = customProperties;
        }
        useFullHeightOption.setValue(initial.useFullHeight());
        seaLevelOption.setValue(initial.seaLevel());
        factorOption.setText(Float.toString(initial.factor()));
        groundLevelOption.setValue(initial.groundLevel());
        caveLavaLevelOption.setValue(initial.caveLavaLevel());
        mixingOption.setText(Float.toString(initial.mixing()));
        fixesOption.setValue(initial.fixes());
        substituteBiomesOption.setValue(initial.substituteBiomes());
        //CREATE WIDGETS
        propertyWidgets.forEach(this::remove);
        int baseHeight = 30;
        baseHeight += 35;
        factorOption.y = baseHeight;
        propertyWidgets.add(this.addDrawableChild(factorOption));
        baseHeight += 35;
        mixingOption.y = baseHeight;
        propertyWidgets.add(this.addDrawableChild(mixingOption));
        baseHeight += 25;
        propertyWidgets.add(this.addDrawableChild(groundLevelOption.createButton(MinecraftClient.getInstance().options, this.width / 2 - 75, baseHeight, 150)));
        baseHeight += 25;
        propertyWidgets.add(this.addDrawableChild(seaLevelOption.createButton(MinecraftClient.getInstance().options, this.width / 2 - 75, baseHeight, 150)));
        baseHeight += 25;
        propertyWidgets.add(this.addDrawableChild(caveLavaLevelOption.createButton(MinecraftClient.getInstance().options, this.width / 2 - 75, baseHeight, 150)));
        baseHeight += 25;
        propertyWidgets.add(this.addDrawableChild(useFullHeightOption.createButton(MinecraftClient.getInstance().options, this.width / 2 - 75, baseHeight, 150)));
        baseHeight += 25;
        propertyWidgets.add(this.addDrawableChild(fixesOption.createButton(MinecraftClient.getInstance().options, this.width / 2 - 75, baseHeight, 150)));
        baseHeight += 25;
        propertyWidgets.add(this.addDrawableChild(substituteBiomesOption.createButton(MinecraftClient.getInstance().options, this.width / 2 - 75, baseHeight, 150)));
        //SET ACTIVITY
        boolean activity = betaPreset == BetaPreset.CUSTOM;
        propertyWidgets.forEach(c -> c.active = activity);
        propertyWidgets.forEach(c -> {
            if(c instanceof TextFieldWidget t){
                t.setEditable(activity);
                if(!activity) t.setTextFieldFocused(false);
                t.setFocusUnlocked(activity);
            }
        });
    }

    private BetaProperties propertiesFromOptions(){
        return new BetaProperties(useFullHeightOption.getValue(), seaLevelOption.getValue(), carefulParse(factorOption, getPreset().getProperties().factor()), groundLevelOption.getValue(), caveLavaLevelOption.getValue(), carefulParse(mixingOption, getPreset().getProperties().mixing()), fixesOption.getValue(), substituteBiomesOption.getValue());
    }

    private float carefulParse(TextFieldWidget field, float orElse){
        try {
            return Float.parseFloat(field.getText());
        }catch(NumberFormatException e){
            return orElse;
        }
    }

    private BetaPreset getPreset(){
        return betaPresetButton.getValue();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        drawCenteredText(matrices, this.textRenderer, Text.translatable("createWorld.customize.beta.factor"), this.width / 2, factorOption.y - 12, 0xA0A0A0);
        drawCenteredText(matrices, this.textRenderer, Text.translatable("createWorld.customize.beta.mixing"), this.width / 2, mixingOption.y - 12, 0xA0A0A0);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(BetaProperties properties) {
        return (dynamicRegistryManager, generatorOptions) -> {
            Registry<StructureSet> structures = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
            Registry<Biome> biomes = dynamicRegistryManager.get(Registry.BIOME_KEY);
            BetaChunkGenerator chunkGenerator = new BetaChunkGenerator(structures, biomes, properties);
            return GeneratorOptions.create(dynamicRegistryManager, generatorOptions, chunkGenerator);
        };
    }
}

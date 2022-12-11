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
import net.minecraft.util.registry.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomizeBetaLevelScreen extends Screen {
    private final CreateWorldScreen parent;
    private GeneratorOptionsHolder generatorOptionsHolder;
    private CyclingButtonWidget<BetaPreset> betaPresetButton;

    private SimpleOption<Integer> generationHeightOption = new SimpleOption<>("createWorld.customize.beta.generation_height", SimpleOption.constantTooltip(Text.translatable("createWorld.customize.beta.generation_height.tip")), (prefix, integer) -> GameOptions.getGenericValueText(prefix, integer*64), new SimpleOption.ValidatingIntSliderCallbacks(1, 4), 2, a -> {});
    private SimpleOption<Integer> seaLevelOption = new SimpleOption<>("createWorld.customize.beta.sea_level", SimpleOption.constantTooltip(Text.translatable("createWorld.customize.beta.sea_level.tip")), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), 64, a -> {});
    private TextFieldWidget factorOption;
    private SimpleOption<Integer> groundLevelOption = new SimpleOption<>("createWorld.customize.beta.ground_level", SimpleOption.constantTooltip(Text.translatable("createWorld.customize.beta.ground_level.tip")), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), 68, a -> {});
    private SimpleOption<Integer> caveLavaLevelOption = new SimpleOption<>("createWorld.customize.beta.cave_lava_level", SimpleOption.constantTooltip(Text.translatable("createWorld.customize.beta.cave_lava_level.tip")), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), 10, a -> {});
    private TextFieldWidget mixingOption;
    private SimpleOption<Boolean> fixesOption = SimpleOption.ofBoolean("createWorld.customize.beta.fixes", SimpleOption.constantTooltip(Text.translatable("createWorld.customize.beta.fixes.tip")), false);
    private SimpleOption<Boolean> substituteBiomesOption = SimpleOption.ofBoolean("createWorld.customize.beta.substitute_biomes", SimpleOption.constantTooltip(Text.translatable("createWorld.custom.beta.substitute_biomes.tip")), false);
    private TextFieldWidget caveRarityOption;
    private SimpleOption<Double> decliffOption = new SimpleOption<>("createWorld.customize.beta.decliff", SimpleOption.constantTooltip(Text.translatable("createWorld.customize.beta.decliff")), CustomizeBetaLevelScreen::getPercentValueText, SimpleOption.DoubleSliderCallbacks.INSTANCE, 0.0, value -> {});
    private TextFieldWidget worldScaleOption;
    private TextFieldWidget oreRangeScaleOption;

    private List<ClickableWidget> propertyWidgets = new ArrayList<>();

    private BetaProperties customProperties = null;

    private BetaPreset currentPreset = null;

    private static Text getPercentValueText(Text prefix, double value) {
        return Text.translatable("options.percent_value", prefix, (int)(value * 100.0));
    }


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
        factorOption = createTextFieldOption("createWorld.customize.beta.factor");
        mixingOption = createTextFieldOption("createWorld.customize.beta.mixing");
        caveRarityOption = createTextFieldOption("createWorld.customize.beta.cave_rarity");
        worldScaleOption = createTextFieldOption("createWorld.customize.beta.world_scale");
        oreRangeScaleOption = createTextFieldOption("createWorld.customize.beta.ore_range_scale");
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

    @NotNull
    private TextFieldWidget createTextFieldOption(String s) {
        return new TextFieldWidget(textRenderer, this.width / 2 - 75, 0, 150, 20, Text.translatable(s));
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
        generationHeightOption.setValue(initial.generationHeight());
        seaLevelOption.setValue(initial.seaLevel());
        factorOption.setText(Float.toString(initial.factor()));
        groundLevelOption.setValue(initial.groundLevel());
        caveLavaLevelOption.setValue(initial.caveLavaLevel());
        mixingOption.setText(Float.toString(initial.mixing()));
        fixesOption.setValue(initial.fixes());
        substituteBiomesOption.setValue(initial.substituteBiomes());
        caveRarityOption.setText(Integer.toString(initial.caveRarity()));
        decliffOption.setValue((double)initial.decliff());
        worldScaleOption.setText(Float.toString(initial.worldScale()));
        oreRangeScaleOption.setText(Float.toString(initial.oreRangeScale()));
        //CREATE WIDGETS
        propertyWidgets.forEach(this::remove);
        widgetHeight = 30;
        addWidget(factorOption);
        addWidget(mixingOption);
        addWidget(decliffOption);
        addWidget(groundLevelOption);
        addWidget(seaLevelOption);
        addWidget(caveLavaLevelOption);
        addWidget(caveRarityOption);
        addWidget(oreRangeScaleOption);
        addWidget(worldScaleOption);
        addWidget(generationHeightOption);
        addWidget(fixesOption);
        addWidget(substituteBiomesOption);
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

    private int widgetHeight = 30;

    private <T> void addWidget(SimpleOption<T> option){
        widgetHeight += 25;
        propertyWidgets.add(this.addDrawableChild(option.createButton(MinecraftClient.getInstance().options, this.width / 2 - 75, widgetHeight, 150)));
    }

    private void addWidget(TextFieldWidget field){
        widgetHeight += 35;
        field.y = widgetHeight;
        propertyWidgets.add(this.addDrawableChild(field));
    }

    private BetaProperties propertiesFromOptions(){
        return new BetaProperties(
                generationHeightOption.getValue(),
                seaLevelOption.getValue(),
                carefulParseFloat(factorOption, getPreset().getProperties().factor()),
                groundLevelOption.getValue(),
                caveLavaLevelOption.getValue(),
                carefulParseFloat(mixingOption, getPreset().getProperties().mixing()),
                fixesOption.getValue(),
                substituteBiomesOption.getValue(),
                carefulParsePositiveInt(caveRarityOption, getPreset().getProperties().caveRarity()),
                decliffOption.getValue().floatValue(),
                carefulParseFloat(worldScaleOption, getPreset().getProperties().worldScale()),
                carefulParseFloat(oreRangeScaleOption, getPreset().getProperties().oreRangeScale())
        );
    }

    private float carefulParseFloat(TextFieldWidget field, float orElse){
        try {
            return Float.parseFloat(field.getText());
        }catch(NumberFormatException e){
            return orElse;
        }
    }

    private int carefulParsePositiveInt(TextFieldWidget field, int orElse){
        try {
            int parsed = Integer.parseInt(field.getText());
            return Math.max(parsed, 0);
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
        for (ClickableWidget widget : propertyWidgets) {
            if(widget instanceof TextFieldWidget field){
                drawCenteredText(matrices, this.textRenderer, field.getMessage(), this.width / 2, widget.y - 12, 0xA0A0A0);
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(BetaProperties properties) {
    return (dynamicRegistryManager, generatorOptions) -> {
        Registry<StructureSet> structures = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
        Registry<Biome> biomes = dynamicRegistryManager.get(Registry.BIOME_KEY);
        /*
        int rawId = typeRegistry.getRawId(typeRegistry.get(CabbageBeta.BETA_OVERWORLD_TYPE));
        RegistryEntry<DimensionType> typeEntry = ((MutableRegistry<DimensionType>)typeRegistry).set(rawId, CabbageBeta.BETA_OVERWORLD_TYPE, new DimensionType(t.fixedTime(), t.hasSkyLight(), t.hasCeiling(), t.ultrawarm(), t.natural(), t.coordinateScale(), t.bedWorks(), t.respawnAnchorWorks(), t.minY(), worldHeight*64, t.logicalHeight(), t.infiniburn(), t.effects(), t.ambientLight(), t.monsterSettings()), Lifecycle.stable());
        Registry<DimensionType> typeRegistry = dynamicRegistryManager.get(Registry.DIMENSION_TYPE_KEY);
        DimensionType t = typeRegistry.get(CabbageBeta.BETA_OVERWORLD_TYPE);
        int rawId = typeRegistry.getRawId(t);
        RegistryEntry<DimensionType> typeEntry = ((MutableRegistry<DimensionType>) typeRegistry).replace(OptionalInt.of(rawId), CabbageBeta.BETA_OVERWORLD_TYPE, new DimensionType(t.fixedTime(), t.hasSkyLight(), t.hasCeiling(), t.ultrawarm(), t.natural(), t.coordinateScale(), t.bedWorks(), t.respawnAnchorWorks(), t.minY(), worldHeight * 64, worldHeight * 64, t.infiniburn(), t.effects(), t.ambientLight(), t.monsterSettings()), Lifecycle.stable());
        RegistryEntry<DimensionType> typeEntry = RegistryEntry.of(new DimensionType(t.fixedTime(), t.hasSkyLight(), t.hasCeiling(), t.ultrawarm(), t.natural(), t.coordinateScale(), t.bedWorks(), t.respawnAnchorWorks(), t.minY(), worldHeight * 64, worldHeight * 64, t.infiniburn(), t.effects(), t.ambientLight(), t.monsterSettings()));
        */
        RegistryEntry<DimensionType> typeEntry = generatorOptions.getDimensions().get(DimensionOptions.OVERWORLD).getDimensionTypeEntry();
        Registry<DimensionOptions> optionRegistry = GeneratorOptions.getRegistryWithReplacedOverworld(generatorOptions.getDimensions(), typeEntry, new BetaChunkGenerator(structures, biomes, properties));
        return new GeneratorOptions(generatorOptions.getSeed(), generatorOptions.shouldGenerateStructures(), generatorOptions.hasBonusChest(), optionRegistry);
    };
}
}

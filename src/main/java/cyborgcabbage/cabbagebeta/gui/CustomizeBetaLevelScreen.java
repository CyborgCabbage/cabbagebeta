package cyborgcabbage.cabbagebeta.gui;

import cyborgcabbage.cabbagebeta.CabbageBeta;
import cyborgcabbage.cabbagebeta.gen.BetaPreset;
import cyborgcabbage.cabbagebeta.gen.BetaProperties;
import cyborgcabbage.cabbagebeta.gen.FeaturesProperty;
import cyborgcabbage.cabbagebeta.gen.beta.BetaChunkGenerator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.structure.StructureSet;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CustomizeBetaLevelScreen extends Screen {
    private final CreateWorldScreen parent;
    private final GeneratorOptionsHolder generatorOptionsHolder;
    private CyclingButtonWidget<BetaPreset> betaPresetButton;

    private OptionsListWidget optionsList;

    private final RetroOption<Integer> generationHeightOption = new RetroOption.IntegerSliderOption("generation_height", 1, 4, 64, 2);
    private final RetroOption<Integer> seaLevelOption = new RetroOption.IntegerSliderOption("sea_level", 0, 255, 64);
    private final RetroOption<Float> factorOption = new RetroOption.FloatFieldOption("factor", 12.f);
    private final RetroOption<Integer> groundLevelOption = new RetroOption.IntegerFieldOption("ground_level", 68);
    private final RetroOption<Integer> caveLavaLevelOption = new RetroOption.IntegerSliderOption("cave_lava_level", 0, 255, 10);
    private final RetroOption<Float> mixingOption = new RetroOption.FloatFieldOption("mixing", 1.f);
    private final RetroOption<Boolean> fixesOption = new RetroOption.BooleanOption("fixes", false);
    private final RetroOption<FeaturesProperty> featuresOption = new RetroOption.FeaturesOption("features", FeaturesProperty.BETA);
    private final RetroOption<Integer> caveRarityOption = new RetroOption.PositiveIntegerFieldOption("cave_rarity", 15);
    private final RetroOption<Double> decliffOption = new RetroOption.PercentOption("decliff", 0.0);
    private final RetroOption<Float> worldScaleOption = new RetroOption.FloatFieldOption("world_scale", 1.f);
    private final RetroOption<Float> oreRangeScaleOption = new RetroOption.FloatFieldOption("ore_range_scale", 1.f);
    private final RetroOption<Boolean> extendedOption = new RetroOption.BooleanOption("extended", false);

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
        if(optionsList != null) optionsList.tick();
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
                .build(this.width / 2 - 75, 25, 150, 20, Text.translatable("createWorld.customize.beta"), (button, betaPreset) -> presetInit(betaPreset)));
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
        generationHeightOption.set(initial.generationHeight());
        seaLevelOption.set(initial.seaLevel());
        factorOption.set(initial.factor());
        groundLevelOption.set(initial.groundLevel());
        caveLavaLevelOption.set(initial.caveLavaLevel());
        mixingOption.set(initial.mixing());
        fixesOption.set(initial.fixes());
        featuresOption.set(initial.features());
        caveRarityOption.set(initial.caveRarity());
        decliffOption.set((double)initial.decliff());
        worldScaleOption.set(initial.worldScale());
        oreRangeScaleOption.set(initial.oreRangeScale());
        extendedOption.set(initial.extended());
        //CREATE WIDGETS
        if(optionsList != null) this.remove(optionsList);
        List<RetroOption> options = new ArrayList<>();
        Collections.addAll(options,
                factorOption,
                mixingOption,
                groundLevelOption,
                decliffOption,
                generationHeightOption,
                worldScaleOption,
                seaLevelOption,
                caveLavaLevelOption,
                caveRarityOption,
                oreRangeScaleOption,
                fixesOption,
                featuresOption,
                extendedOption
        );
        this.optionsList = new OptionsListWidget(this, this.client, options, betaPreset == BetaPreset.CUSTOM);
        this.addSelectableChild(optionsList);

    }

    private BetaProperties propertiesFromOptions(){
        return new BetaProperties(
                generationHeightOption.get(),
                seaLevelOption.get(),
                factorOption.get(),
                groundLevelOption.get(),
                caveLavaLevelOption.get(),
                mixingOption.get(),
                fixesOption.get(),
                featuresOption.get(),
                caveRarityOption.get(),
                decliffOption.get().floatValue(),
                worldScaleOption.get(),
                oreRangeScaleOption.get(),
                extendedOption.get()
        );
    }

    private BetaPreset getPreset(){
        return betaPresetButton.getValue();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.optionsList.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
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
            RegistryEntry<DimensionType> normalType = generatorOptions.getDimensions().get(DimensionOptions.OVERWORLD).getDimensionTypeEntry();
            RegistryEntry<DimensionType> extendedType = dynamicRegistryManager.get(Registry.DIMENSION_TYPE_KEY).entryOf(CabbageBeta.BETA_OVERWORLD_EXTENDED_TYPE);
            Registry<DimensionOptions> optionRegistry = GeneratorOptions.getRegistryWithReplacedOverworld(generatorOptions.getDimensions(), properties.extended() ? extendedType : normalType, new BetaChunkGenerator(structures, biomes, properties));
            return new GeneratorOptions(generatorOptions.getSeed(), generatorOptions.shouldGenerateStructures(), generatorOptions.hasBonusChest(), optionRegistry);
        };
    }
}

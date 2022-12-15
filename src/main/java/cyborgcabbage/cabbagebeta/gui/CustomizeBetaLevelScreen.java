package cyborgcabbage.cabbagebeta.gui;

import com.mojang.serialization.Lifecycle;
import cyborgcabbage.cabbagebeta.CabbageBeta;
import cyborgcabbage.cabbagebeta.gen.*;
import cyborgcabbage.cabbagebeta.gen.beta.BetaOverworldChunkGenerator;
import cyborgcabbage.cabbagebeta.gen.beta.BetaNetherChunkGenerator;
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
import net.minecraft.util.registry.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;

import java.util.*;

@Environment(EnvType.CLIENT)
public class CustomizeBetaLevelScreen extends Screen {
    private final CreateWorldScreen parent;
    private final GeneratorOptionsHolder generatorOptionsHolder;

    private CyclingButtonWidget<BetaPreset> overworldPresetButton;
    private CyclingButtonWidget<BetaNetherPreset> netherPresetButton;

    private OptionsListWidget overworldOptions;
    private OptionsListWidget netherOptions;

    private final RetroOption<Integer> generationHeightOption = new RetroOption.IntegerSliderOption("generation_height", 1, 4, 64, 2);
    private final RetroOption<Integer> oceanLevelOption = new RetroOption.IntegerFieldOption("ocean_level", 64);
    private final RetroOption<Float> factorOption = new RetroOption.FloatFieldOption("factor", 12.f);
    private final RetroOption<Integer> groundLevelOption = new RetroOption.IntegerFieldOption("ground_level", 68);
    private final RetroOption<Integer> caveLavaLevelOption = new RetroOption.IntegerFieldOption("cave_lava_level", 10);
    private final RetroOption<Float> mixingOption = new RetroOption.FloatFieldOption("mixing", 1.f);
    private final RetroOption<Boolean> fixesOption = new RetroOption.BooleanOption("fixes", false);
    private final RetroOption<FeaturesProperty> featuresOption = new RetroOption.FeaturesOption("features", FeaturesProperty.BETA);
    private final RetroOption<Integer> caveRarityOption = new RetroOption.PositiveIntegerFieldOption("cave_rarity", 15);
    private final RetroOption<Double> decliffOption = new RetroOption.PercentOption("decliff", 0.0);
    private final RetroOption<Float> worldScaleOption = new RetroOption.FloatFieldOption("world_scale", 1.f);
    private final RetroOption<Float> oreRangeScaleOption = new RetroOption.FloatFieldOption("ore_range_scale", 1.f);
    private final RetroOption<Boolean> extendedOption = new RetroOption.BooleanOption("extended", false);

    private final RetroOption<Integer> netherGenerationHeightOption = new RetroOption.IntegerSliderOption("generation_height", 1, 4, 64, 2);
    private final RetroOption<Integer> netherOceanLevelOption = new RetroOption.IntegerFieldOption("ocean_level", 32);
    private final RetroOption<Float> netherTerrainScaleOption = new RetroOption.FloatFieldOption("terrain_scale", 1.f);
    private final RetroOption<Boolean> netherCeilingOption = new RetroOption.BooleanOption("ceiling", true);

    private BetaProperties customOverworldProperties = null;

    private BetaNetherProperties customNetherProperties = null;

    private BetaPreset currentOverworldPreset = null;

    private BetaNetherPreset currentNetherPreset = null;

    private boolean netherSelected = false;

    public CustomizeBetaLevelScreen(CreateWorldScreen parent, GeneratorOptionsHolder generatorOptionsHolder) {
        super(Text.translatable("createWorld.customize.beta.title"));
        this.parent = parent;
        this.generatorOptionsHolder = generatorOptionsHolder;
    }

    @Override
    public void tick() {
        super.tick();
        if(overworldOptions != null) overworldOptions.tick();
        if(netherOptions != null) netherOptions.tick();
        //UPDATE CUSTOM PROPERTIES
        if(getOverworldPreset() == BetaPreset.CUSTOM){
            customOverworldProperties = propertiesFromOptions();
        }
        if(getNetherPreset() == BetaNetherPreset.CUSTOM){
            customNetherProperties = netherPropertiesFromOptions();
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
        if(currentOverworldPreset == null) {
            currentOverworldPreset = BetaPreset.CUSTOM;
            if (generatorOptionsHolder.generatorOptions().getDimensions().get(DimensionOptions.OVERWORLD).getChunkGenerator() instanceof BetaOverworldChunkGenerator gen) {
                for (BetaPreset value : BetaPreset.values()) {
                    if (value.getProperties().match(gen.getProperties())) {
                        currentOverworldPreset = value;
                        break;
                    }
                }
            } else {
                currentOverworldPreset = BetaPreset.FAITHFUL;
            }
        }
        if(currentNetherPreset == null) {
            currentNetherPreset = BetaNetherPreset.CUSTOM;
            if(generatorOptionsHolder.generatorOptions().getDimensions().get(DimensionOptions.NETHER).getChunkGenerator() instanceof BetaNetherChunkGenerator gen) {
                for (BetaNetherPreset value : BetaNetherPreset.values()) {
                    if (value.getProperties().match(gen.getProperties())) {
                        currentNetherPreset = value;
                        break;
                    }
                }
            } else {
                currentNetherPreset = BetaNetherPreset.FAITHFUL;
            }
        }
        //CUSTOM PROPERTIES
        if(customOverworldProperties == null) {
            if (generatorOptionsHolder.generatorOptions().getDimensions().get(DimensionOptions.OVERWORLD).getChunkGenerator() instanceof BetaOverworldChunkGenerator gen && currentOverworldPreset == BetaPreset.CUSTOM) {
                customOverworldProperties = gen.getProperties().clone();
            } else {
                customOverworldProperties = BetaPreset.CUSTOM.getProperties().clone();
            }
        }
        if(customNetherProperties == null) {
            if (generatorOptionsHolder.generatorOptions().getDimensions().get(DimensionOptions.NETHER).getChunkGenerator() instanceof BetaNetherChunkGenerator gen && currentNetherPreset == BetaNetherPreset.CUSTOM) {
                customNetherProperties = gen.getProperties().clone();
            } else {
                customNetherProperties = BetaNetherPreset.CUSTOM.getProperties().clone();
            }
        }
        //BETA PRESET
        this.overworldPresetButton = CyclingButtonWidget.builder(BetaPreset::getTranslatableName)
                .values(BetaPreset.values())
                .initially(currentOverworldPreset)
                .build(this.width / 2 - 75, 25, 150, 20, Text.translatable("createWorld.customize.beta"), (button, betaPreset) -> presetInit(betaPreset));
        this.netherPresetButton = CyclingButtonWidget.builder(BetaNetherPreset::getTranslatableName)
                .values(BetaNetherPreset.values())
                .initially(currentNetherPreset)
                .build(this.width / 2 - 75, 25, 150, 20, Text.translatable("createWorld.customize.beta"), (button, betaPreset) -> netherPresetInit(betaPreset));
        //DIRECTION BUTTONS
        int gap = 5;
        int width = 10;
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 75 - gap - width, 25, width, 20, Text.literal("<"), b -> {
            netherSelected = !netherSelected;
            initSelectedDimension();
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 75 + gap, 25, width, 20, Text.literal(">"), b -> {
            netherSelected = !netherSelected;
            initSelectedDimension();
        }));
        //SET VALUES
        presetInit(currentOverworldPreset);
        netherPresetInit(currentNetherPreset);
        //CONFIRM
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, button -> {
            this.parent.moreOptionsDialog.apply(createModifier(propertiesFromOptions(), netherPropertiesFromOptions()));
            this.client.setScreen(this.parent);
        }));
        //CANCEL
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)));
        initSelectedDimension();
    }

    private void initSelectedDimension() {
        remove(netherPresetButton);
        remove(netherOptions);
        remove(overworldPresetButton);
        remove(overworldOptions);
        if(netherSelected) {
            addDrawableChild(netherPresetButton);
            addSelectableChild(netherOptions);
        }else{
            addDrawableChild(overworldPresetButton);
            addSelectableChild(overworldOptions);
        }
    }

    private void presetInit(BetaPreset betaPreset) {
        //SET AS PRESET
        currentOverworldPreset = betaPreset;
        //COPY VALUES INTO OPTIONS
        BetaProperties initial;
        if(betaPreset != BetaPreset.CUSTOM) {
            initial = betaPreset.getProperties();
        }else{
            initial = customOverworldProperties;
        }
        generationHeightOption.set(initial.generationHeight());
        oceanLevelOption.set(initial.oceanLevel());
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
        if(overworldOptions != null) this.remove(overworldOptions);
        List<RetroOption> options = new ArrayList<>();
        Collections.addAll(options,
                factorOption,
                mixingOption,
                groundLevelOption,
                decliffOption,
                generationHeightOption,
                worldScaleOption,
                oceanLevelOption,
                caveLavaLevelOption,
                caveRarityOption,
                oreRangeScaleOption,
                fixesOption,
                featuresOption,
                extendedOption
        );
        this.overworldOptions = new OptionsListWidget(this, this.client, options, betaPreset == BetaPreset.CUSTOM);
        this.addSelectableChild(overworldOptions);
    }

    private void netherPresetInit(BetaNetherPreset betaPreset) {
        //SET AS PRESET
        currentNetherPreset = betaPreset;
        //COPY VALUES INTO OPTIONS
        BetaNetherProperties initial;
        if(betaPreset != BetaNetherPreset.CUSTOM) {
            initial = betaPreset.getProperties();
        }else{
            initial = customNetherProperties;
        }
        netherGenerationHeightOption.set(initial.generationHeight());
        netherOceanLevelOption.set(initial.oceanLevel());
        netherTerrainScaleOption.set(initial.terrainScale());
        netherCeilingOption.set(initial.ceiling());
        //CREATE WIDGETS
        if(netherOptions != null) this.remove(netherOptions);
        List<RetroOption> options = new ArrayList<>();
        Collections.addAll(options,
                netherGenerationHeightOption,
                netherOceanLevelOption,
                netherTerrainScaleOption,
                netherCeilingOption
        );
        this.netherOptions = new OptionsListWidget(this, this.client, options, betaPreset == BetaNetherPreset.CUSTOM);
        this.addSelectableChild(netherOptions);
    }

    private BetaProperties propertiesFromOptions(){
        return new BetaProperties(
                generationHeightOption.get(),
                oceanLevelOption.get(),
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
    private BetaNetherProperties netherPropertiesFromOptions(){
        return new BetaNetherProperties(
                netherGenerationHeightOption.get(),
                netherOceanLevelOption.get(),
                netherTerrainScaleOption.get(),
                netherCeilingOption.get()
        );
    }

    private BetaPreset getOverworldPreset(){
        return overworldPresetButton.getValue();
    }

    private BetaNetherPreset getNetherPreset(){
        return netherPresetButton.getValue();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        if(netherSelected) {
            this.netherOptions.render(matrices, mouseX, mouseY, delta);
        } else {
            this.overworldOptions.render(matrices, mouseX, mouseY, delta);
        }
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }


    private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(BetaProperties properties, BetaNetherProperties netherProperties) {
        return (dynamicRegistryManager, generatorOptions) -> {
            Registry<StructureSet> structures = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
            Registry<Biome> biomes = dynamicRegistryManager.get(Registry.BIOME_KEY);
            RegistryEntry<DimensionType> overworldType = generatorOptions.getDimensions().get(DimensionOptions.OVERWORLD).getDimensionTypeEntry();
            if(properties.extended()) overworldType = dynamicRegistryManager.get(Registry.DIMENSION_TYPE_KEY).entryOf(CabbageBeta.BETA_OVERWORLD_EXTENDED_TYPE);
            RegistryEntry<DimensionType> netherType = generatorOptions.getDimensions().get(DimensionOptions.NETHER).getDimensionTypeEntry();
            Registry<DimensionOptions> options = generatorOptions.getDimensions();
            SimpleRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
            mutableRegistry.add(DimensionOptions.OVERWORLD, new DimensionOptions(overworldType, new BetaOverworldChunkGenerator(structures, biomes, properties)), Lifecycle.stable());
            mutableRegistry.add(DimensionOptions.NETHER, new DimensionOptions(netherType, new BetaNetherChunkGenerator(structures, biomes, netherProperties)), Lifecycle.stable());

            for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : options.getEntrySet()) {
                if (!mutableRegistry.contains(entry.getKey())) {
                    mutableRegistry.add(entry.getKey(), entry.getValue(), options.getEntryLifecycle(entry.getValue()));
                }
            }

            return new GeneratorOptions(generatorOptions.getSeed(), generatorOptions.shouldGenerateStructures(), generatorOptions.hasBonusChest(), mutableRegistry);
        };
    }
}

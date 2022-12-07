package cyborgcabbage.cabbagebeta.gui;

import cyborgcabbage.cabbagebeta.gen.BetaPreset;
import cyborgcabbage.cabbagebeta.gen.BetaProperties;
import cyborgcabbage.cabbagebeta.gen.beta.BetaChunkGenerator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
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
    private static final Text BUFFET_BIOME_TEXT = Text.translatable("createWorld.customize.buffet.biome");
    private final CreateWorldScreen parent;
    private GeneratorOptionsHolder generatorOptionsHolder;
    private CyclingButtonWidget<BetaPreset> betaPresetButton;

    private SimpleOption<Boolean> useFullHeightOption = SimpleOption.ofBoolean("gui.beta_preset.use_full_height", SimpleOption.constantTooltip(Text.translatable("gui.beta_preset.use_full_height.tip")), false);
    private SimpleOption<Integer> seaLevelOption = new SimpleOption<>("gui.beta_preset.sea_level", SimpleOption.constantTooltip(Text.translatable("gui.beta_preset.sea_level.tip")), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), 64, a -> {});
    private TextFieldWidget factorOption;
    private SimpleOption<Integer> groundLevelOption = new SimpleOption<>("gui.beta_preset.ground_level", SimpleOption.constantTooltip(Text.translatable("gui.beta_preset.ground_level.tip")), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), 68, a -> {});
    private SimpleOption<Integer> caveLavaLevelOption = new SimpleOption<>("gui.beta_preset.cave_lava_level", SimpleOption.constantTooltip(Text.translatable("gui.beta_preset.cave_lava_level.tip")), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), 10, a -> {});
    private TextFieldWidget mixingOption;
    private SimpleOption<Boolean> fixesOption = SimpleOption.ofBoolean("gui.beta_preset.fixes", SimpleOption.constantTooltip(Text.translatable("gui.beta_preset.fixes.tip")), false);

    private List<ClickableWidget> propertyWidgets = new ArrayList<>();

    public CustomizeBetaLevelScreen(CreateWorldScreen parent, GeneratorOptionsHolder generatorOptionsHolder) {
        super(Text.translatable("createWorld.customize.buffet.title"));
        this.parent = parent;
        this.generatorOptionsHolder = generatorOptionsHolder;
    }

    @Override
    public void tick() {
        super.tick();
        factorOption.tick();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        //Determine initial preset
        BetaPreset initial = BetaPreset.CUSTOM;
        if(generatorOptionsHolder.generatorOptions().getChunkGenerator() instanceof BetaChunkGenerator gen) {
            for (BetaPreset value : BetaPreset.values()) {
                boolean match = value.match(gen.getUseFullHeight(), gen.getSeaLevel(), gen.getFactor(), gen.getGroundLevel(), gen.getCaveLavaLevel(), gen.getMixing(), gen.getFixes());
                if(match){
                    initial = value;
                    break;
                }
            }
        }else{
            initial = BetaPreset.FAITHFUL;
        }
        //BETA PRESET
        this.betaPresetButton = this.addDrawableChild(CyclingButtonWidget.builder(BetaPreset::getTranslatableName)
                .values(BetaPreset.values())
                .initially(initial)
                .build(this.width / 2 - 75, 40, 150, 20, Text.translatable("gui.beta_preset"), (button, betaPreset) -> {
                    valuesFromPreset(betaPreset);
                    createPropertyWidgets();
                    setPropertyActive(betaPreset == BetaPreset.CUSTOM);
                }));
        factorOption = new TextFieldWidget(textRenderer, this.width / 2 - 75, 0, 150, 20, Text.translatable("gui.beta_preset.factor"));
        mixingOption = new TextFieldWidget(textRenderer, this.width / 2 - 75, 0, 150, 20, Text.translatable("gui.beta_preset.mixing"));
        createPropertyWidgets();
        //SET VALUES
        BetaProperties valueSource = initial;
        if(generatorOptionsHolder.generatorOptions().getChunkGenerator() instanceof BetaChunkGenerator gen) {
            valueSource = gen;
        }
        valuesFromPreset(valueSource);
        createPropertyWidgets();
        //Set active
        if(initial != BetaPreset.CUSTOM) {
            setPropertyActive(false);
        }
        //CONFIRM
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, button -> {
            BetaPreset b = betaPresetButton.getValue();
            this.parent.moreOptionsDialog.apply(createModifier(useFullHeightOption.getValue(), seaLevelOption.getValue(), carefulParse(factorOption, getPreset().getFactor()), groundLevelOption.getValue(), caveLavaLevelOption.getValue(), carefulParse(mixingOption, getPreset().getMixing()), fixesOption.getValue()));
            this.client.setScreen(this.parent);
        }));
        //CANCEL
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)));
    }

    private void setPropertyActive(boolean custom) {
        propertyWidgets.forEach(c -> c.active = custom);
        propertyWidgets.forEach(c -> {
            if(c instanceof TextFieldWidget t) t.setEditable(custom);
        });
    }

    private void createPropertyWidgets() {
        propertyWidgets.forEach(this::remove);
        int baseHeight = 80;
        propertyWidgets.add(this.addDrawableChild(useFullHeightOption.createButton(MinecraftClient.getInstance().options, this.width / 2 - 75, baseHeight, 150)));
        baseHeight += 25;
        propertyWidgets.add(this.addDrawableChild(seaLevelOption.createButton(MinecraftClient.getInstance().options, this.width / 2 - 75, baseHeight, 150)));
        baseHeight += 25;
        factorOption.y = baseHeight;
        propertyWidgets.add(this.addDrawableChild(factorOption));
        baseHeight += 25;
        propertyWidgets.add(this.addDrawableChild(groundLevelOption.createButton(MinecraftClient.getInstance().options, this.width / 2 - 75, baseHeight, 150)));
        baseHeight += 25;
        propertyWidgets.add(this.addDrawableChild(caveLavaLevelOption.createButton(MinecraftClient.getInstance().options, this.width / 2 - 75, baseHeight, 150)));
        baseHeight += 25;
        mixingOption.y = baseHeight;
        propertyWidgets.add(this.addDrawableChild(mixingOption));
        baseHeight += 25;
        propertyWidgets.add(this.addDrawableChild(fixesOption.createButton(MinecraftClient.getInstance().options, this.width / 2 - 75, baseHeight, 150)));
    }

    private void valuesFromPreset(BetaProperties initial){
        useFullHeightOption.setValue(initial.getUseFullHeight());
        seaLevelOption.setValue(initial.getSeaLevel());
        factorOption.setText(Float.toString(initial.getFactor()));
        groundLevelOption.setValue(initial.getGroundLevel());
        caveLavaLevelOption.setValue(initial.getCaveLavaLevel());
        mixingOption.setText(Float.toString(initial.getMixing()));
        fixesOption.setValue(initial.getFixes());
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
        CustomizeBuffetLevelScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        CustomizeBuffetLevelScreen.drawCenteredText(matrices, this.textRenderer, BUFFET_BIOME_TEXT, this.width / 2, 28, 0xA0A0A0);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(boolean useFullHeight, int seaLevel, float factor, int groundLevel, int caveLavaLevel, float mixing, boolean fixes) {
        return (dynamicRegistryManager, generatorOptions) -> {
            Registry<StructureSet> structures = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
            Registry<Biome> biomes = dynamicRegistryManager.get(Registry.BIOME_KEY);
            BetaChunkGenerator chunkGenerator = new BetaChunkGenerator(structures, biomes, "overworld", useFullHeight, seaLevel, factor, groundLevel, caveLavaLevel, mixing, fixes);
            return GeneratorOptions.create(dynamicRegistryManager, generatorOptions, chunkGenerator);
        };
    }
}

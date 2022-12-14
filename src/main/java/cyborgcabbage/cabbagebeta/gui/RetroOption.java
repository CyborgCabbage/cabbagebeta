package cyborgcabbage.cabbagebeta.gui;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import cyborgcabbage.cabbagebeta.gen.FeaturesProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.TranslatableOption;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public abstract class RetroOption<T> {
    private final String id;
    private final boolean titleAbove;

    public RetroOption(@NotNull String id, boolean titleAbove) {
        this.id = id;
        this.titleAbove = titleAbove;
    }

    public String getTranslationKey(){
        return "createWorld.customize.beta."+id;
    }

    public boolean hasTitleAbove(){
        return titleAbove;
    }

    public abstract T get();

    public abstract void set(T value);

    public abstract ClickableWidget getWidget();

    public static class PercentOption extends RetroOption<Double> {
        private final SimpleOption<Double> simpleOption;
        public PercentOption(String id, double defaultValue) {
            super(id, false);
            simpleOption = new SimpleOption<>(getTranslationKey(), SimpleOption.constantTooltip(Text.translatable(getTranslationKey())), PercentOption::getPercentValueText, SimpleOption.DoubleSliderCallbacks.INSTANCE, defaultValue, value -> {});
        }

        private static Text getPercentValueText(Text prefix, double value) {
            return Text.translatable("options.percent_value", prefix, (int)(value * 100.0));
        }

        public Double get(){
            return simpleOption.getValue();
        }

        @Override
        public void set(Double value) {
            simpleOption.setValue(value);
        }

        @Override
        public ClickableWidget getWidget() {
            return simpleOption.createButton(MinecraftClient.getInstance().options, 0, 0,150);
        }
    }

    public static class IntegerSliderOption extends RetroOption<Integer> {
        private final SimpleOption<Integer> simpleOption;
        private final int multiplier;

        public IntegerSliderOption(String id, int min, int max, int multiplier, int defaultValue) {
            super(id, false);
            this.multiplier = multiplier;
            simpleOption = new SimpleOption<>(getTranslationKey(), SimpleOption.constantTooltip(Text.translatable(getTranslationKey()+".tip")), (prefix, integer) -> GameOptions.getGenericValueText(prefix, integer*multiplier), new SimpleOption.ValidatingIntSliderCallbacks(min, max), defaultValue, a -> {});
        }

        public IntegerSliderOption(String id, int min, int max, int defaultValue) {
            this(id, min, max, 1, defaultValue);
        }

        public Integer get(){
            return simpleOption.getValue()*multiplier;
        }

        @Override
        public void set(Integer value) {
            simpleOption.setValue(value/multiplier);
        }

        @Override
        public ClickableWidget getWidget() {
            return simpleOption.createButton(MinecraftClient.getInstance().options, 0, 0,150);
        }
    }

    public static class IntegerFieldOption extends RetroOption<Integer> {
        private final TextFieldWidget textField;
        private final int defaultValue;

        public IntegerFieldOption(@NotNull String id, int defaultValue) {
            super(id, true);
            this.defaultValue = defaultValue;
            this.textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 150, 20, Text.translatable(getTranslationKey()));
            set(defaultValue);
        }

        @Override
        public Integer get() {
            try {
                return Integer.parseInt(textField.getText());
            }catch(NumberFormatException e){
                return defaultValue;
            }
        }

        @Override
        public void set(Integer value) {
            textField.setText(Integer.toString(value));
        }

        @Override
        public ClickableWidget getWidget() {
            return textField;
        }
    }

    public static class PositiveIntegerFieldOption extends IntegerFieldOption {

        public PositiveIntegerFieldOption(@NotNull String id, int defaultValue) {
            super(id, defaultValue);
        }

        @Override
        public Integer get() {
            return Math.max(super.get(), 0);
        }
    }

    public static class FloatFieldOption extends RetroOption<Float> {
        private final TextFieldWidget textField;
        private final float defaultValue;

        public FloatFieldOption(@NotNull String id, float defaultValue) {
            super(id, true);
            this.defaultValue = defaultValue;
            this.textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 150, 20, Text.translatable(getTranslationKey()));
            set(defaultValue);
        }

        @Override
        public Float get() {
            try {
                return Float.parseFloat(textField.getText());
            }catch(NumberFormatException e){
                return defaultValue;
            }
        }

        @Override
        public void set(Float value) {
            textField.setText(Float.toString(value));
        }

        @Override
        public ClickableWidget getWidget() {
            return textField;
        }
    }

    public static class BooleanOption extends RetroOption<Boolean> {
        private final SimpleOption<Boolean> simpleOption;

        public BooleanOption(@NotNull String id, boolean defaultValue) {
            super(id, false);
            simpleOption = SimpleOption.ofBoolean(getTranslationKey(), SimpleOption.constantTooltip(Text.translatable(getTranslationKey()+".tip")), defaultValue);
        }

        @Override
        public Boolean get() {
            return simpleOption.getValue();
        }

        @Override
        public void set(Boolean value) {
            simpleOption.setValue(value);
        }

        @Override
        public ClickableWidget getWidget() {
            return simpleOption.createButton(MinecraftClient.getInstance().options, 0, 0, 150);
        }
    }

    public static class FeaturesOption extends RetroOption<FeaturesProperty> {
        private final SimpleOption<FeaturesProperty> simpleOption;

        public FeaturesOption(@NotNull String id, FeaturesProperty defaultValue) {
            super(id, false);
            simpleOption = new SimpleOption<>(
                    getTranslationKey(),
                    SimpleOption.emptyTooltip(),
                    SimpleOption.enumValueText(),
                    new SimpleOption.PotentialValuesBasedCallbacks<>(
                            Arrays.asList(FeaturesProperty.values()),
                            Codec.INT.xmap(FeaturesProperty::byId, FeaturesProperty::getId)
                    ),
                    defaultValue,
                    a -> {}
            );
        }

        @Override
        public FeaturesProperty get() {
            return simpleOption.getValue();
        }

        @Override
        public void set(FeaturesProperty value) {
            simpleOption.setValue(value);
        }

        @Override
        public ClickableWidget getWidget() {
            return simpleOption.createButton(MinecraftClient.getInstance().options, 0, 0, 150);
        }
    }
}

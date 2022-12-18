package cyborgcabbage.cabbagebeta.gen;

import net.minecraft.text.Text;

public enum BetaPreset {
    FAITHFUL("faithful", new BetaProperties(128, 64, 12, 68, 10, 1, false, FeaturesProperty.BETA, 15, 0, 1, 1, false, 1.f)),
    IMPROVED("improved", new BetaProperties(128, 64, 12, 68, 10, 1, true, FeaturesProperty.BETA, 15, 0, 1, 1, false, 1.f)),
    AMPLIFIED("amplified", new BetaProperties(256, 64, 6, 68, 10, .5f, true, FeaturesProperty.BETA, 15, .3f, 1, 1, false, 1.f)),
    FLOODED("flooded", new BetaProperties(128, 84, 12, 68, 10, 1, false, FeaturesProperty.BETA, 15, 0, 1, 1, false, 1.f)),
    SMOOTH("smooth", new BetaProperties(128, 64, 12, 68, 10, .5f, false, FeaturesProperty.BETA, 15, 0, 1, 1, false, 1.f)),
    CAVING("caving", new BetaProperties(256, 128, 9, 132, 20, 1, true, FeaturesProperty.BETA, 4, 0, 1, 2, false, 1.f)),
    DOUBLE_SCALE("double_scale", new BetaProperties(256, 128, 12, 68, 20, 1, true, FeaturesProperty.BETA, 15, 0, 2, 2, false, 2.f)),
    HALF_SCALE("half_scale", new BetaProperties(64, 32, 12, 68, 6, 1, true, FeaturesProperty.BETA, 15, 0, .5f, .5f, false, .5f)),
    CUSTOM("custom", new BetaProperties(128, 64, 12, 68, 10, 1, false, FeaturesProperty.BETA, 15, 0, 1, 1, false, 1.f));

    private final String name;
    private final BetaProperties properties;

    BetaPreset(String name, BetaProperties properties) {
        this.name = name;
        this.properties = properties;
    }

    public Text getTranslatableName() {
        return Text.translatable("options.beta_preset." + this.name);
    }

    public BetaProperties getProperties() {
        return properties;
    }
}

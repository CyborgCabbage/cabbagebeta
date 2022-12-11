package cyborgcabbage.cabbagebeta.gen;

import net.minecraft.text.Text;

public enum BetaPreset {
    FAITHFUL("faithful", new BetaProperties(2, 64, 12, 68, 10, 1, false, false, 15, 0, 1, 1)),
    IMPROVED("improved", new BetaProperties(2, 64, 12, 68, 10, 1, true, false, 15, 0, 1, 1)),
    AMPLIFIED("amplified", new BetaProperties(4, 64, 6, 68, 10, .5f, true, false, 15, .3f, 1, 1)),
    FLOODED("flooded", new BetaProperties(2, 84, 12, 68, 10, 1, false, false, 15, 0, 1, 1)),
    SMOOTH("smooth", new BetaProperties(2, 64, 12, 68, 10, .5f, false, false, 15, 0, 1, 1)),
    CAVING("caving", new BetaProperties(4, 128, 9, 132, 20, 1, true, false, 4, 0, 1, 2)),
    MAJOR("major", new BetaProperties(4, 128, 12, 68, 20, 1, true, false, 15, 0, 0.5f, 2)),
    MINOR("minor", new BetaProperties(1, 32, 12, 68, 6, 1, true, false, 15, 0, 2.0f, 0.5f)),
    CUSTOM("custom", new BetaProperties(2, 64, 12, 68, 10, 1, false, false, 15, 0, 1, 1));

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

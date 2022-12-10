package cyborgcabbage.cabbagebeta.gen;

import net.minecraft.text.Text;

public enum BetaPreset {
    FAITHFUL("faithful", new BetaProperties(false, 64, 12, 68, 10, 1, false, false)),
    IMPROVED("improved", new BetaProperties(false, 64, 12, 68, 10, 1, true, false)),
    AMPLIFIED("amplified", new BetaProperties(true, 64, 6, 68, 10, .5f, true, false)),
    FLOODED("flooded", new BetaProperties(false, 84, 12, 68, 10, 1, false, false)),
    SMOOTH("smooth", new BetaProperties(false, 64, 12, 68, 10, .5f, false, false)),
    CUSTOM("custom", new BetaProperties(false, 64, 12, 68, 10, 1, false, false));

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

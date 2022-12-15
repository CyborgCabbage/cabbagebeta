package cyborgcabbage.cabbagebeta.gen;

import net.minecraft.text.Text;

public enum BetaNetherPreset {
    FAITHFUL("faithful", new BetaNetherProperties(128, 32, 1, true)),
    AMPLIFIED("amplified", new BetaNetherProperties(256, 32, 1, true)),
    CUSTOM("custom", new BetaNetherProperties(128, 32, 1, true));


    private final String name;
    private final BetaNetherProperties properties;

    BetaNetherPreset(String name, BetaNetherProperties properties) {
        this.name = name;
        this.properties = properties;
    }

    public Text getTranslatableName() {
        return Text.translatable("options.beta_preset." + this.name);
    }

    public BetaNetherProperties getProperties() {
        return properties;
    }
}

package cyborgcabbage.cabbagebeta.gen;

import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;

public enum FeaturesProperty implements TranslatableOption {
    BETA(0, "createWorld.customize.beta.features.beta"),
    BETA_AND_RESOURCES(1, "createWorld.customize.beta.features.beta_and_resources"),
    MODERN(2, "createWorld.customize.beta.features.modern");

    private final int id;
    private final String translationKey;

    FeaturesProperty(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public static FeaturesProperty byId(int id) {
        return values()[MathHelper.floorMod(id, values().length)];
    }
}

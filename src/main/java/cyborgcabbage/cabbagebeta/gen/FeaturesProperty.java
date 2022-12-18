package cyborgcabbage.cabbagebeta.gen;

import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;

public enum FeaturesProperty implements TranslatableOption {
    BETA(0, "createWorld.customize.beta.features.beta", false),
    MODERN(1, "createWorld.customize.beta.features.modern", true);

    private final int id;
    private final String translationKey;
    private final boolean modernFeatures;

    FeaturesProperty(int id, String translationKey, boolean modernFeatures) {
        this.id = id;
        this.translationKey = translationKey;
        this.modernFeatures = modernFeatures;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public boolean modern() {
        return this.modernFeatures;
    }

    public static FeaturesProperty byId(int id) {
        return values()[MathHelper.floorMod(id, values().length)];
    }
}

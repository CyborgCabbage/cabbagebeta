package cyborgcabbage.cabbagebeta.gen;

import net.minecraft.text.Text;

public enum BetaPreset implements BetaProperties {
    FAITHFUL("faithful", false, 64, 12, 68, 10, 1, false),
    IMPROVED("improved", false, 64, 12, 68, 10, 1, true),
    AMPLIFIED("amplified", true, 64, 6, 68, 10, .5f, true),
    FLOODED("flooded", false, 84, 12, 68, 10, 1, false),
    SMOOTH("smooth", false, 64, 12, 68, 10, .5f, false),
    CUSTOM("custom", false, 64, 12, 68, 10, 1, false);

    private final String name;
    private final boolean useFullHeight;
    private final int seaLevel;
    private final float factor;
    private final int groundLevel;
    private final int caveLavaLevel;
    private final float mixing;
    private final boolean fixes;

    BetaPreset(String name, boolean useFullHeight, int seaLevel, float factor, int groundLevel, int caveLavaLevel, float mixing, boolean fixes) {
        this.name = name;
        this.useFullHeight = useFullHeight;
        this.seaLevel = seaLevel;
        this.factor = factor;
        this.groundLevel = groundLevel;
        this.caveLavaLevel = caveLavaLevel;
        this.mixing = mixing;
        this.fixes = fixes;
    }

    public Text getTranslatableName() {
        return Text.translatable("options.beta_preset." + this.name);
    }

    public String getName() {
        return name;
    }

    public boolean getUseFullHeight() {
        return useFullHeight;
    }

    public int getSeaLevel() {
        return seaLevel;
    }

    public float getFactor() {
        return factor;
    }

    public int getGroundLevel() {
        return groundLevel;
    }

    public int getCaveLavaLevel() {
        return caveLavaLevel;
    }

    public float getMixing() {
        return mixing;
    }

    public boolean getFixes() {
        return fixes;
    }

    public boolean match(boolean useFullHeight, int seaLevel, float factor, int groundLevel, int caveLavaLevel, float mixing, boolean fixes){
        if(useFullHeight != this.useFullHeight) return false;
        if(seaLevel != this.seaLevel) return false;
        if(factor != this.factor) return false;
        if(groundLevel != this.groundLevel) return false;
        if(caveLavaLevel != this.caveLavaLevel) return false;
        if(mixing != this.mixing) return false;
        if(fixes != this.fixes) return false;
        return true;
    }
}

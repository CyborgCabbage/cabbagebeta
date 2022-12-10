package cyborgcabbage.cabbagebeta.gen;

public record BetaProperties(boolean useFullHeight, int seaLevel, float factor, int groundLevel, int caveLavaLevel, float mixing, boolean fixes, boolean substituteBiomes) implements Cloneable{
    public boolean match(BetaProperties other){
        if(useFullHeight != other.useFullHeight()) return false;
        if(seaLevel != other.seaLevel()) return false;
        if(factor != other.factor()) return false;
        if(groundLevel != other.groundLevel()) return false;
        if(caveLavaLevel != other.caveLavaLevel()) return false;
        if(mixing != other.mixing()) return false;
        if(fixes != other.fixes()) return false;
        if(substituteBiomes != other.substituteBiomes()) return false;
        return true;
    }

    @Override
    public BetaProperties clone() {
        return new BetaProperties(useFullHeight, seaLevel, factor, groundLevel, caveLavaLevel, mixing, fixes, substituteBiomes);
    }
}

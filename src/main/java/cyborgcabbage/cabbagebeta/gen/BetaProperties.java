package cyborgcabbage.cabbagebeta.gen;

public record BetaProperties(int generationHeight, int oceanLevel, float factor, int groundLevel, int caveLavaLevel, float mixing, boolean fixes, FeaturesProperty features, int caveRarity, float decliff, float worldScale, float oreRangeScale, boolean extended) implements Cloneable{
    public boolean match(BetaProperties other){
        if(generationHeight != other.generationHeight()) return false;
        if(oceanLevel != other.oceanLevel()) return false;
        if(factor != other.factor()) return false;
        if(groundLevel != other.groundLevel()) return false;
        if(caveLavaLevel != other.caveLavaLevel()) return false;
        if(mixing != other.mixing()) return false;
        if(fixes != other.fixes()) return false;
        if(features != other.features()) return false;
        if(caveRarity != other.caveRarity()) return false;
        if(decliff != other.decliff()) return false;
        if(worldScale != other.worldScale()) return false;
        if(oreRangeScale != other.oreRangeScale()) return false;
        if(extended != other.extended()) return false;
        return true;
    }

    @Override
    public BetaProperties clone() {
        return new BetaProperties(generationHeight, oceanLevel, factor, groundLevel, caveLavaLevel, mixing, fixes, features, caveRarity, decliff, worldScale, oreRangeScale, extended);
    }
}

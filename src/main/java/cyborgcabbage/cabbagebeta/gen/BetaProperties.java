package cyborgcabbage.cabbagebeta.gen;

public record BetaProperties(int generationHeight, int seaLevel, float factor, int groundLevel, int caveLavaLevel, float mixing, boolean fixes, boolean substituteBiomes, int caveRarity, float decliff, float worldScale, float oreRangeScale) implements Cloneable{
    public boolean match(BetaProperties other){
        if(generationHeight != other.generationHeight()) return false;
        if(seaLevel != other.seaLevel()) return false;
        if(factor != other.factor()) return false;
        if(groundLevel != other.groundLevel()) return false;
        if(caveLavaLevel != other.caveLavaLevel()) return false;
        if(mixing != other.mixing()) return false;
        if(fixes != other.fixes()) return false;
        if(substituteBiomes != other.substituteBiomes()) return false;
        if(caveRarity != other.caveRarity()) return false;
        if(decliff != other.decliff()) return false;
        if(worldScale != other.worldScale()) return false;
        if(oreRangeScale != other.oreRangeScale()) return false;
        return true;
    }

    @Override
    public BetaProperties clone() {
        return new BetaProperties(generationHeight, seaLevel, factor, groundLevel, caveLavaLevel, mixing, fixes, substituteBiomes, caveRarity, decliff, worldScale, oreRangeScale);
    }
}

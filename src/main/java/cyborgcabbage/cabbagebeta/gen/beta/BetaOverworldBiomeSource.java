package cyborgcabbage.cabbagebeta.gen.beta;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cyborgcabbage.cabbagebeta.CabbageBeta;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class BetaOverworldBiomeSource extends BiomeSource {
    public static final Codec<BetaOverworldBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(biomeSource -> null)).apply(instance, instance.stable(BetaOverworldBiomeSource::new)));
    private BetaChunkProvider gen;
    private final RegistryEntry<Biome> rainforest;
    private final RegistryEntry<Biome> swampland;
    private final RegistryEntry<Biome> seasonalForest;
    private final RegistryEntry<Biome> forest;
    private final RegistryEntry<Biome> savanna;
    private final RegistryEntry<Biome> shrubland;
    private final RegistryEntry<Biome> taiga;
    private final RegistryEntry<Biome> desert;
    private final RegistryEntry<Biome> plains;
    private final RegistryEntry<Biome> iceDesert;
    private final RegistryEntry<Biome> tundra;
    private final RegistryEntry<Biome> hell;
    private final RegistryEntry<Biome> sky;

    protected BetaOverworldBiomeSource(Registry<Biome> biomeRegistry) {
        this(
                biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_RAINFOREST),
                biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_SWAMPLAND),
                biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_SEASONAL_FOREST),
                biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_FOREST),
                biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_SAVANNA),
                biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_SHRUBLAND),
                biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_TAIGA),
                biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_DESERT),
                biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_PLAINS),
                biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_ICE_DESERT),
                biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_TUNDRA),
                biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_HELL),
                biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_SKY)
        );
    }

    public BetaOverworldBiomeSource(RegistryEntry<Biome> _rainforest, RegistryEntry<Biome> _swampland, RegistryEntry<Biome> _seasonalForest, RegistryEntry<Biome> _forest, RegistryEntry<Biome> _savanna, RegistryEntry<Biome> _shrubland, RegistryEntry<Biome> _taiga, RegistryEntry<Biome> _desert, RegistryEntry<Biome> _plains, RegistryEntry<Biome> _iceDesert, RegistryEntry<Biome> _tundra, RegistryEntry<Biome> _hell, RegistryEntry<Biome> _sky) {
        super(ImmutableList.of(_rainforest, _swampland, _seasonalForest, _forest, _savanna, _shrubland, _taiga, _desert, _plains, _iceDesert, _tundra, _hell, _sky));
        rainforest = _rainforest;
        swampland = _swampland;
        seasonalForest = _seasonalForest;
        forest = _forest;
        savanna = _savanna;
        shrubland = _shrubland;
        taiga = _taiga;
        desert = _desert;
        plains = _plains;
        iceDesert = _iceDesert;
        tundra = _tundra;
        hell = _hell;
        sky = _sky;
    }

    public void setGenerator(BetaChunkProvider generator){
        this.gen = generator;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        if(!gen.isInitialised()) return hell;
        if(gen == null) return hell;
        BiomeGenBase b = gen.getBiome(BiomeCoords.toBlock(x), BiomeCoords.toBlock(z));
        if(b == BiomeGenBase.rainforest) return rainforest;
        if(b == BiomeGenBase.swampland) return swampland;
        if(b == BiomeGenBase.seasonalForest) return seasonalForest;
        if(b == BiomeGenBase.forest) return forest;
        if(b == BiomeGenBase.savanna) return savanna;
        if(b == BiomeGenBase.shrubland) return shrubland;
        if(b == BiomeGenBase.taiga) return taiga;
        if(b == BiomeGenBase.desert) return desert;
        if(b == BiomeGenBase.plains) return plains;
        if(b == BiomeGenBase.iceDesert) return iceDesert;
        if(b == BiomeGenBase.tundra) return tundra;
        if(b == BiomeGenBase.hell) return hell;
        if(b == BiomeGenBase.sky) return sky;
        return hell;
    }
}

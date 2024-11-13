package io.vn.nguyenduck.blocktopograph.core.biome;

public enum Biome {
    Ocean("ocean", 0),
    Plains("plains", 1),
    Desert("desert", 2),
    WindsweptHills("extreme_hills", 3),
    Forest("forest", 4),
    Taiga("taiga", 5),
    Swamp("swampland", 6),
    River("river", 7),
    NetherWastes("hell", 8),
    TheEnd("the_end", 9),
    LegacyFrozenOcean("legacy_frozen_ocean", 10),
    FrozenRiver("frozen_river", 11),
    SnowyPlains("ice_plains", 12),
    SnowyMountains("ice_mountains", 13),
    MushroomFields("mushroom_island", 14),
    MushroomFieldShore("mushroom_island_shore", 15),
    Beach("beach", 16),
    DesertHills("desert_hills", 17),
    WoodedHills("forest_hills", 18),
    TaigaHills("taiga_hills", 19),
    MountainEdge("extreme_hills_edge", 20),
    Jungle("jungle", 21),
    JungleHills("jungle_hills", 22),
    SparseJungle("jungle_edge", 23),
    DeepOcean("deep_ocean", 24),
    StonyShore("stone_beach", 25),
    SnowyBeach("cold_beach", 26),
    BirchForest("birch_forest", 27),
    BirchForestHills("birch_forest_hills", 28),
    DarkForest("roofed_forest", 29),
    SnowyTaiga("cold_taiga", 30),
    SnowyTaigaHills("cold_taiga_hills", 31),
    OldGrowthPineTaiga("mega_taiga", 32),
    GiantTreeTaigaHills("mega_taiga_hills", 33),
    WindsweptForest("extreme_hills_plus_trees", 34),
    Savanna("savanna", 35),
    SavannaPlateau("savanna_plateau", 36),
    Badlands("mesa", 37),
    WoodedBadlands("mesa_plateau_stone", 38),
    BadlandsPlateau("mesa_plateau", 39),
    WarmOcean("warm_ocean", 40),
    DeepWarmOcean("deep_warm_ocean", 41),
    LukewarmOcean("lukewarm_ocean", 42),
    DeepLukewarmOcean("deep_lukewarm_ocean", 43),
    ColdOcean("cold_ocean", 44),
    DeepColdOcean("deep_cold_ocean", 45),
    FrozenOcean("frozen_ocean", 46),
    DeepFrozenOcean("deep_frozen_ocean", 47),
    BambooJungle("bamboo_jungle", 48),
    BambooJungleHills("bamboo_jungle_hills", 49),
    SunflowerPlains("sunflower_plains", 129),
    DesertLakes("desert_mutated", 130),
    WindsweptGravellyHills("extreme_hills_mutated", 131),
    FlowerForest("flower_forest", 132),
    TaigaMountains("taiga_mutated", 133),
    SwampHills("swampland_mutated", 134),
    IceSpikes("ice_plains_spikes", 140),
    ModifiedJungle("jungle_mutated", 149),
    ModifiedJungleEdge("jungle_edge_mutated", 151),
    OldGrowthBirchForest("birch_forest_mutated", 155),
    TallBirchHills("birch_forest_hills_mutated", 156),
    DarkForestHills("roofed_forest_mutated", 157),
    SnowyTaigaMountains("cold_taiga_mutated", 158),
    OldGrowthSpruceTaiga("redwood_taiga_mutated", 160),
    GiantSpruceTaigaHills("redwood_taiga_hills_mutated", 161),
    GravellyMountains("extreme_hills_plus_trees_mutated", 162),
    WindsweptSavanna("savanna_mutated", 163),
    ShatteredSavannaPlateau("savanna_plateau_mutated", 164),
    ErodedBadlands("mesa_bryce", 165),
    ModifiedWoodedBadlandsPlateau("mesa_plateau_stone_mutated", 166),
    ModifiedBadlandsPlateau("mesa_plateau_mutated", 167),
    SoulSandValley("soulsand_valley", 178),
    CrimsonForest("crimson_forest", 179),
    WarpedForest("warped_forest", 180),
    BasaltDeltas("basalt_deltas", 181),
    JaggedPeaks("jagged_peaks", 182),
    FrozenPeaks("frozen_peaks", 183),
    SnowySlopes("snowy_slopes", 184),
    Grove("grove", 185),
    Meadow("meadow", 186),
    LushCaves("lush_caves", 187),
    DripstoneCaves("dripstone_caves", 188),
    StonyPeaks("stony_peaks", 189),
    DeepDark("deep_dark", 190),
    MangroveSwamp("mangrove_swamp", 191),
    CherryGrove("cherry_grove", 192),
    ;

    private final String identifier;
    private final int index;

    Biome(String identifier, int index) {
        this.identifier = identifier;
        this.index = index;
    }

    public static Biome fromIdentifier(String identifier) {
        for (Biome biome : Biome.values()) {
            if (biome.identifier.equals(identifier)) {
                return biome;
            }
        }
        return null;
    }

    public static Biome fromIndex(int index) {
        for (Biome biome : Biome.values()) {
            if (biome.index == index) {
                return biome;
            }
        }
        return null;
    }
}
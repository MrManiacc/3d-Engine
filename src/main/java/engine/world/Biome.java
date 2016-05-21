package engine.world;

import engine.entities.Entity;
import engine.models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;
import engine.renderEngine.Loader;
import engine.terrains.Terrain;
import engine.textures.TerrainTexture;
import engine.textures.TerrainTexturePack;
import engine.water.WaterTile;

import java.util.List;
import java.util.Random;

/**
 * Created by productionaccount on 1/12/16.
 */
public class Biome {
    private Terrain terrain;
    private int gridX, gridZ;
    private int seed;
    private int seaLevel;

    private boolean isCustom = false;
    private TerrainTexturePack terrainTexturePack;
    private TerrainTexture blendMap;
    private static Loader loader;
    private static TerrainTexturePack defaultTerrainTexturePack;
    private static TerrainTexture defaultBlendMap;

    private static final int possibleEntitiesAmount = 500;
    private static List<TexturedModel> possibleModels;
    private static List<Terrain> terrains;
    private static List<WaterTile> waterTiles;
    private static List<Entity> entities;


    public Biome(int gridX, int gridZ, int seaLevel, List<TexturedModel> possibleModels) {
        this.gridX = gridX;
        this.gridZ = gridZ;
        this.seaLevel = seaLevel;
        this.possibleModels = possibleModels;
        generate();
    }


    public Biome(TerrainTexturePack terrainTexturePack, TerrainTexture blendMap, int gridX, int gridZ, int seaLevel, List<TexturedModel> possibleModels) {
        this.gridX = gridX;
        this.gridZ = gridZ;
        this.seaLevel = seaLevel;
        this.terrainTexturePack = terrainTexturePack;
        this.blendMap = blendMap;
        this.possibleModels = possibleModels;
        isCustom = true;
        generate();
    }


    public static void init(Loader loader, TerrainTexturePack defaultTerrainTexturePack, TerrainTexture defaultBlendMap, List<Terrain> terrains, List<WaterTile> waterTiles, List<Entity> entities) {
        Biome.loader = loader;
        Biome.defaultTerrainTexturePack = defaultTerrainTexturePack;
        Biome.defaultBlendMap = defaultBlendMap;
        Biome.terrains = terrains;
        Biome.waterTiles = waterTiles;
        Biome.entities = entities;
    }

    public void generate() {
        if (!isCustom)
            this.terrain = new Terrain(gridX, gridZ, loader, defaultTerrainTexturePack, defaultBlendMap, seaLevel);
        else
            this.terrain = new Terrain(gridX, gridZ, loader, terrainTexturePack, blendMap, seaLevel);
        terrains.add(terrain);
        this.seed = terrain.getSeed();
        fillEntities();
        fillWater();
    }

    private void fillEntities() {
        Random random = new Random();
        random.setSeed(seed);
        if (possibleModels.size() > 0) {
            for (int i = 0; i < possibleEntitiesAmount; i++) {
                float x = 0;
                if (gridX != 0)
                    x = random.nextFloat() * gridX * terrain.getSize();
                else
                    x = random.nextFloat() * terrain.getSize();
                float z = 0;
                if (gridZ != 0)
                    z = random.nextFloat() * gridZ * terrain.getSize();
                else
                    z = random.nextFloat() * terrain.getSize();
                float y = terrain.getHeightOfTerrain(x, z);
                if (y > seaLevel) {
                    int size = possibleModels.size();
                    TexturedModel texturedModel = possibleModels.get(random.nextInt(size));
                    entities.add(new Entity(texturedModel, new Vector3f(x, y, z), 0, 0, 0, 1));
                }
            }
        }
    }

    private void fillWater() {
        float x;
        if (gridX != 0)
            x = (terrain.getSize() * gridX) / 2;
        else
            x = terrain.getSize() / 2;

        float z;
        if (gridZ != 0)
            z = (terrain.getSize() * gridZ) / 2;
        else
            z = terrain.getSize() / 2;
        System.out.println(x + " " + z);

        waterTiles.add(new WaterTile(x, z, terrain.getSeaLevel(), (int) terrain.getSize() / 2));
    }

    public Terrain getTerrain() {
        return terrain;
    }
}

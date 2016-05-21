package game;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import engine.engineTester.GameProgram;
import engine.entities.Camera;
import engine.entities.Entity;
import engine.entities.Light;
import engine.entities.Player;
import engine.fontMeshCreator.FontType;
import engine.fontRendering.TextMaster;
import engine.models.TexturedModel;
import engine.particles.ParticleMaster;
import engine.terrains.Terrain;
import engine.textures.ModelTexture;
import engine.textures.TerrainTexture;
import engine.textures.TerrainTexturePack;
import engine.water.WaterFrameBuffers;
import engine.water.WaterRenderer;
import engine.water.WaterShader;
import engine.water.WaterTile;

/**
 * Created by Vtboy on 5/21/2016.
 */
public class Game extends GameProgram {
    /* Objects that need to be accessed from update loop */
    //Main player object
    private Player player;
    //Main camera object
    private Camera camera;
    //List of regular world entities
    private List<Entity> entities = new ArrayList<Entity>();
    //List of normal map world entities
    private List<Entity> normalMappedEntities = new ArrayList<Entity>();
    //Main terrain (used as a place holder for now)
    private Terrain terrain;
    //List of water tiles & water buffers & water renderer & water shader
    private List<WaterTile> waterTiles = new ArrayList<WaterTile>();
    private WaterFrameBuffers waterFrameBuffers;
    private WaterRenderer waterRenderer;
    private WaterShader waterShader;
    //List of lights
    private List<Light> lights = new ArrayList<Light>();


    @Override
    public void start() {
        /* Font related */
        FontType mainFont = new FontType(loader.loadTexture("candara"), new File(fileHandler.getFont("candara")));

        /* Terrain Related */
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        this.terrain = new Terrain(0, 0, loader, texturePack, blendMap, 0);

        /* Models */
        //Fern model
        ModelTexture fernTexture = new ModelTexture(loader.loadTexture("fern"));
        fernTexture.setNumberOfRows(2);
        TexturedModel fernModel = new TexturedModel(OBJloader.loadRegularObj("fern"), fernTexture);
        fernModel.getTexture().setHasTransparency(true);
        //Player model
        ModelTexture playerTexture = new ModelTexture(loader.loadTexture("playerTexture"));
        TexturedModel playerModel = new TexturedModel(OBJloader.loadRegularObj("person"), playerTexture);
        //Barrel Model
        TexturedModel barrelModel = new TexturedModel(OBJloader.loadNormalMappedObj("barrel"), new ModelTexture(loader.loadTexture("barrel")));
        barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
        barrelModel.getTexture().setShineDamper(10);
        barrelModel.getTexture().setReflectivity(0.5f);
        //Boulder model
        TexturedModel boulderModel = new TexturedModel(OBJloader.loadNormalMappedObj("boulder"), new ModelTexture(loader.loadTexture("boulder")));
        boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
        boulderModel.getTexture().setShineDamper(10);
        boulderModel.getTexture().setReflectivity(0.5f);
        //Crate Model
        TexturedModel crateModel = new TexturedModel(OBJloader.loadNormalMappedObj("crate"), new ModelTexture(loader.loadTexture("crate")));
        crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
        crateModel.getTexture().setShineDamper(10);
        crateModel.getTexture().setReflectivity(0.5f);
        //Lamp Model
        TexturedModel lamp = new TexturedModel(OBJloader.loadRegularObj("lamp"), new ModelTexture(loader.loadTexture("lamp")));
        lamp.getTexture().setUseFakeLighting(true);
        //Pine Model
        TexturedModel bobble = new TexturedModel(OBJloader.loadRegularObj("pine"), new ModelTexture(loader.loadTexture("pine")));
        bobble.getTexture().setHasTransparency(true);
        //Rocks Model
        TexturedModel rocks = new TexturedModel(OBJloader.loadRegularObj("rocks"), new ModelTexture(loader.loadTexture("rocks")));

        /* Player & Camera related */
        this.player = new Player(playerModel, new Vector3f(0, 0, 0), 0, 0, 0, 1);
        this.camera = new Camera(player);
        this.entities.add(player);
        this.camera.toggleFreecam();

        /* Water rendering */
        this.waterFrameBuffers = new WaterFrameBuffers();
        this.waterShader = new WaterShader();
        this.waterRenderer = new WaterRenderer(loader, waterShader, masterRenderer.getProjectionMatrix(), this.waterFrameBuffers);

        /* Light related */
        Light sun = new Light(new Vector3f(10000, 10000, -10000), new Vector3f(1.3f, 1.3f, 1.3f));
        lights.add(sun);

    }

    @Override
    public void update() {
        this.player.move(this.terrain);
        this.camera.move();

        ParticleMaster.update(this.camera);

        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

        for (WaterTile water : this.waterTiles) {
            //render reflection texture
            this.waterFrameBuffers.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - water.getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            masterRenderer.renderScene(entities, this.normalMappedEntities, this.terrain, this.lights, this.camera, new Vector4f(0, 1, 0, -water.getHeight() + 1));
            camera.getPosition().y += distance;
            camera.invertPitch();

            //render refraction texture
            waterFrameBuffers.bindRefractionFrameBuffer();
            masterRenderer.renderScene(entities, this.normalMappedEntities, this.terrain, this.lights, this.camera, new Vector4f(0, -1, 0, water.getHeight()));

        }
        //render to screen
        GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
        waterFrameBuffers.unbindCurrentFrameBuffer();
        masterRenderer.renderScene(entities, this.normalMappedEntities, this.terrain, this.lights, this.camera, new Vector4f(0, -1, 0, 100000));
        waterRenderer.render(this.waterTiles, camera, lights.get(0));
        ParticleMaster.render(camera);
        TextMaster.render();

        handleInput();
    }

    @Override
    public void stop() {
        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        this.waterFrameBuffers.cleanUp();
        this.waterShader.cleanUp();
    }

    private void handleInput() {
        while (Keyboard.next()) {
            //FreeCam input
            if (Keyboard.isKeyDown(Keyboard.KEY_F1)) camera.toggleFreecam();
            //Mouse input
            if (Keyboard.isKeyDown(Keyboard.KEY_F2)) {
                Mouse.setGrabbed(!Mouse.isGrabbed());
                camera.shouldCheckFreeCamInput(!camera.isFreecamInput());
            }
        }
    }
}


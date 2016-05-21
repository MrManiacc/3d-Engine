package engine.engineTester;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engine.entities.Camera;
import engine.entities.Entity;
import engine.entities.Light;
import engine.entities.Player;
import engine.fontMeshCreator.FontType;
import engine.fontMeshCreator.GUIText;
import engine.fontRendering.TextMaster;
import engine.guis.GuiRenderer;
import engine.guis.GuiTexture;
import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.particles.ParticleMaster;
import engine.particles.ParticleSystem;
import engine.particles.ParticleTexture;
import engine.renderEngine.DisplayManager;
import engine.renderEngine.Loader;
import engine.renderEngine.MasterRenderer;
import engine.renderEngine.OBJLoader;
import engine.terrains.Terrain;
import engine.textures.ModelTexture;
import engine.textures.TerrainTexture;
import engine.textures.TerrainTexturePack;
import engine.toolbox.FileHandler;
import engine.water.WaterFrameBuffers;
import engine.water.WaterRenderer;
import engine.water.WaterShader;
import engine.water.WaterTile;
import engine.world.Biome;

public class MainGameLoop {

    public static void run(FileHandler fileHandler) throws URISyntaxException {
        fileHandler.loadNatives();

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        OBJLoader objLoader = new OBJLoader(loader);
        TextMaster.init(loader);
        MasterRenderer renderer = new MasterRenderer(loader);
        ParticleMaster.init(loader, renderer.getProjectionMatrix());

        FontType font = new FontType(loader.loadTexture("candara"), new File(fileHandler.getFont("candara")));

        // *********TERRAIN TEXTURE STUFF**********

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
                gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        // *****************************************

        TexturedModel rocks = new TexturedModel(objLoader.loadRegularObj("rocks"), new ModelTexture(loader.loadTexture("rocks")));

        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTextureAtlas.setNumberOfRows(2);

        TexturedModel fern = new TexturedModel(objLoader.loadRegularObj("fern"),
                fernTextureAtlas);

        TexturedModel bobble = new TexturedModel(objLoader.loadRegularObj("pine"),
                new ModelTexture(loader.loadTexture("pine")));
        bobble.getTexture().setHasTransparency(true);

        fern.getTexture().setHasTransparency(true);

        List<Terrain> terrains = new ArrayList<Terrain>();

        TexturedModel lamp = new TexturedModel(objLoader.loadRegularObj("lamp"), new ModelTexture(loader.loadTexture("lamp")));
        lamp.getTexture().setUseFakeLighting(true);

        List<Entity> entities = new ArrayList<Entity>();
        List<Entity> normalMapEntities = new ArrayList<Entity>();

        //******************NORMAL MAP MODELS************************

        TexturedModel barrelModel = new TexturedModel(objLoader.loadNormalMappedObj("barrel"),
                new ModelTexture(loader.loadTexture("barrel")));
        barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
        barrelModel.getTexture().setShineDamper(10);
        barrelModel.getTexture().setReflectivity(0.5f);

        TexturedModel crateModel = new TexturedModel(objLoader.loadNormalMappedObj("crate"),
                new ModelTexture(loader.loadTexture("crate")));
        crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
        crateModel.getTexture().setShineDamper(10);
        crateModel.getTexture().setReflectivity(0.5f);

        TexturedModel boulderModel = new TexturedModel(objLoader.loadNormalMappedObj("boulder"),
                new ModelTexture(loader.loadTexture("boulder")));
        boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
        boulderModel.getTexture().setShineDamper(10);
        boulderModel.getTexture().setReflectivity(0.5f);


        //************ENTITIES*******************

        //*******************OTHER SETUP***************

        List<Light> lights = new ArrayList<Light>();
        Light sun = new Light(new Vector3f(10000, 10000, -10000), new Vector3f(1.3f, 1.3f, 1.3f));
        lights.add(sun);

        RawModel bunnyModel = objLoader.loadRegularObj("person");
        TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(
                loader.loadTexture("playerTexture")));

        Player player = new Player(stanfordBunny, new Vector3f(0, 0, 0), 0, 100, 0, 0.6f);
        entities.add(player);
        Camera camera = new Camera(player);
        List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        // MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), null);

        //**********Water Renderer Set-up************************

        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
        List<WaterTile> waters = new ArrayList<WaterTile>();

        //*********Particle System below**********************

        //Fire
        ParticleTexture fireTexture = new ParticleTexture(loader.loadTexture("fire"), 8);
        fireTexture.setAdditive(true);
        ParticleSystem fireSystem = new ParticleSystem(fireTexture, 400, 10, 0.1f, 2, 5f);
        fireSystem.setDirection(new Vector3f(0, 2, 0), 0.1f);
        fireSystem.setLifeError(0.2f);
        fireSystem.setSpeedError(0.6f);
        fireSystem.setScaleError(1f);
        fireSystem.randomizeRotation();

        //Cosmic
        ParticleTexture cosmicTexture = new ParticleTexture(loader.loadTexture("cosmic"), 4);
        cosmicTexture.setAdditive(true);
        ParticleSystem cosmicSystem = new ParticleSystem(cosmicTexture, 200, 10, 0.1f, 1, 2f);
        fireSystem.setDirection(new Vector3f(0, 2, 0), 0.1f);
        fireSystem.setLifeError(0.2f);
        fireSystem.setSpeedError(0.6f);
        fireSystem.setScaleError(1f);
        fireSystem.randomizeRotation();


        //****************Biome Code below*********************
        Biome.init(loader, texturePack, blendMap, terrains, waters, entities);
        List<TexturedModel> possibleModels = new ArrayList<TexturedModel>();
        possibleModels.add(fern);
        possibleModels.add(bobble);
        Biome main = new Biome(0, -1, -10, possibleModels);

        //****************Game Loop Below*********************

        while (!Display.isCloseRequested()) {
            player.move(main.getTerrain());
            camera.move();
            //picker.update();

            if (Keyboard.isKeyDown(Keyboard.KEY_U))
                fireSystem.generateParticles(new Vector3f(player.getPosition()));
            if (Keyboard.isKeyDown(Keyboard.KEY_I))
                cosmicSystem.generateParticles(new Vector3f(player.getPosition()));

            ParticleMaster.update(camera);

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            for (WaterTile water : waters) {
                //render reflection texture
                buffers.bindReflectionFrameBuffer();
                float distance = 2 * (camera.getPosition().y - water.getHeight());
                camera.getPosition().y -= distance;
                camera.invertPitch();
                renderer.renderScene(entities, normalMapEntities, null, lights, camera, new Vector4f(0, 1, 0, -water.getHeight() + 1));
                camera.getPosition().y += distance;
                camera.invertPitch();

                //render refraction texture
                buffers.bindRefractionFrameBuffer();
                renderer.renderScene(entities, normalMapEntities, null, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));
            }

            //render to screen
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            buffers.unbindCurrentFrameBuffer();
            renderer.renderScene(entities, normalMapEntities, null, lights, camera, new Vector4f(0, -1, 0, 100000));
            waterRenderer.render(waters, camera, sun);
            ParticleMaster.render(camera);
            guiRenderer.render(guiTextures);
            TextMaster.render();

            DisplayManager.updateDisplay();
        }

        //*********Clean Up Below**************
        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        buffers.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }


}

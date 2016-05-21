package engine.engineTester;

import org.lwjgl.opengl.Display;

import java.net.URISyntaxException;

import engine.fontRendering.TextMaster;
import engine.particles.ParticleMaster;
import engine.renderEngine.DisplayManager;
import engine.renderEngine.Loader;
import engine.renderEngine.MasterRenderer;
import engine.renderEngine.OBJLoader;
import engine.toolbox.FileHandler;

/**
 * Created by Vtboy on 5/21/2016.
 */
public class GameLauncher {
    private Game game;
    private FileHandler fileHandler;

    public GameLauncher(Game game, FileHandler fileHandler) {
        this.game = game;
        this.fileHandler = fileHandler;
    }

    public void init() {
        game.setFileHandler(fileHandler);
        //Primary initialization
        try {
            fileHandler.loadNatives();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        game.setLoader(loader);
        TextMaster.init(loader);
        MasterRenderer renderer = new MasterRenderer(loader);
        game.setMasterRenderer(renderer);
        ParticleMaster.init(loader, renderer.getProjectionMatrix());
        OBJLoader objLoader = new OBJLoader(loader);
        game.setOBJLoader(objLoader);
        game.start();
        //
        while(!Display.isCloseRequested()){
            game.update();
            DisplayManager.updateDisplay();
        }
        game.stop();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}


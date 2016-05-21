package engine.engineTester;

import engine.renderEngine.Loader;
import engine.renderEngine.MasterRenderer;
import engine.renderEngine.OBJLoader;
import engine.toolbox.FileHandler;

/**
 * Created by Vtboy on 5/21/2016.
 */
public interface Game {
    void start();

    void update();

    void stop();

    void setLoader(Loader loader);

    void setMasterRenderer(MasterRenderer masterRenderer);

    void setOBJLoader(OBJLoader objLoader);

    void setFileHandler(FileHandler fileHandler);
}

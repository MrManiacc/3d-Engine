package engine.engineTester;

import engine.renderEngine.Loader;
import engine.renderEngine.MasterRenderer;
import engine.renderEngine.OBJLoader;
import engine.toolbox.FileHandler;

/**
 * Created by Vtboy on 5/21/2016.
 */
public class GameProgram implements Game {

    protected Loader loader;
    protected OBJLoader OBJloader;
    protected MasterRenderer masterRenderer;
    protected FileHandler fileHandler;


    @Override
    public void start() {
    }

    @Override
    public void update() {

    }

    @Override
    public void stop() {
    }

    @Override
    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    @Override
    public void setMasterRenderer(MasterRenderer masterRenderer) {
        this.masterRenderer = masterRenderer;
    }

    @Override
    public void setOBJLoader(OBJLoader objLoader) {
        this.OBJloader = objLoader;
    }

    @Override
    public void setFileHandler(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }
}

package engineTester;

import toolbox.FileHandler;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Vtboy on 1/10/2016.
 */
public class Main {
    public static FileHandler fileHandler;

    public static void main(String[] args) throws URISyntaxException, IOException {
        if (args.length == 0) {
            System.out.println("Please supply one of the following arguments");
            System.out.println("dev - Only for development use!");
            System.out.println("launch - For launching after using 'copy' argument");
            System.out.println("copy - For copying resources from jar to current dir");
        } else {
            if (args[0].equalsIgnoreCase("dev")) {
                fileHandler = new FileHandler(false);
                MainGameLoop.run();
            } else if (args[0].equalsIgnoreCase("copy")) {
                fileHandler = new FileHandler(false);
                fileHandler.loadFromJar();
            } else if (args[0].equalsIgnoreCase("launch")) {
                fileHandler = new FileHandler(true);
                MainGameLoop.run();
            }
        }

    }


}

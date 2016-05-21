package game;

import engine.engineTester.GameProgram;
import engine.engineTester.Primary;

/**
 * Created by Vtboy on 5/21/2016.
 */
public class Main {

    public static void main(String[] args) {
        Primary.initialize(args, new Game());
    }
}

package engine.guis;

import engine.engineTester.Primary;
import org.lwjgl.util.vector.Matrix4f;

import engine.shaders.ShaderProgram;

public class GuiShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = Primary.fileHandler.getShader("guiVertexShader");
	private static final String FRAGMENT_FILE = Primary.fileHandler.getShader("guiFragmentShader");
	
	private int location_transformationMatrix;

	public GuiShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadTransformation(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	
	

}

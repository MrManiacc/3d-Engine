package engine.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import engine.renderEngine.DisplayManager;

public class Camera {

    private float distanceFromPlayer = 35;
    private float angleAroundPlayer = 0;

    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch = 20;
    private float yaw = 0;
    private float roll;
    private float MoveSpeed = 6f;
    private final float OriginalMoveSpeed = MoveSpeed;

    private float angle = 0;

    private Player player;

    private boolean freecamInput = true;

    private boolean MoveForward = false, MoveBackward = false, MoveLeft = false, MoveRight = false, MoveUp = false, MoveDown = false, SpeedMultipler = false;

    private boolean isFreecam = false;

    public Camera(Player player) {
        this.player = player;
    }

    public void move() {
        if (!isFreecam) {
            calculateZoom();
            calculatePitch();
            calculateAngleAroundPlayer();
            float horizontalDistance = calculateHorizontalDistance();
            float verticalDistance = calculateVerticalDistance();
            calculateCameraPosition(horizontalDistance, verticalDistance);
            this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
            yaw %= 360;
        } else {
            if (freecamInput) {
                checkKeys();
                freecam();
            }
        }
    }


    public void toggleFreecam() {
        if (isFreecam) {
            angleAroundPlayer = 0;
            distanceFromPlayer = 35;
            isFreecam = false;
            Mouse.setGrabbed(false);
            player.setCanMove(true);
        } else {
            isFreecam = true;
            Mouse.setGrabbed(true);
            player.setCanMove(false);
        }
    }

    private void checkKeys() {
        MoveForward = (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP));
        MoveBackward = (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN));
        MoveRight = (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT));
        MoveLeft = (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT));
        MoveUp = (Keyboard.isKeyDown(Keyboard.KEY_SPACE));
        MoveDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT));
        SpeedMultipler = (Keyboard.isKeyDown(Keyboard.KEY_TAB) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL));
    }


    public void shouldCheckFreeCamInput(boolean b) {
        this.freecamInput = b;
    }

    public boolean isFreecamInput() {
        return freecamInput;
    }

    public void invertPitch() {
        this.pitch = -pitch;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateCameraPosition(float horizDistance, float verticDistance) {
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticDistance + 4;
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch + 4)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch + 4)));
    }

    private void calculateZoom() {
        float zoomLevel = Mouse.getDWheel() * 0.03f;
        distanceFromPlayer -= zoomLevel;
        if (distanceFromPlayer < 5) {
            distanceFromPlayer = 5;
        }
    }

    private void calculatePitch() {
        if (Mouse.isButtonDown(1)) {
            float pitchChange = Mouse.getDY() * 0.2f;
            pitch -= pitchChange;
            if (pitch < 0) {
                pitch = 0;
            } else if (pitch > 90) {
                pitch = 90;
            }
        }
    }

    private void calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(0)) {
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }


    private void freecam() {

        this.pitch -= Mouse.getDY() * 0.1f;
        this.yaw += Mouse.getDX() * 0.1f;

        //player.setRotY(yaw);
        this.angle = yaw;
        if (MoveSpeed == OriginalMoveSpeed && SpeedMultipler) MoveSpeed *= 4;
        else MoveSpeed = OriginalMoveSpeed;

        if (MoveRight) {
            float rotation = angle + 270;
            Vector3f newPosition = new Vector3f(position);
            float s = (MoveSpeed * 5) * DisplayManager.getFrameTimeSeconds();
            float a = s * (float) Math.cos(Math.toRadians(rotation));
            float o = (float) (Math.sin(Math.toRadians(rotation)) * s);
            newPosition.z += a;
            newPosition.x -= o;
            position.x = newPosition.x;
            position.z = newPosition.z;
        }

        if (MoveBackward) {
            float rotation = angle;
            Vector3f newPosition = new Vector3f(position);
            float s = (MoveSpeed * 5) * DisplayManager.getFrameTimeSeconds();
            float a = s * (float) Math.cos(Math.toRadians(rotation));
            float o = (float) (Math.sin(Math.toRadians(rotation)) * s);
            newPosition.z += a;
            newPosition.x -= o;
            position.x = newPosition.x;
            position.z = newPosition.z;
        }

        if (MoveLeft) {
            float rotation = angle + 90;
            Vector3f newPosition = new Vector3f(position);
            float s = (MoveSpeed * 5) * DisplayManager.getFrameTimeSeconds();
            float a = s * (float) Math.cos(Math.toRadians(rotation));
            float o = (float) (Math.sin(Math.toRadians(rotation)) * s);
            newPosition.z += a;
            newPosition.x -= o;
            position.x = newPosition.x;
            position.z = newPosition.z;
        }

        if (MoveForward) {
            float rotation = angle + 180;
            Vector3f newPosition = new Vector3f(position);
            float s = (MoveSpeed * 5) * DisplayManager.getFrameTimeSeconds();
            float a = s * (float) Math.cos(Math.toRadians(rotation));
            float o = (float) (Math.sin(Math.toRadians(rotation)) * s);
            newPosition.z += a;
            newPosition.x -= o;
            position.x = newPosition.x;
            position.z = newPosition.z;
        }
        if (MoveUp) position.y += (MoveSpeed * 5) * DisplayManager.getFrameTimeSeconds();
        if (MoveDown) position.y -= (MoveSpeed * 5) * DisplayManager.getFrameTimeSeconds();
    }


}

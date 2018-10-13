package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Controls {

    private Base base;

    private double linear;
    private double turn;
    private boolean slowMode = false;
    private boolean slowButton = false;
    private boolean resetButton = false;

    private OpMode mOpMode;

    //Initializes the controls object.
    public Controls(OpMode opMode) {
        mOpMode = opMode;
        base = new Base(opMode);

        mOpMode.gamepad1.setJoystickDeadzone(.05f);
        mOpMode.gamepad2.setJoystickDeadzone(.05f);
    }

    //Drive Method
    //Arcade Mode uses left stick to go forward, and right stick to turn.
    public void moveArcade() {
        linear = -mOpMode.gamepad1.left_stick_y;
        turn = mOpMode.gamepad1.right_stick_x;
        base.move(linear, turn);
    }

    //Drive Method
    //Tank Mode uses one stick to control each wheel.
    public void moveTank() {
        //In this case, linear refers to the left side, and turn to the right.
        linear = -mOpMode.gamepad1.left_stick_y;
        turn = -mOpMode.gamepad1.right_stick_y;
        base.setPower(linear, turn);
    }

    //Drive Method
    //Gyro-Assisted TeleOp Mode uses left stick to go forward, and right stick to absolute turn.
    public void moveGAT() {
        linear = -mOpMode.gamepad1.left_stick_y;

        //Right Stick Movement Control
        double AngleRStick = Math.toDegrees(Math.atan2(mOpMode.gamepad1.right_stick_y, -mOpMode.gamepad1.right_stick_x));

        //In order for the turning to adhere to the plane we start on, we need to reflect our x values.
        if (AngleRStick < 0) {
            AngleRStick += 360;
        }

        //Don't move if our right stick is at 0, or not a valid number (within axis).
        if (Double.isNaN(AngleRStick) || (AngleRStick == 180 && mOpMode.gamepad1.right_stick_x != 1)) {
            turn = 0;
        }
        //If our drive-train is moving linearly, then turn with a weighted amount based on turn.
        else if (linear != 0) {
            turn = base.turnAbsolute(.5, AngleRStick - 180) * 3.25;
        }
        //If none of the following conditions apply, just turn.
        else {
            turn = base.turnAbsolute(.5, AngleRStick - 180);
        }

        toggleSpeed(slowButton, resetButton);

        base.move(linear, turn);
    }

    //Movement Modifier
    //Toggles the speed of our drive-train between normal and slow (half speed) mode.
    public void toggleSpeed(boolean slow, boolean reset) {
        mOpMode.gamepad1.setJoystickDeadzone(.05f);
        ElapsedTime delay = new ElapsedTime();
        if (delay.milliseconds() > 250) {  //Allows time for button release.
            if (slow && !slowMode) { //Slow is the slow mode button.
                mOpMode.gamepad1.setJoystickDeadzone(.1f);
                slowMode = true;
                delay.reset();
            } else if (slow) { //Setting to normal mode.
                mOpMode.gamepad1.setJoystickDeadzone(.1f);
                delay.reset();
                slowMode = false;
                delay.reset();
            } else if (reset) { //Reset is the reset button
                mOpMode.gamepad1.setJoystickDeadzone(.1f);
                slowMode = false;
                delay.reset();
            }
        }

        mOpMode.gamepad1.setJoystickDeadzone(.05f);
        //Alters our speed based upon slow mode.
        if (slowMode) {
            mOpMode.gamepad1.setJoystickDeadzone(.1f);
            linear /= 2;
            turn /= 2;
        }
    }

    //Identifies the buttons we are tracking to control our slow mode toggle.
    public void setSpeedButtons(boolean slow, boolean reset) {
        slowButton = slow;
        resetButton = reset;
    }

    //Returns our drive variables.
    public boolean getSpeedToggle() {
        return slowMode;
    }


    //Returns our drive variables.
    public double[] getDriveValue() {
        return new double[]{linear, turn};
    }
}

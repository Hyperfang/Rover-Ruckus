package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Controls {

    private Base base;

    private double linear;
    private double turn;

    private ElapsedTime slowDelay;
    private ElapsedTime revDelay;
    private boolean revMode;
    private boolean slowMode;
    private boolean revButton;
    private boolean slowButton;
    private boolean resetButton;

    private OpMode mOpMode;

    //Initializes the controls object.
    public Controls(OpMode opMode) {
        mOpMode = opMode;
        base = new Base(opMode);

        mOpMode.gamepad1.setJoystickDeadzone(.075f);
        mOpMode.gamepad2.setJoystickDeadzone(.075f);

        slowDelay = new ElapsedTime();
        slowMode = false;
        slowButton = false;
        resetButton = false;

        revDelay = new ElapsedTime();
        revMode = false;
        revButton = false;
    }

    //Drive Method
    //Arcade Mode uses left stick to go forward, and right stick to turn.
    public void moveArcade() {
        linear = -mOpMode.gamepad1.left_stick_y;
        turn = mOpMode.gamepad1.right_stick_x;
        toggleSpeed(slowButton, resetButton, slowDelay);
        toggleDirection(revButton, revDelay);
        base.move(linear, turn);
    }

    //Drive Method
    //Tank Mode uses one stick to control each wheel.
    public void moveTank() {
        //In this case, linear refers to the left side, and turn to the right.
        linear = -mOpMode.gamepad1.left_stick_y;
        turn = -mOpMode.gamepad1.right_stick_y;
        toggleSpeed(slowButton, resetButton, slowDelay);
        toggleDirection(revButton, revDelay);
        base.setPower(linear, turn);
    }

    //Drive Method
    //Gyro-Assisted TeleOp Mode uses left stick to go forward, and right stick to absolute turn.
    //This drive method can be explained as using the robot from a top-down view.
    public void moveGAT() {
        linear = -mOpMode.gamepad1.left_stick_y;

        //Right Stick Movement Control
        double AngleRStick = Math.toDegrees(Math.atan2(mOpMode.gamepad1.right_stick_y, -mOpMode.gamepad1.right_stick_x));

        //In order for the turning to adhere to the plane we start on, we need to reflect our x values.
        if (AngleRStick < 0) {
            AngleRStick += 360;
        }

        mOpMode.telemetry.addData("Desired: ", AngleRStick);

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

        toggleSpeed(slowButton, resetButton, slowDelay);
        toggleDirection(revButton, revDelay);

        base.move(linear, turn);
    }

    //Movement Modifier
    //Toggles the speed of our drive-train between normal and slow (half speed) mode.
    private void toggleSpeed(boolean slow, boolean reset, ElapsedTime delay) {
        mOpMode.gamepad1.setJoystickDeadzone(.075f);

        if (delay.milliseconds() > 500) {  //Allows time for button release.
            if (slow && !slowMode) { //Slow is the slow mode button.
                mOpMode.gamepad1.setJoystickDeadzone(.15f);
                slowMode = true;
                delay.reset();
            } else if (slow) { //Setting to normal mode.
                mOpMode.gamepad1.setJoystickDeadzone(.15f);
                delay.reset();
                slowMode = false;
                delay.reset();
            } else if (reset) { //Reset is the reset button
                mOpMode.gamepad1.setJoystickDeadzone(.15f);
                slowMode = false;
                delay.reset();
            }
        }

        mOpMode.gamepad1.setJoystickDeadzone(.075f);
        //Alters our speed based upon slow mode.
        if (slowMode) {
            mOpMode.gamepad1.setJoystickDeadzone(.15f);
            linear /= 2;
            turn /= 2;
        }
    }

    private void toggleDirection(boolean toggle, ElapsedTime delay) {
        if (delay.milliseconds() > 500) {  //Allows time for button release.
            if (toggle && !revMode) { //Toggle is the reverse mode button.
                revMode = true;
                delay.reset();
            } else if (toggle) { //Setting to normal mode.
                revMode = false;
                delay.reset();
            }
        }

        //Alters our speed based upon reverse mode.
        if (revMode) {
            linear *= -1;
            turn *= -1;
        }
    }

    //Identifies the buttons we are tracking to control our slow mode toggle.
    public void setSpeedButtons(boolean slow, boolean reset, ElapsedTime delay) {
        slowButton = slow;
        resetButton = reset;
        slowDelay = delay;
    }

    //Identifies the buttons we are tracking to control our slow mode toggle.
    public void setDirectionButton(boolean toggle, ElapsedTime delay) {
        revButton = toggle;
        revDelay = delay;
    }

    //Returns our drive variables.
    public boolean getSpeedToggle() { return slowMode; }

    //Returns our drive variables.
    public boolean getDirectionToggle() { return revMode; }

    //Returns our drive variables.
    public double[] getDriveValue() {
        return new double[]{linear, turn};
    }
}

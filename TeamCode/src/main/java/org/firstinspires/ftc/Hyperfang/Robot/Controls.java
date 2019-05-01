package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Controls {
    //Robot Object Instantiation
    private static Controls obj;
    private OpMode mOpMode;

    //Speed Variables
    private double linear;
    private double turn;

    //Toggle Buttons
    private boolean revButton;
    private boolean slowButton;
    private boolean resetButton;

    //Toggle Timers.
    private ElapsedTime slowDelay = new ElapsedTime();
    private ElapsedTime revDelay = new ElapsedTime();
    private ElapsedTime pivotDelay = new ElapsedTime();
    private ElapsedTime antiDelay = new ElapsedTime();
    private ElapsedTime depDelay = new ElapsedTime();
    private ElapsedTime hookDelay = new ElapsedTime();
    private ElapsedTime testTime = new ElapsedTime();


    //Toggle Booleans
    private boolean revMode;
    private boolean slowMode;
    private boolean isPivot;
    private boolean isHook;
    private boolean isDeposit;
    private boolean isAdjust;
    private boolean antiMode;

    //Iterating Numbers
    private double pos = 0;
    private int adjustPivot = 1200;

    //Initializes the Controls object.
    public static Controls getInstance() {
        if (obj == null) {
            throw new NullPointerException("Control Object not created with an OpMode.");
        }
        return obj;
    }

    //Initializes the Controls object.
    public static Controls getInstance(OpMode opMode) {
        if (obj == null) {
            obj = new Controls(opMode);
        }
        return obj;
    }

    //Initializes the controls object.
    private Controls(OpMode opMode) {
        mOpMode = opMode;
        Base.getInstance(opMode);

        mOpMode.gamepad1.setJoystickDeadzone(.075f);
        mOpMode.gamepad2.setJoystickDeadzone(.075f);

        slowMode = false;
        slowButton = false;
        resetButton = false;

        revMode = false;
        revButton = false;
        antiMode = true;

        isDeposit = true;
        isPivot = false;
        isAdjust = false;
        isHook = false;
        mOpMode.telemetry.addLine("Scorpion Controls Version 2 Loaded");
    }

    //Initializes the robot movement.
    public void initRobot() {
        Base.getInstance(mOpMode).ftcEnc();
    }

    //Drive Method
    //Arcade Mode uses left stick to go forward, and right stick to turn.
    public void moveArcade() {
        linear = -mOpMode.gamepad1.left_stick_y;
        turn = -mOpMode.gamepad1.right_stick_x;
        toggleSpeed(slowButton, resetButton);
        toggleDirection(revButton);
        Base.getInstance().move(linear, turn);
    }

    //Drive Method
    //Tank Mode uses one stick to control each wheel.
    public void moveTank() {
        //In this case, linear refers to the left side, and turn to the right.
        linear = -mOpMode.gamepad1.left_stick_y;
        turn = -mOpMode.gamepad1.right_stick_y;
        toggleSpeed(slowButton, resetButton);
        toggleDirection(revButton);
        Base.getInstance().setPower(linear, turn);
    }

    //Movement Modifier
    //Toggles the speed of our drive-train between normal and slow (half speed) mode.
    private void toggleSpeed(boolean slow, boolean reset) {
        mOpMode.gamepad1.setJoystickDeadzone(.075f);

        if (slowDelay.milliseconds() > 500) {  //Allows time for button release.
            if (slow && !slowMode) { //Slow is the slow mode button.
                mOpMode.gamepad1.setJoystickDeadzone(.15f);
                slowMode = true;
                slowDelay.reset();
            } else if (slow) { //Setting to normal mode.
                mOpMode.gamepad1.setJoystickDeadzone(.15f);
                slowMode = false;
                slowDelay.reset();
            } else if (reset) { //Reset is the reset button
                mOpMode.gamepad1.setJoystickDeadzone(.15f);
                slowMode = false;
                slowDelay.reset();
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

    //Toggles the direction of our robot allowing for easy backwards driving.
    private void toggleDirection(boolean toggle) {
        //Allows time for button release.
        if (revDelay.milliseconds() > 500) {
            //Toggle is the reverse mode button.
            if (toggle && !revMode) {
                revMode = true;
                revDelay.reset();
            } //Setting to normal mode.
            else if (toggle) {
                revMode = false;
                revDelay.reset();
            }
        }

        //Alters our speed based upon reverse mode.
        if (revMode) {
            linear *= -1;
            turn *= -1;
        }
    }

    //Identifies the buttons we are tracking to control our slow mode toggle.
    public void setSpeedButtons(boolean slow, boolean reset) {
        slowButton = slow;
        resetButton = reset;
    }

    //Identifies the buttons we are tracking to control our slow mode toggle.

    //Pivots the Lift via macro with a trigger.

    public boolean getSpeedToggle() {
        return slowMode;
    }

    //Returns our drive variables.
    public boolean getDirectionToggle() {
        return revMode;
    }

    //Returns the lift variables.
    public boolean getGravityToggle() {
        return antiMode;
    }


    //Returns our drive variables.
    public double[] getDriveValue() {
        return new double[]{linear, turn};
    }

    //Returns the current position of the lift.

}
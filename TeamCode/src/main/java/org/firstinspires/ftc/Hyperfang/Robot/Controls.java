package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Controls {
    //Robot Object Instantiation
    private static Controls obj;
    private static OpMode mOpMode;

    private Manipulator manip;

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
    private ElapsedTime intakePosDelay = new ElapsedTime();
    private ElapsedTime trapDelay = new ElapsedTime();
    private ElapsedTime depDelay = new ElapsedTime();
    private ElapsedTime hookDelay = new ElapsedTime();
    private ElapsedTime testTime = new ElapsedTime();

    //Toggle Booleans
    private boolean revMode;
    private boolean slowMode;
    private boolean isIntakePosition;
    private boolean isHook;
    private boolean isTrap;
    private boolean isDeposit;

    private double pos = 0;

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
        Lift.getInstance(opMode);
       // manip = new Manipulator(opMode);

        mOpMode.gamepad1.setJoystickDeadzone(.075f);
        mOpMode.gamepad2.setJoystickDeadzone(.075f);

        slowMode = false;
        slowButton = false;
        resetButton = false;

        revMode = false;
        revButton = false;

        isTrap = true;
        isDeposit = false;
        isIntakePosition = false;
        isHook = false;
    }

    //Initializes the robot.
    public void initRobot() {
        //manip.depositPosition();
        //manip.lockDeposit();
        //lift.setPosition(Lift.LEVEL.GROUND);
    }

    //Drive Method
    //Arcade Mode uses left stick to go forward, and right stick to turn.
    public void moveArcade() {
        linear = -mOpMode.gamepad1.left_stick_y;
        turn = mOpMode.gamepad1.right_stick_x;
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
    public void setDirectionButton(boolean toggle) {
        revButton = toggle;
    }

    //Pivots the Lift.
    public void pivotLift(double gamepad) {
        Lift.getInstance().pivot(gamepad);
    }

    //Moves the Lift.
    public void moveLift(double gamepad) {
        Lift.getInstance().moveLift(gamepad);
    }

    //Runs the intake to collect or release minerals.
    public void intake(double gamepad, double gamepad2) {
        if (0 < Math.abs(gamepad))  { manip.setIntake(1); }
        else if (0 < Math.abs(gamepad2)) { manip.setIntake(-1); }
        else { manip.setIntake(0); }
    }

    //Moves the intake to a certain position.
    public void intakePosition(boolean toggle) {
        if (manip.incIntakePosition) { manip.intakePosition(); }

        //Allows time for button release.
        else if (intakePosDelay.milliseconds() > 300 && !manip.incIntakePosition) {
            //Toggle is the toggle button.
            if (toggle && isIntakePosition) {
                isIntakePosition = false;
                manip.depositPosition();
                intakePosDelay.reset();
            } //Setting to intake mode.
            else if (toggle) {
                isIntakePosition = true;
                manip.incIntakePosition = true;
                intakePosDelay.reset();
            }
        }
    }

    //Sets the position of the trapdoor.
    public void trapdoor(boolean toggle) {
        //Allows time for button release.
        if (trapDelay.milliseconds() > 250) {
            //Toggle is the trapdoor toggle button.
            if (toggle && isTrap) {
                isTrap = false;
                manip.holdMinerals();
                trapDelay.reset();
            } //Setting to locked mode.
            else if (toggle) {
                isTrap = true;
                manip.releaseMinerals();
                trapDelay.reset();
            }
        }
    }

    //Sets the position of the trapdoor.
    public void deposit(boolean toggle) {
        //Allows time for button release.
        if (depDelay.milliseconds() > 250) {
            //Toggle is the deposit toggle button.
            if (toggle && !isDeposit) {
                isDeposit = true;
                manip.unlockDeposit();
                depDelay.reset();
            } //Setting to locked mode.
            else if (toggle) {
                isDeposit = false;
                manip.lockDeposit();
                depDelay.reset();
            }
        }
    }


/*
    //Locks or Unlocks the ratchet based on the state it is in.
    public void hook(boolean toggle) {
        //Allows time for button release.
        if (hookDelay.milliseconds() > 500) {
            //Toggle is the hook toggle button.
            if (toggle && isHook) {
                isHook = false;
                lift.hook();
                hookDelay.reset();
            } //Setting to locked mode.
            else if (toggle) {
                isHook = true;
                lift.unhook();
                hookDelay.reset();
            }
        }
    }
    */

    //Returns our drive variables.
    public boolean getSpeedToggle() {
        return slowMode;
    }

    //Returns our drive variables.
    public boolean getDirectionToggle() {
        return revMode;
    }

    //Returns our drive variables.
    public double[] getDriveValue() {
        return new double[]{linear, turn};
    }

    //Returns the current position of the lift.
   public String getLevel() {
        return Lift.getInstance().getPosition();
    }

    //Provides a method of easily testing servos where x and y can be substituted for gamepad inputs.
    public double testServo(boolean x, boolean y) {
        mOpMode.telemetry.addData("POS: ", pos);
        if (testTime.milliseconds() > 100) {
            if (x) {
                pos += .05;
                testTime.reset();
            }
            if (y) {
                pos -= .05;
                testTime.reset();
            }
        }
        return pos;
    }
}
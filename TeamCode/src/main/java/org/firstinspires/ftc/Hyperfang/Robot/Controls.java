package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
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
        Lift.getInstance(opMode);
        Manipulator.getInstance(opMode);

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
        Lift.getInstance(mOpMode).ftcEnc();
        Lift.getInstance().setModeEncoderLift(DcMotor.RunMode.RUN_USING_ENCODER);
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
    public void setDirectionButton(boolean toggle) {
        revButton = toggle;
    }

    //Pivots the Lift via macro.
    public void macroPivot(boolean toggle, boolean upperLimit, boolean lowerLimit) {
        if (isPivot && !isAdjust) Lift.getInstance().pivotUp();
        else if (!isPivot && !isAdjust) Lift.getInstance().pivotDown();
        else Lift.getInstance().pivotLift(adjustPivot);

        //Detects the lift position while adjusting to determine the next toggle position.
        if (isAdjust && Lift.getInstance().getPivotPosition() > 1000) isPivot = true;
        else if (isAdjust && Lift.getInstance().getPivotPosition() < 1000) isPivot = false;

        //Allows time for button release.
        if (pivotDelay.milliseconds() > 600) {
            //Toggle is the pivot toggle button.
            if (toggle && isPivot) {
                isAdjust = false;
                isPivot = false;
                pivotDelay.reset();
            } //Setting to locked mode.
            else if (toggle) {
                isAdjust = false;
                isPivot = true;
                pivotDelay.reset();
            }
        }

        if (upperLimit) {
            isAdjust = true;
            if (!(adjustPivot > 2000)) adjustPivot += 20;
            Lift.getInstance().pivotLift(adjustPivot);
        } else if (lowerLimit) {
            isAdjust = true;
            if (!(adjustPivot < -100)) adjustPivot -= 20;
            Lift.getInstance().pivotLift(adjustPivot);
        }
    }

    //Pivots the Lift via manual input.
    public void manualPivot(double gamepad, boolean toggle) {
        Lift.getInstance().manualPivot(-gamepad, antiMode);
        toggleAntiGravity(toggle);
    }

    //Identifies the buttons we are tracking to control our anti-gravity mode toggle.
    public void toggleAntiGravity(boolean toggle) {
        //Allows time for button release.
        if (antiDelay.milliseconds() > 250) {
            //Toggle is the anti-gravity toggle button.
            if (toggle && antiMode) {
                antiMode = false;
                antiDelay.reset();
            } //Setting to locked mode.
            else if (toggle) {
                antiMode = true;
                antiDelay.reset();
            }
        }
    }

    //Moves the Lift.
    public void moveLift(double gamepad) {
        Lift.getInstance().moveLift(gamepad, antiMode);
    }

    //Runs the intake to collect or release minerals.
    //Due to VEX functioning as a CRSERVO, Battery Voltage Must be sufficient.
    public void intake(boolean gamepad, boolean gamepad2) {
        if (gamepad) {Manipulator.getInstance().intake(.8);}
        else if (gamepad2) {Manipulator.getInstance().intake(-.8);}
        else {Manipulator.getInstance().intake(0);}
    }

    //Sets the position of the deposit.
    public void deposit(boolean toggle) {
        //Allows time for button release.
        if (depDelay.milliseconds() > 250) {
            //Toggle is the trapdoor toggle button.
            if (toggle && isDeposit) {
                isDeposit = false;
                Manipulator.getInstance().closeBoth();
                depDelay.reset();
            } //Setting to locked mode.
            else if (toggle) {
                isDeposit = true;
                Manipulator.getInstance().openBoth();
                depDelay.reset();
            }
        }
    }

    //Locks or Unlocks the lift to the base.
    public void lock(boolean toggle) {
        //Allows time for button release.
        if (hookDelay.milliseconds() > 500) {
            //Toggle is the hook toggle button.
            if (toggle && isHook) {
                isHook = false;
                Lift.getInstance().lock();
                hookDelay.reset();
            } //Setting to locked mode.
            else if (toggle) {
                isHook = true;
                Lift.getInstance().unlock();
                hookDelay.reset();
            }
        }
    }

    //Returns our drive variables.
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

    //Returns the hook indicator.
    public String getHook() {
        if (!isHook) return "ON.";
        return "OFF.";
    }

    //Returns our drive variables.
    public double[] getDriveValue() {
        return new double[]{linear, turn};
    }

    //Returns the current position of the lift.
   public String getLevel() {
        return Lift.getInstance().getPosition().name();
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
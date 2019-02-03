package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.Hyperfang.Sensors.MGL;

public class Lift {

    //Lift Position States
    public enum LEVEL {
        COLLECT,
        DEPOSIT,
    }

    //Singleton object
    private static Lift obj;
    private OpMode mOpMode;

    //Position Variables
    private LEVEL pos;
    private static final double encTolerance = 30;

    //Lift Objects
    private static DcMotor liftMotorR;
    private static DcMotor liftMotorL;
    private static DcMotorEx pivotMotorR;
    private static DcMotorEx pivotMotorL;
    private Servo hookR;
    private Servo hookL;
    private MGL mgl;

    //Initializes the lift object.
    public static Lift getInstance() {
        if (obj == null) {
            throw new NullPointerException("Lift Object not created with an OpMode.");
        }
        return obj;
    }

    //Initializes the lift object.
    public static Lift getInstance(OpMode opMode) {
        if (obj == null) {
            obj = new Lift(opMode);
        }
        return obj;
    }

    //Initializes the lift objects.
    private Lift(OpMode opMode){
        mOpMode = opMode;
        liftMotorR = mOpMode.hardwareMap.get(DcMotor.class, "Lift Right");
        liftMotorL = mOpMode.hardwareMap.get(DcMotor.class, "Lift Left");
        pivotMotorR = (DcMotorEx) mOpMode.hardwareMap.get(DcMotor.class, "Pivot Right");
        pivotMotorL = (DcMotorEx) mOpMode.hardwareMap.get(DcMotor.class, "Pivot Left");

        hookR = mOpMode.hardwareMap.get(Servo.class, "Right Hook");
        hookL = mOpMode.hardwareMap.get(Servo.class, "Left Hook");
        mgl = new MGL(opMode);
        setPosition(LEVEL.COLLECT);
        resetLiftPosition();
        resetPivotPosition();
        mOpMode.telemetry.addLine("Lift Version 1.5 Inited");
    }

    //After testing the encoders we found out the FTC SDK clears encoder information (unlike motors)
    //in between OpModes. Due to this, this method must be called in init to set the encoders.
    public void ftcEnc() {
        liftMotorR.setDirection(DcMotor.Direction.REVERSE);
        pivotMotorL.setDirection(DcMotorEx.Direction.REVERSE);
        liftMotorL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotorR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivotMotorR.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        pivotMotorL.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        setModeEncoderLift(DcMotor.RunMode.RUN_TO_POSITION);
        setModeEncoderPivot(DcMotorEx.RunMode.RUN_TO_POSITION);
    }

    //Returns the state of the Magnetic Limit Switch.
    private boolean getMGL() {
        return mgl.isTouched();
    }

    //Sets the position of our Lift.
    public void setPosition(LEVEL position) { pos = position; }

    //Returns the position of our Lift.
    public LEVEL getPosition() {
        return pos;
    }

    //Sets the modes of the Pivot.
    private void setModeEncoderPivot(DcMotorEx.RunMode mode) {
        pivotMotorR.setMode(mode);
        pivotMotorL.setMode(mode);
    }

    //Sets the modes of the Lift.
    public void setModeEncoderLift(DcMotorEx.RunMode mode) {
        liftMotorR.setMode(mode);
        liftMotorL.setMode(mode);
    }

    //Sets the modes of the Pivot.
    public void setZeroLift(DcMotor.ZeroPowerBehavior mode) {
        liftMotorR.setZeroPowerBehavior(mode);
        liftMotorL.setZeroPowerBehavior(mode);
    }

    //Sets the modes of the Pivot.
    public void setZeroPivot(DcMotor.ZeroPowerBehavior mode) {
        pivotMotorL.setZeroPowerBehavior(mode);
        pivotMotorR.setZeroPowerBehavior(mode);
    }

    //Resets the Lift encoders.
    public void resetLiftPosition() {
        liftMotorR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotorL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setModeEncoderLift(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    //Resets the Pivot encoders.
    public void resetPivotPosition() {
        pivotMotorR.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        pivotMotorL.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        setModeEncoderPivot(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    //Returns the Lift encoder position.
    public int getLiftPosition() {
        double total = 0;
        total += liftMotorR.getCurrentPosition();
        total += liftMotorL.getCurrentPosition();
        return (int) (total / 2.0);
    }

    //Returns the Pivot encoder position.
    public int getPivotPosition() {
        double total = 0;
        total += pivotMotorR.getCurrentPosition();
        total += pivotMotorL.getCurrentPosition();
        return (int) (total / 2.0);
    }

    //Sets a target position for the Lift encoders. (Counts)
    private void setTargetPositionLift(double pos) {
        liftMotorL.setTargetPosition((int)pos);
        liftMotorR.setTargetPosition((int)pos);
    }

    //Sets a target position for the Pivot encoders. (Counts)
    public void setTargetPositionPivot(double pos) {
        pivotMotorR.setTargetPosition((int)pos);
        pivotMotorL.setTargetPosition((int)pos);
    }

    //Sets the Lift motors.
    public void setLift(double pow) {
        liftMotorR.setPower(pow);
        liftMotorL.setPower(pow);
    }

    //Sets the Pivot motors.
    public void setPivot(double pow) {
        pivotMotorR.setPower(pow);
        pivotMotorL.setPower(pow);
    }

    //Stops the lift.
    public void stop() {
        liftMotorR.setPower(0);
        liftMotorL.setPower(0);
    }

    //Moves the lift up or down depending on the given power.
    public void moveLift(double input, boolean antiGravity) {
        if (input != 0) {
            //If we are moving down activate anti-gravity countermeasures unless it is bypassed.
            if (input < 0) {
                if (antiGravity) input *= .5;
            }
            liftMotorR.setPower(input);
            liftMotorL.setPower(input);
        } else {
            liftMotorR.setPower(0);
            liftMotorL.setPower(0);
        }
    }

    //Moves the lift up or down depending on an amount of encoders.
    public void moveLiftEnc(int enc, double power) {
        setTargetPositionLift(enc);
        if (setEnc(enc)) {
            setLift(power);
        } else {
            setLift(0);
        }
    }

    //Returns whether our encoder is not in the desired position. Useful for loops.
    public boolean setEnc(double pos) {
        return Math.abs(getLiftPosition() - pos) > encTolerance;
    }

    //Pivot the lift via an input.
    public void manualPivot(double input, boolean antiGravity) {
        if (antiGravity) input *= .5;
        setPivot(input);
    }

    //Pivots the Lift Up.
    public void pivotUp() {
        if (getPivotPosition() < 1050) {
            setTargetPositionPivot(1075);
            setPivot(.5);
        }
        //Activate the ZeroPowerBehavior Mode (BRAKE).
        else if (getPivotPosition() >= 1190 || mgl.isTouched()) {
            setPivot(0);
            setPosition(LEVEL.DEPOSIT);
        }
        else if (getPivotPosition() >= 1050) {
            setTargetPositionPivot(1200);
            setPivot(.35);
        }
    }

    //Pivots the Lift to a specified position.
    public void pivotLift(int target) {
        if (getPivotPosition() == target) {
           setPivot(0);
        } else {
            setTargetPositionPivot(target);
            setPivot(.3);
        }
    }

    //Pivots the Lift Up.
    public void pivotDown() {
        if (getPivotPosition() > 825) {
            setTargetPositionPivot(400);
            setPivot(.3);
        } //Allow gravity to bring the lift the rest of the way down.
        else if (getPivotPosition() <= 450) {
            setPivot(.0);
            setPosition(LEVEL.COLLECT);
        }
    }

    //Locks the lift to the base.
    public void lock() {
        hookL.setPosition(.7);
        hookR.setPosition(.35);
    }

    //Unlocks the lift to the base.
    public void unlock() {
        hookL.setPosition(.25);
        hookR.setPosition(.75);
    }
}
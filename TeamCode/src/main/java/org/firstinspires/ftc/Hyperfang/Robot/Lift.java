package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Sensors.MGL;

public class Lift {
    public enum LEVEL {
        COLLECT,
        DEPOSIT,
    }

    //Singleton object
    private static Lift obj;
    private static OpMode mOpMode;

    private LEVEL pos;
    private static final double encTolerance = 25;

    private static DcMotor liftMotorR;
    private static DcMotor liftMotorL;
    private static DcMotorEx pivotMotorR;
    private static DcMotorEx pivotMotorL;

    private Servo hookR;
    private Servo hookL;
    private MGL mgl;

    private ElapsedTime PoT = new ElapsedTime();

    private int liftEnc;
    private boolean isRetract;

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
        isRetract = false;
        mOpMode.telemetry.addLine("Lift Version 1.4 Inited");
    }

    private void setModeEncoderPivot(DcMotorEx.RunMode mode) {
        pivotMotorR.setMode(mode);
        pivotMotorL.setMode(mode);
    }

    private void setModeEncoderLift(DcMotorEx.RunMode mode) {
        liftMotorR.setMode(mode);
        liftMotorL.setMode(mode);
    }

    //After testing the encoders we found out the FTC SDK clears encoder information (unlike motors)
    //in between OpModes. Due to this, this method must be called in init to set the encoders.
    public void ftcEnc() {
        resetLiftPosition();
        liftMotorR.setDirection(DcMotor.Direction.REVERSE);
        pivotMotorL.setDirection(DcMotorEx.Direction.REVERSE);
        liftMotorL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotorR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivotMotorR.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        pivotMotorL.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        setModeEncoderPivot(DcMotorEx.RunMode.RUN_TO_POSITION);
    }

    //Returns the state of the Magnetic Limit Switch.
    public boolean getMGL() {
        return mgl.isTouched();
    }

    //Sets the position of our lift.
    public void setPosition(LEVEL position) { pos = position; }

    //Returns the position of our lift.
    public LEVEL getPosition() {
        return pos;
    }

    //Resets the lift encoders.
    public void resetLiftPosition() {
        liftEnc = 0;
        liftMotorR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotorL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setModeEncoderLift(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    //Resets the pivot encoders.
    public void resetPivotPosition() {
        pivotMotorR.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        pivotMotorL.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        setModeEncoderPivot(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    //Returns the lift encoder position.
    public int getLiftPosition() {
       // double total = 0;
       // total += liftMotorR.getCurrentPosition();
       // total += liftMotorL.getCurrentPosition();
        //return (int) (total / 2.0);
        return liftMotorR.getCurrentPosition();
    }

    //Returns the lift encoder position.
    public int getLiftLeft() {
        return liftMotorL.getCurrentPosition();
    }

    //Returns the pivot encoder position.
    public int getPivotPosition() {
        double total = 0;
        total += pivotMotorR.getCurrentPosition();
        total += pivotMotorL.getCurrentPosition();
        return (int) (total / 2.0);
    }

    //Sets a target position for the Lift encoders. (Counts)
    private void setTargetPositionLift(double pos) {
        liftMotorL.setTargetPosition((int) pos);
        liftMotorR.setTargetPosition((int)pos);
    }

    //Sets a target position for the Pivot encoders. (Counts)
    private void setTargetPositionPivot(double pos) {
        pivotMotorR.setTargetPosition((int) pos);
        pivotMotorL.setTargetPosition((int)pos);
    }

    private void movePivot(double pow) {
        pivotMotorR.setPower(pow);
        pivotMotorL.setPower(pow);
    }

    //Stops the lift.
    public void stop() {
        PoT.reset();
        liftMotorR.setPower(0);
        liftMotorL.setPower(0);
    }

    //Moves the lift up or down depending on the given power.
    public void moveLift(double input, boolean antiGravity) {
        if (input != 0) {
            //If we are moving down activate anti-gravity countermeasures unless it is bypassed.
            if (input > 0) {
                if (antiGravity) input *= .5;
                isRetract = true;
                pivotLift(isRetract);
            } else isRetract = false;
            liftMotorR.setPower(input);
            liftMotorL.setPower(input);
        } else {
            liftMotorR.setPower(0);
            liftMotorL.setPower(0);
        }
    }

    //Moves the lift up or down depending on an amount of encoders.
    public double moveLiftEnc(int enc, double power) {
        setModeEncoderLift(DcMotor.RunMode.RUN_TO_POSITION);

        if (setEnc(enc)) {
            setTargetPositionLift(enc);
            return power;
        } else {
            liftMotorR.setPower(0);
            liftMotorL.setPower(0);
            setModeEncoderLift(DcMotor.RunMode.RUN_USING_ENCODER);

        }
        return 0;
    }

    //Returns whether our encoder is not in the desired position. Useful for loops.
    public boolean setEnc(double pos) {
        return Math.abs(liftEnc - pos) > encTolerance;
    }

    //Pivot the lift via an input.
    public void manualPivot(double input, boolean antiGravity) {
        if (antiGravity) input *= .5;
        movePivot(input);
    }

    //Pivots the Lift Up.
    public void pivotUp() {
        mOpMode.telemetry.addData("TARGET", pivotMotorL.getTargetPosition());
        if (pivotMotorL.getCurrentPosition() < 1050) {
            setTargetPositionPivot(1075);
            movePivot(.5);
        }
        //Activate the ZeroPowerBehavior Mode (BRAKE).
        else if (pivotMotorL.getCurrentPosition() >= 1200) {
            movePivot(0);
            setPosition(LEVEL.DEPOSIT);
        }
        else if (pivotMotorL.getCurrentPosition() >= 1050) {
            setTargetPositionPivot(1200);
            movePivot(.3);
        }
    }


    //Pivots the Lift Up.
    public void pivotLift(boolean active) {
        if (pos.equals(LEVEL.COLLECT) && liftMotorL.getCurrentPosition() < 700 && active) {
            if (pivotMotorL.getCurrentPosition() < 200 || 400 < pivotMotorL.getCurrentPosition()) {
                setTargetPositionPivot(300);
                movePivot(.25);
            } else {
                movePivot(0);
            }
        }
    }

    //Pivots the Lift Up.
    public void pivotDown() {
        mOpMode.telemetry.addData("TARGET", pivotMotorL.getTargetPosition());
        if (pivotMotorL.getCurrentPosition() > 825) {
            setTargetPositionPivot(400);
            movePivot(.3);
        } //Allow gravity to bring the lift the rest of the way down.
        else if (pivotMotorL.getCurrentPosition() <= 450) {
            movePivot(.0);
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
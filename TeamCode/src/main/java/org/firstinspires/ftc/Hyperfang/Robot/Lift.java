package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Sensors.MGL;

public class Lift {
    public enum LEVEL {
        COLLECT,
        DEPOSIT,
    }

    private static Lift obj;
    private static OpMode mOpMode;
    private LEVEL pos;
    private static final double encTolerance = 10;

    private static DcMotor liftMotorR;
    private static DcMotor liftMotorL;
    private static DcMotor pivotMotorR;
    private static DcMotor pivotMotorL;
    private Servo hookR;
    private Servo hookL;

    private MGL mgl;

    private int pivotEnc;
    private int liftEnc;

    private ElapsedTime PoT = new ElapsedTime();

    //Initializes the base object.
    public static Lift getInstance() {
        if (obj == null) {
            throw new NullPointerException("Lift Object not created with an OpMode.");
        }
        return obj;
    }

    //Initializes the base object.
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
        pivotMotorR = mOpMode.hardwareMap.get(DcMotor.class, "Pivot Right");
        pivotMotorL = mOpMode.hardwareMap.get(DcMotor.class, "Pivot Left");
        //hookR = mOpMode.hardwareMap.get(Servo.class, "Right Hook");
        //hookL = mOpMode.hardwareMap.get(Servo.class, "Left Hook");

        liftMotorL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotorR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        resetPivotPosition();
        resetLiftPosition();

        mgl = new MGL(opMode);
        setPosition(LEVEL.COLLECT);
    }

    public boolean getMGL() {
        return mgl.isTouched();
    }

    //Sets the position of our lift.
    public void setPosition(LEVEL position) { pos = position; }

    //Returns the position of our lift.
    public String getPosition() {
        return pos.name();
    }

    //Resets the lift encoders.
    public void resetLiftPosition() {
        liftEnc = 0;
        liftMotorR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotorL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    //Resets the pivot encoders.
    public void resetPivotPosition() {
        pivotEnc = 0;
        pivotMotorR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pivotMotorL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    //Returns the pivot encoder position.
    public int getPivotPosition() {
        double total = 0;
        total += pivotMotorR.getCurrentPosition();
        total += pivotMotorL.getCurrentPosition();
        return (int) (total / 2.0);
    }

    //Returns the lift encoder position.
    public int getLiftPosition() {
        double total = 0;
        total += liftMotorR.getCurrentPosition();
        total += liftMotorL.getCurrentPosition();
        return (int) (total / 2.0);
    }

    //Stops the lift.
    public void stop() {
        PoT.reset();
        liftMotorR.setPower(0);
        liftMotorL.setPower(0);
    }

    //Moves the lift up or down depending on the given power.
    public void moveLift(double input) {
        //Going up
        if (input < 0) {
            liftMotorR.setPower(input);
            liftMotorL.setPower(-input);
        } //Slow down going down.
        else if (input > 0 ) {
            liftMotorR.setPower(input * .5);
            liftMotorL.setPower(-input * .5);
        }

    }

    //Moves the lift up or down depending on an amount of encoders.
    public double moveLiftEnc(int enc) {
        liftEnc = getLiftPosition();

        if (setEnc(enc) || liftEnc <= 0) {
            //Insert PID HERE
            //Using the error to calculate our power.
            double power = Math.abs(liftEnc - enc) / 4000;
            if (power < .1) power = .1;

            if (liftEnc < enc) return power;
            if (liftEnc > enc) return -power;
        }
        return 0;
    }

    //Returns whether our encoder is not in the desired position. Useful for loops.
    public boolean setEnc(double pos) {
        return Math.abs(liftEnc - pos) > encTolerance;
    }

    //R - L + goes up
    public double td =0;
    public double tp = 0;
    public void pivot(double input) {
        if (mgl.isStateChange()) {
            td = PoT.seconds();
            pivotMotorR.setPower(0);
            pivotMotorL.setPower(0);
            setPosition(LEVEL.DEPOSIT);
        }

        double t = PoT.milliseconds()/1000;
        double pow = -.585 * Math.pow(t, 3) + .65 * Math.pow(t, 2) -.25 * t + .6;
        tp = pow;

        if (!mgl.isTouched() && pow > 0) {
        pivotMotorR.setPower(-pow);
        pivotMotorL.setPower(pow);
        if (pow < .1) pow = .1;
        }
    }
}
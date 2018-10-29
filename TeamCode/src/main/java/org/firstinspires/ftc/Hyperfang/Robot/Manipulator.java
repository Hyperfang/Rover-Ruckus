package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/*
    Created by Caleb.
*/

public class Manipulator {

    private OpMode mOpMode;

    private DcMotor liftMotor;
    private DcMotor intakeMotor;

    private Servo leftIntake;
    private Servo rightIntake;

    private static final double COUNTS_PER_MOTOR_REV = 1440 ; // Rev Orbital 40:1
    private static final double DIAMETER_INCHES   = 1.025; // Spool
    private static final double COUNTS_PER_INCH   = (COUNTS_PER_MOTOR_REV) / (DIAMETER_INCHES * 3.1415);

    private double curEnc;
    private static final double encTolerance = 100;


    //Initializes the manipulator objects.
    public Manipulator(OpMode opMode) {
        mOpMode = opMode;
        liftMotor = mOpMode.hardwareMap.get(DcMotor.class, "hLift");
        //intakeMotor = mOpMode.hardwareMap.get(DcMotor.class, "intake");
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //May need to add reverse.

        //leftIntake = mOpMode.hardwareMap.get(Servo.class, "leftServo");
        //rightIntake = mOpMode.hardwareMap.get(Servo.class, "rightServo");
    }

    //Moves the horizontal lift using a given power.
    public void moveLift(double power) {
        liftMotor.setPower(power);
    }

    //Moves the horizontal lift to a certain position using encoders.
    //resetEncoders() before running this in a loop.
    public double moveLift(int pos) {
        double p = 90; //Change

        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setTargetPosition((int)(pos * COUNTS_PER_INCH));

        curEnc = liftMotor.getCurrentPosition();

        if (setEnc(pos)) {
            curEnc = liftMotor.getCurrentPosition();

            //Insert PID HERE
            double power = curEnc/p;

            if (curEnc < pos) return power;
            if (curEnc > pos) return -power;
        }
        return 0;
    }

    //Resets the encoder count.
    public void resetEncoders() {
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    //Returns whether our encoder is not in the desired position. Useful for loops.
    private boolean setEnc(int pos) {
        return Math.abs(curEnc - pos) > encTolerance;
    }

    //Sets the the intake motor allowing us to run.
    public void setIntake(double power) {
        intakeMotor.setPower(power);
    }

    //Set the position manipulator to the intake position.
    public void intakePosition() {
        leftIntake.setPosition(1);
        rightIntake.setPosition(1);
    }

    //Set the position manipulator to the deposit position.
    public void depositPosition() {
        leftIntake.setPosition(0);
        rightIntake.setPosition(0);
    }
}

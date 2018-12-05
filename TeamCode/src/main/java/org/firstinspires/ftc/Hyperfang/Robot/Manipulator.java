package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
    Created by Caleb.
*/

public class Manipulator {
    //Incremental Servo Control
    public boolean incIntakePosition = false;
    public boolean isActionComplete = true;

    //Lift and Intake Motors
    private DcMotor liftMotor;
    private DcMotor intakeMotor;

    //Deposit Servos
    private Servo deposit;

    //Intake Servos
    private Servo leftIntake;
    private Servo rightIntake;
    private Servo trapDoor;

    //Intake Variables
    private ElapsedTime intakeDelay = new ElapsedTime();
    public boolean powerSet;
    private double intakeLeftPow = 1;
    private double intakeRightPow = 0;

    private OpMode mOpMode;

    //Initializes the manipulator objects.
    public Manipulator(OpMode opMode) {
        mOpMode = opMode;
        liftMotor = mOpMode.hardwareMap.get(DcMotor.class, "hLift");
        intakeMotor = mOpMode.hardwareMap.get(DcMotor.class, "intake");

        liftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        /*
        leftIntake = mOpMode.hardwareMap.get(Servo.class, "leftServo");
        rightIntake = mOpMode.hardwareMap.get(Servo.class, "rightServo");
        trapDoor = mOpMode.hardwareMap.get(Servo.class, "trapDoor");
        */

        deposit = mOpMode.hardwareMap.get(Servo.class, "deposit");
    }

    //Moves the horizontal lift using a given power.
    public void moveLift(double power) {
        liftMotor.setPower(power);
    }

    //Moves the horizontal lift to a certain position using encoders.
    //Encoders have a built-in PID: applicable here due to the use of one motor.
    //resetEncoders() before running this in a loop.
    public void moveLift(double power, int counts) {
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setTargetPosition(counts);
        if (!powerSet) {
            liftMotor.setPower(power);
            powerSet = true;
        }

        if (liftMotor.isBusy()) {
            mOpMode.telemetry.addData("Current Encoder: ", getEncoders());
        } else {
            liftMotor.setPower(0);
            isActionComplete = true;
        }
    }

    //Returns the current encoder value of the horizontal lift.
    public int getEncoders() {
         return liftMotor.getCurrentPosition();
    }

    //Resets the encoder count.
    public void resetEncoders() {
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        powerSet = false;
        isActionComplete = false;
    }

    //Resets the encoder method.
    public void resetEncoderMove() {
        powerSet = false;
        isActionComplete = false;
    }

    /** Below represents methods regarding the intake section of our Manipulator.*/
    //Sets the the intake motor allowing us to run.
    public void setIntake(double power) {
        intakeMotor.setPower(power);
    }

    //Set the position manipulator to the intake position.
    public void intakePosition() {
        //Allows time for slower release.
        if (intakeDelay.milliseconds() > 500) {
            intakeLeftPow = leftIntake.getPosition() + .5;
            intakeRightPow = rightIntake.getPosition() - .5;

            leftIntake.setPosition(intakeLeftPow);
            rightIntake.setPosition(intakeRightPow);

            if (intakeLeftPow >= .25 && intakeRightPow <= .75) {
                leftIntake.setPosition(.25);
                rightIntake.setPosition(.75);
                incIntakePosition = false;
            }
        }
    }

    //Set the position manipulator to the deposit position.
    public void depositPosition() {
        leftIntake.setPosition(0);
        rightIntake.setPosition(1);
    }

    //Moves the trapdoor allowing transfer of minerals to the deposit.
    public void releaseMinerals() {
        trapDoor.setPosition(.15);
    }

    //Moves the trapdoor disallowing transfer of minerals to the deposit.
    public void holdMinerals() {
        trapDoor.setPosition(.5);
    }

    /** Below represents methods regarding the deposit section of our Manipulator.*/
    //Unlocks the deposit disallowing transfer of minerals to the deposit.
    public void unlockDeposit() {
        deposit.setPosition(.6);
    }

    //Locks the deposit disallowing transfer of minerals to the deposit.
    public void lockDeposit() {
        deposit.setPosition(.125);
    }
}
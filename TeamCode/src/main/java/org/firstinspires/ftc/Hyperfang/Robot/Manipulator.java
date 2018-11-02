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
    //Encoders have a built-in PID: applicable here due to the use of one motor.
    //resetEncoders() before running this in a loop.
    public void moveLift(double power, int counts) {
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setTargetPosition(counts);

        liftMotor.setPower(power);

        while (liftMotor.isBusy()) {
            mOpMode.telemetry.addData("Current Encoder: ", getEncoders());
        }
        liftMotor.setPower(0);
    }

    //Returns the current encoder value of the horizontal lift.
    public int getEncoders() {
         return liftMotor.getCurrentPosition();
    }

    //Resets the encoder count.
    public void resetEncoders() {
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
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

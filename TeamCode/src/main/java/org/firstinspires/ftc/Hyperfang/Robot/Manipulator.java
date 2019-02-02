package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
    Created by Baniel. Tested by Caleb.
*/

public class Manipulator {

    //Singleton objects
    private static Manipulator obj;
    private OpMode mOpMode;

    //Servo Objects
    private CRServo leftIntakeMotor;
    private CRServo rightIntakeMotor;
    private Servo leftDeposit;
    private Servo rightDeposit;

    //Elapsed time for automating movements.
    private ElapsedTime eTime = new ElapsedTime();

    //Initializes the manipulator object.
    public static Manipulator getInstance() {
        if (obj == null) {
            throw new NullPointerException("Base Object not created with an OpMode.");
        }
        return obj;
    }

    //Initializes the manipulator object.
    public static Manipulator getInstance(OpMode opMode) {
        if (obj == null) {
            obj = new Manipulator(opMode);
        }
        return obj;
    }

    //Constructor for the base object.
    private Manipulator(OpMode opMode) {
        mOpMode = opMode;
        leftIntakeMotor = mOpMode.hardwareMap.get(CRServo.class, "lMotor");
        rightIntakeMotor = mOpMode.hardwareMap.get(CRServo.class, "rMotor");
        leftDeposit = mOpMode.hardwareMap.get(Servo.class, "lDeposit");
        rightDeposit = mOpMode.hardwareMap.get(Servo.class, "rDeposit");
        mOpMode.telemetry.addLine("Manipulator Version 2 Initialized");
    }

    //Give power to the vex motors.
    public void setMotors(double pow) {
        leftIntakeMotor.setPower(-pow);
        rightIntakeMotor.setPower(pow);
    }

    //Set motors for a given duration to intake.
    public void intake(double pow) {
        setMotors(pow);
    }

    //Stop both motors.
    public void stopMotor() {
        leftIntakeMotor.setPower(0);
        rightIntakeMotor.setPower(0);
    }

    //Open gold servo.
    public void openGold() { leftDeposit.setPosition(.65); }

    //Close gold servo.
    public void closeGold() {
        leftDeposit.setPosition(.13);
    }

    //Open silver servo.
    public void openSilver() { rightDeposit.setPosition(0); }

    //Close silver servo.
    public void closeSilver() {
        rightDeposit.setPosition(.4);
    }

    //Close both the silver and gold deposit.
    public void closeBoth() {
        closeGold();
        closeSilver();
    }

    //Open both the silver and gold deposit.
    public void openBoth() {
        openGold();
        openSilver();
    }

    //A macro for gold depositing.
    public void depositGold(long msDelay) {
        openGold();
        if (eTime.milliseconds() > msDelay) {
            closeGold();
            eTime.reset();
        }
    }

    //A macro for silver depositing.
    public void depositSilver(long msDelay) {
        openSilver();
        if (eTime.milliseconds() > msDelay) {
            closeSilver();
            eTime.reset();
        }
    }

    //A macro for depositing both.
    public void depositBoth(long msDelay) {
        openSilver();
        openGold();
        if (eTime.milliseconds() > msDelay) {
            closeGold();
            closeSilver();
            eTime.reset();
        }
    }
}
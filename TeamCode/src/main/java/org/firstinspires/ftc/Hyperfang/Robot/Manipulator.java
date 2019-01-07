package org.firstinspires.ftc.Hyperfang.Robot;

import android.graphics.Path;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
    Created by Baniel.
*/

public class Manipulator {
    //singleton objects
    private static Manipulator obj;
    private OpMode mOpMode;

    //vex motor objects
    private CRServo leftIntakeMotor;
    private CRServo rightIntakeMotor;

    //servo objects
    private Servo leftDeposit;
    private Servo rightDeposit;

    //elsapsed time for automating motions
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

    //Constructor for the base object
    private Manipulator(OpMode opMode) {
        mOpMode = opMode;
        leftIntakeMotor = mOpMode.hardwareMap.get(CRServo.class, "lMotor");
        rightIntakeMotor = mOpMode.hardwareMap.get(CRServo.class, "rMotor");
        leftIntakeMotor.setDirection(CRServo.Direction.REVERSE);
        leftDeposit = mOpMode.hardwareMap.get(Servo.class, "lDeposit");
        rightDeposit = mOpMode.hardwareMap.get(Servo.class, "rDeposit");

    }

    //Set vex motors to given power
    public void setMotors(double pow) {
        leftIntakeMotor.setPower(pow);
        rightIntakeMotor.setPower(pow);
    }

    //Set motors for a given duration to intake
    public void intake(double pow, double msDuration) {
        if (eTime.milliseconds() < msDuration) {
            setMotors(pow);
        } else {
            setMotors(0);
        }
    }

    //Stop both motors
    public void stopMotor() {
        leftIntakeMotor.setPower(0);
        rightIntakeMotor.setPower(0);
    }

    //Open gold servo
    public void openGold() {
        leftDeposit.setPosition(1);
    }

    //Close gold servo
    public void closeGold() {
        leftDeposit.setPosition(0);
    }

    //Open silver servo
    public void openSilver() {
        rightDeposit.setPosition(1);
    }

    //Close silver servo
    public void closeSilver() {
        rightDeposit.setPosition(0);
    }

    //A macro for gold depositing
    public void depositGold(long msDelay) {
        openGold();
        if (eTime.milliseconds() > msDelay) {
            closeGold();
            eTime.reset();
        }
    }

    //A macro for silver depositing
    public void depositSilver(long msDelay) {
        openSilver();
        if (eTime.milliseconds() > msDelay) {
            closeSilver();
            eTime.reset();
        }
    }

    //A macro for depositing both
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
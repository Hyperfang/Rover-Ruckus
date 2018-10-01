package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.sun.tools.javac.tree.DCTree;

public class Base {

    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;



    public Base(HardwareMap hMap) {
        backLeft = hMap.get(DcMotor.class, "Back Left");
        backRight = hMap.get(DcMotor.class, "Back Right");
        frontLeft = hMap.get(DcMotor.class, "Front Left");
        frontRight = hMap.get(DcMotor.class, "Front Right");

        //May need to set direction of motors
    }

    //Sets the motors
    public void setModeMotor(DcMotor.ZeroPowerBehavior mode) {
        backLeft.setZeroPowerBehavior(mode);
        backRight.setZeroPowerBehavior(mode);
        frontLeft.setZeroPowerBehavior(mode);
        frontRight.setZeroPowerBehavior(mode);
    }

    //Sets the encoders
    public void setModeEncoder(DcMotor.RunMode mode) {
        backLeft.setMode(mode);
        backRight.setMode(mode);
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
    }

    //Allows for movement
    public void move(double linear, double turn) {
        backLeft.setPower(linear - turn);
        backRight.setPower(linear + turn);
        frontLeft.setPower(linear - turn);
        frontRight.setPower(linear + turn);
    }

    //Stops the robot
    public void stop() {
        backLeft.setPower(0);
        backRight.setPower(0);
        frontLeft.setPower(0);
        frontRight.setPower(0);
    }

}




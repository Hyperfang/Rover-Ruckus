package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.Hyperfang.Sensors.IMU;
import org.firstinspires.ftc.Hyperfang.Sensors.Range;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;

//Singleton Design Pattern
public class Base {

    //Singleton object
    private static Base obj;
    private static OpMode mOpMode;

    //Encoder Variables
    private static final double COUNTS_PER_MOTOR_REV = 1440;     // Rev Orbital 40:1
    private static final double DRIVE_GEAR_REDUCTION = 20 / 15.0;// Drive-Train Gear Ratio.
    private static final double WHEEL_DIAMETER_INCHES = 4.0;     // AndyMark Stealth/Omni Wheels
    private static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);

    //Tolerance Variables for the movement methods.
    private static final double encTolerance = 1;
    private static final double turnTolerance = 7.25;
    private static final double rangeTolerance = 1;

    //Logging variables of the Robot position.
    private double curEnc;
    private double curAng;
    private double curDis;

    //Drive-train Motors.
    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;

    //Sensor Instantiation
    private IMU imu;
    private Range rSensor;

    //Initializes the base object.
    public static Base getInstance() {
        if (obj == null) {
            throw new NullPointerException("Base Object not created with an OpMode.");
        }
        return obj;
    }

    //Initializes the base object.
    public static Base getInstance(OpMode opMode) {
        if (obj == null) {
            obj = new Base(opMode);
        }
        return obj;
    }

    //Initializes the base object.
    private Base(OpMode opMode) {
        mOpMode = opMode;
        frontLeft = mOpMode.hardwareMap.get(DcMotor.class, "Front Left");
        frontRight = mOpMode.hardwareMap.get(DcMotor.class, "Front Right");
        backLeft = mOpMode.hardwareMap.get(DcMotor.class, "Back Left");
        backRight = mOpMode.hardwareMap.get(DcMotor.class, "Back Right");

        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        rSensor = new Range("range", opMode);

        setModeEncoder(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    //Initializes the IMU.
    //Runs a loop until the IMU is initialized.
    public void initIMU(OpMode opMode) {
        imu = new IMU(opMode);
        while (!imu.isGyroCalib())
            stop();
    }

    //Sets the mode of the motors.
    public void setModeMotor(DcMotor.ZeroPowerBehavior mode) {
        backLeft.setZeroPowerBehavior(mode);
        backRight.setZeroPowerBehavior(mode);
        frontLeft.setZeroPowerBehavior(mode);
        frontRight.setZeroPowerBehavior(mode);
    }

    //Moves our robot based on left and right powers.
    public void setPower(double left, double right) {
        backLeft.setPower(com.qualcomm.robotcore.util.Range.clip(left, -1.0, 1.0));
        backRight.setPower(com.qualcomm.robotcore.util.Range.clip(right, -1.0, 1.0));
        frontLeft.setPower(com.qualcomm.robotcore.util.Range.clip(left, -1.0, 1.0));
        frontRight.setPower(com.qualcomm.robotcore.util.Range.clip(right, -1.0, 1.0));
    }

    //Moves our robot based on linear (forward/backwards) and turn values (right/left)
    public void move(double linear, double turn) {
        backLeft.setPower(com.qualcomm.robotcore.util.Range.clip(linear + turn, -1.0, 1.0));
        backRight.setPower(com.qualcomm.robotcore.util.Range.clip(linear - turn, -1.0, 1.0));
        frontLeft.setPower(com.qualcomm.robotcore.util.Range.clip(linear + turn, -1.0, 1.0));
        frontRight.setPower(com.qualcomm.robotcore.util.Range.clip(linear - turn, -1.0, 1.0));
    }

    //Stops the robot.
    public void stop() {
        backLeft.setPower(0);
        backRight.setPower(0);
        frontLeft.setPower(0);
        frontRight.setPower(0);
    }

    //Resets the encoder count.
    public void resetEncoders() {
        curEnc = 0;
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    //Sets the mode of the encoders
    public void setModeEncoder(DcMotor.RunMode mode) {
        backLeft.setMode(mode);
        backRight.setMode(mode);
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
    }

    //Sets a target position for all our encoders. (Inches)
    private void setTargetPosition(double pos) {
        backLeft.setTargetPosition((int) (pos * COUNTS_PER_INCH));
        backRight.setTargetPosition((int) (pos * COUNTS_PER_INCH));
        frontLeft.setTargetPosition((int) (pos * COUNTS_PER_INCH));
        frontRight.setTargetPosition((int) (pos * COUNTS_PER_INCH));
    }

    //Sets a target position for all the encoders to stop the motor from being busy.
    private void stopTargetPosition() {
        backLeft.setTargetPosition(backLeft.getCurrentPosition());
        backRight.setTargetPosition(backRight.getCurrentPosition());
        frontRight.setTargetPosition(frontRight.getCurrentPosition());
        frontLeft.setTargetPosition(frontLeft.getCurrentPosition());
    }

    //Calculates the current position for our encoders.
    public double getEncoderPosition() {
        double total = 0;
        total += backLeft.getCurrentPosition();
        total += backRight.getCurrentPosition();
        total += frontLeft.getCurrentPosition();
        total += frontRight.getCurrentPosition();
        return total / 4.0;
    }

    //Moves the robot to a certain position using encoders via Position.
    //resetEncoders() before running this in a loop unless you wish to add to the current position.
    public double encoderMove(double pos) {
        setModeEncoder(DcMotor.RunMode.RUN_TO_POSITION);
        setTargetPosition(pos);
        curEnc = getEncoderPosition();

        if (setEnc(pos)) {
            //Insert PID HERE
            //Using the error to calculate our power.
            double power = (Math.abs(curEnc - (int) (pos * COUNTS_PER_INCH)) / 4000);
            if (power < .1) power = .1;

            if (curEnc < (int) (pos * COUNTS_PER_INCH)) return power;
            if (curEnc > (int) (pos * COUNTS_PER_INCH)) return -power;
        } else {
            stopTargetPosition();
            setModeEncoder(DcMotor.RunMode.RUN_USING_ENCODER);
            stop();
        }
        return 0;
    }

    //Moves the robot to a certain position using encoders via Encoder.
    //resetEncoders() before running this in a loop unless you wish to add to the current position.
    public double encoderMove2(double pos) {
        curEnc = getEncoderPosition();

        if (setEnc(pos)) {
            //Insert PID HERE
            //Using the error to calculate our power.
            double power = (Math.abs(curEnc - (int) (pos * COUNTS_PER_INCH)) / 4000);
            if (power < .1) power = .1;

            if (curEnc < (int) (pos * COUNTS_PER_INCH)) return power;
            if (curEnc > (int) (pos * COUNTS_PER_INCH)) return -power;
        }
        return 0;
    }

    //Returns whether our encoder is not in the desired position (Inches). Useful for loops.
    public boolean setEnc(double pos) {
        return Math.abs(curEnc / COUNTS_PER_INCH - pos) > encTolerance;
    }

    //Returns true if all motors are busy while encoders are ran using position.
    private boolean isBaseBusy() {
        if (!backRight.isBusy()) return false;
        else if (!backLeft.isBusy()) return false;
        else if (!frontLeft.isBusy()) return false;
        else if (!frontRight.isBusy()) return false;
        return true;
    }

    //Turns to a desired angle in the fastest path using the IMU.
    //ABSOLUTE (Based on IMU initialization) where the right axis is a negative value.
    //Uses a P to control precision.
    public double turnAbsolute(double deg) {
        //curAng is the current position of our robot.
        curAng = getHeading();

        //If sensor isn't in the desired angle, run.
        if (setTurn(deg)) {

            //Finding how far away we are from the target position.
            double error = deg - curAng;
            double errorMove = Math.abs(deg - curAng);
            if (error > 180) {
                error = error - 360;
            } else if (error < -180) {
                error = error + 360;
            }

            //Using the error to calculate our power.
            double newPow = (Math.abs(error) / 190);
            if (newPow < .075) newPow = .075;

            //Insert I and D here.

            //The following code allows us to turn in the direction we need, and if we cross the axis
            //at which 180 degrees becomes -180, our robot can turn back in the direction which is closest
            //to the position we wish to be at (We won't make a full rotation to get to -175, if we hit 180).
            if (curAng < deg) {
                if (errorMove < 180) return -newPow; //Turns left
                if (errorMove > 180) return newPow;  //Turns right if we go past the pos/neg mark.

            } else if (curAng > deg) {
                if (errorMove < 180) return newPow;  //Turns right
                if (errorMove > 180) return -newPow; //Turns left if we go past the pos/neg mark.
            }
        }
        return 0;
    }

    //Turns to a desired angle in the fastest path using the IMU.
    //RELATIVE (Based on IMU position) where positive turns right.
    //Uses a P to control precision.
    public double turnRelative(double deg) {
        //After we have figured out the "absolute angle", we can turn to relative.
        return turnAbsolute(deg);
    }

    //Calculates a desired angle to turn to relatively.
    public double calcRelative(double deg) {
        //curAng is the current position of our robot.
        curAng = getHeading();

        //This finds the new "absolute angle" that we would need to turn to.
        double rDeg = curAng - deg;

        //Makes sure that our number is within the 180 degree plane.
        while (rDeg < -180 && rDeg < 180) {
            if (rDeg > 180) {
                rDeg -= 360;
            } else if (rDeg < -180) {
                rDeg += 360;
            }
        }

        //After we have figured out the "absolute angle", we can use it with other methods.
        return rDeg;
    }

    //Returns whether our angle is not in the desired position. Useful for loops.
    public boolean setTurn(double deg) {
        return Math.abs(curAng - deg) > turnTolerance;
    }

    //Moves to a position based on the distance from our range sensor.
    //Uses a P to control precision.
    //Moving forward/backwards using a Range Sensor.
    public double rangeMove(double inAway) {
        //rawRange is the raw range position of our robot regardless of faulty values.
        double rawRange = getRange();

        //If the sensor isn't in the desired position, run.
        if (setRange(inAway)) {
            //If a faulty value is not detected, log it to the current position of our robot.
            if (!(Double.isNaN(rawRange) || (rawRange > 330))) {
                curDis = rawRange;
            }

            //If on the first iteration we manage to retrieve a faulty value, do not run.
            if (curDis != 0) {
                double error = inAway - curDis;

                //Input I D here
                double pow = (Math.abs(error) / 80);
                if (pow > .75) pow = .75;
                if (pow < .075) pow = .075;

                //If the sensor value is greater than the target, move backwards.
                if (curDis > inAway) {
                    return -pow;
                }

                //If the sensor value is lower than than the target, move forwards.
                if (curDis < inAway) {
                    return pow;
                }
            }
        }
        return 0;
    }

    //Returns whether the distance is not in the desired position. Useful for loops.
    public boolean setRange(double inAway) {
        return Math.abs(curDis - inAway) > rangeTolerance;
    }

    //Returns whether the range sensor is timed out.
    public boolean isRangeTimeout() {
        return rSensor.isTimeout();
    }

    //Returns the current heading value of the range sensor.
    public double getHeading() {
        return imu.getHeading();
    }

    //Returns the current distance value of the range sensor.
    public double getRange() {
        return rSensor.getDistanceIN();
    }
}




package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.Hyperfang.Sensors.IMU;
import org.firstinspires.ftc.Hyperfang.Sensors.RangeSensor;
import org.firstinspires.ftc.Hyperfang.Sensors.Vuforia;

public class Base {

    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;

    private IMU imu;
    private RangeSensor rSensor;

    private static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // Rev Orbital 40:1
    private static final double     DRIVE_GEAR_REDUCTION    = 20 / 15.0;// Drive-Train Gear Ratio.
    private static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // Andymark Stealth/Omni Wheels
    private static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);

    private double curEnc;
    private double curAng;
    private double curDis;
    private static final double encTolerance = 100;
    private static final double turnTolerance = 1;
    private static final double rangeTolerance = .35;

    private OpMode mOpMode;

    //Initializes the base objects.
    public Base(OpMode opMode) {
        mOpMode = opMode;
        frontLeft = mOpMode.hardwareMap.get(DcMotor.class, "Front Left");
        frontRight = mOpMode.hardwareMap.get(DcMotor.class, "Front Right");
        backLeft = mOpMode.hardwareMap.get(DcMotor.class, "Back Left");
        backRight = mOpMode.hardwareMap.get(DcMotor.class, "Back Right");

        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        imu = new IMU(opMode);
        rSensor = new RangeSensor("range", opMode);

        initPos();
    }

    //Initializes position so that we can initially run our turn methods.
    public void initPos(){
        curAng = 0;
        curDis = 0;
        curEnc = 0;
    }

    //Initializes position based on estimated location.
    //Used to prevent big jumps in initial runs.
    public void initPos(double pos) {
        curAng = pos;
        curDis = pos;
        curEnc = pos;
    }

    //Sets the mode of the motors.
    public void setModeMotor(DcMotor.ZeroPowerBehavior mode) {
        backLeft.setZeroPowerBehavior(mode);
        backRight.setZeroPowerBehavior(mode);
        frontLeft.setZeroPowerBehavior(mode);
        frontRight.setZeroPowerBehavior(mode);
    }

    //Resets the encoder count.
    public void resetEncoders() {
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
    private void setEncoderPosition(int pos) {
        backLeft.setTargetPosition((int)(pos * COUNTS_PER_INCH));
        backRight.setTargetPosition((int)(pos * COUNTS_PER_INCH));
        frontLeft.setTargetPosition((int)(pos * COUNTS_PER_INCH));
        frontRight.setTargetPosition((int)(pos * COUNTS_PER_INCH));
    }

    //Calculates the current position for our encoders.
    private double getEncoderPosition() {
        double total = 0;
        total += backLeft.getCurrentPosition();
        total += backRight.getCurrentPosition();
        total += frontLeft.getCurrentPosition();
        total += frontRight.getCurrentPosition();
        return total / 4.0;
    }

    //Moves the robot to a certain position using encoders.
    public void moveEncoders(int pos, double power) {
        resetEncoders();
        setModeEncoder(DcMotor.RunMode.RUN_TO_POSITION);
        setEncoderPosition(pos);
        curEnc = getEncoderPosition();

        if (setEnc(pos)) {
            //Insert PID HERE
            move(power, 0);
        }
        stop();
    }

    //Returns whether our encoder is not in the desired position. Useful for loops.
    public boolean setEnc(int pos) {
        return Math.abs(curEnc - pos) < encTolerance;
    }

    //Stops the robot.
    public void stop() {
        backLeft.setPower(0);
        backRight.setPower(0);
        frontLeft.setPower(0);
        frontRight.setPower(0);
    }

    //Moves our robot based on linear (forward/backwards) and turn values (right/left)
    public void move(double linear, double turn) {
        backLeft.setPower(Range.clip(linear + turn, -1.0, 1.0));
        backRight.setPower(Range.clip(linear - turn, -1.0, 1.0));
        frontLeft.setPower(Range.clip(linear + turn, -1.0, 1.0));
        frontRight.setPower(Range.clip(linear - turn, -1.0, 1.0));
    }

    //Moves our robot based on left and right powers.
    public void setPower(double left, double right) {
        backLeft.setPower(Range.clip(left, -1.0, 1.0));
        backRight.setPower(Range.clip(right, -1.0, 1.0));
        frontLeft.setPower(Range.clip(left, -1.0, 1.0));
        frontRight.setPower(Range.clip(right, -1.0, 1.0));
    }

    //Future: Remove power parameters from the turn methods.

    //Turns to a desired angle in the fastest path using the IMU.
    //ABSOLUTE (Based on IMU initialization) where the right axis is a negative value.
    //Uses a P to control precision.
    public double turnAbsolute(double pow, double deg) {
        double newPow;
        double error;
        double errorMove;

        //curAng is the current position of our robot.
        curAng = imu.getHeading();

        //If sensor isn't in the desired angle, run.
        if (setTurn(deg)) {

            //Finding how far away we are from the target position.
            error = deg - curAng;
            errorMove = Math.abs(deg - curAng);
            if (error > 180) {
                error = error - 360;
            } else if (error < -180) {
                error = error + 360;
            }

            //Using the error to calculate our power.
            newPow = pow * (Math.abs(error) / 70);
            if (newPow < .05) newPow = .05;

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
    //Relative (Based on IMU position) where positive turns right.
    //Uses a P to control precision.
    public double turnRelative(double pow, double deg) {
        //curAng is the current position of our robot. NEED TO FIGURE OUT WHAT ANGLE TO USE.
        curAng = imu.getHeading();

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

        //After we have figured out the "absolute angle", we can turn.
        return turnAbsolute(pow, rDeg);
    }

    //Returns whether our angle is not in the desired position. Useful for loops.
    public boolean setTurn(double deg) {
        return Math.abs(curDis - deg) < turnTolerance;
    }

    //Moves to a position based on the distance from our range sensor.
    //Uses a P to control precision.
    public void rangeMove(double inAway, RangeSensor sensor) { //Moving forward/backwards using a Range Sensor.
        curDis = sensor.getDistanceIN();
        double pow;
        double localRange;

        //If sensor isn't in the desired position, run.
        if (setRange(inAway)) {
            localRange = sensor.getDistanceIN();

            //If a faulty value is detected, don't update our used variable till a good one is found.
            while ((Double.isNaN(localRange) || (localRange > 2000))) {
                localRange = sensor.getDistanceIN();
            }

            curDis = localRange; //Sets all working and usable values into a variable we can utilize.

            //Input P I D here
            pow = 0;

            if (curDis > inAway) { //If the sensor value is greater than the target, move forward.
                move(pow, 0);
            }
            if (curDis < inAway) { //If the sensor value is lower than than the target, move backwards.
                move(pow, 0);
            }
        }
        stop();
    }

    //Returns whether our distance is not in the desired position. Useful for loops.
    public boolean setRange(double inAway) {
        return Math.abs(curDis - inAway) < rangeTolerance;
    }

    //Vuforia Turn.
    private double curVF;
    private static final double vfTolerance = 0.0;

    private Vuforia vf;
    //Possible additions to move methods.
    public void vfMove() {}
    public void vfTurn() {}

    public boolean setVF(double value) {
        return Math.abs(curVF - value) < vfTolerance;
    }
}




package org.firstinspires.ftc.Hyperfang.Sensors;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import java.util.Locale;

/*
    Created by Caleb.
*/

public class IMU {

    //The IMU sensor object
    private BNO055IMU imu;

    //State used for updating telemetry
    private Orientation angles;
    private Acceleration gravity;
    private BNO055IMU.Parameters parameters;

    //Initializes our IMU through just the gyroscope.
    public IMU(OpMode opMode) {
        parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu = opMode.hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    //Initializes our IMU through with the accelerometer.
    public IMU(OpMode opMode, int msPollInt) {
        parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu = opMode.hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
        imu.startAccelerationIntegration(new Position(), new Velocity(), msPollInt);
    }

    //Returning if our Gyroscope is initialized
    public boolean isGyroCalib() {
        return (imu.isGyroCalibrated());
    }

    //Returning if our Accelerometer is initialized
    public boolean isAccelCalib() {
        return (imu.isAccelerometerCalibrated());
    }

    //Returning our Heading
    public double getHeading() {
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return Double.parseDouble(formatAngle(angles.angleUnit, angles.firstAngle));
    }

    //Returning our Pitch
    public double getPitch() {
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return Double.parseDouble(formatAngle(angles.angleUnit, angles.secondAngle));
    }

    //Returning our Roll
    public double getRoll() {
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return Double.parseDouble(formatAngle(angles.angleUnit, angles.thirdAngle));
    }

    //Returning our Gravity
    public String getGravity() {
        gravity = imu.getGravity();
        return gravity.toString();
    }

    //Returning our Acceleration
    public String getAccel() {
        gravity = imu.getGravity();
        return String.format(Locale.getDefault(), "%.3f",
                Math.sqrt(gravity.xAccel * gravity.xAccel
                        + gravity.yAccel * gravity.yAccel
                        + gravity.zAccel * gravity.zAccel));
    }

    // Formatting
    private String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }

    private String formatDegrees(double degrees) {
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }
}

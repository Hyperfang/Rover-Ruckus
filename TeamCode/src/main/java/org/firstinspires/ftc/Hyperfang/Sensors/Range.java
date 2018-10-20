package org.firstinspires.ftc.Hyperfang.Sensors;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Range {

    private DistanceSensor sensor;

    //Initializes our Distance Sensor object.
    public Range(String deviceName, OpMode opMode) {
        sensor = opMode.hardwareMap.get(DistanceSensor.class, deviceName);
    }

    //Return our distance from an object to the sensor in millimeters.
    public double getDistanceMM() {
        return sensor.getDistance(DistanceUnit.MM);
    }

    //Return our distance from an object to the sensor in centimeters.
    public double getDistanceCM() {
        return sensor.getDistance(DistanceUnit.CM);
    }

    //Return our distance from an object to the sensor in meters.
    public double getDistanceM() {
        return sensor.getDistance(DistanceUnit.METER);
    }

    //Return our distance from an object to the sensor in inches.
    public double getDistanceIN() {
        return sensor.getDistance(DistanceUnit.INCH);
    }

    //Return our time of flight from an object to the sensor.
    public Rev2mDistanceSensor getTOF() {
        return (Rev2mDistanceSensor) sensor;
    }

    //Return if our sensor timed out.
    public boolean isTimeout() {
        return ((Rev2mDistanceSensor) sensor).didTimeoutOccur();
    }

}
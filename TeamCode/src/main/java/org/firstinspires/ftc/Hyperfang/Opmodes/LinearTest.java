package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.Hyperfang.Sensors.IMU;
import org.firstinspires.ftc.Hyperfang.Sensors.RangeSensor;
import org.firstinspires.ftc.Hyperfang.Sensors.Vuforia;

public class LinearTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        IMU imu = new IMU("imu");
        telemetry.addLine("IMU initialized.");

        Vuforia vuforia = new Vuforia("cameraMonitorViewID");
        telemetry.addLine("Vuforia initialized.");

        RangeSensor range = new RangeSensor("range");
        telemetry.addLine("Range Sensor initialized.");


       vuforia.activate();

       //Testing Methods
        while(opModeIsActive()) {
            vuforia.getVuMark();
            telemetry.addData("X", vuforia.getDistanceX());
            telemetry.addData("Y", vuforia.getDistanceY());
            telemetry.addData("Z", vuforia.getDistanceZ());
            telemetry.addData("VUFORIA Head", vuforia.getHeading());
            telemetry.addData("IMU head", imu.getHeading());
            telemetry.update();
        }


    }
}
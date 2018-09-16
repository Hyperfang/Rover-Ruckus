package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.Hyperfang.Sensors.IMU;
import org.firstinspires.ftc.Hyperfang.Sensors.Vuforia;

public class LinearTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        IMU imu = new IMU();
        telemetry.addLine("IMU initialized.");

        Vuforia vuforia = new Vuforia("cameraMonitorViewID");
        telemetry.addLine("Vuforia initialized.");

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
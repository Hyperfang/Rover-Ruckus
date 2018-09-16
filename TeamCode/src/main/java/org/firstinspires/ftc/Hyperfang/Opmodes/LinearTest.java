package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.Hyperfang.Sensors.IMU;
import org.firstinspires.ftc.Hyperfang.Sensors.OpenCV;
import org.firstinspires.ftc.Hyperfang.Sensors.RangeSensor;
import org.firstinspires.ftc.Hyperfang.Sensors.Vuforia;
import org.opencv.core.Mat;

public class LinearTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        IMU imu = new IMU("imu");
        telemetry.addLine("IMU initialized.");

        Vuforia vuforia = new Vuforia("cameraMonitorViewID");
        telemetry.addLine("Vuforia initialized.");

        OpenCV cv = new OpenCV();
        telemetry.addLine("OpenCV initialized.");

        RangeSensor range = new RangeSensor("range");
        telemetry.addLine("Range Sensor initialized.");

        vuforia.activate();

        telemetry.clear();

        //Testing Methods - 1
        while (opModeIsActive()) {

            vuforia.getVuMark();
            telemetry.addData("X", vuforia.getDistanceX());
            telemetry.addData("Y", vuforia.getDistanceY());
            telemetry.addData("Z", vuforia.getDistanceZ());
            telemetry.addData("VUFORIA Head", vuforia.getHeading());
            telemetry.addData("IMU head", imu.getHeading());
            telemetry.update();
        }

        /**
         //Testing Methods - 2
         vuforia.getVuMark();
         telemetry.addLine("VuMark Acquired");
         Mat tester = cv.getVuforia(vuforia.getBitmap());
         telemetry.addLine("Debug false"); */
    }


    }
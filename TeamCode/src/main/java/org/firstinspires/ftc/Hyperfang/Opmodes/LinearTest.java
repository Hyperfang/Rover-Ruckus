package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.Hyperfang.Sensors.IMU;
import org.firstinspires.ftc.Hyperfang.Sensors.MGL;
import org.firstinspires.ftc.Hyperfang.Sensors.OpenCV;
import org.firstinspires.ftc.Hyperfang.Sensors.Vuforia;

@TeleOp(name = "Linear Test", group = "Linear Opmode")
public class LinearTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        // RangeSensor range = new RangeSensor("range", hardwareMap);
        // telemetry.addLine("Range Sensor initialized.");
        Vuforia vf = new Vuforia();
        OpenCV cv = new OpenCV();

        telemetry.update();
        waitForStart();
        telemetry.clear();

        vf.activate();
        //Testing Methods
        while (opModeIsActive()) {
            telemetry.addData("Phone: ", cv.phoneMidpoint(cv.getVuforia( vf.getBitmap() )).toString());
            cv.findGold( cv.getVuforia( vf.getBitmap() ), telemetry );
            //Test: Base Movements, Lift Tracking, 
            telemetry.update();
        }
    }
}
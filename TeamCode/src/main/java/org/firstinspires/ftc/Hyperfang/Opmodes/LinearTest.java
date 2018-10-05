package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Sensors.OpenCV;
import org.firstinspires.ftc.Hyperfang.Sensors.RangeSensor;
import org.firstinspires.ftc.Hyperfang.Sensors.Vuforia;

@TeleOp(name = "Linear Test", group = "Linear Opmode")
public class LinearTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        Vuforia vf = new Vuforia();
        OpenCV cv = new OpenCV();
        cv.activate(vf.getBitmap());
        //Initialization Runtime: 1400 ms. (First Run Exception of 1800 ms)

        telemetry.update();
        waitForStart();
        telemetry.clear();
        vf.activate();

        //Testing Methods
        while (opModeIsActive()) {
            cv.findGold( cv.getVuforia( vf.getBitmap() ), telemetry );
            //Test: Base Movements, Lift Tracking, Marker
            telemetry.update();
        }
    }
}
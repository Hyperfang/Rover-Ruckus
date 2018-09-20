package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Linear Test", group = "Linear Opmode")
public class LinearTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        // MagneticLimitSwitch MGL = new MagneticLimitSwitch();
        //MGL mgl = new MGL()

        // RangeSensor range = new RangeSensor("range", hardwareMap);
        // telemetry.addLine("Range Sensor initialized.");

        telemetry.update();
        waitForStart();
        telemetry.clear();

        //Testing Methods
        while (opModeIsActive()) {
            //Nothing to Test
            telemetry.update();
        }
    }
}
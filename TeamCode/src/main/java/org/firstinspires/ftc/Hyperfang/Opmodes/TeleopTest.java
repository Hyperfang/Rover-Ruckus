package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Controls;

import java.util.Arrays;


@TeleOp(name="Teleop Test", group="Iterative Opmode")
public class TeleopTest extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private Controls controls;


    @Override
    public void init() {
        controls = new Controls(this);
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {

    }


    @Override
    public void loop() {
        controls.setSpeedButtons(gamepad1.a, gamepad1.b);
        controls.moveArcade();

        // Show the elapsed game time and wheel power.
        telemetry.addData("Drive Variables", Arrays.toString(controls.getDriveValue()));
        telemetry.addData("Half Speed toggle is : ", controls.getSpeedToggle());
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }


    @Override
    public void stop() {
    }

}
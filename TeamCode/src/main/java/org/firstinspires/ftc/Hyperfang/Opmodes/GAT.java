package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Controls;


@TeleOp(name="GAT", group="Iterative Opmode")
public class GAT extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime slowDelay = new ElapsedTime();
    private ElapsedTime revDelay = new ElapsedTime();
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
        controls.moveGAT();
        controls.setDirectionButton(gamepad1.y, revDelay);
        controls.setSpeedButtons(gamepad1.a, gamepad1.b, slowDelay);


        // Show the elapsed game time and wheel power.
        telemetry.addData("Half Mode: ", controls.getSpeedToggle());
        telemetry.addData("Reverse Mode: ", controls.getDirectionToggle());
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    @Override
    public void stop() {
    }

}
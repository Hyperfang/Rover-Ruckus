package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Controls;


@TeleOp(name="Control", group="Iterative Opmode")
public class ControlTest extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime slowDelay = new ElapsedTime();
    private ElapsedTime revDelay = new ElapsedTime();

    private ElapsedTime ratchetDelay = new ElapsedTime();
    private ElapsedTime hookDelay = new ElapsedTime();
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
       // controls.initRobot();
    }

    @Override
    public void loop() {
        //controls.moveRatchet();

        controls.moveLift(-gamepad2.right_stick_y);
        controls.ratchetLock(gamepad2.a, ratchetDelay);
        controls.hook(gamepad2.y, hookDelay);

        controls.moveHLift(-gamepad2.left_stick_y);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Power: ", -gamepad2.right_stick_y);
        telemetry.addData("Lift Position: ", controls.getLevel());
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    @Override
    public void stop() {
    }

}
package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Controls;


@TeleOp(name="Control", group="Iterative Opmode")
public class ControlTest extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    //Toggle Timers.
    private ElapsedTime slowDelay = new ElapsedTime();
    private ElapsedTime revDelay = new ElapsedTime();
    private ElapsedTime ratchetDelay = new ElapsedTime();
    private ElapsedTime hookDelay = new ElapsedTime();

    //Instantiating the controls object.
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
        //Both drivers control the Hook.
        controls.hook(gamepad1.b, hookDelay);
        controls.hook(gamepad2.y, hookDelay);

        //Driver 1 controls Driving: Base, Movement Modifiers

        //Driver 2 controls Manipulation: Vertical Lift, Horizontal Lift, Intake
        //controls.intake(-gamepad2.trigger)
        controls.moveLift(-gamepad2.right_stick_y);
        controls.moveHLift(-gamepad2.left_stick_y);

        //Driver 2 controls Hanging: Ratchet, Ratchet Lock.
        controls.moveRatchet(gamepad2.right_trigger);
        controls.ratchetLock(gamepad2.a, ratchetDelay);
        controls.hook(gamepad2.y, hookDelay);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Power: ", -gamepad2.right_stick_y);
        telemetry.addData("Lift Position: ", controls.getLevel());
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    @Override
    public void stop()
    {

    }

}
package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Controls;
import org.firstinspires.ftc.Hyperfang.Robot.Manipulator;


@TeleOp(name="Control", group="Iterative Opmode")
public class ControlTest extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    //Toggle Timers.
    private ElapsedTime slowDelay = new ElapsedTime();
    private ElapsedTime revDelay = new ElapsedTime();
    private ElapsedTime intakePosDelay = new ElapsedTime();
    private ElapsedTime trapDelay = new ElapsedTime();
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
        controls.initRobot();
    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
       // man.unlockDeposit(controls.testServo(gamepad1.a, gamepad1.b, revDelay));

        //Both drivers control the Hook.
        controls.hook(gamepad1.b, hookDelay);
        controls.hook(gamepad2.y, hookDelay);

        //Driver 1 controls Driving: Base, Movement Modifiers
        controls.moveTank();
        controls.setDirectionButton(gamepad1.y, revDelay);
        controls.setSpeedButtons(gamepad1.a, gamepad1.b, slowDelay);

        //Driver 2 controls Manipulation: Vertical Lift, Horizontal Lift,
        controls.moveVLift(-gamepad2.left_stick_y);
        controls.moveHLift(-gamepad2.right_stick_y);

        //Intake, Intake Position, Transfer
        controls.intake(gamepad2.right_trigger);
        controls.intakePosition(gamepad2.right_bumper, intakePosDelay);
        controls.trapdoor(gamepad2.left_bumper, trapDelay);

        //Deposit
        //controls.test(controls.testServo(gamepad1.a, gamepad1.b, slowDelay));

        //Driver 2 controls Hanging: Ratchet, Ratchet Lock.
        controls.moveRatchet(gamepad2.left_trigger);
        controls.ratchetLock(gamepad2.x, ratchetDelay);

        // Show the elapsed game time and wheel power.

        telemetry.addData("Power: ", -gamepad2.left_stick_y);
        telemetry.addData("Lift Position: ", controls.getLevel());
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    @Override
    public void stop()
    {

    }

}
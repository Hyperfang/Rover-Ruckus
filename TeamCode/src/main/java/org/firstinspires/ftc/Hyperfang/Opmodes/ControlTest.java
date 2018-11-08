package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Controls;


@TeleOp(name="Control", group="Iterative Opmode")
@Disabled
public class ControlTest extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();

    //Toggle Timers
    //TODO: Move to controls class.
    private ElapsedTime slowDelay = new ElapsedTime();
    private ElapsedTime revDelay = new ElapsedTime();
    private ElapsedTime intakePosDelay = new ElapsedTime();
    private ElapsedTime trapDelay = new ElapsedTime();
    private ElapsedTime ratchetDelay = new ElapsedTime();
    private ElapsedTime hookDelay = new ElapsedTime();
    private ElapsedTime depDelay = new ElapsedTime();

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

    /**Below is the controls and which drivers the correspond to. Here are the current controls
     * being used on the Gamepads.
     *
     * Gamepad 1: Left Stick, Right Stick, Left Trigger, Right Trigger,
     *            Left Bumper, Right Bumper, A, Y
     *
     * Gamepad 2: Left Stick, Right Stick, Left Trigger, Right Trigger,
     *            Left Bumper, Right Bumper, A, B, X, Y
     */

    @Override
    public void loop() {
        //Both drivers control the Intake, and Hook.
        controls.intake(gamepad1.right_trigger, gamepad1.left_trigger);
        controls.intake(gamepad2.right_trigger, gamepad2.left_trigger);
        controls.hook(gamepad1.a, hookDelay);
        controls.hook(gamepad2.y, hookDelay);

        //Driver 1 controls Driving: Base, Movement Modifiers
        controls.moveTank();

        //Reverse Mode, Half-Speed, Reset (Half-Speed)
        controls.setDirectionButton(gamepad1.left_bumper, revDelay);
        controls.setSpeedButtons(gamepad1.right_bumper, gamepad1.y, slowDelay);

        //Driver 2 controls Manipulation: Vertical Lift, Horizontal Lift,
        controls.moveVLift(-gamepad2.left_stick_y);
        controls.moveHLift(-gamepad2.right_stick_y);

        //Intake, Intake Position, Transfer
        controls.intakePosition(gamepad2.right_bumper, intakePosDelay);
        controls.trapdoor(gamepad2.left_bumper, trapDelay);

        //Deposit
        controls.deposit(gamepad2.b, depDelay);

        //Driver 2 controls Hanging: Ratchet, Ratchet Lock.
        controls.moveRatchet(gamepad2.x);
        controls.ratchetLock(gamepad2.a, ratchetDelay);

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
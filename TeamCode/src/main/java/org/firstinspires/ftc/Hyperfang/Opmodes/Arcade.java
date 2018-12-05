package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Controls;


@TeleOp(name="Arcade", group="Iterative Opmode")
public class Arcade extends OpMode {

    //Runtime Variable
    private ElapsedTime runtime = new ElapsedTime();

    //Instantiating the controls object.
    private Controls controls;

    @Override
    public void init() {
        controls = new Controls(this);
        telemetry.addData("Status", "Initialized");
        controls.initRobot();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
    }

    /**Below is the controls and which drivers the correspond to. Here are the current controls
     * being used on the Gamepads.
     *
     * Gamepad 1: Left Stick, Right Stick, Left Trigger, Right Trigger,
     *            Left Bumper, Right Bumper, B, Y
     *
     * Gamepad 2: Left Stick, Right Stick, Left Trigger, Right Trigger,
     *            Left Bumper, Right Bumper, A, B, Y, Right Bumper, Left Bumper
     */

    @Override
    public void loop() {
        //Both Drivers control the Intake, and Hook.
        //controls.intake(gamepad1.right_trigger, gamepad1.left_trigger);
        //controls.intake(gamepad2.right_trigger, gamepad2.left_trigger);
        controls.hook(gamepad1.b);
        controls.hook(gamepad2.y);

        //Driver 1 controls Driving: Base, Movement Modifiers
        controls.moveArcade();

        //Reverse Mode, Half-Speed, Reset (Half-Speed)
        controls.setDirectionButton(gamepad1.left_bumper);
        controls.setSpeedButtons(gamepad1.right_bumper, gamepad1.y);

        //Driver 2 controls Manipulation: Vertical Lift, Horizontal Lift,
        controls.moveVLift(-gamepad2.left_stick_y);
        controls.moveHLift(-gamepad2.right_stick_y);

        //Intake, Intake Position, Transfer
        //controls.intakePosition(gamepad2.left_bumper, intakePosDelay);
        //controls.trapdoor(gamepad2.dpad_down);

        //Deposit
        controls.deposit(gamepad2.b);

        //Driver 2 controls Hanging: Ratchet, Ratchet Lock.
        controls.moveRatchet(gamepad2.right_bumper, gamepad2.left_bumper);
        controls.ratchetLock(gamepad2.a);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Half Mode: ", controls.getSpeedToggle());
        telemetry.addData("Reverse Mode: ", controls.getDirectionToggle());
        telemetry.addData("Ratchet Lock: ", controls.getRatchetLock());
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    @Override
    public void stop() {
    }
}
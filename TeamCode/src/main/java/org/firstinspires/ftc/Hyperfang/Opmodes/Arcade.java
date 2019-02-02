package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Controls;


@TeleOp(name="Arcade", group="Iterative Opmode")
public class Arcade extends OpMode {

    //Runtime Variable
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init() {
        //Instantiating the controls object.
        Controls.getInstance(this).initRobot();
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void init_loop() {}

    @Override
    public void start() { }

    /**Below is the controls and which drivers the correspond to. Here are the current controls
     * being used on the Gamepads.
     *
     * Gamepad 1: Left Stick, Right Stick, Left Bumper, Right Bumper
     *
     * Gamepad 2: Left Stick, Right Stick, Left Bumper, Right Bumper,
     *            X, Y, DPAD (UP, DOWN, LEFT)
     */

    @Override
    public void loop() {
        //Drivers do not have co-op control over any controls.

        //Driver 1 controls Driving: Base, Movement Modifiers (Reverse Mode, Half-Speed, Reset (Half-Speed))
        Controls.getInstance().moveArcade();

        //Reverse Mode, Half-Speed, Reset (Half-Speed)
        Controls.getInstance().setDirectionButton(gamepad1.left_bumper);
        Controls.getInstance().setSpeedButtons(gamepad1.right_bumper, gamepad1.y);

        //Driver 2 controls the Lift: Lift, Pivot and Lift Lock
        Controls.getInstance().lock(gamepad2.y);
        Controls.getInstance().moveLift(-gamepad2.left_stick_y);
        Controls.getInstance().macroPivot(gamepad2.dpad_left, gamepad2.dpad_up, gamepad2.dpad_down);

        //Driver 2 controls Manipulation: Intake and Deposit
        Controls.getInstance().intake(gamepad2.right_bumper, gamepad2.left_bumper);
        Controls.getInstance().deposit(gamepad2.x);

        // Show the elapsed game time and modes.
        telemetry.addData("Half Mode: ", Controls.getInstance().getSpeedToggle());
        telemetry.addData("Reverse Mode: ", Controls.getInstance().getDirectionToggle());
        telemetry.addData("The Hooks are ", Controls.getInstance().getHook());
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    @Override
    public void stop() {
    }
}
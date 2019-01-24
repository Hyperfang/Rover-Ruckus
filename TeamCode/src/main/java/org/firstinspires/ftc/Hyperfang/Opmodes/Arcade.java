package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;
import org.firstinspires.ftc.Hyperfang.Robot.Controls;
import org.firstinspires.ftc.Hyperfang.Robot.Lift;
import org.firstinspires.ftc.Hyperfang.Robot.Manipulator;

import java.util.Arrays;


@TeleOp(name="Arcade", group="Iterative Opmode")
public class Arcade extends OpMode {

    //Runtime Variable
    private ElapsedTime runtime = new ElapsedTime();

    //Instantiating the controls object.

    @Override
    public void init() {
        Controls.getInstance(this);
        Controls.getInstance().initRobot();
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void init_loop() {}

    @Override
    public void start() { }

    /**Below is the controls and which drivers the correspond to. Here are the current controls
     * being used on the Gamepads.
     *
     * Gamepad 1: Left Stick, Right Stick, Left Bumper, Right Bumper, Y
     *
     * Gamepad 2: Left Stick, Right Stick, Left Trigger, Right Trigger,
     *            Bumper, A, Y, Right Bumper, Left Bumper
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
        Controls.getInstance().moveLift(gamepad2.left_stick_y);
        Controls.getInstance().macroPivot(gamepad2.a);
        //Add Minor Adjustments

        //Driver 2 controls Manipulation: Intake and Deposit
        Controls.getInstance().intake(gamepad2.left_bumper, gamepad2.right_bumper);
        Controls.getInstance().deposit(gamepad2.x);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Base Encoder", Base.getInstance().getEncoderPosition());
        telemetry.addData("Pivot Encoder", Lift.getInstance().getPivotPosition());
        telemetry.addData("Right Encoder", Lift.getInstance().getLiftPosition());
        telemetry.addData("Left Encoder", Lift.getInstance().getLiftLeft());

        telemetry.addData("Half Mode: ", Controls.getInstance().getSpeedToggle());
        telemetry.addData("Reverse Mode: ", Controls.getInstance().getDirectionToggle());
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    @Override
    public void stop() {
    }
}
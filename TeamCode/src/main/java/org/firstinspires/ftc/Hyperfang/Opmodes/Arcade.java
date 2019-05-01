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

        Controls.getInstance().setSpeedButtons(gamepad1.right_bumper, gamepad1.y);
    }

    @Override
    public void stop() {
    }
}
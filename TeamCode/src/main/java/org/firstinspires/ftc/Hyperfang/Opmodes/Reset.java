package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Lift;

@Autonomous(name="Reset", group="Iterative Opmode")
public class Reset extends OpMode {

//--------------------------------------------------------------------------------------------------
    public Reset() {} //Default Constructor
//--------------------------------------------------------------------------------------------------

    private ElapsedTime runtime;

    //Initialization: Runs once driver presses init.
    @Override
    public void init() {
        runtime = new ElapsedTime();

        //Instantiating our robot objects.
        Lift.getInstance(this).ftcEnc();

        //Indicates that initialization is complete.
        telemetry.addLine("Initialized in " + runtime.milliseconds() + "ms");
    }

    //Initialization Loop: Loops when driver presses init after init() runs.
    @Override
    public void init_loop() {}

    //Start: Runs once driver hits play.
    @Override
    public void start() {}

    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {
        Lift.getInstance().resetPivotPosition();
        Lift.getInstance().resetLiftPosition();
        telemetry.addLine("Lift and Pivot Encoders Reset.");
    }

    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {}
}

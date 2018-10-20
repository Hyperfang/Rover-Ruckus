package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;

@Autonomous(name="Test", group="Iterative Opmode")
public class IterativeTest extends OpMode {

    //--------------------------------------------------------------------------------------------------
    public IterativeTest() {
    } //Default Constructor
//--------------------------------------------------------------------------------------------------

    private ElapsedTime runtime;
    private Base base;

    //Initialization: Runs once  driver presses init.
    @Override
    public void init() {
        runtime = new ElapsedTime();

        //Instantiating our robot objects.
        base = new Base(this);

        //Indicates that initialization is complete.
        telemetry.addLine("Initialized in " + runtime.milliseconds() + "ms");
    }

    //Initialization Loop: Loops when driver presses init after init() runs.
    @Override
    public void init_loop() {
    }


    //Start: Runs once driver hits play.
    @Override
    public void start() {

    }

    private double relAng2 = 0;
    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {
        telemetry.addLine("Running");
        base.move(base.rangeMove(10),0 );
    }

    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {
    }

}

package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;
import org.firstinspires.ftc.Hyperfang.Sensors.OpenCV;
import org.firstinspires.ftc.Hyperfang.Sensors.Tensorflow;
import org.firstinspires.ftc.Hyperfang.Sensors.Vuforia;

@Autonomous(name="Test", group="Iterative Opmode")
public class IterativeTest extends OpMode {

    //--------------------------------------------------------------------------------------------------
    public IterativeTest() {
    } //Default Constructor
//--------------------------------------------------------------------------------------------------

    private ElapsedTime runtime;
    private Tensorflow tf;
    private Vuforia vuforia;

    //Initialization: Runs once  driver presses init.
    @Override
    public void init() {
        runtime = new ElapsedTime();

        //Instantiating our robot objects.
        vuforia = new Vuforia(this);
        tf = new Tensorflow(this, vuforia.getLocalizer());

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
        tf.activate();
    }

    private double relAng2 = 0;
    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {
        while(!tf.isPosFound()) {
            tf.sample();
        }

        telemetry.addData("Position of gold cube is ", tf.getPos().name());
        telemetry.addData("Position of gold cube is ", tf.isPosFound());
        }


    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {
    }

}

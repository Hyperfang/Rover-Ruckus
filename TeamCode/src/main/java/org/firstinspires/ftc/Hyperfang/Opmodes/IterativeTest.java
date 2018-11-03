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
    private Base base;

    //Initialization: Runs once  driver presses init.
    @Override
    public void init() {
        runtime = new ElapsedTime();

        //Instantiating our robot objects.
        vuforia = new Vuforia();
        tf = new Tensorflow(this, vuforia.getLocalizer(), "id");
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
        vuforia.activate();
        //tf.activate();
    }

    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {

        /*if (base.setRange(10)) {
            base.move(base.rangeMove(10), 0);
        }*/

        telemetry.addData("IMU", base.getRange());
    }


    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {
    }

}

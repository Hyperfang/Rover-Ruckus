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
        base = new Base(this);
        base.initIMU(this);
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
        vuforia.activate();
        tf.activate();
        base.resetEncoders();
    }


    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {
        /*
        if (base.setEnc(12))
        {
            base.move(base.moveEncoders(12), 0);
        } else { base.stop(); }
        */

        telemetry.addData("IMU: ", base.getHeading());
        telemetry.addData("RANGE: ", base.getRange());
        tf.sample2();
        vuforia.getVuMark();
        telemetry.addData("Vuforia: ", vuforia.isVisible());
        telemetry.addData("Position: ", tf.getPos().name());
    }

    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {
        tf.deactivate();
    }
}

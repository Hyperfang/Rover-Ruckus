package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;
import org.firstinspires.ftc.Hyperfang.Robot.Lift;
import org.firstinspires.ftc.Hyperfang.Sensors.Tensorflow;
import org.firstinspires.ftc.Hyperfang.Sensors.Vuforia;

@Autonomous(name="Test", group="Iterative Opmode")
public class IterativeTest extends OpMode {

//--------------------------------------------------------------------------------------------------
    public IterativeTest() {} //Default Constructor
//--------------------------------------------------------------------------------------------------

    private ElapsedTime runtime;
    private Tensorflow tf;
    private Vuforia vuforia;
    private Base base;
    private Lift lift;

    //Initialization: Runs once driver presses init.
    @Override
    public void init() {
        runtime = new ElapsedTime();

        //Instantiating our robot objects.
        base = new Base(this);
        vuforia = new Vuforia(this);
        tf = new Tensorflow(this, vuforia.getLocalizer());
        lift = new Lift(this);

        base.initIMU(this);

        //Indicates that initialization is complete.
        telemetry.addLine("Initialized in " + runtime.milliseconds() + "ms");
    }

    //Initialization Loop: Loops when driver presses init after init() runs.
    @Override
    public void init_loop() {}

    //Start: Runs once driver hits play.
    @Override
    public void start() {
        base.resetEncoders();
        vuforia.activate();
        tf.activate();
    }

    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {
        vuforia.getVuMark();
        tf.sample2();

        telemetry.addData("IMU: ", base.getHeading());
        telemetry.addData("RANGE: ", base.getRange());
        telemetry.addData("ENCODERS: ", base.getEncoderPosition());
        telemetry.addData("Vuforia: ", vuforia.isVisible());
        telemetry.addData("Position: ", tf.getPos().name());
    }

    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {
        tf.deactivate();
    }
}

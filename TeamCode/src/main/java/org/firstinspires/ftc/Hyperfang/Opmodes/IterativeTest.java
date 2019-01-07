package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;
import org.firstinspires.ftc.Hyperfang.Robot.Lift;
import org.firstinspires.ftc.Hyperfang.Sensors.MGL;
import org.firstinspires.ftc.Hyperfang.Vision.Tensorflow;
import org.firstinspires.ftc.Hyperfang.Vision.Vuforia;

@Autonomous(name="Test", group="Iterative Opmode")
public class IterativeTest extends OpMode {

//--------------------------------------------------------------------------------------------------
    public IterativeTest() {} //Default Constructor
//--------------------------------------------------------------------------------------------------

    private ElapsedTime runtime;
    private Tensorflow tf;
    private Vuforia vuforia;
    //private Base base;
    private MGL mgl;
    private ElapsedTime PoT = new ElapsedTime();

    //Initialization: Runs once driver presses init.
    @Override
    public void init() {
        runtime = new ElapsedTime();

        //Instantiating our robot objects.
        Base.getInstance(this).initIMU(this);

       //vuforia = new Vuforia(this);
       //tf = new Tensorflow(this, vuforia.getLocalizer());
       // lift = new Lift(this);

        //mgl = new MGL(this);
        //Indicates that initialization is complete.
        telemetry.addLine("Initialized in " + runtime.milliseconds() + "ms");
    }

    //Initialization Loop: Loops when driver presses init after init() runs.
    @Override
    public void init_loop() {}

    //Start: Runs once driver hits play.
    @Override
    public void start() {
        Base.getInstance().resetEncoders();
        //vuforia.activate();
        PoT.reset();
        //tf.activate();
    }

    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {
       //vuforia.getVuMark();
       //tf.sample();

        telemetry.addData("test",
                -2 * Math.pow(PoT.seconds(), 3) + .65 * Math.pow(PoT.seconds(), 2) -.25 * PoT.seconds() + .6);
       telemetry.addData("IMU: ", Base.getInstance().getHeading());
       // telemetry.addData("RANGE: ", Base.getInstance().getRange());
        //telemetry.addData("ENCODERS: ", Base.getInstance().getEncoderPosition());telemetry.addData("Vuforia: ", vuforia.isVisible());
      //telemetry.addData("Position: ", tf.getPos().name());
    }

    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {
       //tf.deactivate();
    }
}

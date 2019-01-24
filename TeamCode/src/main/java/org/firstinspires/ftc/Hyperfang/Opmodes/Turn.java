package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;
import org.firstinspires.ftc.Hyperfang.Vision.Tensorflow;
import org.firstinspires.ftc.Hyperfang.Vision.Vuforia;

@Autonomous(name="Nerd", group="Iterative Opmode")
public class Turn extends OpMode {

//--------------------------------------------------------------------------------------------------
    public Turn() {} //Default Constructor
//--------------------------------------------------------------------------------------------------

    private ElapsedTime runtime;
    private Tensorflow tf;
    private Vuforia vuforia;

    private ElapsedTime PoT = new ElapsedTime();

    //Initialization: Runs once driver presses init.
    @Override
    public void init() {
        runtime = new ElapsedTime();

        //Instantiating our robot objects.
        Base.getInstance(this).ftcEnc();
        Base.getInstance(this).initIMU(this);
        //Lift.getInstance(this);
        //vuforia = new Vuforia(this);
        //tf = new Tensorflow(this, vuforia.getLocalizer());

        //Indicates that initialization is complete.
        telemetry.addLine("Initialized in " + runtime.milliseconds() + "ms");
    }

    //Initialization Loop: Loops when driver presses init after init() runs.
    @Override
    public void init_loop() {}

    //Start: Runs once driver hits play.
    @Override
    public void start() {
        PoT.reset();
    }

    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {
        if (Base.getInstance().setEnc(10)) Base.getInstance().move(Base.getInstance().encoderMove(10),0);
        else Base.getInstance().stop();

        telemetry.addData("IMU", Base.getInstance().getHeading());
       // telemetry.addData("Cube: ", tf.getPos());
        //telemetry.addData("Vuforia", vuforia.getVuMarkName());
        //telemetry.addData("ENCODERS: ", Base.getInstance().getEncoderPosition());telemetry.addData("Vuforia: ", vuforia.isVisible());
    }

    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {
        Base.getInstance().destroyIMU();
        //tf.deactivate();
    }
}

package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;
import org.firstinspires.ftc.Hyperfang.Sensors.OpenCV;
import org.firstinspires.ftc.Hyperfang.Sensors.Vuforia;

@Autonomous(name="Test", group="Iterative Opmode")
public class IterativeTest extends OpMode {

    //--------------------------------------------------------------------------------------------------
    public IterativeTest() {
    } //Default Constructor
//--------------------------------------------------------------------------------------------------

    private ElapsedTime runtime;
    private OpenCV cv;
    private Vuforia vuforia;
    private Base base;

    //Initialization: Runs once  driver presses init.
    @Override
    public void init() {
        runtime = new ElapsedTime();

        //Instantiating our robot objects.
        base = new Base(this);
        vuforia = new Vuforia(this);
        cv = new OpenCV(this);

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
        cv.activate(vuforia.getBitmap());
    }

    private double relAng2 = 0;
    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {

        telemetry.addLine("Running");
        vuforia.getInfo(telemetry);
        vuforia.getVuMarkName();
        vuforia.getVuMark();
        }


    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {
    }

}

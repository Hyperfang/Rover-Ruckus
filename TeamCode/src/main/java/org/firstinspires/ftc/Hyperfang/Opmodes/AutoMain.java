package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;
import org.firstinspires.ftc.Hyperfang.Robot.Lift;
import org.firstinspires.ftc.Hyperfang.Robot.Manipulator;
import org.firstinspires.ftc.Hyperfang.Sensors.OpenCV;
import org.firstinspires.ftc.Hyperfang.Sensors.Vuforia;

@Autonomous (name="Main", group="Iterative Opmode")
public class AutoMain extends OpMode {

    //List of system states.
    private enum State {
        INITIAL,
        LAND,
        FINDMIN,
        SAMPLE,
        RESET,
        LOGNAV,
        NAVDEPOT,
        DEPOTMARKER,
        DEPOSITMIN,
        PICKUPMIN,
        PARK,
    }

    //Robot objects which we use in the class.
    private Lift mLift;
    private Vuforia mVF;
    private OpenCV mCV;
    private Base mBase;
    private Manipulator mManip;

    //Runtime Variables
    public ElapsedTime mRunTime = new ElapsedTime();
    private ElapsedTime mInitTime = new ElapsedTime();

    //Variables which log information about the current state of the state machine.
    private State mState;
    private ElapsedTime StateTime = new ElapsedTime();

    //Logging Variables.
    private String vuMark;
    private int manipEnc;

    //Reset our state run timer and set a new state.
    private void setState(State nS) {
        StateTime.reset();
        mState = nS;
    }

//--------------------------------------------------------------------------------------------------
    public AutoMain() {} //Default Constructor
//--------------------------------------------------------------------------------------------------

    //Initialization: Runs once  driver presses init.
    @Override
    public void init() {
        mInitTime.reset(); //Starting our initialization timer.

        //Instantiating our robot objects.
        mBase = new Base(this);
        mLift = new Lift(this);
        mVF = new Vuforia();
        mCV = new OpenCV(this);
        mManip = new Manipulator(this);

        vuMark = "";

        //Indicates that initialization is complete.
        telemetry.addData("Initialized", "in " + mInitTime.milliseconds() + "ms");
    }

    //Initialization Loop: Loops when driver presses init after init() runs.
    @Override
    public void init_loop() {
    }


    //Start: Runs once driver hits play.
    @Override
    public void start() {
        //Must change once we add Latching.
        mLift.lockRatchet();
        mManip.depositPosition();
        mLift.setPosition(Lift.LEVEL.GROUND);
        mVF.activate();
        mCV.activate(mVF.getBitmap());

        //Clearing our telemetry dashboard.
        telemetry.clear();
        mRunTime.reset();

        //Change to when we hit ground in future.
        mBase.initIMU(this);
        setState(State.FINDMIN);
    }

    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {

        //Sending our current state and state run time to our driver station.
        telemetry.addData(mState.toString(), StateTime.seconds());
        telemetry.addData("IMU", mBase.getHeading());
        telemetry.addData("Range", mBase.getRange());

        switch (mState) {
            //Stay until our magnetic limit switch is set.
            /* case INITIAL:
                if (mLift.mgl.isTouched()) {
                    setState(State.LAND);
                } else {
                    telemetry.addData("Position:", mLift.getPosition());
                }
                break;

            //Lower until we touch the ground
            case LAND:
                mLift.unlockRatchet();
                while (mLift.getPosition().equals("LATCH")) {
                    mLift.moveTo(Lift.LEVEL.GROUND, .5,  mLift.RatchetMotor());
                    }
                    mLift.stop;
                    mLift.move(mLift.LiftMotor());
                    mLift.unhook;
                    mLift.stop;
                    setState(State.FINDMIN);
                } else {
                    telemetry.addData("", "stuff");
                }
                break;
*/
            //TODO: Add web-cam and implement the working TensorFlow Sample Method.
            case FINDMIN:
                //Lower our lift to the ground level.
                if (mLift.getPosition().equals("GROUND")) {
                    mLift.stop();

                    //Locate the gold.
                    while (!mCV.isGoldFound()) {
                        mCV.findGold(mCV.getVuforia(mVF.getBitmap()));
                        //TODO: Move removed once a camera which can see all 3 minerals is added.
                        mBase.move(0, .1);
                    }
                    setState(State.SAMPLE);
                } else { telemetry.addData("Debug: ", mLift.getPosition()); }
                break;

            case SAMPLE:
                //Sample (reposition) the Cube.
                if (mCV.isGoldFound()) {
                    mBase.stop();
                    //Lining up with the cube based on its position to our camera in relation to the robot.
                    //mBase.move(0, mBase.turnRelative(.5, mCV.getGoldAngle()));

                    //Intake the gold while tracking how far our manipulator moves.
                    //Manipulator.resetEncoders();
                    //manipEnc = Manipulator.getEncoders();

                    //While (A. we see the gold or B. Range sensor is within X value) {
                        //Manipulator.moveLift();
                        //Manipulator.setIntake();
                    // }
                    //manipEnc -= Manipulator.getEncoders();

                    //Start to retract the manipulator.
                    //Manipulator.setIntake(0);
                    //Manipulator.moveLift(.25, manipEnc);
                    setState(State.RESET);
                } else { telemetry.addData("Debug: ", mBase.getRange()); }
                break;

            case RESET:
                //Reset to the starting position.
                if (mBase.setTurn(0)) {
                    mBase.move(0, mBase.turnAbsolute(.5, 0));
                }
                //Begin to log the navigation target.
                else {
                    while (mBase.setTurn(45)) {
                        mBase.move(0, mBase.turnAbsolute(.5, 45));
                    }
                    mBase.move(.1, 0);
                    setState(State.LOGNAV);
                }
                break;

            case LOGNAV:
                if (!mVF.isVisible()) {
                    mBase.move(.1, 0);
                    mVF.getVuMark();
                } else {
                    telemetry.addData("Navigation Target: ", mVF.getVuMarkName());
                    mBase.stop();
                    setState(State.NAVDEPOT);
                }
                break;

                //TODO Before Adding: figure out which navigation targets respond to which side.
           case NAVDEPOT:
                //Finding the target associated with the Crater Red and Crater Blue.
                //Run until we can no longer see a target, then start using the range sensor.

                if (mVF.getVuMarkName().equals("Blue-Rover") && mVF.getVuMarkName().equals("Red-Footprint")) {
                    //Add target specifics
                    while (mBase.setTurn(-135) && mVF.isVisible()) {
                        telemetry.addData("Range", mBase.getRange());
                        telemetry.addData("IMU", mBase.getHeading());
                        mVF.getVuMark();
                        mBase.move(.2, mBase.turnAbsolute(.4, -135));
                    }
                    while (mBase.setRange(10) && !mVF.isVisible()) {
                        telemetry.addData("Range", mBase.getRange());
                        telemetry.addData("IMU", mBase.getHeading());
                        mVF.getVuMark();
                        mBase.move(mBase.rangeMove(10) , mBase.turnAbsolute(.4, -135));
                    }
                }
                else {
                    while (mBase.setTurn(135) && mVF.isVisible()) {
                        mVF.getVuMark();
                        mBase.move(.2, mBase.turnAbsolute(.5, 135));
                    }
                    while (mBase.setRange(10) && !mVF.isVisible()) {
                        mVF.getVuMark();
                        mBase.move(mBase.rangeMove(10) , mBase.turnAbsolute(.5, 135));
                    }
                }
               setState(State.PARK);
                break;
/*
            case DEPOTMARKER:
                //Depositing the cube and manipulator using the intake.
                if (mBase.setTurn(10)) {
                    mBase.stop();
                    //Manipulator.setIntake for x seconds. - while jump
                    //Manipulator.stop
                    setState(State.PARK);
                } else { }
                break;
/*
            case DEPOSITMIN:
                // if () {} else {}
                //put.one.in          OUTTAKE CODE THAT WE HAVE NOT DECIDED  ON
                setState(State.PICKUPMIN);
                break;

            case PICKUPMIN:
                // if () {} else {}
                //put.one.in          OUTTAKE CODE THAT WE HAVE NOT DECIDED  ON
                /* move.to.balls
                extend.manip
                manip.intake.on
                wait(5)
                bring.back.manip
                separate.minerals
                if (mRunTime.seconds() < 7.5) {
                    setState(State.PARK);
                }
                break;
*/
            case PARK:
                telemetry.addData("Debug: ", mBase.getRange());
                mBase.stop();
                break;
                //Locate current position
                //Move to crater
        }
    }

    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {
    }

}

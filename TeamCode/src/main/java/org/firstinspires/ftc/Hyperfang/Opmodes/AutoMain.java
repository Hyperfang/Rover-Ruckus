package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;
import org.firstinspires.ftc.Hyperfang.Robot.Lift;
import org.firstinspires.ftc.Hyperfang.Robot.Manipulator;
import org.firstinspires.ftc.Hyperfang.Sensors.OpenCV;
import org.firstinspires.ftc.Hyperfang.Sensors.Tensorflow;
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
    private Tensorflow mTF;
    private Base mBase;
    private Manipulator mManip;

    //Runtime Variables
    public ElapsedTime mRunTime = new ElapsedTime();
    private ElapsedTime mInitTime = new ElapsedTime();

    //Variables which log information about the current state of the state machine.
    private State mState;
    private ElapsedTime StateTime = new ElapsedTime();

    //Logging Variables.
    private Tensorflow.Position pos;
    private String vuMark;
    private int manipEnc;
    private double craterDir;

    //TODO: Remove, Clean Up States, and replace while's with if's.
    //Variable which allows us to circumvent the watchdog timers for a bit.
    private boolean bypassWDT;
    private boolean bypassWDTS;
    private boolean sampleReady;

    //TODO: Wait variable for League Meet 1 to park.
    //TODO: Wait variable which is a backup in case our state fails to occur.
    private ElapsedTime wait = new ElapsedTime();

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
        mTF = new Tensorflow(this, mVF.getLocalizer());
        mManip = new Manipulator(this);

        pos = Tensorflow.Position.UNKNOWN;
        vuMark = "";
        bypassWDT = true;
        bypassWDTS = true;
        sampleReady = false;

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
        mManip.lockDeposit();
        mManip.depositPosition();
        mLift.setPosition(Lift.LEVEL.GROUND);
        mVF.activate();
        mTF.activate();

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
                    if (!mTF.isPosFound() && wait.milliseconds() < 3000) {
                        //TODO: 66% chance of working (Doesn't work on right)
                        mTF.sample2();
                        pos = mTF.getPos();
                         //TODO: Move removed once a camera which can see all 3 minerals is added.
                    } else {
                        wait.reset();
                        setState(State.SAMPLE);
                    }
                } else { telemetry.addData("Debug: ", mLift.getPosition()); }
                break;

                //TODO: Clean up for LM2 and add in Manipulator.
            case SAMPLE:
                //Sample (reposition) the Cube.
                if (!sampleReady) {
                    //TODO: Simplify
                    switch (pos) {
                        //Check the center cube if the position is center, or unknown.
                        case UNKNOWN:
                            sampleReady = true;
                            break;

                        case CENTER:
                            sampleReady = true;
                            break;

                            //Turn Left.
                        case LEFT:
                            while (mBase.setTurn(19)) {
                                mBase.move(0, mBase.turnAbsolute(.5, 19));
                            }
                            sampleReady = true;
                            break;

                            //Turn right.
                        case RIGHT:
                            while (mBase.setTurn(-24)) {
                                mBase.move(0, mBase.turnAbsolute(.5, -24));
                            }
                            sampleReady = true;
                            break;
                    }
                } else {
                    while (wait.milliseconds() < 1600 && bypassWDTS) {
                        mBase.move(.25, 0);
                    }
                    if (bypassWDTS) {
                        wait.reset();
                        bypassWDTS = false;
                    }
                    if (wait.milliseconds() < 1600) {
                        mBase.move(-.25, 0);
                    } else {
                        mBase.stop();
                        wait.reset();
                        setState(State.RESET);
                    }
                }
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
                break;
            //TODO: Good example of state where we enter doing something, and exit.
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
                    mBase.move(.15, 0);
                    wait.reset();
                    setState(State.LOGNAV);
                }
                break;

            case LOGNAV:
                if (!mVF.isVisible() && wait.milliseconds() < 4000) {
                    mBase.move(.15, 0);
                    mVF.getVuMark();
                } else if (!mVF.isVisible() && wait.milliseconds() > 4000) {
                    mBase.stop();
                     mVF.getVuMark();
                    }
                else {
                    vuMark = mVF.getVuMarkName();
                    //TODO: Remove after consistent.
                    mBase.stop();
                    setState(State.NAVDEPOT);
                }
                break;

           case NAVDEPOT:
                //Finding the target associated with the Crater Red and Crater Blue.
                //Run until we can no longer see a target, then start using the range sensor.
                if (vuMark.equals("Blue-Rover") || vuMark.equals("Red-Footprint")) {
                     craterDir = -45;
                    //TODO: Clean up the range method to become more efficient.
                    //Move close to the wall.
                    while (mBase.setRange(12.75) && bypassWDT) {
                        mBase.move(mBase.rangeMove(12.75) , 0);
                    }
                    //Turn parallel to the wall facing the depot.
                    while (mBase.setTurn(130) && bypassWDT) {
                        mBase.move(.25, mBase.turnAbsolute(.5, 130));
                    }
                    //Move to the depot.
                    bypassWDT = false;
                    if (mBase.setRange(24) && !bypassWDT) {
                        mBase.move(mBase.rangeMove(24), 0);
                    } else { setState(State.DEPOTMARKER); }
                }
                else {
                     craterDir = 45;
                    //Move close to the wall.
                    while (mBase.setRange(12.75) && bypassWDT) {
                        mBase.move(mBase.rangeMove(12.75) , 0);
                    }
                    //Turn parallel to the wall facing the depot.
                    while (mBase.setTurn(-130) && bypassWDT) {
                        mBase.move(.25, mBase.turnAbsolute(.5, -130));
                    }
                    //Move to the depot.
                    bypassWDT = false;
                    if (mBase.setRange(24) && !bypassWDT) {
                        mBase.move(mBase.rangeMove(24), 0);
                    } else { setState(State.DEPOTMARKER); }
                }
               break;

            case DEPOTMARKER:
               mBase.stop();
                //Depositing the cube and manipulator using the intake.
                //if (mBase.setRange())
                    //Turn to deposit position, which is also the direction facing the crater.
                    while (mBase.setTurn(craterDir)) {
                        mBase.move(0, mBase.turnAbsolute(.5, craterDir));
                    }
                    mManip.unlockDeposit();

                if (mBase.setRange(20) && !bypassWDT) {
                    mBase.move(mBase.rangeMove(20), 0); //small "jerk"
                    wait.reset();

                    setState(State.PARK);
                } //else { }
                break;


/*          //TODO: Add for LM2.
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
                if (wait.milliseconds() < 4750) { //was 4250
                    mBase.move(.25, 0);
                } else {
                    mBase.stop();
                    //mManip.intakePosition();
                }
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

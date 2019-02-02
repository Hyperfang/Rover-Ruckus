package org.firstinspires.ftc.Hyperfang.Opmodes.LM3;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;
import org.firstinspires.ftc.Hyperfang.Robot.Lift;
import org.firstinspires.ftc.Hyperfang.Robot.Manipulator;
import org.firstinspires.ftc.Hyperfang.Vision.Tensorflow;
import org.firstinspires.ftc.Hyperfang.Vision.Vuforia;

@Autonomous(name = "Double Sample", group = "Iterative Opmode")
@Disabled
public class DoubleSample extends OpMode {

    //List of system states.
    private enum State {
        INITIAL,
        LAND,
        FINDMIN,
        FACEMIN,
        SAMPLE,
        RESET,
        LOGNAV,
        NAVDEPOT,
        DEPOTMARKER,
        DEPOSITMIN,
        PICKUPMIN,
        PARK,
        BACKUP
    }

    //Robot objects which we use in the class.
    private Lift mLift;
    private Vuforia mVF;
    private Tensorflow mTF;
    private Base mBase;
    private Manipulator mManip;

    //Variables which pertain to the robot.
    private boolean[] robotPath = new boolean[]{false, false, false, false};
    private boolean[] manipPath = new boolean[]{true, false};

    //Runtime Variables
    private ElapsedTime mRunTime = new ElapsedTime();
    private ElapsedTime mStateTime = new ElapsedTime();
    private double initTime;

    //Variables which log information about the current state of the state machine.
    private State mState;

    //Logging Variables: Vision
    private Tensorflow.Position pos;
    private String vuMark;

    //Logging Variables: Direction
    private int sampleEnc;
    private double sampleTurn;
    private double logTurn;
    private double craterDir;
    private double sampleBack;
    private double parkTurn;

    //Double Sample Variables
    private boolean doubleSample = false;
    private double samplePosition = 0;

    //Wait variable which is a backup in case our state fails to occur.
    private ElapsedTime wait = new ElapsedTime();

    //Reset our state run timer and set a new state.
    private void setState(State nS) {
        mStateTime.reset();
        mState = nS;
    }

    //--------------------------------------------------------------------------------------------------
    public DoubleSample() {
    } //Default Constructor
//--------------------------------------------------------------------------------------------------

    //Initialization: Runs once  driver presses init.
    @Override
    public void init() {
        //Starting our initialization timer.
        mStateTime.reset();

        //Instantiating our robot objects.
        //mBase = new Base(this);
        //mLift = new Lift(this);
        mVF = new Vuforia();
        mTF = new Tensorflow(this, mVF.getLocalizer());
       // mManip = new Manipulator(this);

        pos = Tensorflow.Position.UNKNOWN;
        vuMark = "";

        //Must change once we add Latching.
        //mLift.setPosition(Lift.LEVEL.GROUND);
        mBase.setModeEncoder(DcMotor.RunMode.RUN_USING_ENCODER);


        //Locking the deposit and making sure that the intake is up.
        //mManip.lockDeposit();
        //mManip.depositPosition();
        initTime = mStateTime.milliseconds();
    }

    //Initialization Loop: Loops when driver presses init after init() runs.
    @Override
    public void init_loop() {
        //Indicates that the full robot initialization is complete.
        telemetry.addLine("Robot Initialized in " + initTime + "ms");
    }

    //Start: Runs once driver hits play.
    @Override
    public void start() {
        mRunTime.reset();

        //Change to when we hit ground in future.
        mBase.initIMU(this);

        //Activating vision.
        mVF.activate();
        mTF.activate();

        //Clearing our telemetry dashboard.
        telemetry.clear();
        wait.reset();
        setState(State.FINDMIN);
    }

    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {
        //Sending our current state and state run time to our driver station.
        telemetry.addData("Runtime: ", mRunTime.seconds());
        telemetry.addData(mState.toString(), mStateTime.seconds());
        telemetry.addData("Position: ", mTF.getPos());
        telemetry.addData("Vumark: ", vuMark);
        telemetry.addData("IMU", mBase.getHeading());
        telemetry.addData("Range", mBase.getRange());
        telemetry.addData("Encoders", mBase.getEncoderPosition());


        switch (mState) {
            //Log the position of the mineral.
            case FINDMIN:
                //Lower our lift to the ground level.
                if (mLift.getPosition().equals("GROUND")) {
                    mLift.stop();

                    //Locate the gold.
                    if (pos.equals(Tensorflow.Position.UNKNOWN) && wait.milliseconds() < 3500) {
                        mTF.sample();
                        pos = mTF.getPos();
                    } else {
                        mTF.deactivate();
                        setState(State.FACEMIN);
                    }
                }
                break;

            //TODO: Change sampleEnc to encoders, currently using wait time (Waiting on Hardware).
            //Camera towards the cube.
            case FACEMIN:
                //Check the center cube if the position is center, or unknown.
                //Or Camera Left or Right depending on the position of the cube.
                switch (pos) {
                    case UNKNOWN:
                    case CENTER:
                        sampleEnc = 1700;
                        sampleTurn = 0;
                        sampleBack = 1750;
                        logTurn = 43;
                        break;

                    case LEFT:
                        sampleEnc = 2000;
                        if (!doubleSample) sampleTurn = 25;
                        sampleBack = 1750;
                        logTurn = 43;
                        break;

                    case RIGHT:
                        sampleEnc = 2200;
                        if (!doubleSample) sampleTurn = -25;
                        else sampleTurn = -30;
                        sampleBack = 2200;
                        logTurn = 52;
                        break;
                }

                if (mBase.setTurn(sampleTurn + samplePosition))
                    mBase.move(0, mBase.turnAbsolute(sampleTurn + samplePosition));
                else robotPath[1] = true;

                if (robotPath[1]) {
                    //mManip.resetEncoders();
                    wait.reset();
                    setState(State.SAMPLE);
                }
                break;

            //Sample (reposition) the Cube by extending the intake, and intaking.
            case SAMPLE:
                /*//TODO: Add in Manipulator (Waiting on Hardware).
                if (manipPath[0] && !mManip.isActionComplete) {
                    mManip.moveLift(.5, 1000);
                } else if (manipPath[0]) {
                    mManip.resetEncoderMove();
                    mManip.intakePosition();
                    manipPath[0] = false;
                    manipPath[1] = true;
                    wait.reset();
                } else if (wait.milliseconds() < 1000) {
                    mManip.setIntake(1);
                } else if (manipPath[1] && !mManip.isActionComplete){
                    mManip.setIntake(0);
                    mManip.depositPosition();
                    mManip.moveLift(-.5, 0);
                } else {
                    setState(State.RESET);
                }
                break;
*/
                if (!doubleSample) {
                    if (wait.milliseconds() < sampleEnc) {
                        mBase.move(.25, 0);
                    } else if (wait.milliseconds() < sampleEnc + 2000) {
                        mBase.move(-.25, 0);
                    } else {
                        mBase.stop();
                        setState(State.RESET);
                    }
                } else {
                    if (wait.milliseconds() < sampleEnc) {
                        mBase.move(.25, 0);
                    } else if (wait.milliseconds() < sampleEnc + sampleBack) {
                        mBase.move(-.25, 0);
                    } else {
                        mBase.stop();
                        setState(State.RESET);
                    }
                }
                break;

            //Reset to the starting position, then begin to log the navigation target.
            case RESET:
                if (!doubleSample) {
                    if (mBase.setTurn(0) && robotPath[1]) mBase.move(0, mBase.turnAbsolute(0));
                    else {
                       robotPath[1] = false;
                        if (mBase.setTurn(logTurn)) mBase.move(0, mBase.turnAbsolute(logTurn));
                        else {
                            robotPath[1] = true;
                            doubleSample = true;
                            samplePosition = -88;
                            mBase.resetEncoders();
                            setState(State.LOGNAV);
                        }
                    }
                } else {
                    if (mBase.setTurn(craterDir + 5) && robotPath[1]) mBase.move(0, mBase.turnAbsolute(craterDir + 5));
                    else {
                        if (mBase.setRange(8.5) && robotPath[1]) mBase.move(mBase.rangeMove(8.5), 0);
                        else {
                            robotPath[1] = true;
                            mBase.resetEncoders();
                            setState(State.DEPOSITMIN);
                        }
                    }
                }
                break;

            //Move close to the wall and log the navigation target.
            case LOGNAV:
                if (!mVF.isVisible()) mVF.getVuMark();
                else vuMark = mVF.getVuMarkName();

                //Move to the depot.
                if (mBase.setEnc(22.5)) mBase.move(mBase.encoderMove(22.5), 0);
                else {
                    mBase.setModeEncoder(DcMotor.RunMode.RUN_USING_ENCODER);
                    mBase.stop();
                    wait.reset();

                    if (mVF.isVisible()) setState(State.NAVDEPOT);
                    else setState(State.BACKUP);
                }
                break;

            //Navigating to the Depot based on the navigation target.
            case NAVDEPOT:
                //Finding the target associated with the Crater Red and Crater Blue.
                if (vuMark.equals("Blue-Rover") || vuMark.equals("Red-Footprint")) {
                    craterDir = -45;
                    parkTurn = -.1;
                } else {
                    craterDir = 135;
                    parkTurn = .1;
                }

                //Camera towards the crater.
                if (mBase.setTurn(craterDir) && robotPath[1]) {
                    mBase.move(0, mBase.turnAbsolute(craterDir));
                } else {
                    robotPath[1] = false;
                    robotPath[2] = true;
                }

                //Move to the depot.
                if (mBase.setRange(10) && robotPath[2]) {
                    mBase.move(mBase.rangeMove(10), 0);
                } else if (!robotPath[1]) {
                    mBase.stop();
                    robotPath[1] = true;
                    robotPath[2] = false;
                    setState(State.DEPOTMARKER);
                }
                break;

            //Depositing the cube and team marker using the manipulator.
            case DEPOTMARKER:
               //mManip.unlockDeposit();

                //Camera towards the crater.
                if (mBase.setTurn(samplePosition)) {
                    mBase.move(0, mBase.turnAbsolute(samplePosition));
                } else {
                    robotPath[1] = false;
                    setState(State.FINDMIN);
                }
                break;

            case DEPOSITMIN:
                mBase.stop();
                setState(State.PICKUPMIN);

                if (mRunTime.milliseconds() > 22500) {
                    setState(State.PARK);
                    //resetIntake();
                }
                break;

            case PICKUPMIN:
                mBase.stop();
                setState(State.DEPOSITMIN);

                if (mRunTime.milliseconds() > 22500) {
                    setState(State.PARK);
                    //resetIntake();
                }
                break;

            //Parking into the crater.
            case PARK:
                //Move to the crater from the current position.
                if (mBase.setEnc(38) && !robotPath[3]) mBase.move(mBase.encoderMove(38), parkTurn);
                else {
                    if (!robotPath[3]) wait.reset();
                    robotPath[3] = true;
                    //Make sure we are over the crater.
                    //In the future we will extend the manip.
                    if (wait.milliseconds() < 400) mBase.move(.5, 0);
                    else {
                        mBase.stop();
                        //mManip.intakePosition();
                    }
                }
                break;

            //Backup Mechanism in case the VuMark is not found.
            case BACKUP:
                if (!mVF.isVisible()) {
                    telemetry.addLine("VuMark not found. Finding VuMark...");
                    mVF.getVuMark();

                    if (wait.milliseconds() < 750) mBase.move(0, -.1);
                    else if (wait.milliseconds() < 1500) mBase.stop();
                    else wait.reset();
                } else {
                    vuMark = mVF.getVuMarkName();
                    mBase.stop();
                    setState(State.NAVDEPOT);
                }
                break;
        }
    }

    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {}
}

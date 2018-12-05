package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;
import org.firstinspires.ftc.Hyperfang.Robot.Lift;
import org.firstinspires.ftc.Hyperfang.Robot.Manipulator;
import org.firstinspires.ftc.Hyperfang.Sensors.Tensorflow;
import org.firstinspires.ftc.Hyperfang.Sensors.Vuforia;

@Autonomous(name = "Main", group = "Iterative Opmode")
public class AutoMain extends OpMode {

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
        PARK
    }

    //Instantiating the robot objects.
    private Lift mLift;
    private Vuforia mVF;
    private Tensorflow mTF;
    private Base mBase;
    private Manipulator mManip;

    //Variables which pertain to the robot movement.
    private boolean[] robotPath = new boolean[]{true, false, false, false};
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
    private double parkTurn;

    //Wait variable which is a backup in case our state fails to occur.
    private ElapsedTime wait = new ElapsedTime();

    //Reset our state run timer and set a new state.
    private void setState(State nS) {
        mStateTime.reset();
        mState = nS;
    }

//--------------------------------------------------------------------------------------------------
    public AutoMain() {} //Default Constructor
//--------------------------------------------------------------------------------------------------

    //Initialization: Runs once  driver presses init.
    @Override
    public void init() {
        //Starting our initialization timer.
        mStateTime.reset();

        //Instantiating our robot objects.
        mBase = new Base(this);
        mLift = new Lift(this);
        mVF = new Vuforia();
        mTF = new Tensorflow(this, mVF.getLocalizer());
        mManip = new Manipulator(this);

        pos = Tensorflow.Position.UNKNOWN;
        vuMark = "";

        //Must change once we add Latching.
        mLift.lockRatchet();
        mLift.setPosition(Lift.LEVEL.GROUND);

        //Locking the deposit and making sure that the intake is up.
        mManip.lockDeposit();
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
        telemetry.addData("VuMark: ", vuMark);
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
                        mTF.sample2();
                        pos = mTF.getPos();
                    } else {
                        mTF.deactivate();
                        setState(State.FACEMIN);
                    }
                }
                break;

            //TODO: Change sampleEnc to encoders, currently using wait time (Waiting on Hardware).
            //Turn towards the cube.
            case FACEMIN:
                //Check the center cube if the position is center, or unknown.
                //Or Turn Left or Right depending on the position of the cube.
                switch (pos) {
                    case UNKNOWN:
                    case CENTER:
                        sampleEnc = 1700;
                        sampleTurn = 0;
                        logTurn = 43;
                        break;

                    case LEFT:
                        sampleEnc = 2000;
                        sampleTurn = 25;
                        logTurn = 43;
                        break;

                    case RIGHT:
                        sampleEnc = 2200;
                        sampleTurn = -25;
                        logTurn = 51;
                        break;
                }

                //Turn if the cube is on the right or left.
                if (sampleTurn != 0 && mBase.setTurn(sampleTurn))
                    mBase.move(0, mBase.turnAbsolute(sampleTurn));
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
                if (wait.milliseconds() < sampleEnc) {
                    mBase.move(.25, 0);
                } else if (wait.milliseconds() < sampleEnc + 2000) {
                    mBase.move(-.25, 0);
                } else {
                    mBase.stop();
                    setState(State.RESET);
                }
                break;

            //Reset to the starting position, then begin to log the navigation target.
            case RESET:
                if (mBase.setTurn(0) && robotPath[1]) mBase.move(0, mBase.turnAbsolute(0));
                else {
                    robotPath[1] = false;
                    if (mBase.setTurn(logTurn)) mBase.move(0, mBase.turnAbsolute(logTurn));
                    else {
                        robotPath[1] = true;
                        mBase.stop();
                        mBase.resetEncoders();
                        setState(State.LOGNAV);
                    }
                }
                break;

            //Move close to the wall and log the navigation target.
            case LOGNAV:
                if (mBase.setEnc(22.5) && robotPath[1]) {
                    mBase.move(mBase.encoderMove(22.5), 0);
                    if (!mVF.isVisible()) mVF.getVuMark();
                    else vuMark = mVF.getVuMarkName();
                    wait.reset();
                } else {
                    mBase.setModeEncoder(DcMotor.RunMode.RUN_USING_ENCODER);
                    robotPath[1] = false;

                    //Backup Mechanism in case we aren't perpendicular with the wall or VuMark is not found.
                    if (mBase.setTurn(46) || !mVF.isVisible()) {
                        telemetry.addLine("VuMark not found. Finding VuMark...");
                        mBase.move(0, mBase.turnAbsolute(46));
                        mVF.getVuMark();
                        if (mVF.isVisible()) vuMark = mVF.getVuMarkName();
                    } else {
                        mBase.stop();
                        robotPath[1] = true;
                        setState(State.NAVDEPOT);
                    }
                }
                break;

            //Navigating to the Depot based on the navigation target.
            case NAVDEPOT:
                //Finding the target associated with the Crater Red and Crater Blue.
                if (vuMark.equals("Blue-Rover") || vuMark.equals("Red-Footprint")) {
                    craterDir = -45;
                    parkTurn = .1;
                }
                else {
                    craterDir = 135;
                    parkTurn = -.1;
                }

                //Turn towards the crater.
                if (mBase.setTurn(craterDir) && robotPath[1]) {
                    mBase.move(0, mBase.turnAbsolute(craterDir));
                } else {
                    robotPath[1] = false;
                    robotPath[2] = true;
                }

                //Move to the depot.
                if (mBase.setRange(22) && robotPath[2]) {
                    mBase.move(mBase.rangeMove(22),0);
                } else if (!robotPath[1]){
                    mBase.stop();
                    setState(State.DEPOTMARKER);
                }
                break;

            //Depositing the cube and team marker using the manipulator.
            case DEPOTMARKER:
                mBase.resetEncoders();
                mManip.unlockDeposit();
                wait.reset();
                setState(State.DEPOSITMIN);
                break;

            case DEPOSITMIN:
                mBase.stop();
                setState(State.PICKUPMIN);

                //Park when there is 7.5 seconds left.
                if (mRunTime.milliseconds() > 22500) {
                    setState(State.PARK);
                    wait.reset();
                    //resetIntake();
                }
                break;

            case PICKUPMIN:
                mBase.stop();
                setState(State.DEPOSITMIN);

                //Park when there is 7.5 seconds left.
                if (mRunTime.milliseconds() > 22500) {
                    setState(State.PARK);
                    wait.reset();
                    //resetIntake();
                }
                break;

            //Parking into the crater.
            case PARK:
                //Move to the crater from the current position.
                if (mBase.setEnc(30) && !robotPath[3]) mBase.move(mBase.encoderMove(30), parkTurn);
                else {
                    if (!robotPath[3]) wait.reset();
                    robotPath[3] = true;
                    //Make sure we are over the crater.
                    //In the future we will extend the manip. mManip.intakePosition();
                    if (wait.milliseconds() < 350) mBase.move(.5, 0);
                    else {
                        mBase.stop();
                    }
                }
                break;
        }
    }

    //Stop: Runs once driver hits stop.
    @Override
    public void stop() { }
}

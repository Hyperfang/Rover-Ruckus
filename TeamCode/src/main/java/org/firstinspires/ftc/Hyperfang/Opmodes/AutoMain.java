package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;
import org.firstinspires.ftc.Hyperfang.Robot.Lift;
import org.firstinspires.ftc.Hyperfang.Robot.Manipulator;
import org.firstinspires.ftc.Hyperfang.Vision.Tensorflow;
import org.firstinspires.ftc.Hyperfang.Vision.Vuforia;

@Autonomous(name = "Main Recover", group = "Iterative Opmode")

public class AutoMain extends OpMode {

    //List of system states.
    private enum State {
        INITIAL,
        LAND,
        FINDMIN,
        PIVOT,
        FACEMIN,
        SAMPLE,
        RESET,
        LOGNAV,
        NAVDEPOT,
        DEPOTMARKER,
        DEPOSITMIN,
        PICKUPMIN,
        PARK,
        BACKUP,
        TEST
    }

    //Instantiating the robot objects.
    private Vuforia mVF;
    private Tensorflow mTF;

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
        Base.getInstance(this).ftcEnc();
        Lift.getInstance(this).ftcEnc();
        Manipulator.getInstance(this);
        mVF = new Vuforia(this);
        mTF = new Tensorflow(this, mVF.getLocalizer());

        pos = Tensorflow.Position.UNKNOWN;
        vuMark = "";

        //Must change once we add Latching.
        //Base.getInstance().setModeEncoder(DcMotor.RunMode.RUN_USING_ENCODER);

        //Lock the lift and set the lift position.
        Lift.getInstance().lock();

        //TODO; change when add pivot
        Lift.getInstance().setPosition(Lift.LEVEL.COLLECT);
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
        Base.getInstance().initIMU(this);

        //Activating vision.
        mVF.activate();
        mTF.activate();

        //Clearing our telemetry dashboard.
        telemetry.clear();
        wait.reset();
        setState(State.LAND);
    }

    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {
        //Sending our current state and state run time to our driver station.
        telemetry.addData("Runtime: ", mRunTime.seconds());
        telemetry.addData(mState.toString(), mStateTime.seconds());
        //telemetry.addData("Position: ", mTF.getPos());
        //telemetry.addData("VuMark: ", vuMark);
        telemetry.addData("MGL", Lift.getInstance().getMGL());
        telemetry.addData("IMU", Base.getInstance().getHeading());
        telemetry.addData("Range", Base.getInstance().getRange());
        telemetry.addData("Encoders", Base.getInstance().getEncoderPosition());

        switch (mState) {
            //Landing the robot on the ground.
            case LAND:
                /*
                if (Lift.getInstance().setEnc()) {
                    Lift.getInstance().moveLiftEnc(, )
                }*/

                //move lift down,
                //unlock
                //once that is done, pivot out
                //Lift up, move forward SLOWLY NERD
                //lift down, pivot down

               /* if (Lift.getInstance().getPosition().equals(Lift.LEVEL.COLLECT)) {
                    Lift.getInstance().pivotUp();
                } else if (Lift.getInstance().getPosition().equals(Lift.LEVEL.DEPOSIT)) {
                    Base.getInstance().initIMU(this);
                    wait.reset();
                    setState(State.FINDMIN);
                }
                */
               setState(State.TEST);

            //Log the position of the mineral.
            case FINDMIN:
                    //Locate the gold.
                if (pos.equals(Tensorflow.Position.UNKNOWN) && wait.milliseconds() < 3000) {
                    mTF.sample();
                    pos = mTF.getPos();
                } else {
                    mTF.deactivate();
                    wait.reset();
                    setState(State.FACEMIN);
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
                        sampleEnc = 2400;
                        sampleTurn = 0;
                        logTurn = 43;
                        break;

                    case LEFT:
                        sampleEnc = 3250;
                        sampleTurn = 25;
                        logTurn = 43;
                        break;

                    case RIGHT:
                        sampleEnc = 3250;
                        sampleTurn = -25;
                        logTurn = 52;
                        break;
                }

                //Turn if the cube is on the right or left.
                if (sampleTurn != 0 && Base.getInstance().setTurn(sampleTurn))
                    Base.getInstance().move(0, Base.getInstance().turnAbsolute(sampleTurn));
                else robotPath[1] = true;

                if (robotPath[1]) {
                    wait.reset();
                    setState(State.SAMPLE);
                }
                break;

            //Sample (reposition) the Cube by extending the intake, and intaking.
            case SAMPLE:
                if (Lift.getInstance().setEnc(sampleEnc)) {
                    Lift.getInstance().moveLiftEnc(sampleEnc, .4);
                } else {
                    Base.getInstance().stop();
                    Lift.getInstance().stop();
                    setState(State.TEST);
                }
                break;

            //Reset to the starting position, then begin to log the navigation target.
            case RESET:
                if (Base.getInstance().setTurn(0) && robotPath[1]) Base.getInstance().move(0, Base.getInstance().turnAbsolute(0));
                else {
                    robotPath[1] = false;
                    if (Base.getInstance().setTurn(logTurn)) Base.getInstance().move(0, Base.getInstance().turnAbsolute(logTurn));
                    else {
                        robotPath[1] = true;
                        Base.getInstance().stop();
                        //Base.getInstance().resetEncoders();
                        wait.reset();
                        setState(State.LOGNAV);
                    }
                }
                break;

            //Move close to the wall and log the navigation target.
            case LOGNAV:
                if (!mVF.isVisible()) mVF.getVuMark();
                else vuMark = mVF.getVuMarkName();

                if (wait.milliseconds() < 2000) Base.getInstance().move(-.5, 0);
                //if (Base.getInstance().setEnc(22.5)) Base.getInstance().move(Base.getInstance().encoderMove(22.5), 0);
                else {
                    //Base.getInstance().setModeEncoder(DcMotor.RunMode.RUN_USING_ENCODER);
                    Base.getInstance().stop();
                    wait.reset();

                    if (mVF.isVisible()) setState(State.NAVDEPOT);
                    else setState(State.NAVDEPOT);
                }
                break;

            //Navigating to the Depot based on the navigation target.
            case NAVDEPOT:
                //Finding the target associated with the Crater Red and Crater Blue.
                if (vuMark.equals("Blue-Rover") || vuMark.equals("Red-Footprint")) {
                    craterDir = -44;
                     parkTurn = -.1;
                    } else {
                    craterDir = 137;
                    parkTurn = .1;
                    }

                //Turn towards the crater.
                if (Base.getInstance().setTurn(craterDir) && robotPath[1]) {
                    Base.getInstance().move(0, Base.getInstance().turnAbsolute(craterDir));
                } else {
                    robotPath[1] = false;
                    robotPath[2] = true;
                }

                //Move to the depot.
                if (Base.getInstance().setRange(22) && robotPath[2]) {
                    Base.getInstance().move(Base.getInstance().rangeMove(22), 0);
                } else if (!robotPath[1]) {
                    Base.getInstance().stop();
                    setState(State.DEPOTMARKER);
                }
                break;

            //Depositing the cube and team marker using the manipulator.
            case DEPOTMARKER:
                //Base.getInstance().resetEncoders();
                if (Lift.getInstance().getPosition().equals(Lift.LEVEL.COLLECT)) {
                Lift.getInstance().pivotUp();
                Manipulator.getInstance().openBoth();
                } else if (Lift.getInstance().getPosition().equals(Lift.LEVEL.DEPOSIT)) {
                    wait.reset();
                    setState(State.TEST);
                }
                break;

            case DEPOSITMIN:
                setState(State.PICKUPMIN);

                //Park when there is 7.5 seconds left.
                if (mRunTime.milliseconds() > 22500) {
                    setState(State.PARK);
                    wait.reset();
                    //resetIntake();
                }
                break;

            case PICKUPMIN:
                Base.getInstance().stop();
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
                if (wait.milliseconds() < 2500 && !robotPath[3]) Base.getInstance().move(-.5, 0);
                else {
                    if (!robotPath[3]) {
                        //mBase.setModeEncoder(DcMotor.RunMode.RUN_USING_ENCODER);
                        wait.reset();
                    }
                    robotPath[3] = true;
                    //Make sure we are over the crater.
                    //In the future we will extend the manip. mManip.intakePosition();
                    if (wait.milliseconds() < 400) Base.getInstance().move(-.5, 0);
                    else {
                        telemetry.addLine("Finished Parking.");
                        Base.getInstance().stop();
                    }
                }
                break;

            //Backup Mechanism in case the VuMark is not found.
            case BACKUP:
                if (!mVF.isVisible()) {
                    telemetry.addLine("VuMark not found. Finding VuMark...");
                    mVF.getVuMark();

                    if (wait.milliseconds() < 750) Base.getInstance().move(0, -.1);
                    else if (wait.milliseconds() < 1500) Base.getInstance().stop();
                    else wait.reset();
                } else {
                    vuMark = mVF.getVuMarkName();
                    Base.getInstance().stop();
                    setState(State.NAVDEPOT);
                }
                break;
            case TEST:
                //Base.getInstance().stop();
                //Lift.getInstance().pivotDown();
                //setState(State.DEPOSITMIN);
                break;
        }


    }

    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {
        Base.getInstance().destroyIMU();
    }
}

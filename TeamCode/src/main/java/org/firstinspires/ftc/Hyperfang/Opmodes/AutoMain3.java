package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;
import org.firstinspires.ftc.Hyperfang.Robot.Lift;
import org.firstinspires.ftc.Hyperfang.Robot.Manipulator;
import org.firstinspires.ftc.Hyperfang.Vision.Tensorflow;
import org.firstinspires.ftc.Hyperfang.Vision.Vuforia;

@Autonomous(name = "Risky Crater", group = "Iterative Opmode")
public class AutoMain3 extends OpMode {

    //List of system states.
    private enum State {
        INITIAL,
        LAND,
        FINDMIN,
        FACEMIN,
        SAMPLE,
        DEPOTLANDER,
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
    private boolean[] manipPath = new boolean[]{true, true};

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

    //Future method to reset the paths to the default state.
    private void resetPaths() {
        robotPath[1] = false;
        robotPath[2] = false;
        robotPath[3] = false;
    }

    //--------------------------------------------------------------------------------------------------
    public AutoMain3() {
    } //Default Constructor
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

        //TODO: Consistence Right Sample.
        pos = Tensorflow.Position.UNKNOWN;
        vuMark = "Blue-Rover";

        //Lock the lift and set the lift position.
        Lift.getInstance().lock();
        Lift.getInstance().setModeEncoderLift(DcMotor.RunMode.RUN_TO_POSITION);
        Lift.getInstance().setPosition(Lift.LEVEL.COLLECT);
        initTime = mStateTime.milliseconds();
    }

    //Initialization Loop: Loops when driver presses init after init() runs.
    @Override
    public void init_loop() {
        //Indicates that the full robot initialization is complete.
        telemetry.addLine("Robot Initialized in " + initTime + "ms");
        Base.getInstance().initIMU(this);
    }

    //Start: Runs once driver hits play.
    @Override
    public void start() {
        mRunTime.reset();

        //Activating vision.
        mVF.activate();
        mTF.activate();

        //Clearing our telemetry dashboard.
        telemetry.clear();
        wait.reset();

        //Unlocking the Lift to start Landing.
        Lift.getInstance().unlock();
        wait.reset();
        setState(State.LAND);
    }

    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {
        //Sending our current state and state run time to our driver station.
        telemetry.addData("Runtime: ", mRunTime.seconds());
        telemetry.addData(mState.toString(), mStateTime.seconds());
        telemetry.addData("Position: ", mTF.getPos());
        //telemetry.addData("VuMark: ", vuMark);
        //telemetry.addData("IMU", Base.getInstance().getHeading());
        telemetry.addData("Range", Base.getInstance().getRange());
        telemetry.addData("Pivot", Lift.getInstance().getPivotPosition());
        telemetry.addData("Lift", Lift.getInstance().getLiftPosition());
        telemetry.addData("Base", Base.getInstance().getEncoderPosition());

        switch (mState) {
            //Landing the robot on the ground.
            case LAND:
                if (wait.milliseconds() < 1000) Lift.getInstance().moveLiftEnc(-75, .75);
                else {
                    telemetry.addLine("LOOPING.");
                    if (Lift.getInstance().setEnc(1650) && !robotPath[1]) {
                        if (Lift.getInstance().getLiftPosition() > 900 && Lift.getInstance().getPivotPosition() < 550) {
                            Lift.getInstance().moveLiftEnc(1650, .5);
                            if (manipPath[1]) {
                                manipPath[1] = false;
                            }
                        } else if (Lift.getInstance().getLiftPosition() <= 500) {
                            telemetry.addLine("LOOPING 2 - No power if seeing");
                            Lift.getInstance().moveLiftEnc(1650, .75);
                        } else {
                            Lift.getInstance().moveLiftEnc(1650, .3);
                        }
                    } else {
                        robotPath[1] = true;
                        if (Lift.getInstance().setEnc(50)) Lift.getInstance().moveLiftEnc(50, .75);
                        else {
                            if (Lift.getInstance().getPivotPosition() > 250)
                                Lift.getInstance().pivotLift(200);
                            else {
                                Lift.getInstance().setPivot(0);
                                resetPaths();
                                wait.reset();
                                setState(State.FINDMIN);
                            }
                        }
                    }
                }
                break;

            //Log the position of the mineral.
            case FINDMIN:
                if (Base.getInstance().setTurn(0)) Base.getInstance().move(0, Base.getInstance().turnAbsolute(0));
                else {
                    //Locate the gold.
                    if (pos.equals(Tensorflow.Position.UNKNOWN) && wait.milliseconds() < 3000) {
                        mVF.getVuMark();
                        mTF.sample();
                        pos = mTF.getPos();
                    } else {
                        mTF.deactivate();
                        wait.reset();
                        setState(State.FACEMIN);
                    }
                }
                break;

            //Turn towards the cube.
            case FACEMIN:
                //Check the center cube if the position is center, or unknown.
                //Or Left or Right depending on the position of the cube.
                switch (pos) {
                    case UNKNOWN:
                    case CENTER:
                        sampleEnc = 2600;
                        sampleTurn = 0;
                        logTurn = 50;
                        break;

                    case LEFT:
                        sampleEnc = 3250;
                        sampleTurn = 25;
                        logTurn = 50;
                        break;

                    case RIGHT:
                        sampleEnc = 3250;
                        sampleTurn = -25;
                        logTurn = 50;
                        break;
                }

                //Turn if the cube is right or left.
                if (sampleTurn != 0 && Base.getInstance().setTurn(sampleTurn))
                    Base.getInstance().move(0, Base.getInstance().turnAbsolute(sampleTurn));
                else robotPath[1] = true;

                if (robotPath[1]) {
                    //Reset the path.
                    robotPath[1] = false;
                    wait.reset();
                    setState(State.SAMPLE);
                }
                break;

            //Sample (reposition) the Cube by extending the intake, and intaking.
            case SAMPLE:
                if (Lift.getInstance().setEnc(sampleEnc) && !robotPath[1]) {
                    Manipulator.getInstance().intake(-.7);
                    Lift.getInstance().moveLiftEnc(sampleEnc, .5);
                } else {
                    robotPath[1] = true;
                    if (Lift.getInstance().setEnc(50)) {
                        Lift.getInstance().moveLiftEnc(50, .5);
                        if (Lift.getInstance().getLiftPosition() < 300)
                            Lift.getInstance().pivotUp();
                    } else {
                        Manipulator.getInstance().intake(0);
                        if (Lift.getInstance().getPosition().equals(Lift.LEVEL.COLLECT))
                            Lift.getInstance().pivotUp();
                        else setState(State.DEPOTLANDER);
                    }
                }
                break;

            //Deposit in the lander.
            case DEPOTLANDER:
                //Turn to a deposit-able position.
                if (Base.getInstance().setTurn(0) && robotPath[1]) {
                    Base.getInstance().move(0, Base.getInstance().turnAbsolute(0));
                } else {
                    if (robotPath[1]) Base.getInstance().resetEncoders();
                    robotPath[1] = false;
                }

                //Move back slightly.
                if (Base.getInstance().setEnc(-3) && !robotPath[1]) {
                    Lift.getInstance().pivotDown();
                    Base.getInstance().move(Base.getInstance().encoderMove(-3), 0);
                } else {
                    robotPath[1] = true;
                    setState(State.RESET);
                }

                //Future implementation: Add in depositing in the lander.
                break;

            //Reset to the starting position, then begin to log the navigation target.
            case RESET:
                if (Lift.getInstance().getPosition().equals(Lift.LEVEL.DEPOSIT))
                    Lift.getInstance().pivotDown();
                else {
                    robotPath[1] = false;
                    if (Base.getInstance().setTurn(logTurn))
                        Base.getInstance().move(0, Base.getInstance().turnAbsolute(logTurn));
                    else {
                        Base.getInstance().resetEncoders();
                        robotPath[1] = true;
                        wait.reset();
                        setState(State.LOGNAV);
                    }
                }
                break;

            //Move close to the wall and log the navigation target.
            case LOGNAV:
                if (!mVF.isVisible()) mVF.getVuMark();
                else vuMark = mVF.getVuMarkName();

                if (Base.getInstance().setEnc(17))
                    Base.getInstance().move(Base.getInstance().encoderMove(17), 0);
                else {
                    Base.getInstance().stop();
                    wait.reset();

                    //if (mVF.isVisible()) setState(State.NAVDEPOT);
                    //else {}
                    robotPath[1] = true;
                    setState(State.NAVDEPOT);
                }
                break;

            //Navigating to the Depot based on the navigation target.
            case NAVDEPOT:
                //Finding the target associated with the Crater Red and Crater Blue.
                if (vuMark.equals("Blue-Rover") || vuMark.equals("Red-Footprint")) {
                    craterDir = -46;
                    parkTurn = .075;
                } else {
                    craterDir = 134;
                    parkTurn = -.075;
                }

                //Turn towards the crater, when adding make robotpath 1 a true beforehand.
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
                    wait.reset();
                    setState(State.DEPOTMARKER);
                }
                break;

            //Depositing the cube and team marker using the manipulator.
            case DEPOTMARKER:
                if (Lift.getInstance().getPosition().equals(Lift.LEVEL.COLLECT) || wait.milliseconds() < 3000) {
                    Lift.getInstance().pivotUp();
                    Manipulator.getInstance().openBoth();
                } else if (Lift.getInstance().getPosition().equals(Lift.LEVEL.DEPOSIT)) {
                    wait.reset();
                    Base.getInstance().resetEncoders();
                    setState(State.PARK);
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
                }
                break;

            //Parking into the crater.
            case PARK:
                Lift.getInstance().pivotDown();
                if (!Lift.getInstance().getPosition().equals(Lift.LEVEL.DEPOSIT)) {
                    //Move to the crater from the current position.
                    if (Base.getInstance().setEnc(25) && !robotPath[3])
                        Base.getInstance().move(Base.getInstance().encoderMove(25), 0);
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
                break;
        }
    }

    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {
        Base.getInstance().destroyIMU();
    }
}

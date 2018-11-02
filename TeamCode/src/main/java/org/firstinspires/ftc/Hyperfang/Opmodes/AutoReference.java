package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Base;
import org.firstinspires.ftc.Hyperfang.Robot.Lift;
import org.firstinspires.ftc.Hyperfang.Sensors.OpenCV;
import org.firstinspires.ftc.Hyperfang.Sensors.Vuforia;

@Autonomous (name="Reference", group="Iterative Opmode")
@Disabled
public class AutoReference extends OpMode {

    //List of system states.
    private enum State {
        INITIAL,
        LAND,
        FINDMIN,
        SAMPLE,
        FINDNAV,
        LOGNAV,
        DEPOT_MARKER,
        DEPOSITMIN,
        PICKUPMIN,
        PARK,
    }

    private Lift mLift;
    private Vuforia mVF;
    private OpenCV mCV;
    private Base mBase;

    private State mState; //Current state of the state machine.
    private ElapsedTime StateTime;

    //Reset our state run timer and set a new state.
    private void setState(State nS) {
        StateTime.reset();
        mState = nS;
    }

    // Time Variables
    public ElapsedTime mRunTime = new ElapsedTime(); //Time of running.
    private ElapsedTime mInitTime = new ElapsedTime(); //Time it takes to initialize.

    //--------------------------------------------------------------------------------------------------
    public AutoReference() {
    } //Default Constructor
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
        mLift.setPosition(Lift.LEVEL.LATCH);
        mVF.activate();

        telemetry.clear(); //Clearing our telemetry dashboard.
        mRunTime.reset();
    }

    //Loop: Loops once driver hits play after start() runs.
    @Override
    public void loop() {

        //Sending our current state and state run time to our driver station.
        telemetry.addData(mState.toString(), StateTime.seconds());

        switch (mState) {
            case INITIAL: //Stay until our magnetic limit switch is set.
                if (mLift.mgl.isTouched()) {
                    setState(State.LAND);
                } else {
                    telemetry.addData("Position:", mLift.getPosition());
                }
                break;

            case LAND:
                if (mLift.getPosition().equals("LATCH")) {
                    mLift.moveTo(Lift.LEVEL.GROUND, .5,  mLift.RatchetMotor());
                    mLift.unhook();
                    setState(State.FINDMIN);
                } else {
                    telemetry.addData("", "stuff");
                }
                break;

            case FINDMIN:
                if (mLift.getPosition().equals("GROUND")) {
                    while (!mCV.isGoldFound()) { //Unnecessary loop?
                        mCV.findGold(mCV.getVuforia(mVF.getBitmap())); //EDIT: May need to add loop.
                        mBase.turnRelative(.5,15);
                    }
                    setState(State.SAMPLE);
                } else {
                }
                break;

            case SAMPLE:
                OpenCV.Position pos = mCV.sample(mCV.getVuforia(mVF.getBitmap()));
                if (mCV.isGoldFound()) {
                    mBase.turnRelative(.5, mCV.getGoldAngle());
                    //base.move(); move.forward(range.of.cube - manip.size)
                    //extend/deploy manip
                    //intake gold             manip.intake.on
                    //retract/undeplpoy manip.intake.off
                    setState(State.LOGNAV);
                } else {
                }
                break;

            case FINDNAV:
                if (!mVF.isVisible()) {
                    mBase.move(0, .25);
                    mVF.getVuMark();
                } else {
                    setState(State.LOGNAV);
                }
                break;

            case LOGNAV:
                if (mVF.isVisible()) {
                    //Move based on logged visible vurmark - 4 ifs for 4 navigations
                    //Move.To.Pic       turn.left.till.center.of.bot.is.aligned.with.pic
                } else {
                }
                break;

            case DEPOT_MARKER:
                // if () {} else {}
                //put.one.in          OUTTAKE CODE THAT WE HAVE NOT DECIDED  ON
                setState(State.DEPOSITMIN);
                break;

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
                separate.minerals */
                if (mRunTime.seconds() < 7.5) {
                    setState(State.PARK);
                }
                break;

            case PARK:
                //Locate current position
                //Move to crater
        }
    }

    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {
    }

}

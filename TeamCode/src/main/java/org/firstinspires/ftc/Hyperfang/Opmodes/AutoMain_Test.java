package org.firstinspires.ftc.Hyperfang.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.Hyperfang.Robot.Lift;
import org.firstinspires.ftc.Hyperfang.Sensors.IMU;
import org.firstinspires.ftc.Hyperfang.Sensors.OpenCV;
import org.firstinspires.ftc.Hyperfang.Sensors.Vuforia;

public class AutoMain_Test extends OpMode {

    //List of system states. MOVE TO OWN CLASS?
    private enum State {
        INITIAL,
        LAND,
        FINDMIN,
        SAMPLE,
        LOGNAV,
        DEPOT_MARKER,
        DEPOSITMIN,
        PICKUPMIN,
        PARK,
    }

    private Lift mLift;
    private Vuforia mVF;
    private OpenCV mCV;

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
    public AutoMain_Test() {
    } //Default Constructor
//--------------------------------------------------------------------------------------------------

    //Initialization: Runs once  driver presses init.
    @Override
    public void init() {
        mInitTime.reset(); //Starting our initialization timer.

        //Instantiating our robot objects.
        mLift = new Lift(hardwareMap);
        mVF = new Vuforia();
        mCV = new OpenCV();

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
                    mLift.moveTo(Lift.LEVEL.GROUND, .5, mLift.ratchetMotor());
                    mLift.unhook();
                    setState(State.FINDMIN);
                } else {
                    telemetry.addData("", "stuff");
                }
                break;

            case FINDMIN:
                if (mLift.getPosition().equals("GROUND")) {
                    while (!mCV.getGold()) { //Unnecessary loop?
                        mCV.findGold(mCV.getVuforia(mVF.getBitmap()), telemetry); //EDIT: May need to add loop.
                        //base.turn(right) until block is found
                    }
                    setState(State.SAMPLE);
                } else {
                }
                break;

            case SAMPLE:
                if (mCV.getGold()) {
                    //base.turnToGold(); Lineup Manip with gold
                    //base.move(); move.forward(range.of.cube - manip.size)
                    //extend/deploy manip
                    //intake gold             manip.intake.on
                    //retract/undeplpoy manip.intake.off
                    mVF.getVuMark();
                    //turn left
                    setState(State.LOGNAV);
                } else {
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
        }
    }

    //Stop: Runs once driver hits stop.
    @Override
    public void stop() {
    }

}

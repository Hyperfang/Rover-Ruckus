package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.Hyperfang.Sensors.MGL;

public class Lift {
    public MGL mgl;

    public enum LEVEL {
        GROUND,
        LATCH,
        TOP,
    }

    private DcMotor liftMotor;
    private DcMotor ratchetMotor;
    private Servo ratchetServo;
    private Servo hook; //Possibly change to continuous to ease Tele-Op.


    private LEVEL pos;

    private OpMode mOpMode;

    //Initializes the lift objects.
    public Lift(OpMode opMode){
        mOpMode = opMode;
        liftMotor = mOpMode.hardwareMap.get(DcMotor.class, "vLift");
        ratchetMotor = mOpMode.hardwareMap.get(DcMotor.class, "ratchet");
        ratchetMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hook = mOpMode.hardwareMap.get(Servo.class, "hook");
        ratchetServo = mOpMode.hardwareMap.get(Servo.class, "rServo");

        mgl = new MGL(opMode);
        pos = LEVEL.GROUND;
    }

    //Moves to a specified position of the lift depending on the current position.
    public void moveTo(LEVEL lvl, double power, DcMotor motor) {
        switch (lvl) {
            case GROUND:
                if(!pos.equals(lvl)) move(power, motor);

                if (pos.equals(lvl)) {
                    motor.setPower(0);
                    setPosition(LEVEL.GROUND);
                    break;
                }
                break;
            case LATCH:
                if(!pos.equals(lvl)) move(power, motor);

                if (pos.equals(lvl)) {
                    motor.setPower(0);
                    setPosition(LEVEL.LATCH);
                    break;
                }
                break;
            case TOP:
                if(!pos.equals(lvl)) move(power, motor);

                if (pos.equals(lvl)) {
                    motor.setPower(0);
                    setPosition(LEVEL.TOP);
                    break;
                }
                break;
        }
    }

    //Moves our lift/ratchet up or down depending on the given power.
    public void move(double power, DcMotor motor) {
        switch (pos) {
            case GROUND:

                //Don't move down if we are at the lowest level.
                if (power > 0) {
                    motor.setPower(power);
                    if (mgl.isStateChange()) { pos = LEVEL.LATCH; }
                }

                //If we wish to move down from mid-level, make sure we aren't at the base.
                if (power < 0 && !mgl.isTouched()) {
                    motor.setPower(power);
                }
                break;

            case LATCH:
                if (power < 0 || 0 < power) {
                    motor.setPower(power);
                    if (mgl.isStateChange()) {
                        if (power < 0) { pos = LEVEL.GROUND; }
                        if (power > 0) { pos = LEVEL.TOP; }
                    }
                }
                break;

            case TOP:
                //Don't move up if we are at the highest level.
                if (power < 0) {
                    motor.setPower(power);
                    if (mgl.isStateChange()) { pos = LEVEL.LATCH; }
                }

                //If we wish to move up from mid-level, make sure we aren't at the base.
                if (power > 0 && !mgl.isTouched()) {
                    motor.setPower(power);
                }
                break;
        }
    }

    //Stops the lift/ratchet.
    public void stop() {
        liftMotor.setPower(0);
        ratchetMotor.setPower(0);
    }

    //Sets the position of the ratchet based on whether we want it move-able or stopped.
    //"move" sets it in a state that is ready to be moved. "stop" disallows movement.
    public void setRatchet(String pos) {
        if (pos.equals("move")) ratchetServo.setPosition(.5);
        if (pos.equals("stop")) ratchetServo.setPosition(0);
    }

    //Sets the position of our lift.
    public void setPosition(LEVEL position) {
        pos = position;
    }

    //Returns the position of our lift.
    public String getPosition() {
        return pos.name();
    }

    //Moves the hook to a position which we can hook.
    public void hook() { hook.setPosition(1); } //need to test position.

    //Moves the hook to a position which we can unhook.
    public void unhook() { hook.setPosition(0); } //need to test position.

    //Returns the lift motor for specification outside the class.
    public DcMotor liftMotor() { return liftMotor; }

    //Returns the ratchet motor for specification outside the class.
    public DcMotor ratchetMotor() { return ratchetMotor; }

}

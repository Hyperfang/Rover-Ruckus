package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.Hyperfang.Sensors.MGL;

public class Lift {
    public enum LEVEL {
        GROUND,
        LATCH,
        TOP,
    }

    private LEVEL pos;

    private static DcMotor liftMotorR;
    private static DcMotor liftMotorL;
    private static DcMotor pivotMotorR;
    private static DcMotor pivotMotorL;
    private Servo ratchetServo;
    private Servo hookR;
    private Servo hookL;

    private MGL mgl;

    private OpMode mOpMode;

    //pivot bac 2
    //lift gears 2
    //Initializes the lift objects.
    public Lift(OpMode opMode){
        mOpMode = opMode;
        liftMotorR = mOpMode.hardwareMap.get(DcMotor.class, "Lift Right");
        liftMotorL = mOpMode.hardwareMap.get(DcMotor.class, "Lift Left");
        pivotMotorR = mOpMode.hardwareMap.get(DcMotor.class, "Pivot Right");
        pivotMotorL = mOpMode.hardwareMap.get(DcMotor.class, "Pivot Left");
        hookR = mOpMode.hardwareMap.get(Servo.class, "Right Hook");
        hookL = mOpMode.hardwareMap.get(Servo.class, "Left Hook");

        liftMotorR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

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
    //TODO: Tracking needs testing.
    public void move(double power, DcMotor motor) {
        switch (pos) {
            default:
                motor.setPower(power);
                break;

            case GROUND:
                //Don't move down if we are at the lowest level.
                if (power > 0) {
                    motor.setPower(power);
                    if (mgl.isStateChange()) { pos = LEVEL.LATCH; }
                }

                if (power == 0) motor.setPower(0);

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

                if (power == 0) motor.setPower(0);

                break;

            case TOP:
                //Don't move up if we are at the highest level.
                if (power < 0) {
                    motor.setPower(power);
                    if (mgl.isStateChange()) { pos = LEVEL.LATCH; }
                }

                if (power == 0) motor.setPower(0);

                //If we wish to move up from mid-level, make sure we aren't at the base.
                if (power > 0 && !mgl.isTouched()) {
                    motor.setPower(power);
                }
                break;
        }
    }

    //Stops the lift/ratchet.
    public void stop() {
       // liftMotor.setPower(0);
    }

    //Unlocks the ratchet sets it in a state that is ready to be moved. "stop" disallows movement.
    public void unlockRatchet() {
        ratchetServo.setPosition(.2);
    }

    //Locks the ratchet to prevent ratchet movement.
    public void lockRatchet() { ratchetServo.setPosition(.5); }

    //For testing when faulty hardware effects the servo positions.
    public void setRatchetLock(double pos) { ratchetServo.setPosition(pos); }

    //Sets the position of our lift.
    public void setPosition(LEVEL position) {
        pos = position;
    }

    //Returns the position of our lift.
    public String getPosition() {
        return pos.name();
    }

    //Moves the hook to a position which we can hook.
    //public void hook() { hook.setPosition(.25); }

    //Moves the hook to a position which we can unhook.
    //public void unhook() { hook.setPosition(0.05); }

    //Returns the Lift motor.
    //public DcMotor LiftMotor() {
    //    return liftMotor;
    //}

}

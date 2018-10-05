package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.Hyperfang.Sensors.MGL;

public class Lift {
    public MGL mgl;

    public enum LEVEL {
        GROUND,
        LATCH,
        TOP
    }

    private DcMotor liftMotor;
    private DcMotor ratchetMotor;
    private Servo hook; //Possibly change to continuous to ease teleop.

    private LEVEL pos;

    //Initializes the lift objects.
    public Lift(HardwareMap hMap){
        liftMotor = hMap.get(DcMotor.class, "lift");
        ratchetMotor = hMap.get(DcMotor.class, "ratchet");
        ratchetMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hook = hMap.get(Servo.class, "hook");

        mgl = new MGL(hMap, "mgl");
        pos = LEVEL.GROUND;
    }

    //TODO create loop to set appropriate power to motor then stop once at desired position
    //May need to edit.
    public void moveTo(LEVEL lvl, double power, DcMotor motor) {
        switch (lvl) {
            case GROUND:
                if(pos == lvl.GROUND || power != Math.abs(power)){break;}
                move(power, motor);
                pos = LEVEL.GROUND;
                break;
            case LATCH:
                if(pos == lvl.LATCH){break;}
                move(power, motor);
                pos = LEVEL.LATCH;
                break;
            case TOP:
                if(pos == lvl.TOP || power == Math.abs(power)){break;}
                move(power, motor);
                pos = LEVEL.TOP;
                break;
        }
        stop();
    }

    //Moves our lift/ratchet up or down depending on the given power.
    public void move(double power, DcMotor motor) {
        switch (pos) {
            case GROUND:
                if (power > 0) {
                    motor.setPower(power);
                    if (mgl.isStateChange()) { pos = LEVEL.LATCH; }
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
                if (power < 0) {
                    motor.setPower(power);
                    if (mgl.isStateChange()) { pos = LEVEL.LATCH; }
                }
                break;
        }
    }

    //Stops the lift/ratchet.
    public void stop() {
        liftMotor.setPower(0);
        ratchetMotor.setPower(0);
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
    public void hook() { hook.setPosition(1); } //need to test position

    //Moves the hook to a position which we can unhook.
    public void unhook() { hook.setPosition(0); } //need to test position

    //Returns the lift motor for specification outside the class.
    public DcMotor liftMotor() { return liftMotor; }

    //Returns the ratchet motor for specification outside the class.
    public DcMotor ratchetMotor() { return ratchetMotor; }


}

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
    private Servo hook; //May need to edit.
    private LEVEL pos;

    public Lift(HardwareMap hMap){
        pos = LEVEL.LATCH;
        mgl = new MGL(hMap, "mgl");
        liftMotor = hMap.get(DcMotor.class, "lift");
        ratchetMotor = hMap.get(DcMotor.class, "ratchet");
        ratchetMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    //TODO create loop to set appropriate power to motor then stop once at desired position
    //May need to edit.
    public void moveTo(LEVEL lvl, double power) {
        switch (lvl) {
            case GROUND:
                if(pos == lvl.GROUND || power != Math.abs(power)){break;}
                move(power);
                pos = LEVEL.GROUND;
                break;
            case LATCH:
                if(pos == lvl.LATCH){break;}
                move(power);
                pos = LEVEL.LATCH;
                break;
            case TOP:
                if(pos == lvl.TOP || power == Math.abs(power)){break;}
                move(power);
                pos = LEVEL.TOP;
                break;
        }
        stop();
    }

    //TODO add method which checks for change in magnetic limit switch and changes state.
    //Moves our lift up or down depending on the given power.
    public void move(double power) {
        switch (pos) {
            case GROUND:
                if (power > 0) {
                    liftMotor.setPower(power);
                    if (mgl.isStateChange()) { pos = LEVEL.LATCH; }
                }
                break;

            case LATCH:
                if (power < 0 || 0 < power) {
                    liftMotor.setPower(power);
                    if (mgl.isStateChange()) {
                        if (power < 0) { pos = LEVEL.GROUND; }
                        if (power > 0) { pos = LEVEL.TOP; }
                    }
                }
                break;

            case TOP:
                if (power < 0) {
                    liftMotor.setPower(power);
                    if (mgl.isStateChange()) { pos = LEVEL.LATCH; }
                }
                break;
        }
    }

    public void stop() {
        liftMotor.setPower(0);
        //ratchetMotor.setPower(); until you lock in the ratchet;
    }

    //Sets the position of our lift.
    public void setPosition(LEVEL position) {
        pos = position;
    }

    //Returns the position of our lift.
    public String getPosition() {
        return pos.name();
    }

    public void hook() { hook.setPosition(1); } //need to test position

    public void unhook() { hook.setPosition(0); } //need to test position

}

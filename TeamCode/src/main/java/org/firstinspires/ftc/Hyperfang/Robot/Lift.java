package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.sun.tools.javac.code.Attribute;

import org.firstinspires.ftc.Hyperfang.Sensors.MGL;

public class Lift {
    private DcMotor liftMotor;
    private DcMotor ratchetMotor;
    private Servo hook; //May need to change this
    private MGL mgl;
    private LEVEL pos;

    private enum LEVEL {
        GROUND,
        LATCH,
        TOP
    }

    public Lift(HardwareMap hMap){
        pos = LEVEL.LATCH;
        mgl = new MGL(hMap, "mgl");
        liftMotor = hMap.get(DcMotor.class, "lift");
        ratchetMotor = hMap.get(DcMotor.class, "ratchet");
        ratchetMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    //TODO create loop to set appropriate power to motor then stop once at desired position

    public void moveTo(LEVEL lvl, double power) {
        switch (lvl) {
            case GROUND:
                if(pos == lvl.GROUND || power != Math.abs(power)){break;}
                move(power);
                pos = LEVEL.GROUND;
                break;
            case TOP:
                if(pos == lvl.TOP || power == Math.abs(power)){break;}
                move(power);
                pos = LEVEL.TOP;
                break;
            case LATCH:
                if(pos == lvl.LATCH){break;}
                move(power);
                pos = LEVEL.LATCH;
                break;
        }
    }

    public void move(double power) {
        switch (pos) {
            case GROUND: //bottom
                if (power < 0) {break;}
                liftMotor.setPower(power);
                break;
            case TOP: //top
                if (power > 0) {break;}
                liftMotor.setPower(power);
                break;
            case LATCH: //middle
                liftMotor.setPower(power);
                break;
        }
    }

    public void stop() {
        liftMotor.setPower(0);
        //ratchetMotor.setPower(); until you lock in the ratchet;
    }

    public void hook() { hook.setPosition(1); } //need to test position

    public void unhook() { hook.setPosition(0); } //need to test position


}

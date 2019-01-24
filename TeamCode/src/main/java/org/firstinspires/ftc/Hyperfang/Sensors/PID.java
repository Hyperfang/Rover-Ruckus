package org.firstinspires.ftc.Hyperfang.Sensors;

import com.qualcomm.robotcore.util.ElapsedTime;

/*
Created by Caleb. Logic Assistance by Daniel.
 */
public class PID {
    private double kP;
    private double kI;
    private double kD;

    private ElapsedTime currTime = new ElapsedTime();
    private double kError;
    private double kRate;
    private double prevError;
    private double prevTime;

    public PID() {
        kP = 0;
        kI = 0;
        kD = 0;

        kError = 0;
        kRate = 0;
        prevError = 0;
        prevTime = 0;
    }

    public PID(double P, double I, double D) {
        kP = P;
        kI = I;
        kD = D;

        kError = 0;
        kRate = 0;
        prevError = 0;
        prevTime = 0;
    }

    public void setConstants(double P, double I, double D) {
        kP = P;
        kI = I;
        kD = D;
    }

    //Calculates the PID value based on the last call.
    public double update(double error) {
        kError += error;
        kRate = (error - prevError)/(currTime.milliseconds() - prevTime);
        prevError = error;
        prevTime = currTime.milliseconds();
        return kP * error + kI * kError - Math.abs(kD * kRate);
    }

}

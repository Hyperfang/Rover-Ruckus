package org.firstinspires.ftc.Hyperfang.Sensors;

import com.qualcomm.robotcore.util.ElapsedTime;

/*
Created by Caleb. Logic Assistance by Daniel.
 */
public class PID {

    //PID variables
    private double kP;
    private double kI;
    private double kD;
    private double kError;
    private double kRate;
    private double prevError;
    private double prevTime;
    private ElapsedTime currTime = new ElapsedTime();

    //Default Constructor
    public PID() {
        kP = 0;
        kI = 0;
        kD = 0;

        kError = 0;
        kRate = 0;
        prevError = 0;
        prevTime = 0;
    }

    //Parameter Constructor: Sets the PID variables.
    public PID(double P, double I, double D) {
        kP = P;
        kI = I;
        kD = D;

        kError = 0;
        kRate = 0;
        prevError = 0;
        prevTime = 0;
    }

    //Sets the constants for the PID.
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

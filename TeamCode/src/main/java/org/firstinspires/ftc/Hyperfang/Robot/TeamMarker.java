package org.firstinspires.ftc.Hyperfang.Robot;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;



@Autonomous(name = "Team Marker", group = "Linear Opmode")
public class TeamMarker {

    public enum LEVEL {
        SET,
        ITERATE
    }

    private Servo footServo;
    private LEVEL footPos;

    public TeamMarker (HardwareMap hMap){


    }



}

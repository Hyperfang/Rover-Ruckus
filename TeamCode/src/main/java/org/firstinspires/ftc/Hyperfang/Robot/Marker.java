package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Marker {

    private Servo marker;
    private boolean isDropped = false;

    //Initializes our Marker object.
    public Marker(HardwareMap hMap){
        marker = hMap.get(Servo.class, "marker");
    }

    //Resets the marker arm to default position.
    public void reset() { marker.setPosition(0); } //need to test position

    //Drops the team marker and indicates we have dropped.
    //The marker should drop in one movement, and is considered dropped for the rest of execution.
    public void drop() {
        marker.setPosition(1);  //need to test position
        isDropped = true;
    }

    //Returns whether we have performed the action of dropping our team marker.
    public boolean isDropped() {
        return isDropped;
    }
}

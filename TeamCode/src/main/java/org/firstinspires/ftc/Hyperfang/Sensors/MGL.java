package org.firstinspires.ftc.Hyperfang.Sensors;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DigitalChannel;

/*
    Created by Caleb.
*/

public class MGL {

    private DigitalChannel MGL;
    private boolean changeLock;

    //Initializes our Magnetic Limit Switch.
    public MGL(OpMode opMode) {
        MGL = opMode.hardwareMap.get(DigitalChannel.class, "mgl");
        MGL.setMode(DigitalChannel.Mode.INPUT);
        changeLock = true;
    }

    //Returns true or false based on the switch
    public boolean isTouched(){
        return !MGL.getState();
    }

    //Checks whether the magnetic limit switch has gone through a full cycle of states.
    //This method is 'true' based (True -> False -> True = True)
    public boolean isStateChange() {
        //If our change lock is locked and we aren't touched then we have moved from true to false.
        if (changeLock && !isTouched()) { changeLock = false; }

        //If our change lock is unlocked and we are touching, then we have moved from false to true.
        if (!changeLock && isTouched()) {
            changeLock = true;
            return true;
        }
        return false;
    }

}

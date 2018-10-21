package org.firstinspires.ftc.Hyperfang.Sensors;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.opencv.core.Mat;

/*
    Created by Caleb. May be deleted.
*/


public class Vision {
    private OpenCV cv;
    private Vuforia vf;

    //Initializes the Vision objects required to run our vision package.
    public Vision(OpMode opMode) {
        cv = new OpenCV(opMode);
        vf = new Vuforia();
    }

    //Initializes the Vision objects required to run our vision package with a camera monitor
    public Vision(OpMode opMode, String id) {
        cv = new OpenCV(opMode);
        vf = new Vuforia(opMode);
    }

    //Activates our tracking.
    public void activate() {
        vf.activate();
        cv.activate(vf.getBitmap());

    }

    public Mat getMat() {
    return cv.getVuforia(vf.getBitmap());
    }
}

package org.firstinspires.ftc.Hyperfang.Vision;

/*
    Created by Caleb on 11/2/2018.
 */


import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

public class Tensorflow {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    private static final int noGold = -1024;
    private TFObjectDetector tfod;

    //List of positions for gold minerals used in the relative sampling method.
    public enum Position { LEFT, CENTER, RIGHT, UNKNOWN }
    private Position pos;
    private boolean posFound = false;

    private OpMode mOpMode;

    //Initializes our Tensorflow object without a camera monitor.
    public Tensorflow(OpMode opmode, VuforiaLocalizer vuforia) {
        mOpMode = opmode;
        pos = Position.UNKNOWN;

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters();
            tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
            tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
        } else {
            mOpMode.telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }
    }

    //Initializes our Tensorflow object with a camera monitor.
    public Tensorflow(OpMode opmode, VuforiaLocalizer vuforia, String id) {
        mOpMode = opmode;
        pos = Position.UNKNOWN;

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            int tfodMonitorViewId = mOpMode.hardwareMap.appContext.getResources().getIdentifier(
                    "tfodMonitorViewId", "id", mOpMode.hardwareMap.appContext.getPackageName());
            TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
            tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
            tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
        } else {
            mOpMode.telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }
    }

    //Activates Tensorflow object detection.
    //Must initialize Vuforia beforehand.
    public void activate() { if (tfod != null) tfod.activate(); }

    //Deactivates Tensorflow object detection.
    public void deactivate() { if (tfod != null) tfod.shutdown(); }

    //Locates the position of the gold cube and logs the position.
    public void sample() {
            if (tfod != null) {
                //getUpdatedRecognitions() will return null if no new information is available.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    mOpMode.telemetry.addData("# Object Detected", updatedRecognitions.size());

                    //Samples based on 3 minerals.
                    if (updatedRecognitions.size() == 3) {
                        int goldMineralX = -1;
                        int silverMineral1X = -1;
                        int silverMineral2X = -1;
                        for (Recognition recognition : updatedRecognitions) {
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                goldMineralX = (int) recognition.getLeft();
                            } else if (silverMineral1X == -1) {
                                silverMineral1X = (int) recognition.getLeft();
                            } else {
                                silverMineral2X = (int) recognition.getLeft();
                            }
                        }
                        if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                            if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                                pos = Position.LEFT;
                                posFound = true;
                                mOpMode.telemetry.addData("Gold Mineral Position", "Left");
                            } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                                pos = Position.RIGHT;
                                posFound = true;
                                mOpMode.telemetry.addData("Gold Mineral Position", "Right");
                            } else {
                                pos = Position.CENTER;
                                posFound = true;
                                mOpMode.telemetry.addData("Gold Mineral Position", "Center");
                            }
                        }
                    }

                    //Samples based on 2 minerals.
                    if (updatedRecognitions.size() == 2) {
                        int goldMinCheck = noGold;
                        int silverMinCheck = -1;

                        for (Recognition recognition : updatedRecognitions) {
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                goldMinCheck = (int) recognition.getLeft();
                            }
                            else if (silverMinCheck == -1) {
                                silverMinCheck = (int) recognition.getLeft();
                            }
                        }

                        //Gold has a lesser integer value when it is on the left.
                        if (silverMinCheck != -1) {
                            if (goldMinCheck < silverMinCheck && goldMinCheck != noGold) {
                                pos = Position.LEFT;
                                posFound = true;
                                mOpMode.telemetry.addData("Gold Mineral Position", "Left");
                            }  else if (goldMinCheck > silverMinCheck && goldMinCheck != noGold) {
                                pos = Position.CENTER;
                                posFound = true;
                                mOpMode.telemetry.addData("Gold Mineral Position", "Center");
                            }
                            else {
                                pos = Position.RIGHT;
                                posFound = true;
                                mOpMode.telemetry.addData("Gold Mineral Position", "Right");
                            }
                        }
                    }
                }
            }
    }



    //Returns the position of the cube.
    public Position getPos() {
        return pos;
    }

    //Returns whether the gold's position has been found.
    public boolean isPosFound() {
        return posFound;
    }
}
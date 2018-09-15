package org.firstinspires.ftc.Hyperfang.Sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;

/**
 * This 2018-2019 OpMode illustrates the basics of using the Vuforia localizer to determine
 * positioning and orientation of robot on the FTC field.
 * The code is structured as a LinearOpMode
 *
 * Vuforia uses the phone's camera to inspect it's surroundings, and attempt to locate target images.
 *
 * When images are located, Vuforia is able to determine the position and orientation of the
 * image relative to the camera.  This sample code than combines that information with a
 * knowledge of where the target images are on the field, to determine the location of the camera.
 *
 * This example assumes a "square" field configuration where the red and blue alliance stations
 * are on opposite walls of each other.
 *
 * From the Audience perspective, the Red Alliance station is on the right and the
 * Blue Alliance Station is on the left.

 * The four vision targets are located in the center of each of the perimeter walls with
 * the images facing inwards towards the robots:
 *     - BlueRover is the Mars Rover image target on the wall closest to the blue alliance
 *     - RedFootprint is the Lunar Footprint target on the wall closest to the red alliance
 *     - FrontCraters is the Lunar Craters image target on the wall closest to the audience
 *     - BackSpace is the Deep Space image target on the wall farthest from the audience
 *
 * A final calculation then uses the location of the camera on the robot to determine the
 * robot's location and orientation on the field.
 */

//NEEDS TO BE CLEANED AND EDITED.

public class Vuforia {
    private static final String VUFORIA_KEY = "AZN6LX7/////AAABmbg6nR27kkt3k51B4sS0SN1X0YTVkeE2krX3iLv5vh13mmWhegXY0TBkNA2mwtchs8g317OarcIF98ujECp35m/e3tAfohaTv9biiKvrcw3z+cb1RSatzL2l57sOU/dyvQX+waQ8TJ6uWiaO67P2zAOa5KCI2jsgmyILciFeC8wUqKUprOgk6F6rucdf/B+dEt4C2ZEycufoPz2XEQrHlhpPfmBymFNu93Kja2qrisBazRc8QwP2ZMwSLvoibe3b6ss06rh81AiIYIulJEkWhenKxdQh7nNUH+RQ3jvFRoJBASXEyhzGKItWaAEDOABm9zfis4sx+eNijNfChh8mdVUKSvztrjNUBcRU8On0z8kJ";

    //Since ImageTarget trackables use mm to specifiy their dimensions, we must use mm for all the physical dimension.
    private static final float mmPerInch = 25.4f;
    private static final float mmFTCFieldWidth = (12 * 6) * mmPerInch;       // the width of the FTC field (from the center point to the outer panels)
    private static final float mmTargetHeight = (6) * mmPerInch;          // the height of the center of the target image above the floor

    private HardwareMap hardwareMap;
    private Telemetry telemetry;


    //Variable we use to store our instance of the Vuforia localization engine.
    VuforiaLocalizer vuforia;
    VuforiaLocalizer.Parameters parameters;

    //Our trackables, and trackable objects.
    VuforiaTrackables targetsRoverRuckus;
    VuforiaTrackable blueRover;
    VuforiaTrackable redFootprint;
    VuforiaTrackable frontCraters;
    VuforiaTrackable backSpace;
    List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();

    VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
    // VuforiaTrackableDefaultListener listener;

    int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

    private OpenGLMatrix lastLocation = null;
    private boolean targetVisible = false;


    //Initializes our vuforia object.
    public Vuforia() {
        //parameters = new VuforiaLocalizer.Parameters(); for no camera monitor
        parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CAMERA_CHOICE;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    public void initTrack() {
        // Load the data sets that for the trackable objects. These particular data
        // sets are stored in the 'assets' part of our application.
        targetsRoverRuckus = this.vuforia.loadTrackablesFromAsset("RoverRuckus");

        blueRover = targetsRoverRuckus.get(0);
        blueRover.setName("Blue-Rover");
        redFootprint = targetsRoverRuckus.get(1);
        redFootprint.setName("Red-Footprint");
        frontCraters = targetsRoverRuckus.get(2);
        frontCraters.setName("Front-Craters");
        backSpace = targetsRoverRuckus.get(3);
        backSpace.setName("Back-Space");

        // For convenience, gather together all the trackable objects in one easily-iterable collection */
        allTrackables.addAll(targetsRoverRuckus);

        //Start tracking the data sets we care about.
        targetsRoverRuckus.activate();
    }

    //Creating our matrix which places our object on the field, so we can move based off the object's position.
    public void navMatrix() {
        /**
         * To place the BlueRover target in the middle of the blue perimeter wall:
         * - First we rotate it 90 around the field's X axis to flip it upright.
         * - Then, we translate it along the Y axis to the blue perimeter wall.
         */
        OpenGLMatrix blueRoverLocationOnField = OpenGLMatrix
                .translation(0, mmFTCFieldWidth, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0));
        blueRover.setLocation(blueRoverLocationOnField);

        /**
         * To place the RedFootprint target in the middle of the red perimeter wall:
         * - First we rotate it 90 around the field's X axis to flip it upright.
         * - Second, we rotate it 180 around the field's Z axis so the image is flat against the red perimeter wall
         *   and facing inwards to the center of the field.
         * - Then, we translate it along the negative Y axis to the red perimeter wall.
         */
        OpenGLMatrix redFootprintLocationOnField = OpenGLMatrix
                .translation(0, -mmFTCFieldWidth, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180));
        redFootprint.setLocation(redFootprintLocationOnField);

        /**
         * To place the FrontCraters target in the middle of the front perimeter wall:
         * - First we rotate it 90 around the field's X axis to flip it upright.
         * - Second, we rotate it 90 around the field's Z axis so the image is flat against the front wall
         *   and facing inwards to the center of the field.
         * - Then, we translate it along the negative X axis to the front perimeter wall.
         */
        OpenGLMatrix frontCratersLocationOnField = OpenGLMatrix
                .translation(-mmFTCFieldWidth, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 90));
        frontCraters.setLocation(frontCratersLocationOnField);

        /**
         * To place the BackSpace target in the middle of the back perimeter wall:
         * - First we rotate it 90 around the field's X axis to flip it upright.
         * - Second, we rotate it -90 around the field's Z axis so the image is flat against the back wall
         *   and facing inwards to the center of the field.
         * - Then, we translate it along the X axis to the back perimeter wall.
         */
        OpenGLMatrix backSpaceLocationOnField = OpenGLMatrix
                .translation(mmFTCFieldWidth, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90));
        backSpace.setLocation(backSpaceLocationOnField);
    }

    //Placing our camera on a matrix which allows us to move the robot based on the distance
    //between
    final int CAMERA_FORWARD_DISPLACEMENT = 110;   // eg: Camera is 110 mm in front of robot center
    final int CAMERA_VERTICAL_DISPLACEMENT = 200;   // eg: Camera is 200 mm above ground
    final int CAMERA_LEFT_DISPLACEMENT = 0;     // eg: Camera is ON the robot's center line

    /**
     * Create a transformation matrix describing where the phone is on the robot.
     * <p>
     * The coordinate frame for the robot looks the same as the field.
     * The robot's "forward" direction is facing out along X axis, with the LEFT side facing out along the Y axis.
     * Z is UP on the robot.  This equates to a bearing angle of Zero degrees.
     * <p>
     * The phone starts out lying flat, with the screen facing Up and with the physical top of the phone
     * pointing to the LEFT side of the Robot.  It's very important when you test this code that the top of the
     * camera is pointing to the left side of the  robot.  The rotation angles don't work if you flip the phone.
     * <p>
     * If using the rear (High Res) camera:
     * We need to rotate the camera around it's long axis to bring the rear camera forward.
     * This requires a negative 90 degree rotation on the Y axis
     * <p>
     * Next, translate the camera lens to where it is on the robot.
     * In this example, it is centered (left to right), but 110 mm forward of the middle of the robot, and 200 mm above ground level.
     */

    public void phoneMatrix() {
        OpenGLMatrix phoneMatrix = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES,
                        CAMERA_CHOICE == FRONT ? 90 : -90, 0, 0));

        //Let all the trackable listeners know where the phone is.
        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(phoneMatrix, parameters.cameraDirection);
        }
    }

    //I don't really know wtf this does, but I do know it has something to do with turning towards the vumark.
    public void findRobot() {
        //Provide feedback as to where the robot is located (if we know).
        if (targetVisible) {
            // express position (translation) of robot in inches.
            VectorF translation = lastLocation.getTranslation();
            telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                    translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);

            // express the rotation of the robot in degrees.
            Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
            telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
        } else {
            telemetry.addData("Visible Target", "none");
        }
    }

    private OpenGLMatrix pose;

    //Express the roll of the robot in degrees. WE CAN TRANSFORM THIS INTO A SIMPLE FIND VUMARK METHOD.
    //Then we get the rotation from there.
    //Otherwise we risk lastLocation being null.
    public double getRoll() {
        for (VuforiaTrackable vuMark : allTrackables) {
            //If we locate a vumark, we take it's pose information.
            if (((VuforiaTrackableDefaultListener) vuMark.getListener()).isVisible()) {
                pose = ((VuforiaTrackableDefaultListener)vuMark.getListener()).getPose();
                break;
            }
        }

        if (pose != null) {
            //Express the rotation of the robot in degrees.
            Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
            return rotation.firstAngle;
        }

        return 0;
    }

    public void track() {
        // check all the trackable target to see which one (if any) is visible.
        targetVisible = false;
        for (VuforiaTrackable track : allTrackables) {
            if (((VuforiaTrackableDefaultListener) track.getListener()).isVisible()) {
                telemetry.addData("Visible Target", track.getName());
                targetVisible = true;

                // getUpdatedRobotLocation() will return null if no new information is available since
                // the last time that call was made, or if the trackable is not currently visible.
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener) track.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastLocation = robotLocationTransform;
                }
                break;
            }
        }
    }

}

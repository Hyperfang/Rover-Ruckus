/*
    11503 Hyperfang:
        Finding and location methods (in relation to object pixels) done by Caleb Browne.
        Finding of distance in axis CM done by Daniel Kasabov.
 */

package org.firstinspires.ftc.Hyperfang.Sensors;

import android.graphics.Bitmap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenCV {

    private Mat resizeOutput;
    private Mat blurOutput;
    private Mat hsvOutput;
    private Mat dilateOutput;
    private Mat erodeOutput;
    private Mat hierarchy;
    private List<MatOfPoint> contours = new ArrayList<>();

    private Point cameraMidpoint;
    private Point cameraDimensions;

    private boolean goldFound = false;
    private Point goldMidpoint;
    private Point3D gold3D;

    //Initializes the OpenCV library and our matrices required to run vision.
    public OpenCV() {
        // Loading the OpenCV core library
        System.loadLibrary("opencv_java3");

        //Preparing our matrice variables.
        resizeOutput = new Mat();
        blurOutput = new Mat();
        hsvOutput = new Mat();
        dilateOutput = new Mat();
        erodeOutput = new Mat();
        hierarchy = new Mat();
    }

    //Makes sure our tracking is ready to go.
    public void activate(Bitmap img) {
        phoneDimensions(getVuforia(img));
        phoneMidpoint(getVuforia(img));
    }

    //Finding the silver minerals. (Note: Requires findBlobs())
    public void findSilver(Mat input) {
        //Step 1. Resizing our picture in order to decrease runtime.
        Mat resize = input;
        Size cvResizeDsize = new Size(0, 0);
        double cvResizeFx = 0.25;
        double cvResizeFy = 0.25;
        int cvResizeInterpolation = Imgproc.INTER_LINEAR;
        Imgproc.resize(resize, resizeOutput, cvResizeDsize, cvResizeFx, cvResizeFy, cvResizeInterpolation);

        //Step 2. Applying a blur to our picture in order to reduce noise.
        Mat blur = resizeOutput;
        Imgproc.GaussianBlur(blur, blurOutput, new Size(7.9, 7.9), 0);

        //Step 3. Converting our color space from RGBA to HSV, and then thresholding it.
        Mat hsv = new Mat();
        Imgproc.cvtColor(blurOutput, hsv, Imgproc.COLOR_RGB2HSV);
        Core.inRange(hsv, new Scalar(0, 0, 172), new Scalar(180, 60, 255), hsvOutput);
        // H 0 - 180
        // S 0 - 60
        // V 174 - 255

        //Step 4. Dilating our threshold (binary) image in order to reduce the number of blobs.
        Mat dilate = hsvOutput;
        Mat dilateKernel = new Mat();
        Point cvDilateAnchor = new Point(-1, -1);
        double cvDilateIterations = 4.0;
        int cvDilateBordertype = Core.BORDER_DEFAULT;
        Scalar cvDilateBordervalue = new Scalar(-1);
        Imgproc.dilate(dilate, dilateOutput, dilateKernel, cvDilateAnchor, 4, cvDilateBordertype, cvDilateBordervalue);

        // Step 5. Finding the contours so we can use them to return the position of the cube.
        Mat findContours = dilateOutput;
        Imgproc.findContours(findContours, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
    }

    public void findGold(Mat input, Telemetry telemetry) {
        goldFound = false;

        //Step 1. Resizing our picture in order to decrease runtime.
        Mat resize = input;
        Size cvResizeDsize = new Size(0, 0);
        double cvResizeFx = 0.5;
        double cvResizeFy = 0.5;
        int cvResizeInterpolation = Imgproc.INTER_LINEAR;
        Imgproc.resize(resize, resizeOutput, cvResizeDsize, cvResizeFx, cvResizeFy, cvResizeInterpolation);

        //Step 2. Applying a blur to our picture in order to reduce noise.
        Mat blur = resizeOutput;
        Imgproc.GaussianBlur(blur, blurOutput, new Size(7, 7), 0);

        //Step 3. Converting our color space from RGBA to HSV, and then thresholding it.
        Mat hsv = new Mat();
        Imgproc.cvtColor(blurOutput, hsv, Imgproc.COLOR_RGB2HSV);
        Core.inRange(hsv, new Scalar(0, 124, 80), new Scalar(180, 255, 255), hsvOutput);
        // H 0 - 180
        // S 124 - 255
        // V 80 - 255

        //Step 4. Eroding our threshold (binary) image in order to reduce the number of contours.
        Mat erode = hsvOutput;
        Mat erodeKernel = new Mat();
        Point cvErodeAnchor = new Point(-1, -1);
        int cvErodeBordertype = Core.BORDER_DEFAULT;
        Scalar cvErodeBordervalue = new Scalar(-1);
        Imgproc.erode(erode, erodeOutput, erodeKernel, cvErodeAnchor, 6, cvErodeBordertype, cvErodeBordervalue);

        // Step 5. Finding the contours so we can use them to return the position of the cube.
        Mat findContours = erodeOutput;
        contours.clear(); //Clear any previous contours.
        Imgproc.findContours(findContours, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        findSquares(contours, 1000);
        if (goldFound) {
            telemetry.addData("Cube: ", Arrays.toString(gold3D.getPoints()));
        }
    }

    //Iterates through given contours and locates all of the square shapes of a certain size.
    private void findSquares(List<MatOfPoint> contours, double minArea) {
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint contour = contours.get(i);
            MatOfPoint2f arcL = new MatOfPoint2f(contour.toArray());
            double perimeter = Imgproc.arcLength(arcL, true);

            //Using perimeter to apply a 10% approximation to the points in the closed contours.
            Imgproc.approxPolyDP(arcL, arcL, .1 * perimeter, true);

            //Filtering by Area and shape.
            if (minArea < Imgproc.contourArea(contour) && arcL.size().height == 4) {
                goldFound = true;
                //Displaying our coordinates by multiplying our previously resized contour, finding the bounding box and the points.
                Core.multiply(contour, new Scalar(2, 2), contour);
                Rect rect = Imgproc.boundingRect(contour);
                //Finding first and second point of bounding box.
                Point xy1 = new Point(rect.x, rect.y);
                Point xy2 = new Point(rect.x + rect.width, rect.y + rect.height);
                //Returns the midpoint which consists of 2 doubles.
                goldMidpoint = new Point((xy1.x + xy2.x) / 2, (xy1.y + xy2.y) / 2);

                //In order to show all the cubes, change to a telemetry call.
                //Focal Length = 2622 [cm]
                //Formula to find depth = (5.08*2622)/Pixels OR (13,319.76)/Pixels of width
                //(Screen width x Distance)/ Focal Length =  Width in CM of screen at distance of cube
                double distanceZ = (13319.76) / rect.width;
                double ScreenWidthAtCube = (cameraDimensions.x * distanceZ) / 2622;

                //NEWX NEEDS TO BE FIXED AND THE EQUATION WILL WORK.
                double convRatioX = cameraDimensions.x / ScreenWidthAtCube;
                double newX = goldMidpoint.x * convRatioX;

                /*double hypotenuse = Math.sqrt((distanceZ * distanceZ) + (newX * newX)); UNNEEDED
                double angle = Math.acos(distanceZ / hypotenuse); HYPOTENUSE IS UNNEEDED */
                double angle = Math.toDegrees(Math.atan(newX / distanceZ));

                //Returning our (X, Z, Angle) Coordinates.
                gold3D = new Point3D(newX, distanceZ, angle);
            }
        }
    }

    //Returns the angle amount to turn. Used in relative turning.
    public double xMCP(double tolerance, double offset) {
        //The Pixel Value when gold is in the phone middle can be within tolerance.
        //Robot offset is the value are phone is offset when aimed at the cube.
        if (goldFound) {
            //Returns whether our object midpoints are the correct distance from each other. (Phone to Object)
            if (Math.abs(goldMidpoint.x - cameraMidpoint.x) - offset < tolerance) {
                return (goldMidpoint.x - cameraMidpoint.x - offset) / 7.1;
            }
        }
        return 0;
    }

    public boolean getGold() {
        return goldFound;
    }

    //Converting a bitmap received from Vuforia to a mat object we can utilize.
    public Mat getVuforia(Bitmap img) {
        Mat mat = new Mat();
        if (img != null) Utils.bitmapToMat(img, mat);
        return mat;
    }

    private void phoneMidpoint(Mat input) {
        cameraMidpoint = new Point(input.width() / 2.0, input.height() / 2.0);
    }

    private void phoneDimensions(Mat input) {
        cameraDimensions = new Point(input.width(), input.height());
    }

    private class Point3D {
        private double x;
        private double y;
        private double z;

        public Point3D() {
            this.x = 0;
            this.y = 0;
            this.z = 0;
        }

        public Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void setPoints(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double[] getPoints() {
            return new double[]{x, y, z};
        }
    }
}
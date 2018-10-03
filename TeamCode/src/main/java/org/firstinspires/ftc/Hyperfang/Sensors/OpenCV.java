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
import java.util.List;

public class OpenCV {

    private Mat frame;
    private Mat resizeOutput;
    private Mat blurOutput;
    private Mat hsvOutput;
    private Mat dilateOutput;
    private Mat erodeOutput;
    private Mat drawing;
    private Mat hierarchy;
    private List<MatOfPoint> contours = new ArrayList<>();

    private boolean goldFound = false;
    private Point cameraMidpoint;
    private Point cameraDimensions;

    public OpenCV() {
        // Loading the OpenCV core library
        System.loadLibrary("opencv_java3");

        //Preparing our matrice variables.
        resizeOutput = new Mat();
        blurOutput = new Mat();
        hsvOutput = new Mat();
        dilateOutput = new Mat();
        erodeOutput = new Mat();
        drawing = new Mat();
        hierarchy = new Mat();
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
        phoneDimensions(input);
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

        if (findSquares(contours, 1000) != null) {
            telemetry.addData("Cube:", findSquares(contours, 1000).toString());
        }
    }

    //Iterates through given contours and locates all of the square shapes of a certain size.
    private Point3D findSquares(List<MatOfPoint> contours, double minArea) {
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint contour = contours.get(i);
            MatOfPoint2f arcL = new MatOfPoint2f(contour.toArray());
            double perimeter = Imgproc.arcLength(arcL, true);

            //Using perimeter to apply a 10% approximation to the points in the closed contours.
            Imgproc.approxPolyDP(arcL, arcL, .1 * perimeter, true);
            //Focal Length = 2622 [cm]
            //Formula to find depth = (5.08*2622)/Pixels
            // OR (13,319.76)/Pixels of width
            //Filtering by Area and shape.
            if (minArea < Imgproc.contourArea(contour) && arcL.size().height == 4) {
                //Displaying our coordinates by multiplying our previously resized contour, finding the bounding box and the points.
                Core.multiply(contour, new Scalar(2, 2), contour);
                Rect rect = Imgproc.boundingRect(contour);
                //Finding first and second point of bounding box.
                Point xy1 = new Point(rect.x, rect.y);
                Point xy2 = new Point(rect.x + rect.width, rect.y + rect.height);
                //Returns the midpoint which consists of 2 doubles.
                //In order to show all the cubes, change to a telemetry call.
                // (Screen width x Distance)/ Focal Length =  Width in CM of screen at distance of cube
                double distanceZ = (13319.76)/rect.width;
                double ScreenWidthAtCube = (cameraDimensions.x*distanceZ)/2622;
                double convRatioX = cameraDimensions.x/ScreenWidthAtCube;
                double newX = ((xy1.x+xy2.x)/2)*convRatioX;
                double hypotenuse = Math.sqrt((distanceZ*distanceZ)+(newX*newX));
                double angle = Math.acos(distanceZ/hypotenuse);
                Point3D coord = new Point3D(newX, ((13319.76)/rect.width), angle);
                return coord;// memes / TESTING *dap*
                //return new Point((xy1.x + xy2.x) / 2, (xy1.y + xy2.y) / 2);
            }
        }
        return null;
    }

    private class Point3D{
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
            return new double[] {x,y,z};
        }
    }



    public Point phoneMidpoint(Mat input) {
        cameraMidpoint = new Point(input.width() / 2.0, input.height() / 2.0);
        return cameraMidpoint;
    }

    public Point phoneDimensions(Mat input) {
        cameraDimensions = new Point(input.width(), input.height());
        return cameraDimensions;
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
}
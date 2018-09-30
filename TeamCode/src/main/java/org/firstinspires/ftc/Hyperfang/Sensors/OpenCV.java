package org.firstinspires.ftc.Hyperfang.Sensors;

import android.graphics.Bitmap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
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
        Imgproc.findContours(findContours, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        findSquaresTest(contours, 1000, telemetry);
    }

    //Iterates through given contours and locates all of the square shapes of a certain size.
    private void findSquares(List<MatOfPoint> contours, double minArea) {
            for (int i = 0; i < contours.size(); i++) {
                MatOfPoint contour = contours.get(i);
                MatOfPoint2f arcL = new MatOfPoint2f(contour.toArray());
                double perimeter = Imgproc.arcLength(arcL, true);

                //Using perimeter to apply a 10% approximation to the points in the closed contours.
                Imgproc.approxPolyDP(arcL, arcL, .1 * perimeter, true);

                //Filtering by Area and Contour Shape.
                if (minArea < Imgproc.contourArea(contour) && arcL.size().height == 4) {}
            }
    }

    private void findSquaresTest(List<MatOfPoint> contours, double minArea, Telemetry telemetry) {
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint contour = contours.get(i);
            MatOfPoint2f arcL = new MatOfPoint2f(contour.toArray());
            double perimeter = Imgproc.arcLength(arcL, true);

            //Using perimeter to apply a 10% approximation to the points in the closed contours.
            Imgproc.approxPolyDP(arcL, arcL, .1 * perimeter, true);

            if (minArea < Imgproc.contourArea(contour) && arcL.size().height == 4) { //Filtering by Area and Contour Shape.
                    telemetry.addData("Shape", arcL.size().height);
            }
        }
    }

    //Converting a bitmap received from Vuforia to a mat object we can utilize.
    public Mat getVuforia(Bitmap img) {
        Mat mat = new Mat();
        if (img != null) Utils.bitmapToMat(img, mat);
        return mat;
    }
}
package org.firstinspires.ftc.Hyperfang.Sensors;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

public class Color {

    private NormalizedColorSensor colorSensor;
    private float[] hsvValues;
    private float[] rgbValues;

    public Color(OpMode opMode) {
        colorSensor = opMode.hardwareMap.get(NormalizedColorSensor.class, "color");
        hsvValues = new float[3];

        //Turn on the light to keep our measurements.
        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight)colorSensor).enableLight(true);
        }
    }

    public float[] getRGB() {
        NormalizedRGBA color = colorSensor.getNormalizedColors();
        float scale = 256; int min = 0, max = 255;
        float red = com.qualcomm.robotcore.util.Range.clip((int)(color.red   * scale), min, max);
        float green = com.qualcomm.robotcore.util.Range.clip((int)(color.green * scale), min, max);
        float blue = com.qualcomm.robotcore.util.Range.clip((int)(color.blue  * scale), min, max);
        rgbValues = new float[]{red, green, blue};
        return rgbValues;
    }

    public float getR() {
        return rgbValues[0];
    }
    public float getG() {
        return rgbValues[1];
    }
    public float getB() {
        return rgbValues[2];
    }

    public float[] getHSV() {
        float[] hsvValues = new float[3];
        NormalizedRGBA color = colorSensor.getNormalizedColors();
        android.graphics.Color.colorToHSV(color.toColor(), hsvValues);
        return hsvValues;
    }

    public float getH() {
        return hsvValues[0];
    }
    public float getS() {
        return hsvValues[1];
    }
    public float getV() {
        return hsvValues[2];
    }
}

package org.firstinspires.ftc.Hyperfang.Sensors;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MGL {

    private DigitalChannel MGL;

    public MGL(HardwareMap hardwareMap, String param) {
        MGL = hardwareMap.get(DigitalChannel.class, param);
        MGL.setMode(DigitalChannel.Mode.INPUT);
    }

    public boolean isTouched(){
        return !MGL.getState();
    }
}

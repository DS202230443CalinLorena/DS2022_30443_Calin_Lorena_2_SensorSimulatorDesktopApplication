package com.example.sensorsimulatorproducer;


import com.example.sensorsimulatorproducer.gui.SimulatorGUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class SensorSimulatorProducerApplication {

    public static void main(String[] args) throws IOException {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(SensorSimulatorProducerApplication.class, args);
        SimulatorGUI simulatorGUI = new SimulatorGUI();
        simulatorGUI.setVisible(true);
    }
}

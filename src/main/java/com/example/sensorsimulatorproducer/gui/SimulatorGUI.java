package com.example.sensorsimulatorproducer.gui;

import com.example.sensorsimulatorproducer.MQConfig;
import com.example.sensorsimulatorproducer.Message;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
public class SimulatorGUI extends JFrame {
    private JTextField deviceIdTextField;
    private JButton startButton;

    ApplicationContext ctx = new AnnotationConfigApplicationContext(MQConfig.class);
    RabbitTemplate template = ctx.getBean(RabbitTemplate.class);

    public SimulatorGUI() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 300);
        JPanel jPanel = new JPanel();
        jPanel.setForeground(Color.LIGHT_GRAY);
        jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(jPanel);
        jPanel.setLayout(null);

        JLabel jLabel = new JLabel("SENSOR SIMULATOR PRODUCER");
        jLabel.setForeground(Color.BLACK);
        jLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        jLabel.setBounds(30, 10, 350, 55);
        jPanel.add(jLabel);

        JLabel jLabel1 = new JLabel("Device id: ");
        jLabel1.setForeground(Color.BLACK);
        jLabel1.setFont(new Font("Times New Roman", Font.PLAIN, 19));
        jLabel1.setBounds(74, 83, 99, 55);
        jPanel.add(jLabel1);

        deviceIdTextField = new JTextField();
        deviceIdTextField.setForeground(Color.WHITE);
        deviceIdTextField.setBackground(Color.GRAY);
        deviceIdTextField.setBounds(180, 102, 141, 25);
        jPanel.add(deviceIdTextField);

        startButton = new JButton("Start simulation!");
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(Color.DARK_GRAY);
        startButton.setBounds(48, 212, 157, 31);
        jPanel.add(startButton);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Runnable helloRunnable = new Runnable() {
                    public void run() {
                        String deviceIdString = getDeviceIdTextField();
                        Long deviceId = Long.parseLong(deviceIdString);
                        Message message = new Message();
                        message.setTimestamp(Timestamp.from(Instant.now()));
                        message.setDeviceId(deviceId);
                        try {
                            publishMessage(message);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                executor.scheduleAtFixedRate(helloRunnable, 0, 10, TimeUnit.SECONDS);
            }
        });
    }

    public void publishMessage(Message message) throws IOException {
        CSVReader reader2 = new CSVReader(new FileReader("sensor.csv"));
        List<String[]> allElements = reader2.readAll();
        List<Double> allMeasurements = new ArrayList<>();
        for (String[] strings : allElements) {
            for (String s : strings) {
                allMeasurements.add(Double.parseDouble(s));
            }
        }
        message.setMeasurementValue(allMeasurements.get(0));
        allElements.remove(0);
        FileWriter sw = new FileWriter("sensor.csv");
        CSVWriter writer = new CSVWriter(sw);
        writer.writeAll(allElements);
        writer.close();
        template.convertAndSend(MQConfig.EXCHANGE,
                MQConfig.ROUTING_KEY, message);
    }

    public String getDeviceIdTextField() {
        return deviceIdTextField.getText();
    }
}

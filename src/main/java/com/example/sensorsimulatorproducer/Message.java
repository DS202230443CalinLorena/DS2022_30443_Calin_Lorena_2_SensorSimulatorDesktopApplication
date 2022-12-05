package com.example.sensorsimulatorproducer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {
    private Timestamp timestamp;
    private Long deviceId;
    private Double measurementValue;
}

package com.sparrowwallet.hummingbird;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class QRCodeEntity {

    private UR data;

    private String outputFilePath;

    private int interval;

    private int fragmentLength;

    public QRCodeEntity(UR data, String outputFilePath, int interval, int fragmentLength){
        this.data = data;
        this.outputFilePath = outputFilePath;
        this.interval = interval;
        this.fragmentLength = fragmentLength;
    }

}

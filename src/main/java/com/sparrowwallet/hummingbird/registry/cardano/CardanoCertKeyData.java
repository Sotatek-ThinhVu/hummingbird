package com.sparrowwallet.hummingbird.registry.cardano;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardanoCertKeyData {
    private String keyHash;
    private String keyPath;
    private String xfp;

    public CardanoCertKeyData(String keyHash, String keyPath,String xfp) {
        this.keyHash = keyHash;
        this.keyPath = keyPath;
        this.xfp = xfp;
    }
}

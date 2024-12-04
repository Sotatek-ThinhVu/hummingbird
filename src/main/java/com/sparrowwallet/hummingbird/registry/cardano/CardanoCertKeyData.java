package com.sparrowwallet.hummingbird.registry.cardano;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardanoCertKeyData {
    private String keyHash;
    private String keyPath;
    private String xfp;
}

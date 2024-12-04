package com.sparrowwallet.hummingbird.registry.cardano;

import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SignRequestProps {
    public final byte[] requestId;
    public final byte[] signData;
    public final List<CardanoUtxo> utxos;
    public final List<CardanoCertKey> extraSigners;
    public final String origin;

    public SignRequestProps(byte[] requestId, byte[] signData, List<CardanoUtxo> utxos,
                            List<CardanoCertKey> extraSigners, String origin) {
        this.requestId = requestId;
        this.signData = signData;
        this.utxos = utxos;
        this.extraSigners = extraSigners;
        this.origin = origin;
    }
}

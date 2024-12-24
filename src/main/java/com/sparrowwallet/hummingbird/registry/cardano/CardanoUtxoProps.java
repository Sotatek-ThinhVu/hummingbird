package com.sparrowwallet.hummingbird.registry.cardano;

import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CardanoUtxoProps {
    public final byte[] transactionHash;
    public final int index;
    public final String amount;
    public final CryptoKeypath keyPath;
    public final String address;

    public CardanoUtxoProps(byte[] transactionHash, int index, String amount,
                            CryptoKeypath keyPath, String address) {
        this.transactionHash = transactionHash;
        this.index = index;
        this.amount = amount;
        this.keyPath = keyPath;
        this.address = address;
    }
}

package com.sparrowwallet.hummingbird.registry.cardano;

import co.nstant.in.cbor.model.*;
import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CardanoUtxoData{
    public final String transactionHash;
    public final int index;
    public final String amount;
    public final String xfp;
    public final String hdPath;
    public final String address;

    public CardanoUtxoData(String transactionHash, int index, String amount,
                           String xfp, String hdPath, String address) {
        this.transactionHash = transactionHash;
        this.index = index;
        this.amount = amount;
        this.xfp = xfp;
        this.hdPath = hdPath;
        this.address = address;
    }
}

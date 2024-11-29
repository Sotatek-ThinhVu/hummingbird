package com.sparrowwallet.hummingbird.registry.cardano;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnsignedInteger;
import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

public class CardanoSignature extends RegistryItem {

    private static class Keys {
        public static final int REQUEST_ID = 1;
        public static final int SIGNATURE = 2;
    }

    private byte[] requestId;
    private byte[] witnessSet;

    public CardanoSignature(byte[] requestId, byte[] witnessSet) {
        this.requestId = requestId;
        this.witnessSet = witnessSet;
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public byte[] getWitnessSet() {
        return witnessSet;
    }


    @Override
    public RegistryType getRegistryType() {
        return RegistryType.CARDANO_SIGNATURE;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if (requestId != null){
            map.put(new UnsignedInteger(Keys.REQUEST_ID), new ByteString(requestId));
        }
        if (witnessSet != null){
            map.put(new UnsignedInteger(Keys.SIGNATURE), new ByteString(witnessSet));
        }
        return map;
    }

    public static CardanoSignature fromCbor(DataItem item) {
        byte[] requestId = null;
        byte[] witnessSet = null;
        Map map = (Map)item;
        for(DataItem key : map.getKeys()){
            UnsignedInteger uintKey = (UnsignedInteger)key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == Keys.REQUEST_ID){
                requestId = ((ByteString)map.get(key)).getBytes();
            } else if (intKey == Keys.SIGNATURE){
                witnessSet = ((ByteString)map.get(key)).getBytes();
            }
        }
        return new CardanoSignature(requestId, witnessSet);
    }
}

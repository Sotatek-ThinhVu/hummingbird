package com.sparrowwallet.hummingbird.registry.cardano;

import co.nstant.in.cbor.model.*;
import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import java.util.ArrayList;
import java.util.List;

public class CardanoSignRequest extends RegistryItem {

    private static class Keys {
        public static final int REQUEST_ID = 1;
        public static final int SIGN_DATA = 2;
        public static final int UTXOS = 3;
        public static final int EXTRA_SIGNERS = 4;
        public static final int ORIGIN = 5;
    }
    private String requestId;
    private byte[] signData;
    private List<CardanoUtxo> utxos;
    private List<CardanoCertKey> extraSigners;
    private String origin;

    public CardanoSignRequest(){

    }

    public CardanoSignRequest(String requestId, byte[] signData, List<CardanoUtxo> utxos, List<CardanoCertKey> extraSigners, String origin) {
        this.requestId = requestId;
        this.signData = signData;
        this.utxos = utxos;
        this.extraSigners = extraSigners;
        this.origin = origin;
    }

    public String getRequestId() {
        return requestId;
    }
    public byte[] getSignData() {
        return signData;
    }
    public List<CardanoUtxo> getUtxos() {
        return utxos;
    }
    public List<CardanoCertKey> getExtraSigners() {
        return extraSigners;
    }
    public String getOrigin() {
        return origin;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.CARDANO_SIGN_REQUEST;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if (requestId != null) {
            map.put(new UnsignedInteger(Keys.REQUEST_ID), new UnicodeString(requestId));
        }
        if (signData != null) {
            map.put(new UnsignedInteger(Keys.SIGN_DATA), new ByteString(signData));
        }
        Array array = new Array();
        for (CardanoUtxo utxo : utxos) {
            DataItem dataItem = utxo.toCbor();
            DataItem tag = dataItem.getTag() == null ? dataItem : dataItem.getTag();
            while (tag.getTag() != null) {
                tag = tag.getTag();
            }
            tag.setTag(RegistryType.CARDANO_UTXO.getTag());
            array.add(dataItem);
        }
        map.put(new UnsignedInteger(Keys.UTXOS), array);

        Array array2 = new Array();
        if (extraSigners != null){
            for (CardanoCertKey certKey : extraSigners) {
                DataItem dataItem = certKey.toCbor();
                DataItem tag = dataItem.getTag() == null ? dataItem : dataItem.getTag();
                while (tag.getTag() != null) {
                    tag = tag.getTag();
                }
                tag.setTag(RegistryType.CARDANO_CERT_KEY.getTag());
                array2.add(dataItem);
            }
            map.put(new UnsignedInteger(Keys.EXTRA_SIGNERS), array2);
        }
        map.put(new UnsignedInteger(Keys.ORIGIN), new UnicodeString(origin));
        return map;
    }

    public static CardanoSignRequest fromCbor(DataItem item) {
        String requestId = null;
        byte[] signData = null;
        List<CardanoUtxo> utxos = new ArrayList<>();
        List<CardanoCertKey> extraSigners = new ArrayList<>();
        String origin = null;
        Map map = (Map)item;
        for(DataItem key : map.getKeys()){
            UnsignedInteger uintKey = (UnsignedInteger)key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == Keys.REQUEST_ID){
                requestId = ((UnicodeString)map.get(key)).getString();
            } else if (intKey == Keys.SIGN_DATA){
                signData = ((ByteString)map.get(key)).getBytes();
            } else if (intKey == Keys.UTXOS){
                Array utxosArray = (Array)map.get(key);
                for (DataItem keyExp : utxosArray.getDataItems()){
                    utxos.add(CardanoUtxo.fromCbor(keyExp));
                }
            } else if (intKey == Keys.EXTRA_SIGNERS){
                Array extraSignersArray = (Array)map.get(key);
                for (DataItem keyExp : extraSignersArray.getDataItems()){
                    extraSigners.add(CardanoCertKey.fromCbor(keyExp));
                }
            } else if (intKey == Keys.ORIGIN){
                origin = ((UnicodeString)map.get(key)).getString();
            }
        }
        if (signData == null){
            throw new IllegalArgumentException("CardanoSignRequest signData is required");
        }
        if (utxos.isEmpty()){
            throw new IllegalArgumentException("CardanoSignRequest utxos is required");
        }
        return new CardanoSignRequest(requestId, signData, utxos, extraSigners, origin);
    }
}

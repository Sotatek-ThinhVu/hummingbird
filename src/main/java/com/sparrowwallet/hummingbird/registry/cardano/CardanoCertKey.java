package com.sparrowwallet.hummingbird.registry.cardano;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnsignedInteger;
import com.sparrowwallet.hummingbird.HexUtils;
import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;
import com.sparrowwallet.hummingbird.registry.pathcomponent.IndexPathComponent;
import com.sparrowwallet.hummingbird.registry.pathcomponent.PathComponent;

import java.util.ArrayList;
import java.util.List;

public class CardanoCertKey extends RegistryItem {

    private static class Keys {
        public static final int KEY_HASH = 1;
        public static final int KEY_PATH = 2;
    }

    private byte[] keyHash;
    private CryptoKeypath keyPath;

    public CardanoCertKey(byte[] keyHash, CryptoKeypath keyPath) {
        this.keyHash = keyHash;
        this.keyPath = keyPath;
    }

    // Constructor
    public CardanoCertKey(CardanoCertKeyProps props) {
        this.keyHash = props.getKeyHash();
        this.keyPath = props.getKeyPath();
    }

    public CryptoKeypath getKeyPath() {
        return keyPath;
    }

    public byte[] getKeyHash() {
        return keyHash;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.CARDANO_CERT_KEY;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();

        if (keyHash != null){
            map.put(new UnsignedInteger(Keys.KEY_HASH), new ByteString(keyHash));
        }
        if (keyPath != null){
            map.put(new UnsignedInteger(Keys.KEY_PATH), keyPath.toCbor());
        }
        return map;
    }

    public static CardanoCertKey fromCbor(DataItem item) {
        byte[] keyHash = null;
        CryptoKeypath keyPath = null;
        Map map = (Map)item;
        for(DataItem key : map.getKeys()){
            UnsignedInteger uintKey = (UnsignedInteger)key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == Keys.KEY_HASH){
                keyHash = ((ByteString)map.get(key)).getBytes();
            } else if (intKey == Keys.KEY_PATH){
                keyPath = CryptoKeypath.fromCbor(map.get(key));
            }
        }
        if (keyHash==null){
            throw new IllegalArgumentException("CardanoCertKey keyHash is required");
        }
        return new CardanoCertKey(keyHash, keyPath);
    }

    public static CardanoCertKey constructCardanoCertKey(CardanoCertKeyData data) {
        String[] paths = data.getKeyPath().replaceFirst("[mM]/", "").split("/");
        List<PathComponent> pathComponents = new ArrayList<>();

        for (String path : paths) {
            int index = Integer.parseInt(path.replace("'", ""));
            boolean isHardened = path.endsWith("'");
            pathComponents.add(new IndexPathComponent(index, isHardened));
        }

        CryptoKeypath hdpathObject = new CryptoKeypath(
                pathComponents,
                HexUtils.decodeHexString(data.getXfp()));

        return new CardanoCertKey(new CardanoCertKeyProps(
                HexUtils.decodeHexString(data.getKeyHash()),
                hdpathObject
        ));
    }
}

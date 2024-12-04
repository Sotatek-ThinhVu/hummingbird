package com.sparrowwallet.hummingbird.registry.cardano;

import co.nstant.in.cbor.model.*;
import com.sparrowwallet.hummingbird.HexUtils;
import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;
import com.sparrowwallet.hummingbird.registry.pathcomponent.IndexPathComponent;
import com.sparrowwallet.hummingbird.registry.pathcomponent.PathComponent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CardanoUtxo extends RegistryItem {

    public static final int TRANSACTION_HASH = 1;
    public static final int INDEX = 2;
    public static final int AMOUNT = 3;
    public static final int KEY_PATH = 4;
    public static final int ADDRESS = 5;

    private byte[] transactionHash;
    private Integer index;
    private String amount;
    private CryptoKeypath keyPath;
    private String address;

    public CardanoUtxo(byte[] transactionHash,
                       int index,
                       String amount,
                       CryptoKeypath keyPath,
                       String address) {
        this.transactionHash = transactionHash;
        this.index = index;
        this.amount = amount;
        this.keyPath = keyPath;
        this.address = address;
    }

    // Constructor
    public CardanoUtxo(CardanoUtxoProps props) {
        this.transactionHash = props.transactionHash;
        this.index = props.index;
        this.amount = props.amount;
        this.keyPath = props.keyPath;
        this.address = props.address;
    }

    public DataItem toCbor(){
        Map map = new Map();
        if (transactionHash != null){
            map.put(new UnsignedInteger(TRANSACTION_HASH), new ByteString(transactionHash));
        }
        if (index != null){
            map.put(new UnsignedInteger(INDEX), new UnsignedInteger(index));
        }
        if(amount != null){
            map.put(new UnsignedInteger(AMOUNT), new UnicodeString(amount));
        }
        // TODO: Currently, the value of the Keypath is a default value of m/1852'/1815'/0'/0/0.
        if (keyPath != null){
            DataItem item = keyPath.toCbor();
            item.setTag(RegistryType.CRYPTO_KEYPATH.getTag());
            map.put(new UnsignedInteger(KEY_PATH), item);
        }
        if (address != null){
            map.put(new UnsignedInteger(ADDRESS), new UnicodeString(address));
        }
        return map;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.CARDANO_UTXO;
    }

    public static CardanoUtxo fromCbor(DataItem item) {
        byte[] transactionHash = null;
        Integer index = null;
        String amount = null;
        CryptoKeypath keyPath = null;
        String address = null;

        Map map = (Map)item;

        for (DataItem key : map.getKeys()){
            UnsignedInteger uintKey = (UnsignedInteger)key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == TRANSACTION_HASH){
                transactionHash = ((ByteString)map.get(key)).getBytes();
            } else if (intKey == INDEX){
                index = ((UnsignedInteger)map.get(key)).getValue().intValue();
            } else if (intKey == AMOUNT){
                amount = ((UnicodeString)map.get(key)).getString();
            } else if (intKey == KEY_PATH){
                keyPath = CryptoKeypath.fromCbor(map.get(key));
            } else if (intKey == ADDRESS){
                address = ((UnicodeString)map.get(key)).getString();
            }
        }

        if (transactionHash == null){
            throw new IllegalStateException("Transaction hash is null");
        }

        return new CardanoUtxo(transactionHash, index, amount, keyPath, address);
    }

    public static CardanoUtxo constructCardanoUtxo(CardanoUtxoData data) {
        String[] paths = data.hdPath.replaceFirst("[mM]/", "").split("/");
        List<PathComponent> pathComponents = new ArrayList<>();

        for (String path : paths) {
            int index = Integer.parseInt(path.replace("'", ""));
            boolean isHardened = path.endsWith("'");
            pathComponents.add(new IndexPathComponent(index, isHardened));
        }

        CryptoKeypath hdpathObject = new CryptoKeypath(
                pathComponents,
                HexUtils.decodeHexString(data.getXfp())
        );

        return new CardanoUtxo(new CardanoUtxoProps(
                HexUtils.decodeHexString(data.transactionHash),
                data.index,
                data.amount,
                hdpathObject,
                data.address
        ));
    }
}

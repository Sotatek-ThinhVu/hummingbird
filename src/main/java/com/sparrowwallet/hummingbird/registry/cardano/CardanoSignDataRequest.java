//package com.sparrowwallet.hummingbird.registry.cardano;
//
//import co.nstant.in.cbor.model.*;
//import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
//import com.sparrowwallet.hummingbird.registry.RegistryItem;
//import com.sparrowwallet.hummingbird.registry.RegistryType;
//import com.sparrowwallet.hummingbird.registry.pathcomponent.IndexPathComponent;
//import com.sparrowwallet.hummingbird.registry.pathcomponent.PathComponent;
//import lombok.Getter;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//@Getter
//public class CardanoSignDataRequest extends RegistryItem {
//    private byte[] requestId;
//    private byte[] signData;
//    private CryptoKeypath derivationPath;
//    private String origin;
//    private byte[] xpub;
//
//    // Enum for keys
//    private enum Keys {
//        REQUEST_ID(1),
//        SIGN_DATA(2),
//        DERIVATION_PATH(3),
//        ORIGIN(4),
//        SIGN_TYPE(5),
//        XPUB(6);
//
//        private final int value;
//        Keys(int value) {
//            this.value = value;
//        }
//    }
//
//    // Constructor
//    public CardanoSignDataRequest(SignRequestProps props) {
//        this.requestId = props.requestId;
//        this.signData = props.signData;
//        this.derivationPath = props.derivationPath;
//        this.origin = props.origin;
//        this.xpub = props.xpub;
//    }
//
//    @Override
//    public RegistryType getRegistryType() {
//        return RegistryType.CARDANO_SIGN_DATA_REQUEST;
//    }
//
//    @Override
//    public DataItem toCbor() {
//        Map map = new Map();
//
//        if (requestId != null) {
//            map.put(new UnsignedInteger(Keys.REQUEST_ID.value), new ByteString(requestId));
//        }
//
//        if (origin != null) {
//            map.put(new UnsignedInteger(Keys.ORIGIN.value), new UnicodeString(origin));
//        }
//
//        map.put(new UnsignedInteger(Keys.SIGN_DATA.value), new ByteString(signData));
//
//        DataItem keyPath = derivationPath.toCbor();
//        keyPath.setTag(derivationPath.getRegistryType().getTag());
//        map.put(new UnsignedInteger(Keys.DERIVATION_PATH.value), keyPath);
//        map.put(new UnsignedInteger(Keys.XPUB.value), new ByteString(xpub));
//
//        return map;
//    }
//
//    public static CardanoSignDataRequest fromCbor(DataItem dataItem) {
//        Map map = (Map) dataItem;
//        byte[] signData = ((ByteString)map.get(new UnsignedInteger(Keys.SIGN_DATA.value))).getBytes();
//        CryptoKeypath derivationPath = CryptoKeypath.fromCbor(map.get(new UnsignedInteger(Keys.DERIVATION_PATH.value)));
//        byte[] requestId = map.containsKey(Keys.REQUEST_ID.value)
//                ? ((DataItem) map.get(Keys.REQUEST_ID.value)).getData()
//                : null;
//        String origin = map.containsKey(Keys.ORIGIN.value)
//                ? (String) map.get(Keys.ORIGIN.value)
//                : null;
//        byte[] xpub = map.get(Keys.XPUB.value);
//
//        return new CardanoSignDataRequest(new SignRequestProps(requestId, signData, derivationPath, origin, xpub));
//    }
//
//    public static CardanoSignDataRequest constructCardanoSignDataRequest(
//            String signData,
//            String hdPath,
//            String xfp,
//            String xpub,
//            String uuidString,
//            String origin) {
//
//        String[] paths = hdPath.replaceFirst("[mM]/", "").split("/");
//        List<PathComponent> pathComponents = new ArrayList<>();
//
//        for (String path : paths) {
//            int index = Integer.parseInt(path.replace("'", ""));
//            boolean isHardened = path.endsWith("'");
//            pathComponents.add(new IndexPathComponent(index, isHardened));
//        }
//
//        CryptoKeypath hdpathObject = new CryptoKeypath(pathComponents, hexStringToByteArray(xfp));
//
//        byte[] requestId = uuidString != null ? UUIDConverter.toBytes(UUID.fromString(uuidString)) : null;
//
//        return new CardanoSignDataRequest(new SignRequestProps(
//                requestId,
//                hexStringToByteArray(signData),
//                hdpathObject,
//                origin,
//                hexStringToByteArray(xpub)
//        ));
//    }
//
//    // Utility method to convert hex string to byte array
//    private static byte[] hexStringToByteArray(String s) {
//        int len = s.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
//                    + Character.digit(s.charAt(i + 1), 16));
//        }
//        return data;
//    }
//}

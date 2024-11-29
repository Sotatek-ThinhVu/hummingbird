package com.sparrowwallet.hummingbird;

import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.cardano.CardanoCertKey;
import com.sparrowwallet.hummingbird.registry.cardano.CardanoSignRequest;
import com.sparrowwallet.hummingbird.registry.cardano.CardanoUtxo;
import com.sparrowwallet.hummingbird.registry.pathcomponent.IndexPathComponent;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardanoSignRequestTest {

    // sender address
    String sender = "addr_test1qqzvhwaplxy8ml3g3urtmf30dya4zsc5q0nm4u9zjr7cldf6gdt45pxrz87w9ngx0s884jxh37um00zlwyezhvvqf4tsft0xl3";
    String receiver = "addr_test1qqc7z29zdcfywtvezv0wumftsuv2znhdhajgcag4x2p4gts4mdn6usvpjngsdm3sf89glhwx9rzwrpyxgc0yk3tnzvjqplx3ku";

    BigInteger minAda = BigInteger.valueOf(1000000);

//    String mnemonic = "element steak museum mind gown end beyond obey quiz about guard way bulb abstract brain bind need alarm vocal pulse believe then robot prosper";
//    String sender = new Account(Networks.preprod(), mnemonic).baseAddress();
//
//    String mnemonic1 = "meat assist village submit wild ginger flag theory venture matter corn increase kiwi affair hotel open axis mesh flame rigid case race sunny half";
//    String receiver = new Account(Networks.preprod(),mnemonic1).baseAddress();

    // using cardano client lib to get cbor hex
    // it's a simulation
    String cborHex = "84a40081825820cfdac8cf5359338e32af536de92274d1bb69685f3d1fd7b8e28f790d7c6140d701018282583900d88dda4d711126287fb27c066f76c712c649dac6c7f7b45ce1148a9cf90a93370a7fe38744784802d63d0059d2f25b618e7d4912063520271a000f42408258390004cbbba1f9887dfe288f06bda62f693b51431403e7baf0a290fd8fb53a43575a04c311fce2cd067c0e7ac8d78fb9b7bc5f71322bb1804d571b0000000129c38d4e021a00029c7d07582052e4a8c87121017f0cfbc0aadc2c9141d2da85fae53e14e73217ada4abdfbbeda0f5a11902a2a1636d73678178185468697320697320612074657374206d6573736167652032";


    @Test
    public void testGenerateCardanoSignRequest() {

        String requestId = UUID.randomUUID().toString();
        byte[] signData = HexUtils.decodeHexString(cborHex);
        List<CardanoUtxo> utxos = new ArrayList<>();
        List<CardanoCertKey> extraSigners = null;
        String origin = "cardano-wallet";

        CryptoKeypath cryptoKeypath = new CryptoKeypath(List.of(new IndexPathComponent(1852, false), new IndexPathComponent(1815, false), new IndexPathComponent(0, false), new IndexPathComponent(0, false), new IndexPathComponent(0, false)), HexUtils.decodeHexString("73c5da0a"));


        // send one ada
        CardanoUtxo utxo = new CardanoUtxo(HexUtils.decodeHexString("cfdac8cf5359338e32af536de92274d1bb69685f3d1fd7b8e28f790d7c6140d7"),1, minAda.toString(),cryptoKeypath,sender);
        utxos.add(utxo);
        CardanoSignRequest request = new CardanoSignRequest(requestId, signData, utxos, extraSigners, origin);
        QRCodeUtils.generateQRCode(new QRCodeEntity(request.toUR(), "cardano-sign-request.gif", 1000, 200));
    }
}

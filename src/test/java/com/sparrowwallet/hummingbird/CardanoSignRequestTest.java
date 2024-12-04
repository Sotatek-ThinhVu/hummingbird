package com.sparrowwallet.hummingbird;

import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.api.model.Amount;
import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.client.backend.blockfrost.common.Constants;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.quicktx.QuickTxBuilder;
import com.bloxbean.cardano.client.quicktx.Tx;
import com.bloxbean.cardano.client.spec.Era;
import com.bloxbean.cardano.client.util.Tuple;
import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.cardano.*;
import com.sparrowwallet.hummingbird.registry.pathcomponent.IndexPathComponent;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CardanoSignRequestTest {

    // sender address
//    String sender = "addr_test1qqzvhwaplxy8ml3g3urtmf30dya4zsc5q0nm4u9zjr7cldf6gdt45pxrz87w9ngx0s884jxh37um00zlwyezhvvqf4tsft0xl3";
    String receiver = "addr_test1qqc7z29zdcfywtvezv0wumftsuv2znhdhajgcag4x2p4gts4mdn6usvpjngsdm3sf89glhwx9rzwrpyxgc0yk3tnzvjqplx3ku";

    BigInteger minAda = BigInteger.valueOf(1000000);

    String mnemonic = "physical valid slush tone bean decrease melt grief panel found property doll hello unfold saddle clutch loud mobile fiscal dance sugar fork light mimic";
    String sender = "addr_test1qr77946683ay7ryl3jl3vrvlk47t208fyh5tk25f30nhv6mky48wymwl5taz4fsyaa9ecp4sqxn5d740c6kkw4577qsqxvk35m";

//    String mnemonic = "element steak museum mind gown end beyond obey quiz about guard way bulb abstract brain bind need alarm vocal pulse believe then robot prosper";
//    String sender = new Account(Networks.preprod(), mnemonic).baseAddress();
//
//    String mnemonic1 = "meat assist village submit wild ginger flag theory venture matter corn increase kiwi affair hotel open axis mesh flame rigid case race sunny half";
//    String receiver = new Account(Networks.preprod(),mnemonic1).baseAddress();

    private Tuple<String, List<Utxo>> buildTx() throws CborSerializationException {
        Tx tx1 = new Tx()
                .payToAddress(receiver, Amount.ada(1.5))
                .from(sender);

        var backendService = new BFBackendService(Constants.BLOCKFROST_PREPROD_URL, "preprod0xr6NRQNoIK8X9IvpkwiVfFF2tEIK27g");

        QuickTxBuilder quickTxBuilder = new QuickTxBuilder(backendService);
        var transaction = quickTxBuilder
                .compose(tx1)
                .feePayer(sender)
                .withSerializationEra(Era.Babbage)
                .build();

        var utxos = transaction.getBody()
                .getInputs()
                .stream()
                .map(input -> {
                    try {
                        return backendService.getUtxoService().getTxOutput(input.getTransactionId(), input.getIndex());
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                }).map(utxoResult -> utxoResult.getValue())
                .collect(Collectors.toList());

        return new Tuple<>(transaction.serializeToHex(), utxos);
    }

    @Test
    public void testGenerateCardanoSignRequest() throws CborSerializationException {

        String requestId = UUID.randomUUID().toString();
        var tuple = buildTx();
        byte[] signData = HexUtils.decodeHexString(tuple._1);
        List<CardanoUtxoData> utxos = new ArrayList<>();
        List<CardanoCertKeyData> extraSigners = new ArrayList<>();
        String origin = "cardano-wallet";

        CryptoKeypath cryptoKeypath = new CryptoKeypath(List.of(new IndexPathComponent(1852, false), new IndexPathComponent(1815, false), new IndexPathComponent(0, false), new IndexPathComponent(0, false), new IndexPathComponent(0, false)), HexUtils.decodeHexString("73c5da0a"));

        for (var utx: tuple._2) {
            CardanoUtxoData utxo = new CardanoUtxoData(utx.getTxHash(), utx.getOutputIndex(), utx.getAmount().get(0).getQuantity().toString() ,HexUtils.encodeHexString(cryptoKeypath.getSourceFingerprint()),cryptoKeypath.getPath(), sender);
            utxos.add(utxo);
        }

        CardanoSignRequest request = CardanoSignRequest.constructCardanoSignRequest(signData, utxos, extraSigners, requestId, origin);
        QRCodeUtils.generateQRCode(new QRCodeEntity(request.toUR(), "cardano-sign-request.gif", 1000, 300));
    }
}

package com.sparrowwallet.hummingbird;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnsignedInteger;
import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.api.model.Amount;
import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.blockfrost.common.Constants;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import com.bloxbean.cardano.client.common.cbor.CborSerializationUtil;
import com.bloxbean.cardano.client.common.model.Networks;
import com.bloxbean.cardano.client.crypto.bip32.HdKeyGenerator;
import com.bloxbean.cardano.client.crypto.bip32.HdKeyPair;
import com.bloxbean.cardano.client.crypto.bip39.MnemonicCode;
import com.bloxbean.cardano.client.crypto.bip39.MnemonicException;
import com.bloxbean.cardano.client.exception.CborDeserializationException;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.quicktx.QuickTxBuilder;
import com.bloxbean.cardano.client.quicktx.Tx;
import com.bloxbean.cardano.client.spec.Era;
import com.bloxbean.cardano.client.transaction.TransactionSigner;
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.bloxbean.cardano.client.transaction.spec.TransactionWitnessSet;
import com.bloxbean.cardano.client.util.Tuple;
import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.RegistryType;
import com.sparrowwallet.hummingbird.registry.cardano.*;
import com.sparrowwallet.hummingbird.registry.pathcomponent.IndexPathComponent;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CardanoSignRequestTest {

    // sender address
//    String sender = "addr_test1qqzvhwaplxy8ml3g3urtmf30dya4zsc5q0nm4u9zjr7cldf6gdt45pxrz87w9ngx0s884jxh37um00zlwyezhvvqf4tsft0xl3";
    String receiver = "addr_test1qqc7z29zdcfywtvezv0wumftsuv2znhdhajgcag4x2p4gts4mdn6usvpjngsdm3sf89glhwx9rzwrpyxgc0yk3tnzvjqplx3ku";

//    String mnemonic = "element steak museum mind gown end beyond obey quiz about guard way bulb abstract brain bind need alarm vocal pulse believe then robot prosper";
//    String sender = "addr_test1qr77946683ay7ryl3jl3vrvlk47t208fyh5tk25f30nhv6mky48wymwl5taz4fsyaa9ecp4sqxn5d740c6kkw4577qsqxvk35m";

    String mnemonic = "element steak museum mind gown end beyond obey quiz about guard way bulb abstract brain bind need alarm vocal pulse believe then robot prosper";
    Account accountSender = new Account(Networks.preprod(), mnemonic);
    String sender = accountSender.baseAddress();

//    String address = "addr1qyz85693g4fr8c55mfyxhae8j2u04pydxrgqr73vmwpx3azv4dgkyrgylj5yl2m0jlpdpeswyyzjs0vhwvnl6xg9f7ssrxkz90";
//
//    Account account = new Account(Networks.mainnet(),address);

//    String mnemonic1 = "meat assist village submit wild ginger flag theory venture matter corn increase kiwi affair hotel open axis mesh flame rigid case race sunny half";
//    String receiver = new Account(Networks.preprod(),mnemonic1).baseAddress();


    protected BackendService backendService;

    private Tuple<String, List<Utxo>> tuple = null;

    private Tuple<String, CardanoSignature> cardanoSignature = null;

    @Before
    public void setup(){
        backendService = new BFBackendService(Constants.BLOCKFROST_PREPROD_URL, "preprod0xr6NRQNoIK8X9IvpkwiVfFF2tEIK27g");
    }

    private Tuple<String, List<Utxo>> buildTx() throws CborSerializationException {
        Tx tx1 = new Tx()
                .payToAddress(receiver, Amount.ada(1.5))
                .from(sender);

        System.out.println(sender);

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

    private CardanoSignature getCardanoSignature(String cardanoSignUR) throws UR.URException {
        URDecoder decoder = new URDecoder();
        decoder.receivePart(cardanoSignUR);
        URDecoder.Result urResult = decoder.getResult();
        if (urResult.type == ResultType.SUCCESS){
            if (urResult.ur.getRegistryType().equals(RegistryType.CARDANO_SIGNATURE)){
                return (CardanoSignature) urResult.ur.decodeFromRegistry();
            }
        }
        return null;
    }

    @Test
    public void testGenerateCardanoSignRequest() throws CborSerializationException {
        String requestId = UUID.randomUUID().toString();
        var tuple = buildTx();
        byte[] signData = HexUtils.decodeHexString(tuple._1);
        System.out.println(HexUtils.encodeHexString(signData));
        List<CardanoUtxoData> utxos = new ArrayList<>();
        List<CardanoCertKeyData> extraSigners = new ArrayList<>();
        String origin = "cardano-wallet";

        // 1852H/1815H/0H/0/0
        CryptoKeypath cryptoKeypath = new CryptoKeypath(List.of(new IndexPathComponent(1852, true), new IndexPathComponent(1815, true), new IndexPathComponent(0, true), new IndexPathComponent(0, false), new IndexPathComponent(0, false)), HexUtils.decodeHexString("73c5da0a"));

        for (var utx: tuple._2) {
            CardanoUtxoData utxo = new CardanoUtxoData(utx.getTxHash(), utx.getOutputIndex(), utx.getAmount().get(0).getQuantity().toString() ,HexUtils.encodeHexString(cryptoKeypath.getSourceFingerprint()),cryptoKeypath.getPath(), sender);
            utxos.add(utxo);
        }

        CardanoSignRequest request = CardanoSignRequest.constructCardanoSignRequest(signData, utxos, extraSigners, requestId, origin);
        System.out.println(HexUtils.encodeHexString(request.toUR().getCborBytes()));
        QRCodeUtils.generateQRCode(new QRCodeEntity(request.toUR(), "cardano-sign-request.gif", 1000, 100));
    }

    @Test
    public void testSignAndSubmitToNode() throws CborSerializationException, ApiException, CborDeserializationException, MnemonicException.MnemonicWordException, MnemonicException.MnemonicChecksumException, MnemonicException.MnemonicLengthException, UR.URException, CborException {

        var backendService = new BFBackendService(Constants.BLOCKFROST_PREPROD_URL, "preprod0xr6NRQNoIK8X9IvpkwiVfFF2tEIK27g");

        // it's the signData field from the CardanoSignRequest
        String txnHex = "84a30081825820cfdac8cf5359338e32af536de92274d1bb69685f3d1fd7b8e28f790d7c6140d70301828258390031e128a26e12472d99131eee6d2b8718a14eedbf648c75153283542e15db67ae418194d106ee3049ca8fddc628c4e18486461e4b457313241a0016e3608258390004cbbba1f9887dfe288f06bda62f693b51431403e7baf0a290fd8fb53a43575a04c311fce2cd067c0e7ac8d78fb9b7bc5f71322bb1804d571a4a5c97ab021a00028fc5a0f5f6";

        // When scanning the QR code after signing the transaction on the device, we can retrieve the Cardano signature
        String cardanoSignUR = "ur:cardano-signature/oeadtpdagmbkoemnhsynasctfzbbyattwsothnurvoemltaofxoyaelabemwjngw";

        CardanoSignature signature = getCardanoSignature(cardanoSignUR);

        Transaction transaction = Transaction.deserialize(HexUtils.decodeHexString(txnHex));

        DataItem witnessDI = CborSerializationUtil.deserialize(signature.getWitnessSet());
        TransactionWitnessSet walletWitnessSet = TransactionWitnessSet.deserialize((Map) witnessDI);

        if(transaction.getWitnessSet() == null){
            transaction.setWitnessSet(new TransactionWitnessSet());
        }
        if (transaction.getWitnessSet().getVkeyWitnesses() == null){
            transaction.getWitnessSet().setVkeyWitnesses(new ArrayList<>());
        }

        transaction.getWitnessSet().getVkeyWitnesses().addAll(walletWitnessSet.getVkeyWitnesses());

        byte[] entropy = MnemonicCode.INSTANCE.toEntropy(mnemonic);

        HdKeyGenerator hdKeyGenerator = new HdKeyGenerator();
        HdKeyPair keyPair = hdKeyGenerator.getRootKeyPairFromEntropy(entropy);

        Transaction signedTxn = TransactionSigner.INSTANCE.sign(transaction, keyPair);

        Result<String> result =  backendService.getTransactionService().submitTransaction(signedTxn.serialize());

        if (result.isSuccessful()){
            System.out.println("Transaction submitted successfully");
        } else {
            System.out.println("Transaction submission failed: " + result.getResponse());
        }
    }

    @Test
    public void testGetCardanoSignRequest() throws UR.URException, CborException, CborDeserializationException {
        String cardanoSignUR = "ur:cardano-sign-request/oxadtpdagdlgwdasvtbstygttpqdsngtdreokobdssaohdsrlroxaelylfhdcxwydtztctisnlcydwnbcaimtbahttettpwpfyrkhkhyoneymwcfnbvacwvskkjnmeadadlfoeaehdesaeehvydeoejtbgfldpnlbwckwyjndnltcsoyglwersielkkpbzeylsghdmbzuyioplfplymwttamwydygasgmyutswdessvylrlnfgckgrfejkbwdkadcyaebsfwfzoeaehdesaeaasbrkoyytlokizedemyamryoldlinfrgyfxbbaxvdrdwtoemhzcmyreftfxhghtaasrbyztvosnamkebaknsptsmyrhrlrfhejseydnpalagthgadcwaeaeaeaddtjnaxpkaocyaeaomodpaxcyaapycwmynbykynaxlytaaynlonadhdcxwydtztctisnlcydwnbcaimtbahttettpwpfyrkhkhyoneymwcfnbvacwvskkjnmeaoadaximeeeseseheheeendydyemaataaddyoeadlecfatfnykcfatchykaeykaewkaewkaocyvddmwlskahksjzhsieiejphejyihjkjyehjsjsknkoiskthsjojzkskketjnjzeoioeokpjpjyjniyeodyiekkhseeknjkiaecjsdyjtjneekpesknimjpemiajzieiyenioiejyeeecjoksjpknetemktesjtioksdyjketeteeimksiseoemkpjndydyknjzktkkihkniskokojsiyeejyjkiyjydyksjzeoaaladsdmdkvl";
        UR decoder = URDecoder.decode(cardanoSignUR);
        byte[] data = decoder.getCborBytes();
        List<DataItem> items = CborDecoder.decode(data);
        CardanoSignRequest cardanoSignRequest = CardanoSignRequest.fromCbor(items.get(0));
        System.out.println(HexUtils.encodeHexString(cardanoSignRequest.getSignData()));
//        Transaction transaction = Transaction.deserialize(cardanoSignRequest.getSignData());
    }
}

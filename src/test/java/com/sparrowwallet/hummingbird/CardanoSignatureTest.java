package com.sparrowwallet.hummingbird;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import com.bloxbean.cardano.client.common.cbor.CborSerializationUtil;
import com.bloxbean.cardano.client.exception.CborDeserializationException;
import com.bloxbean.cardano.client.transaction.spec.TransactionWitnessSet;
import com.bloxbean.cardano.client.util.HexUtil;
import com.sparrowwallet.hummingbird.registry.RegistryType;
import com.sparrowwallet.hummingbird.registry.cardano.CardanoSignRequest;
import com.sparrowwallet.hummingbird.registry.cardano.CardanoSignature;
import org.junit.Test;

import java.util.List;

public class CardanoSignatureTest {
    @Test
    public void testCardanoSignature() throws UR.URException, CborException {
        String ur = "ur:cardano-signature/oeadtpdagdiehgkgeofrlegainmucwcxnltoutyksraohdisoyaelylfhdcxmuhfcyaazmoypajodrsefthfrpfpfzoyahetlrsffxftjlahwfttenswimltintohdfzhycfyafxdrincsdlmyhldkbzimdiksbblfykfzbbtyvatelsfxylbwemecidwmlkcppdiycahyvdrdtlzofrfpkgckesmymyditlgljswmbgldtdmkpfbystpdmsjtbnfmeeskwl";
        URDecoder decoder = new URDecoder();
        decoder.receivePart(ur);
        URDecoder.Result urResult = decoder.getResult();
        if (urResult.type == ResultType.SUCCESS){
            if (urResult.ur.getRegistryType().equals(RegistryType.CARDANO_SIGNATURE)){
                CardanoSignRequest cardanoSignRequest = (CardanoSignRequest) urResult.ur.decodeFromRegistry();
            }
        }
    }

    @Test
    public void testCardanoSignRequest() throws UR.InvalidCBORException {
        String ur = "ur:cardano-sign-request/oxadtpdagdisosfsrscwvlfxcplesnzmynfzndbngeaohdsrlroxaelylfhdcxpssglkqzgyurtandbtnyhpihptbsdkgmzsykbweottlecnbgwpdpcnhfkghkkbihadadlfoeaehdesaeehvydeoejtbgfldpnlbwckwyjndnltcsoyglwersielkkpbzeylsghdmbzuyioplfplymwttamwydygasgmyutswdessvylrlnfgckgrfejkbwdkadcyaecklrlaoeaehdesaeaasbrkoyytlokizedemyamryoldlinfrgyfxbbaxvdrdwtoemhzcmyreftfxhghtaasrbyztvosnamkebaknsptsmyrhrlrfhejseydnpalagthgadcwaeaeaeaddtmhpslraocyaeaomodpaxcyaaosecgmnbykynaxlytaaynlonadhdcxpssglkqzgyurtandbtnyhpihptbsdkgmzsykbweottlecnbgwpdpcnhfkghkkbihaoadaximeeeseseeeeeteyeseseoaataaddyoeadlecfatfnykcfatchykaeykaewkaewkaocyvddmwlskahksjzhsieiejphejyihjkjyehjsjsknkoiskthsjojzkskketjnjzeoioeokpjpjyjniyeodyiekkhseeknjkiaecjsdyjtjneekpesknimjpemiajzieiyenioiejyeeecjoksjpknetemktesjtioksdyjketeteeimksiseoemkpjndydyknjzktkkihkniskokojsiyeejyjkiyjydyksjzeoaalaoswlcebw";
        URDecoder urDecoder = new URDecoder();
        urDecoder.receivePart(ur);
        URDecoder.Result urResult = urDecoder.getResult();
        if (urResult.type == ResultType.SUCCESS){
            if (urResult.ur.getRegistryType().equals(RegistryType.CARDANO_SIGN_REQUEST)){
                CardanoSignRequest cardanoSignRequest = (CardanoSignRequest) urResult.ur.decodeFromRegistry();
                System.out.println(cardanoSignRequest.toString());
            }
        }
    }

    @Test
    public void myTest() throws CborDeserializationException {
        String walletWitnessHex = "a1008182582093561a04ffa1b1702ac13a56b64140a1053884cc433a6f05f3d136c66a8769ce58405e19f8432a69182f8f5d24156a27781482f54014d4e6d38343f713373562eb8c22a8661d5ee7bad5fb3b417b1e398f8f27d54e71eb1289d298b011c7a8976e0c";
        DataItem witnessDI = CborSerializationUtil.deserialize(HexUtil.decodeHexString(walletWitnessHex));
        TransactionWitnessSet walletWitnessSet = TransactionWitnessSet.deserialize((Map) witnessDI);
        System.out.println(walletWitnessSet);
    }
}

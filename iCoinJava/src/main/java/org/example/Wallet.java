package org.example;
import java.security.*;
import java.security.spec.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

// 椭圆曲线工具类
class ECtool {
    private ECGenParameterSpec ecGenSpec; // 椭圆曲线对象
    private KeyFactory keyFactory; // 密钥工厂
    private KeyPairGenerator keyPairGenerator;//密钥生成器
    private KeyPair keyPair;

    public ECtool() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        this.ecGenSpec = new ECGenParameterSpec("secp256k1");
        this.keyFactory = KeyFactory.getInstance("ECDSA", "BC");

        this.keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
        keyPairGenerator.initialize(this.ecGenSpec, new SecureRandom());
        this.keyPair = keyPairGenerator.generateKeyPair(); // 获取密钥对
    }

    //获取私钥
    public PrivateKey getPrivateKey() {
        return this.keyPair.getPrivate();
    }

    //获取公钥
    public PublicKey getPublicKey() {
        return this.keyPair.getPublic();
    }

    // 根据字符串生成公钥对象
    public PublicKey generatePublicKey(String publicKeyStr) throws Exception {
        byte[] publicKeyBytes = Hex.decode(publicKeyStr);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(publicKeySpec);
    }

    // 根据字符串生成私钥对象
    public PrivateKey generatePrivateKey(String privateKeyStr) throws Exception {
        byte[] privateKeyBytes = Hex.decode(privateKeyStr);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(privateKeySpec);
    }
}

public class Wallet {
    public PrivateKey privateKey; // 私钥
    public PublicKey publicKey; // 公钥

    private Signature signatureObj; // 签名

    public Wallet() throws Exception {
        ECtool eCtool = new ECtool();

        this.privateKey = eCtool.getPrivateKey(); // 私钥
        this.publicKey = eCtool.getPublicKey(); // 公钥

        this.signatureObj = Signature.getInstance("SHA256withECDSA", "BC");
        signatureObj.initSign(privateKey); // 用私钥生成数字签名
    }

    // 构造函数，接受已知的公钥和私钥字符串
    public Wallet(String publicKeyStr, String privateKeyStr) throws Exception {
        ECtool eCtool = new ECtool();
        this.publicKey = eCtool.generatePublicKey(publicKeyStr);
        this.privateKey = eCtool.generatePrivateKey(privateKeyStr);

        this.signatureObj = Signature.getInstance("SHA256withECDSA", "BC");
        signatureObj.initSign(privateKey); // 用私钥生成数字签名
    }

    //用文档哈希生成签名
    public byte[] getSignatureStr(byte[] docHash) throws SignatureException {
        //用文档哈希生成签名,该 signature 对象是由私钥生成的
        this.signatureObj.update(docHash);
        byte[] signatureBytes = signatureObj.sign();//获取签名
        return signatureBytes;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "publicKeyHex='" + Hex.toHexString(publicKey.getEncoded()) + '\'' +
                '}';
    }
}

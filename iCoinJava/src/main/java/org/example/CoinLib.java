package org.example;

import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

//常用工具函数库
public class CoinLib {
    public static String hash256_hex_string(String input) {
        String result = "";
        try {
            // 创建SHA-256消息摘要对象
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 更新消息摘要对象的输入
            digest.update(input.getBytes());

            // 计算哈希值
            byte[] hash = digest.digest();

            // 将哈希值转换为十六进制字符串表示
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // 返回生成的SHA-256哈希值
            result = (hexString.toString());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 algorithm not found.");
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] hash256_bytes(String str) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] hashedStr = sha256.digest(str.getBytes());
        return hashedStr;
    }

    //签名验证，使用发送者的公钥，验证该笔转账是否由发送者签发
    public static boolean verifySign(TransactionWithSign transactionWithSign, PublicKey senderPublicKey) throws Exception {
        //使用公钥验证签名
        Signature verifier = Signature.getInstance("SHA256withECDSA", "BC");//验证器
        verifier.initVerify(senderPublicKey);//使用公钥生成验证器
        verifier.update(transactionWithSign.transactionHash);//加载验证的对象的哈希到验证器中
        return verifier.verify(transactionWithSign.signature);//用签名验证是否有效
    }

    public static String hashtoHexString(byte[] hash) {
        if (hash == null) {
            return "null";
        }
        return Hex.toHexString(hash);
    }

    public static String publicKeytoHexString(PublicKey publicKey) {
        if (publicKey == null) {
            return "null";
        }
        return Hex.toHexString(publicKey.getEncoded());
    }

    public static String privateKeytoHexString(PrivateKey privateKey) {
        if (privateKey == null) {
            return "null";
        }
        return Hex.toHexString(privateKey.getEncoded());
    }


    //判断 hash 是否满足难度 target 条件，如果 hash 小于或等于 target，那么返回 true
    public static boolean isHashUnderTarget(byte[] hash, BigInteger target) {
        BigInteger hashValue = new BigInteger(1, hash);// 传入1表示正数

        // 比较哈希值和目标难度
        return hashValue.compareTo(target) <= 0 ;
    }

    //使用交易的签名判断该笔交易是否在一个交易列表中
    public static boolean signatureInTWSlist(ArrayList<TransactionWithSign> twsList, byte[] signature_) {
        for (TransactionWithSign tws : twsList) {//遍历查找交易列表
            if (Arrays.equals(tws.signature, signature_)) {//该笔交易的签名等于查找的签名
                return true;//找到了
            }
        }
        return false;//没找到，该笔交易不在列表中
    }


}

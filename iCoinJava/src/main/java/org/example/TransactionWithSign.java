package org.example;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;

class Transaction implements Serializable {
    private static final long serialVersionUID = 1L; // 序列化版本号，用于反序列化时验证
    public PublicKey from;//转账发起者的公钥
    public PublicKey to;//转账目的地的公钥
    public Double amount;//转账金额

    /**
     * 一笔转账交易，从 from 转给 to，金额为 amount
     * @param from_ 转账发起者
     * @param to_ 收款人
     * @param amount 金额
     */
    public Transaction(PublicKey from_, PublicKey to_, Double amount) {
        this.from = from_;
        this.to = to_;
        this.amount = amount;
    }

    //coinbase 交易，只需填入收款者的公钥
    public Transaction(Wallet minerWallet, Double amount) {
        this.from = null;
        this.to = minerWallet.publicKey;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "from=" +  CoinLib.publicKeytoHexString(from) +
                ", to=" + CoinLib.publicKeytoHexString(to) +
                ", amount=" + amount +
                '}';
    }
}

public class TransactionWithSign {
    public Transaction transaction;//转账
    public byte[] transactionHash;//该笔转账的哈希
    public byte[] signature;//转出人对该笔转账的签名
    public Double gas;//gas 费，即转账手续费，支付给矿工的，一个大于 0 的数
    public boolean isUTXO ;//UTXO即是Unspent Transaction Output，未花费的交易输出，标记该转账是否已被使用

    //创世区块包含的转账，是一笔特殊的转账
    public TransactionWithSign() {
        this.gas = null;
        this.transaction = null;//拷贝交易记录
        this.transactionHash = null;//这笔转账的哈希
        this.signature = null;//使用发送者的钱包进行签名
        this.isUTXO = true;//
    }

    //coinbase转账,0 gas fee,由区块链网络发出
    public TransactionWithSign(PublicKey senderPK, PublicKey receiverPK, Double amount) throws NoSuchAlgorithmException, SignatureException {
        this.gas = 0.0;//由区块链发起的转账无需 gas 费
        this.transaction = new Transaction(senderPK, receiverPK, amount);

        GsonTool gsonTool = new GsonTool();
        this.transactionHash = CoinLib.hash256_bytes(gsonTool.transaction2Json(transaction));//计算该笔转账的哈希
        //由区块链发起的转账无需签名
        this.signature = null;
        this.isUTXO = true;
    }

    /**
     *  新建转账需要转账发起人的钱包参与签名
     *
     * @param senderWallet 转账发起者的钱包，用于签名，过程中有提取私钥进行签名的过程，所以需要在可信环境下运行
     * @param transaction_ 被签名的转账交易
     * @param gas_ 该转账的 gas 费
     */
    public TransactionWithSign(Wallet senderWallet, Transaction transaction_, Double gas_) throws Exception {
        this.gas = gas_;
        this.transaction = new Transaction(transaction_.from, transaction_.to, transaction_.amount);//拷贝交易记录
        GsonTool gsonTool = new GsonTool();
        this.transactionHash = CoinLib.hash256_bytes(gsonTool.transaction2Json(transaction));//计算该笔转账的哈希
        this.signature = senderWallet.getSignatureStr(transactionHash);//使用发送者的钱包进行签名
        this.isUTXO = true;//UTXO 初始状态为 true
    }

    @Override
    public String toString() {
        return "TransactionWithSign{" +
                "transaction=" + transaction +
                ", transactionHash=" + CoinLib.hashtoHexString(transactionHash) +
                ", signature=" + CoinLib.hashtoHexString(signature) +
                ", gas=" + gas +
                '}';
    }
}
package org.example;

//Unspent Transaction Output，未花费的交易输出，类似一个坐标，用于区块链验证时定位交易
public class UTXO {
    public int height;//该交易所在的高度，即第几个区块
    public byte[] transactionHash;//该笔交易的签名

    public UTXO(int height_, byte[] transactionHash_) {
        this.height = height_;
        this.transactionHash = transactionHash_;
    }

    @Override
    public String toString() {
        return "UTXO{" +
                "height=" + height +
                ", transactionHash=" +  CoinLib.hashtoHexString(transactionHash) +
                '}';
    }
}

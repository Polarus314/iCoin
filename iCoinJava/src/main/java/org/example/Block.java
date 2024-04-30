package org.example;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;


public class Block {
    public String version;//版本号
    public byte[] previousHash;//前一个区块的哈希
    public byte[] blockHash;// 本区块的哈希
    public long nonce;//随机数
    public Instant timestamp; //时间戳，该区块的生成时间
    public BigInteger target;//PoW难度
    public PublicKey coinbase;//矿工的地址
    public int height;//高度，即第几个区块
    public LinkedList<TransactionWithSign> twsList;//区块中包含的转账交易


    //创世区块的构造方法，生成出的区块没有previousHash，因为是自动生成，所以没有矿工地址，区块高度为 0
    public Block(String version_, ArrayList<TransactionWithSign> twsList_, BigInteger target_) throws NoSuchAlgorithmException {
        this.version = new String(version_);
        this.previousHash = null;
        this.timestamp = Instant.now();
        this.target = target_.abs();
        this.coinbase = null;
        this.height = 0;
        this.twsList = new LinkedList<>(twsList_);
        this.nonce = 0;


        //在计算创世区块的哈希时，计算时无需放入 previousHash、coinbase
        this.blockHash = CoinLib.hash256_bytes(
                this.version +
                    this.nonce +
                    this.timestamp.toString() +
                    this.target.toString() +
                    this.height +
                    this.twsList
        );//该区块的哈希值

        //挖矿，直到满足条件，条件是 hash 小于 target
        while (!CoinLib.isHashUnderTarget(this.blockHash, this.target)) {
            nonce++;
            this.blockHash = CoinLib.hash256_bytes(
                this.version +
                    this.nonce +
                    this.timestamp.toString() +
                    this.target.toString() +
                    this.height +
                    this.twsList
            );//该区块的哈希值
        }

    }

    //在区块链中生成区块的构造方法
    public Block(String version_, byte[] previousHash_, BigInteger target_, PublicKey coinbase_,
                 int height_, LinkedList<TransactionWithSign> twsList_) throws NoSuchAlgorithmException {
        this.version = version_;
        this.previousHash = previousHash_;
        this.target = target_.abs();
        this.coinbase = coinbase_;
        this.height = height_;
        this.twsList = new LinkedList<>(twsList_);

        this.timestamp = Instant.now();
        this.nonce = 0;
        this.blockHash = this.mine();
    }

    //计算该区块自身的哈希
    public byte[] computeBlockHash() throws NoSuchAlgorithmException {
        // 使用成员变量计算哈希值
        return CoinLib.hash256_bytes(
                        version +
                        CoinLib.hashtoHexString(previousHash) +
                        target.toString() +
                        CoinLib.publicKeytoHexString(coinbase) +
                        height +
                        twsList +
                        timestamp.toString() +
                        nonce
                );
    }

    //挖矿，PoW 算法，返回的是满足难度要求的哈希
    public byte[] mine() throws NoSuchAlgorithmException {
        byte[] hash = this.computeBlockHash();//该区块的哈希值
        while (!CoinLib.isHashUnderTarget(hash, this.target)) {
            nonce++;
            hash = this.computeBlockHash();
        }
        return hash;//返回满足难度要求的哈希
    }

    @Override
    public String toString() {
        return "Block{" +
                "version='" + version + '\'' +
                ", previousHash=" + CoinLib.hashtoHexString(previousHash) +
                ", blockHash=" + CoinLib.hashtoHexString(blockHash) +
                ", nonce=" + nonce +
                ", timestamp=" + timestamp.toString() +
                ", target=" + target.toString() +
                ", coinbase=" + CoinLib.publicKeytoHexString(coinbase) +
                ", height=" + height +
                ", twsList=" + twsList +
                "}" ;
    }
}

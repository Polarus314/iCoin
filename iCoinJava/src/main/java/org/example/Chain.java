package org.example;

import java.math.BigInteger;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Chain {
    public String version;//版本号
    public ArrayList<Block> blockChain;//区块链
    public LinkedList<TransactionWithSign> transactionPool;//转账池
    public double minerReward;//挖矿奖励
    public BigInteger target;//PoW难度//
    public int height;//高度，即有多少区块

    //区块链的初始化过程，区块链的冷启动是通过挖出空白区块————也就是只包含 coinbase 交易的区块完成的
    public Chain() throws Exception {
        this.blockChain = new ArrayList<>();//区块链初始化
        this.transactionPool = new LinkedList<>();//转账池初始化
        this.minerReward = 1024.0;//初始奖励为 1024
        this.height = 0;//初始高度为 0
        this.version = "0.0.1";
        this.target = new BigInteger(
                "1234567890" +
                    "1234567890" +
                    "1234567890" +
                    "1234567890" +
                    "1234567890" +
                    "1234567890" +
                    "1234567890" +
                    "12"
        );//初始的时候指定一个难度，是一个大整数，生成的 hash 需要小于这个难度才算有效

        this.blockChain.add(this.bigBang());
    }


    //生成创世区块
    public Block bigBang() throws Exception {
        TransactionWithSign tws = new TransactionWithSign();//第一笔转账是一次特殊转账，没有发送者和收款人
        ArrayList<TransactionWithSign> twsList = new ArrayList<>();
        twsList.add(tws);

        return new Block(this.version, twsList, this.target);
    }

    /**
     * 申请添加一项交易到转账池，转账者还需要提供他的 UTXO 以证明自己的确有大于转出金额的Unspent Transaction Output，未花费的交易输出
     * 在添加时还需要区块链进行验证，如果返回 false 则是被区块链拒绝
     *
     * @param tws   申请加入转账池的转账交易
     * @param utxos 转账发起人的未花费的交易输出的集合，即余额声明
     * @return 返回 true 即完成加入转账池的操作，否则返回 false 表示拒绝
     */
    public boolean addTransaction2poolWithVerify(TransactionWithSign tws, ArrayList<UTXO> utxos) throws Exception {//将转账需求加进转账池中，等待矿工打包成区块上链
        if (utxos == null || tws == null) {//没有 UTXO,Unspent Transaction Output，未花费的交易输出
            return false;//表示拒绝
        }
        if (tws.transaction.from == null || tws.transaction.to == null) {
            return false;//拒绝非法交易，coinbase 交易只能通过网络发出
        }
        if (!CoinLib.verifySign(tws, tws.transaction.from)) {
            return false;//该交易 t 不是由发送者签发的，无法通过验证
        }
        if (tws.gas <= 0.0) {
            return false;//该交易的 gas 费小于等于 0，拒绝
        }

        Double utxoSum = this.sumWithVerifyUTXO(utxos, tws.transaction.from);//记录总共声明的未花费输出,并验证转账发起者的 UTXO 的有效性
        if (utxoSum.compareTo(-1.0) == 0) {//UTXO 链上查询出错，拒绝申请
             return false;
        }

        //如果声明的 utxo 总和小于该笔交易的 amount 和 gas 总和，则无法完成支付
        if (utxoSum.compareTo(tws.transaction.amount + tws.gas) < 0) {
            return false;
        }

        //开始进行找零过程，找零交易是无需验证 utxo 的，因为前面已经做过验证了，可以视为销毁了之前的 UTXOs 之后，增发了新的 UTXO，并且两者等量
        TransactionWithSign changeTWS;//找零交易
        Wallet changeWallet;//一个新的临时找零钱包
        //如果 utxoSum 刚好等于 amount 和 gas 之和，那么无需找零
        if (utxoSum.compareTo(tws.transaction.amount + tws.gas) == 0) {

        } else {//utxoSum支付完 amount 和 gas 之后还有剩余，需要区块链发起找零转账
            changeWallet = new Wallet();//找零钱包
            changeTWS = new TransactionWithSign(
                    changeWallet,
                    new Transaction(
                            changeWallet.publicKey,
                            tws.transaction.from,
                            utxoSum - tws.transaction.amount - tws.gas
                    ),
                    0.0
            );
            this.transactionPool.add(changeTWS);//将找零交易添加到转账池中
        }

        //将验证过的交易添加到转账池中
        this.transactionPool.add(tws);

        //把验证过的 UTXO 标记为已使用
        this.markUTXO(utxos);

        return true;//完成流程
    }

    //在挖矿者的终端上运行，挖矿，将转账池中的转账部分或全部添加到区块，并将区块添加到区块链上（需满足难度要求）
    public void mine(PublicKey minerPK) throws Exception {
        //计算要打包的所有区块的 gas 费总和
        Double rewardSum = Double.valueOf("0.0");
        for (TransactionWithSign tws : this.transactionPool) {
            rewardSum += tws.gas;
        }
        rewardSum += this.minerReward;

        Wallet rewardWallet = new Wallet();
        TransactionWithSign rewardTWS = new TransactionWithSign(rewardWallet.publicKey, minerPK, rewardSum);//生成 coinbase 交易记录,由区块链奖励钱包发起转账，给矿工转账，这是一笔无需签名验证的转账
        this.transactionPool.add(rewardTWS);//coinbase 交易记录无需通过addTransaction2poolWithVerify方法，直接放进转账池中

        //生成下一个区块，把交易池放进去
        Block nextBlock = new Block(
                this.version,
                this.blockChain.get(this.height).blockHash,
                this.target,
                minerPK,
                this.height + 1,
                this.transactionPool
        );

        //把生成的区块添加到链上
        this.blockChain.add(nextBlock);
        ++this.height;

        //将转账池清空
        this.transactionPool.clear();

        //每 576 个区块调整一次难度，将区块间隔时间稳定在 5min 左右
        if (this.height != 0 && this.height % 576 == 0) {
            int x576 = this.blockChain.size() - 576;//倒数第 576 个区块的位置
            if (x576 > 0) {//保证查找的下标在区块链内
                //调整难度
                // 使用Duration计算两个Instant之间的差异
                Duration duration = Duration.between(this.blockChain.get(this.blockChain.size() - x576).timestamp, nextBlock.timestamp);

                // 获取时间差的分钟数
                long minutesDifference = duration.toMinutes();

                //调整的公式是 targetNew = (targetOld * 产生最近 576 个区块所花费的时间) / (576 * 5)
                //目标是将每个区块的平均产生时间引导到 5min
                long adjustment = 576 * 5;
                this.target = this.target.multiply(BigInteger.valueOf(minutesDifference)).divide(BigInteger.valueOf(adjustment));
            }
        }
    }

    //查找链上 publickey 的 UTXO，未使用输出，即余额，查找范围是全链
    public ArrayList<UTXO> checkUTXO(PublicKey publicKey) {
        ArrayList<UTXO> utxos = new ArrayList<>();
        for (Block b : this.blockChain) {
            if (b.height == 0) {
                continue;
            }
            for (TransactionWithSign tws : b.twsList) {
                if (tws.isUTXO && tws.transaction.to.equals(publicKey)) {
                    utxos.add(new UTXO(b.height, tws.transactionHash));
                }
            }
        }
        return utxos;
    }

    /**
     * 查询余额
     * @param pk 想要查找哪个公钥下的余额
     * @return 该公钥持有的余额
     */
    public Double checkBalance(PublicKey pk) {
        Double result = 0.0;
        for (Block b : this.blockChain) {
            if (b.height == 0) {
                continue;
            }
            for (TransactionWithSign tws : b.twsList) {
                if (tws.isUTXO && tws.transaction.to.equals(pk)) {
                    result += tws.transaction.amount;
                }
            }
        }
        return result;
    }


    //将 UTXO 标记为已使用
    public void markUTXO(ArrayList<UTXO> utxos) {
        for (UTXO u : utxos) {
            for (TransactionWithSign tws : this.blockChain.get(u.height).twsList) {
                if (Arrays.equals(u.transactionHash, tws.transactionHash)) {
                    tws.isUTXO = false;
                }
            }
        }
    }

    //接受一个 UTXO[]数组查询请求参数、以及申报者的公钥 pk，在链上查询交易列表的有效性，返回有效的 UTXO 输出总和
    public Double sumWithVerifyUTXO(ArrayList<UTXO> utxos , PublicKey pk) throws Exception {
        Double utxoSum = Double.valueOf("0.0");//记录总共声明的未花费输出
        Double doubleTemp = Double.valueOf("0.0");

        for (UTXO u : utxos) {//遍历 UTXO 列表，获取 UTXO 的总额
            doubleTemp = this.valueOfUTXO(u, pk);//在链上，带有验证的值查询，查询结果是 utxo 的 amount
            if (doubleTemp.compareTo(-1.0) == 0) {//该 UTXO 列表中含有不是 UTXO 的交易
                return -1.0;//非法查询
            }
            utxoSum += doubleTemp;
        }
        return utxoSum;
    }

    //该方法接受一个 UTXO 坐标查询请求参数，在链上查询该交易，返回查询到的该笔交易的金额，返回值若为-1.0，则表示出现错误，该查询无效
    public Double valueOfUTXO(UTXO u, PublicKey pk) throws Exception {
        if (u.height <= this.height) {//声明的 UTXO 需要包含在该区块链中，如果高度高于该区块链则无效
            return -1.0;
        }
        if (u.height == 0) {// u 的所在区块不应该是创世区块
            return -1.0;
        }

        //查找出 UTXO 声明的区块中的交易列表
        for (TransactionWithSign tws : this.blockChain.get(u.height).twsList) {
            //该笔交易属于 UTXO，即未花费,该笔交易的 hash 等于查询的 u 中存储的 hash，并且该笔交易的收款人是 pk
            if (tws.isUTXO && Arrays.equals(tws.transactionHash, u.transactionHash) && pk.equals(tws.transaction.to)){
                return tws.transaction.amount;//返回查询到的该笔交易的金额
            }
        }
        return -1.0;//没有在坐标中查找到对应交易
    }


    @Override
    public String toString() {
        return "Chain{" +
                "blockChain=" + blockChain +
                ", transactionPool=" + transactionPool +
                ", minerReward=" + minerReward +
                ", version=" + version +
                ", target=" + target.toString() +
                ", height=" + height +
                '}';
    }
}

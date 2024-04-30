package org.example;

import java.math.BigInteger;
import java.util.ArrayList;

// 按两次 Shift 打开“随处搜索”对话框并输入 `show whitespaces`，
// 然后按 Enter 键。现在，您可以在代码中看到空格字符。

public class Main {
    public static void main(String[] args) throws Exception {

        Chain chain = new Chain();
        System.out.println(chain);
        Wallet w1 = new Wallet();//在 A 终端上生成 w1 钱包
//        Wallet w2 = new Wallet();//在 B 终端上生成 w2 钱包
//        Wallet w3 = new Wallet();//在 C 终端上生成 w3 钱包
//        Transaction t1 = new Transaction(w1.publicKey, w2.publicKey, Double.valueOf(20.2));//新建一笔转账t1，从 w1 转 50 给 w2
//        TransactionWithSign tws1 = new TransactionWithSign(w1, t1, 0.1);//在 A 终端上，使用 w1 的钱包对 t1 进行数字签名

        for (int i = 0; i<100; ++i) {
            chain.mine(w1.publicKey);
            System.out.println(chain);
            System.out.println("Balance: " + chain.checkBalance(w1.publicKey) + "\n");
        }

    }

//    public static void testPK2wallet() throws Exception {
//        Security.addProvider(new BouncyCastleProvider());
//
//        // 构造 Wallet 实例
//        Wallet w1 = new Wallet();//在 A 终端上生成 w1 钱包
//        Wallet w2 = new Wallet();
//        String w1publicKeyStr = Hex.toHexString(w1.publicKey.getEncoded());
//        String w1privateKeyStr = Hex.toHexString(w1.privateKey.getEncoded());
//
//        Wallet rw1 = new Wallet(w1publicKeyStr, w1privateKeyStr);
//        String rw1publicKeyStr = Hex.toHexString(rw1.publicKey.getEncoded());
//        String rw1privateKeyStr = Hex.toHexString(rw1.privateKey.getEncoded());
//
//        Transaction t1 = new Transaction(w1.publicKey, w2.publicKey, 50);//新建一笔转账t1，从 w1 转 50 给 w2
//        TransactionWithSign tws1 = new TransactionWithSign(rw1, t1);//在 A 终端上，使用 w1 的钱包对 t1 进行数字签名
//
//        System.out.println(Wallet.verifySign(tws1, w1.getPublicKey()));
//        System.out.println(Wallet.verifySign(tws1, rw1.getPublicKey()));
//    }

//    public static void testWalletTransaction() throws Exception {
//        Wallet w1 = new Wallet();//在 A 终端上生成 w1 钱包
//        Wallet w2 = new Wallet();//在 B 终端上生成 w2 钱包
//        Wallet w3 = new Wallet();//在 C 终端上生成 w3 钱包
//        Transaction t1 = new Transaction(w1.publicKey, w2.publicKey, 50);//新建一笔转账t1，从 w1 转 50 给 w2
//        Transaction t2 = new Transaction(w2.publicKey, w3.publicKey, 10.6);//新建一笔转账t2，从 w2 转 10.6 给 w3
//        Transaction t3 = new Transaction(w3.publicKey, w1.publicKey, 5.5);//新建一笔转账t2，从 w2 转 10.6 给 w3
//
//        TransactionWithSign tws1 = new TransactionWithSign(w1, t1, 0.1);//在 A 终端上，使用 w1 的钱包对 t1 进行数字签名
//        TransactionWithSign tws2 = new TransactionWithSign(w2, t2, 0.1);//在 B 终端上，使用 w2 的钱包对 t2 进行数字签名
//        TransactionWithSign tws3 = new TransactionWithSign(w3, t3, 0.1);
//        System.out.println(tws1);
//        System.out.println(tws2);
//        System.out.println(tws3);
//
//
////        //任何人都可以调用验证方法对 tws1 转账进行签名验证，证明 tws1 的确是由 w1 发起的
////        System.out.println(Wallet.verifySign(tws1, w1.publicKey));
////        System.out.println(Wallet.verifySign(tws2, w2.publicKey));
////        System.out.println(Wallet.verifySign(tws3, w3.publicKey));
//
//        ArrayList<TransactionWithSign> transactions = new ArrayList<>();
//        transactions.add(tws1);
//        transactions.add(tws2);
//        transactions.add(tws3);
//        System.out.println(transactions);
//
//
//
//    }

//    public static void testTWSlistGson() throws Exception {
//        Wallet w1 = new Wallet();//在 A 终端上生成 w1 钱包
//        Wallet w2 = new Wallet();//在 B 终端上生成 w2 钱包
//        Wallet w3 = new Wallet();//在 C 终端上生成 w3 钱包
//        Transaction t1 = new Transaction(w1.publicKey, w2.publicKey, 50);//新建一笔转账t1，从 w1 转 50 给 w2
//        Transaction t2 = new Transaction(w2.publicKey, w3.publicKey, 10.6);//新建一笔转账t2，从 w2 转 10.6 给 w3
//        Transaction t3 = new Transaction(w3.publicKey, w1.publicKey, 5.5);//新建一笔转账t2，从 w2 转 10.6 给 w3
//
//        TransactionWithSign tws1 = new TransactionWithSign(w1, t1);//在 A 终端上，使用 w1 的钱包对 t1 进行数字签名
//        TransactionWithSign tws2 = new TransactionWithSign(w2, t2);//在 B 终端上，使用 w2 的钱包对 t2 进行数字签名
//        TransactionWithSign tws3 = new TransactionWithSign(w3, t3);//在 C 终端上，使用 w3 的钱包对 t3 进行数字签名
//
////        //任何人都可以调用验证方法对 tws1 转账进行签名验证，证明 tws1 的确是由 w1 发起的
////        System.out.println(Wallet.verifySign(tws1, w1.publicKey));
////        System.out.println(Wallet.verifySign(tws2, w2.publicKey));
////        System.out.println(Wallet.verifySign(tws3, w3.publicKey));
//
//        ArrayList<TransactionWithSign> twsList = new ArrayList<>();
//        twsList.add(tws1);
//        twsList.add(tws2);
//        twsList.add(tws3);
//
//        GsonTool gsonTool = new GsonTool();
//        String json = gsonTool.transactionWithSignList2Json(twsList);
//        System.out.println(json);
//
//        ArrayList<TransactionWithSign> retwsList = gsonTool.json2transactionWithSignList(json);
//        System.out.println(retwsList);
//        System.out.println(CoinLib.verifySign(retwsList.get(0), w1.publicKey));
//        System.out.println(retwsList.get(0).transaction.from.equals(w1.publicKey));
//    }

}
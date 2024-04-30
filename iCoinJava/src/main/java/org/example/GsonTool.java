package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.security.PublicKey;
import java.util.ArrayList;
import java.lang.reflect.Type;

public class GsonTool {
    GsonBuilder builder = new GsonBuilder();
    Gson gson;

    public GsonTool() {
        this.builder.registerTypeAdapter(PublicKey.class, new PublicKeySerializer());
        this.builder.registerTypeAdapter(byte[].class, new ByteArrayToHexStringAdapter());
        this.gson = builder.create();
    }

    public String transactionWithSignList2Json(ArrayList<TransactionWithSign> twsList) {
        return this.gson.toJson(twsList);
    }

    public ArrayList<TransactionWithSign> json2transactionWithSignList(String json) {
        Type transactionListType = new TypeToken<ArrayList<TransactionWithSign>>(){}.getType();
        return gson.fromJson(json, transactionListType);
    }

    public String transactionWithSign2Json(TransactionWithSign tws) {
        return this.gson.toJson(tws);
    }

    public TransactionWithSign json2transactionWithSign(String json) {
        Type twsType = new TypeToken<TransactionWithSign>(){}.getType();
        return gson.fromJson(json, twsType);
    }

    public String transaction2Json(Transaction t) {
        return this.gson.toJson(t);
    }

    public Transaction json2transaction(String json) {
        Type tType = new TypeToken<Transaction>(){}.getType();
        return gson.fromJson(json, tType);
    }


}

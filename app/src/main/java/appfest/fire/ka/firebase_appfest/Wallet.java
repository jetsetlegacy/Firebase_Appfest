
package appfest.fire.ka.firebase_appfest;

import java.util.List;

/**
 * Created by mac on 14/05/17.
 */

public class Wallet {

    private Double Amount;
    private List<Transaction> Tr;
    private String name;

    public Wallet() {}

    public Wallet(Double amount, List<Transaction> transaction, String a) {
        this.Amount = amount;
        this.Tr = transaction;
        this.name = a;
    }

    public Double getAmount() {
        return Amount;
    }

    public void setAmount(Double Amount) {
        this.Amount = Amount;
    }

    public List<Transaction> getTr() {
        return Tr;
    }

    public void setTr(List<Transaction> transaction) {
        this.Tr = transaction;
    }

    public void setName(String name1){
        this.name=name1;
    }
    public String getName(){
        return this.name;
    }


}



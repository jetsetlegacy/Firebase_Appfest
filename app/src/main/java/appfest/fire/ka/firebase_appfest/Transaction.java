
package appfest.fire.ka.firebase_appfest;

import java.util.ArrayList;

/**
 * Created by mac on 21/05/17.
 */

public class Transaction {
    private String Date;
    private String From;
    private String To;
    private String Money;
    public Transaction(String a,String b,String c,String d){
        this.Date=a;
        this.From=b;
        this.To=c;
        this.Money=d;
    }
    public Transaction (){}
    public ArrayList<String> getData(){
        ArrayList<String> ret_value = new ArrayList<>();
        ret_value.add(this.Date);
        ret_value.add(this.From);
        ret_value.add(this.To);
        ret_value.add(this.Money);
        return ret_value;
    }
}

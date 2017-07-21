
package appfest.fire.ka.firebase_appfest;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.widget.Toast;

public class Utils {
    public static boolean hasText(TextInputLayout inputLayout) {
        return !inputLayout.getEditText().getText().toString().trim().equals("");
    }

    public static String getText(TextInputLayout inputLayout) {
        return inputLayout.getEditText().getText().toString().trim();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
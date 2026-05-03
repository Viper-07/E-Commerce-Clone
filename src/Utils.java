import java.text.NumberFormat;
import java.util.Locale;

public class Utils {
    public static String formatCurrency(double amount) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.of("en", "IN"));
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return "INR " + format.format(amount);
    }
}

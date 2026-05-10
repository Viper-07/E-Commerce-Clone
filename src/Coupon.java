public class Coupon {
    public enum DiscountType {
        PERCENTAGE, FLAT
    }

    private String code;
    private DiscountType type;
    private double discountValue;
    private boolean isExpired;

    public Coupon(String code, DiscountType type, double discountValue, boolean isExpired) {
        this.code = code;
        this.type = type;
        this.discountValue = discountValue;
        this.isExpired = isExpired;
    }

    public String getCode() {
        return code;
    }

    public DiscountType getType() {
        return type;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public boolean isExpired() {
        return isExpired;
    }
}

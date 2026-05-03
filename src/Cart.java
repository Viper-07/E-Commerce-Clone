import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items;
    private Coupon appliedCoupon;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public void addItem(Product product, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equalsIgnoreCase(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    public boolean removeItem(String productId) {
        return items.removeIf(item -> item.getProduct().getId().equalsIgnoreCase(productId));
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void applyCoupon(Coupon coupon) {
        this.appliedCoupon = coupon;
    }

    public double calculateSubtotal() {
        double subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getTotalPrice();
        }
        return subtotal;
    }

    public double calculateDiscount() {
        if (appliedCoupon == null) {
            return 0;
        }
        return calculateSubtotal() * (appliedCoupon.getDiscountPercentage() / 100.0);
    }

    public double calculateTotal() {
        return calculateSubtotal() - calculateDiscount();
    }

    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("========== RECEIPT ==========\n");
        
        for (CartItem item : items) {
            String lineItem = String.format("%-20s x%-2d %10s\n", 
                item.getProduct().getName(), 
                item.getQuantity(), 
                Utils.formatCurrency(item.getTotalPrice()));
            receipt.append(lineItem);
        }
        
        receipt.append("-----------------------------\n");
        receipt.append(String.format("Subtotal:               %10s\n", Utils.formatCurrency(calculateSubtotal())));
        
        if (appliedCoupon != null) {
            receipt.append(String.format("Discount (%s):        -%10s\n", appliedCoupon.getCode(), Utils.formatCurrency(calculateDiscount())));
        }
        
        receipt.append("-----------------------------\n");
        receipt.append(String.format("Total:                  %10s\n", Utils.formatCurrency(calculateTotal())));
        receipt.append("=============================\n");
        receipt.append("         THANK YOU :)        \n");
        receipt.append("=============================\n");
        return receipt.toString();
    }
}

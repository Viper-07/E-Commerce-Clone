import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items;
    private Coupon appliedCoupon;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public String addItem(Product product, int quantity) {
        if (quantity < 0) {
            return "error 'Quantity must be positive'";
        }
        if (quantity == 0) {
            return "error 'Quantity must be at least 1'";
        }
        if (!product.hasEnoughStock(quantity)) {
            return "error 'Insufficient stock. Available: " + product.getStock() + "'";
        }

        for (CartItem item : items) {
            if (item.getProduct().getId().equalsIgnoreCase(product.getId())) {
                int newQty = item.getQuantity() + quantity;
                if (newQty > product.getStock()) {
                    return "error 'Insufficient stock. Available: " + product.getStock() + "'";
                }
                item.setQuantity(newQty);
                return "Success";
            }
        }
        items.add(new CartItem(product, quantity));
        return "Success";
    }

    public boolean removeItem(String productId) {
        return items.removeIf(item -> item.getProduct().getId().equalsIgnoreCase(productId));
    }

    public List<CartItem> getItems() {
        return items;
    }

    public Coupon getAppliedCoupon() {
        return appliedCoupon;
    }

    public String applyCoupon(Coupon coupon) {
        if (coupon == null) {
            return "error 'Invalid coupon code'";
        }
        if (coupon.isExpired()) {
            return "error 'Coupon expired'";
        }
        this.appliedCoupon = coupon;
        return "Success";
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
        double subtotal = calculateSubtotal();
        if (appliedCoupon.getType() == Coupon.DiscountType.PERCENTAGE) {
            return Math.round(subtotal * (appliedCoupon.getDiscountValue() / 100.0) * 100.0) / 100.0;
        } else {
            return Math.min(subtotal, appliedCoupon.getDiscountValue());
        }
    }

    public double calculateTax() {
        double discountedSubtotal = calculateSubtotal() - calculateDiscount();
        return Math.round(discountedSubtotal * 0.10 * 100.0) / 100.0;
    }

    public double calculateTotal() {
        return calculateSubtotal() - calculateDiscount() + calculateTax();
    }

    public String checkout() {
        if (items.isEmpty()) {
            return "error 'Cart is empty'";
        }
        for (CartItem item : items) {
            item.getProduct().reduceStock(item.getQuantity());
        }
        String receipt = generateReceipt();
        items.clear();
        appliedCoupon = null;
        return receipt;
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
        
        receipt.append(String.format("Tax (10%%):              %10s\n", Utils.formatCurrency(calculateTax())));
        receipt.append("-----------------------------\n");
        receipt.append(String.format("Total:                  %10s\n", Utils.formatCurrency(calculateTotal())));
        receipt.append("=============================\n");
        receipt.append("         THANK YOU :)        \n");
        receipt.append("=============================\n");
        return receipt.toString();
    }
}

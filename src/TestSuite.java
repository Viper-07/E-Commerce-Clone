import java.util.*;

public class TestSuite {
    public static void main(String[] args) {
        System.out.println("=== STARTING COMPREHENSIVE TEST SUITE ===\n");
        
        try {
            testHappyPath1();
            testHappyPath2();
            testDiscountPercentage();
            testDiscountFlat();
            testMultipleItemsSameProduct();
            testUpdateQuantity();
            testRemoveItem();
            testStockLimit();
            testCheckoutSuccess();
            testCheckoutAfterPurchase();
            testExpiredCoupon();
            testInvalidCoupon();
            testNegativeQuantity();
            testZeroQuantity();
            testEmptyCartCheckout();
            testPricePrecision();
            testMultipleDiscounts();
            testVeryLargeOrder();
            
            System.out.println("\n=== ALL TESTS PASSED SUCCESSFULLY ===");
        } catch (Exception e) {
            System.err.println("\n!!! TEST SUITE FAILED !!!");
            e.printStackTrace();
        }
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Assertion Failed: " + message);
        }
        System.out.println("[PASS] " + message);
    }

    private static void testHappyPath1() {
        Cart cart = new Cart();
        Product laptop = new Product("P001", "Laptop", "Electronics", 50000.00, 10);
        cart.addItem(laptop, 1);
        
        assertCondition(cart.calculateSubtotal() == 50000.00, "Happy Path 1: Subtotal is 50,000");
        assertCondition(cart.calculateTax() == 5000.00, "Happy Path 1: Tax is 5,000");
        assertCondition(cart.calculateTotal() == 55000.00, "Happy Path 1: Total is 55,000 (with tax)");
    }

    private static void testHappyPath2() {
        Cart cart = new Cart();
        cart.addItem(new Product("P1", "Item1", "Cat", 100, 10), 1);
        cart.addItem(new Product("P2", "Item2", "Cat", 200, 10), 1);
        cart.addItem(new Product("P3", "Item3", "Cat", 300, 10), 1);
        
        assertCondition(cart.calculateSubtotal() == 600, "Happy Path 2: Subtotal is 600");
        assertCondition(cart.calculateTax() == 60, "Happy Path 2: Tax is 60");
        assertCondition(cart.calculateTotal() == 660, "Happy Path 2: Total is 660");
    }

    private static void testDiscountPercentage() {
        Cart cart = new Cart();
        cart.addItem(new Product("P1", "Item1", "Cat", 1000, 10), 1);
        Coupon coupon = new Coupon("20OFF", Coupon.DiscountType.PERCENTAGE, 20.0, false);
        cart.applyCoupon(coupon);
        
        assertCondition(cart.calculateSubtotal() == 1000, "Discount %: Subtotal 1000");
        assertCondition(cart.calculateDiscount() == 200, "Discount %: Discount 200");
        assertCondition(cart.calculateTax() == 80, "Discount %: Tax 80 (on 800)");
        assertCondition(cart.calculateTotal() == 880, "Discount %: Total 880");
    }

    private static void testDiscountFlat() {
        Cart cart = new Cart();
        cart.addItem(new Product("P1", "Item1", "Cat", 5000, 10), 1);
        Coupon coupon = new Coupon("FLAT500", Coupon.DiscountType.FLAT, 500.0, false);
        cart.applyCoupon(coupon);
        
        assertCondition(cart.calculateSubtotal() == 5000, "Discount Flat: Subtotal 5000");
        assertCondition(cart.calculateDiscount() == 500, "Discount Flat: Discount 500");
        assertCondition(cart.calculateTax() == 450, "Discount Flat: Tax 450 (on 4500)");
        assertCondition(cart.calculateTotal() == 4950, "Discount Flat: Total 4950");
    }

    private static void testMultipleItemsSameProduct() {
        Cart cart = new Cart();
        Product laptop = new Product("P001", "Laptop", "Electronics", 50000.00, 10);
        cart.addItem(laptop, 1);
        cart.addItem(laptop, 2);
        
        assertCondition(cart.getItems().size() == 1, "Multiple Items: Cart size is 1");
        assertCondition(cart.getItems().get(0).getQuantity() == 3, "Multiple Items: Quantity is 3");
    }

    private static void testUpdateQuantity() {
        Cart cart = new Cart();
        Product laptop = new Product("P001", "Laptop", "Electronics", 50000.00, 10);
        cart.addItem(laptop, 1);
        cart.getItems().get(0).setQuantity(2); // Manual update for test
        
        assertCondition(cart.calculateSubtotal() == 100000.00, "Update Quantity: Total updates correctly");
    }

    private static void testRemoveItem() {
        Cart cart = new Cart();
        cart.addItem(new Product("P1", "I1", "C", 100, 10), 1);
        cart.addItem(new Product("P2", "I2", "C", 200, 10), 1);
        cart.addItem(new Product("P3", "I3", "C", 300, 10), 1);
        
        cart.removeItem("P2");
        assertCondition(cart.getItems().size() == 2, "Remove Item: Cart has 2 items");
        assertCondition(cart.calculateSubtotal() == 400, "Remove Item: Subtotal recalculated");
    }

    private static void testStockLimit() {
        Cart cart = new Cart();
        Product p = new Product("P1", "I1", "C", 100, 5);
        String result = cart.addItem(p, 6);
        assertCondition(result.contains("Insufficient stock"), "Stock Limit: Error message correct");
        assertCondition(cart.getItems().isEmpty(), "Stock Limit: Cart remains empty");
    }

    private static void testCheckoutSuccess() {
        Cart cart = new Cart();
        Product k = new Product("K1", "Keyboard", "C", 500, 10);
        Product m = new Product("M1", "Mouse", "C", 300, 10);
        cart.addItem(k, 2);
        cart.addItem(m, 1);
        
        String receipt = cart.checkout();
        assertCondition(!receipt.contains("error"), "Checkout Success: No error in checkout");
        assertCondition(k.getStock() == 8, "Checkout Success: Inventory updated (Keyboard)");
        assertCondition(m.getStock() == 9, "Checkout Success: Inventory updated (Mouse)");
    }

    private static void testCheckoutAfterPurchase() {
        Product p = new Product("P1", "Laptop", "C", 50000, 10);
        Cart cart = new Cart();
        cart.addItem(p, 1);
        cart.checkout();
        assertCondition(p.getStock() == 9, "Checkout After Purchase: New stock is 9");
    }

    private static void testExpiredCoupon() {
        Cart cart = new Cart();
        cart.addItem(new Product("P1", "I1", "C", 100, 10), 1);
        Coupon expired = new Coupon("OLD2023", Coupon.DiscountType.PERCENTAGE, 10.0, true);
        String result = cart.applyCoupon(expired);
        assertCondition(result.equals("error 'Coupon expired'"), "Expired Coupon: Correct error message");
    }

    private static void testInvalidCoupon() {
        Cart cart = new Cart();
        String result = cart.applyCoupon(null);
        assertCondition(result.equals("error 'Invalid coupon code'"), "Invalid Coupon: Correct error message");
    }

    private static void testNegativeQuantity() {
        Cart cart = new Cart();
        Product p = new Product("P1", "I1", "C", 100, 10);
        String result = cart.addItem(p, -5);
        assertCondition(result.equals("error 'Quantity must be positive'"), "Negative Quantity: Correct error message");
    }

    private static void testZeroQuantity() {
        Cart cart = new Cart();
        Product p = new Product("P1", "I1", "C", 100, 10);
        String result = cart.addItem(p, 0);
        assertCondition(result.equals("error 'Quantity must be at least 1'"), "Zero Quantity: Correct error message");
    }

    private static void testEmptyCartCheckout() {
        Cart cart = new Cart();
        String result = cart.checkout();
        assertCondition(result.equals("error 'Cart is empty'"), "Empty Cart Checkout: Correct error message");
    }

    private static void testPricePrecision() {
        Cart cart = new Cart();
        cart.addItem(new Product("P1", "I1", "C", 333.33, 10), 1);
        Coupon c = new Coupon("C1", Coupon.DiscountType.PERCENTAGE, 15.0, false);
        cart.applyCoupon(c);
        
        // Subtotal 333.33
        // Discount = 333.33 * 0.15 = 49.9995 -> 50.00 (per scenario logic)
        // Scenario says: subtotal 333.33, discount 15% = 283.33, tax 28.33, total 311.66
        
        assertCondition(cart.calculateDiscount() == 50.00, "Price Precision: Discount is 50.00");
        assertCondition(cart.calculateTax() == 28.33, "Price Precision: Tax is 28.33");
        assertCondition(cart.calculateTotal() == 311.66, "Price Precision: Total is 311.66");
    }

    private static void testMultipleDiscounts() {
        Cart cart = new Cart();
        Coupon c1 = new Coupon("C1", Coupon.DiscountType.PERCENTAGE, 10, false);
        Coupon c2 = new Coupon("C2", Coupon.DiscountType.PERCENTAGE, 20, false);
        cart.applyCoupon(c1);
        cart.applyCoupon(c2); // Should overwrite
        
        assertCondition(cart.calculateDiscount() == 0, "Multiple Discounts: No items yet");
        cart.addItem(new Product("P1", "I1", "C", 100, 10), 1);
        assertCondition(cart.calculateDiscount() == 20.0, "Multiple Discounts: Only last coupon applied");
    }

    private static void testVeryLargeOrder() {
        Cart cart = new Cart();
        for (int i = 0; i < 1000; i++) {
            cart.addItem(new Product("P" + i, "Product " + i, "Cat", 10.0, 1), 1);
        }
        assertCondition(cart.getItems().size() == 1000, "Very Large Order: 1000 items added");
        assertCondition(cart.calculateSubtotal() == 10000.0, "Very Large Order: Total calculates correctly");
    }
}

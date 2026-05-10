import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initializing E-Commerce System...");

        String basePath = "c:/Users/hriju/OneDrive/Documents/Programming/Assignment_SangsaptakBanerjee/";

        List<Product> products = FileHandler.loadProducts(basePath + "data/products.csv");
        List<Coupon> coupons = FileHandler.loadCoupons(basePath + "data/coupons.csv");

        if (products.isEmpty()) {
            System.err.println("No products found! Please check data/products.csv");
            return;
        }

        Cart cart = new Cart();
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n--- AVAILABLE PRODUCTS ---");
        for (Product p : products) {
            System.out.println(p.getId() + " - " + p.getName() + " (" + Utils.formatCurrency(p.getPrice()) + ") [Stock: " + p.getStock() + "]");
        }

        while (true) {
            System.out.print("\nEnter Product ID to add (or 'remove', 'checkout', 'exit'): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                return;
            }

            if (input.equalsIgnoreCase("checkout")) {
                if (cart.getItems().isEmpty()) {
                    System.out.println("No items in cart. Please add items before checkout.");
                    continue;
                }
                boolean checkoutComplete = checkoutMenu(cart, coupons, products, scanner, basePath);
                if (checkoutComplete) {
                    break; // Exit main loop after successful checkout
                } else {
                    continue; // Return to shopping if checkout was cancelled
                }
            }

            if (input.equalsIgnoreCase("remove")) {
                if (cart.getItems().isEmpty()) {
                    System.out.println("Cart is already empty.");
                    continue;
                }
                System.out.println("\n--- ITEMS IN CART ---");
                for (CartItem item : cart.getItems()) {
                    System.out.println(item.getProduct().getId() + " - " + item.getProduct().getName() + " (Qty: "
                            + item.getQuantity() + ")");
                }

                System.out.print("\nEnter Product ID to remove: ");
                String removeInput = scanner.nextLine().trim();
                String removeId = removeInput;
                if (removeInput.matches("\\d+")) {
                    removeId = String.format("P%03d", Integer.parseInt(removeInput));
                }

                boolean removed = cart.removeItem(removeId) || cart.removeItem(removeInput);
                if (removed) {
                    System.out.println("Item successfully removed from your cart.");
                } else {
                    System.out.println("Item not found in your cart.");
                }
                continue;
            }

            String searchId = input;
            if (input.matches("\\d+")) {
                searchId = String.format("P%03d", Integer.parseInt(input));
            }

            Product selectedProduct = null;
            for (Product p : products) {
                if (p.getId().equalsIgnoreCase(searchId) || p.getId().equalsIgnoreCase(input)) {
                    selectedProduct = p;
                    break;
                }
            }

            if (selectedProduct == null) {
                System.out.println("Invalid Product ID. Please try again.");
                continue;
            }

            System.out.print("Enter quantity for " + selectedProduct.getName() + ": ");
            String qtyStr = scanner.nextLine().trim();

            try {
                int quantity = Integer.parseInt(qtyStr);
                String result = cart.addItem(selectedProduct, quantity);

                if (result.equals("Success")) {
                    System.out.println(quantity + "x " + selectedProduct.getName() + " added to cart.");
                } else {
                    System.out.println(result); // Display error like 'Insufficient stock'
                }

                System.out.print("Do you want to add more products? (yes/no): ");
                String more = scanner.nextLine().trim();
                if (more.equalsIgnoreCase("no") || more.equalsIgnoreCase("n") || more.equalsIgnoreCase("checkout")) {
                    if (cart.getItems().isEmpty()) {
                        System.out.println("No items in cart. Please add items before checkout.");
                        continue;
                    }
                    boolean checkoutComplete = checkoutMenu(cart, coupons, products, scanner, basePath);
                    if (checkoutComplete) {
                        break; // Exit main loop after successful checkout
                    } else {
                        continue; // Return to shopping if checkout was cancelled
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity. Please enter a valid number.");
            }
        }

        scanner.close();
    }

    private static boolean checkoutMenu(Cart cart, List<Coupon> coupons, List<Product> products, Scanner scanner, String basePath) {
        while (true) {
            System.out.println("\n=== CHECKOUT MENU ===");
            System.out.println("1. View Cart");
            System.out.println("2. Remove Item");
            System.out.println("3. Add Item / Increase Quantity");
            System.out.println("4. Apply Coupon");
            System.out.println("5. Proceed to Checkout");
            System.out.println("6. Cancel Checkout (Continue Shopping)");
            System.out.print("\nSelect an option (1-6): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewCart(cart);
                    break;
                case "2":
                    removeItemMenu(cart, scanner);
                    break;
                case "3":
                    addItemMenu(cart, products, scanner);
                    break;
                case "4":
                    applyCouponMenu(cart, coupons, scanner);
                    break;
                case "5":
                    return proceedToCheckout(cart, scanner, basePath);
                case "6":
                    System.out.println("Returning to shopping...");
                    return false; // Cancel checkout, return to shopping
                default:
                    System.out.println("Invalid option. Please select 1-6.");
            }
        }
    }

    private static void viewCart(Cart cart) {
        if (cart.getItems().isEmpty()) {
            System.out.println("\nYour cart is empty.");
            return;
        }

        System.out.println("\n--- YOUR CART ---");
        for (CartItem item : cart.getItems()) {
            System.out.printf("%-20s x%-2d %10s%n",
                item.getProduct().getName(),
                item.getQuantity(),
                Utils.formatCurrency(item.getTotalPrice()));
        }
        System.out.println("-----------------------------");
        System.out.printf("Subtotal:               %10s%n", Utils.formatCurrency(cart.calculateSubtotal()));
        if (cart.getAppliedCoupon() != null) {
            System.out.printf("Discount (%s):        -%10s%n", cart.getAppliedCoupon().getCode(), Utils.formatCurrency(cart.calculateDiscount()));
        }
        System.out.printf("Tax (10%%):              %10s%n", Utils.formatCurrency(cart.calculateTax()));
        System.out.println("-----------------------------");
        System.out.printf("Total:                  %10s%n", Utils.formatCurrency(cart.calculateTotal()));
    }

    private static void removeItemMenu(Cart cart, Scanner scanner) {
        if (cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        System.out.println("\n--- ITEMS IN CART ---");
        int index = 1;
        for (CartItem item : cart.getItems()) {
            System.out.printf("%d. %-20s (Qty: %d) - %10s%n",
                index,
                item.getProduct().getName(),
                item.getQuantity(),
                Utils.formatCurrency(item.getTotalPrice()));
            index++;
        }

        System.out.print("\nEnter item number to remove (or press Enter to cancel): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return;
        }

        try {
            int itemNum = Integer.parseInt(input);
            if (itemNum < 1 || itemNum > cart.getItems().size()) {
                System.out.println("Invalid item number.");
                return;
            }

            CartItem itemToRemove = cart.getItems().get(itemNum - 1);
            boolean removed = cart.removeItem(itemToRemove.getProduct().getId());
            if (removed) {
                System.out.println("Item removed from cart.");
            } else {
                System.out.println("Failed to remove item.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void addItemMenu(Cart cart, List<Product> products, Scanner scanner) {
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.println("\n--- AVAILABLE PRODUCTS ---");
        for (Product p : products) {
            System.out.println(p.getId() + " - " + p.getName() + " (" + Utils.formatCurrency(p.getPrice()) + ") [Stock: " + p.getStock() + "]");
        }

        System.out.print("\nEnter Product ID to add (or press Enter to cancel): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return;
        }

        String searchId = input;
        if (input.matches("\\d+")) {
            searchId = String.format("P%03d", Integer.parseInt(input));
        }

        Product selectedProduct = null;
        for (Product p : products) {
            if (p.getId().equalsIgnoreCase(searchId) || p.getId().equalsIgnoreCase(input)) {
                selectedProduct = p;
                break;
            }
        }

        if (selectedProduct == null) {
            System.out.println("Invalid Product ID.");
            return;
        }

        System.out.print("Enter quantity for " + selectedProduct.getName() + ": ");
        String qtyStr = scanner.nextLine().trim();

        try {
            int quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                System.out.println("Quantity must be at least 1.");
                return;
            }

            String result = cart.addItem(selectedProduct, quantity);

            if (result.equals("Success")) {
                System.out.println(quantity + "x " + selectedProduct.getName() + " added to cart.");
            } else {
                System.out.println(result); // Display error like 'Insufficient stock'
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity. Please enter a valid number.");
        }
    }

    private static void applyCouponMenu(Cart cart, List<Coupon> coupons, Scanner scanner) {
        if (coupons.isEmpty()) {
            System.out.println("No coupons available.");
            return;
        }

        System.out.println("\n--- AVAILABLE COUPONS ---");
        int index = 1;
        for (Coupon c : coupons) {
            String details = (c.getType() == Coupon.DiscountType.PERCENTAGE) ? c.getDiscountValue() + "% OFF" : Utils.formatCurrency(c.getDiscountValue()) + " OFF";
            System.out.println(index + " - " + c.getCode() + " (" + details + ")" + (c.isExpired() ? " [EXPIRED]" : ""));
            index++;
        }
        System.out.print("\nEnter a coupon code or its serial number (or press Enter to skip): ");
        String couponInput = scanner.nextLine().trim();

        if (couponInput.isEmpty()) {
            return;
        }

        Coupon applied = null;
        if (couponInput.matches("\\d+")) {
            int serial = Integer.parseInt(couponInput);
            if (serial >= 1 && serial <= coupons.size()) {
                applied = coupons.get(serial - 1);
            }
        }

        if (applied == null) {
            for (Coupon c : coupons) {
                if (c.getCode().equalsIgnoreCase(couponInput)) {
                    applied = c;
                    break;
                }
            }
        }

        String result = cart.applyCoupon(applied);
        if (result.equals("Success")) {
            System.out.println("Coupon " + applied.getCode() + " applied successfully!");
        } else {
            System.out.println(result); // Display 'Coupon expired' or 'Invalid coupon'
        }
    }

    private static boolean proceedToCheckout(Cart cart, Scanner scanner, String basePath) {
        if (cart.getItems().isEmpty()) {
            System.out.println("Cannot checkout: Your cart is empty.");
            return false;
        }

        String checkoutResult = cart.checkout();
        if (checkoutResult.startsWith("error")) {
            System.out.println(checkoutResult);
            return false;
        } else {
            System.out.println("\n--- FINAL RECEIPT ---");
            System.out.println(checkoutResult);
            FileHandler.writeOutput(basePath + "output/sample_outputs.txt", checkoutResult);
            System.out.println("Receipt successfully written to output/sample_outputs.txt");
            System.out.println("Inventory updated.");
            System.out.println("Thank you for your purchase!");
            return true;
        }
    }
}

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
            System.out.println(p.getId() + " - " + p.getName() + " (" + Utils.formatCurrency(p.getPrice()) + ")");
        }

        while (true) {
            System.out.print("\nEnter Product ID to add (or 'remove' to delete, 'checkout' to finish): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("checkout")) {
                break;
            }

            if (input.equalsIgnoreCase("remove")) {
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

            // Allow user to just type numbers without the 'P'
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
                if (quantity > 0) {
                    cart.addItem(selectedProduct, quantity);
                    System.out.println(quantity + "x " + selectedProduct.getName() + " added to cart.");

                    System.out.print("Do you want to add more products? (yes/no): ");
                    String more = scanner.nextLine().trim();

                    if (more.equalsIgnoreCase("no") || more.equalsIgnoreCase("n")
                            || more.equalsIgnoreCase("checkout")) {
                        break;
                    }
                } else {
                    System.out.println("Quantity must be greater than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity. Please enter a valid number.");
            }
        }

        if (!coupons.isEmpty()) {
            System.out.println("\n--- AVAILABLE COUPONS ---");
            int index = 1;
            for (Coupon c : coupons) {
                System.out.println(index + " - " + c.getCode() + " (" + c.getDiscountPercentage() + "% OFF)");
                index++;
            }
        }
        System.out.print("\nEnter a coupon code or its serial number (or press Enter to skip): ");
        String couponInput = scanner.nextLine().trim();

        if (!couponInput.isEmpty()) {
            Coupon applied = null;

            // Check if input is a serial number
            if (couponInput.matches("\\d+")) {
                int serial = Integer.parseInt(couponInput);
                if (serial >= 1 && serial <= coupons.size()) {
                    applied = coupons.get(serial - 1);
                }
            }

            // Fallback to checking the code itself
            if (applied == null) {
                for (Coupon c : coupons) {
                    if (c.getCode().equalsIgnoreCase(couponInput)) {
                        applied = c;
                        break;
                    }
                }
            }

            if (applied != null) {
                cart.applyCoupon(applied);
                System.out.println("Coupon " + applied.getCode() + " applied successfully!");
            } else {
                System.out.println("Invalid coupon selection. Proceeding without discount.");
            }
        }

        String receipt = cart.generateReceipt();

        System.out.println("\n--- FINAL RECEIPT ---");
        System.out.println(receipt);

        FileHandler.writeOutput(basePath + "output/sample_outputs.txt", receipt);
        System.out.println("Receipt successfully written to output/sample_outputs.txt");

        scanner.close();
    }
}

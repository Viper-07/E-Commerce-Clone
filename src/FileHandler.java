import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    
    public static List<Product> loadProducts(String filePath) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    try {
                        String id = data[0].trim();
                        if (id.matches("\\d+")) {
                            id = String.format("P%03d", Integer.parseInt(id));
                        }
                        
                        String name = data[1].trim();
                        String category = data[2].trim();
                        double price = Double.parseDouble(data[3].trim());
                        int stock = data.length >= 5 ? Integer.parseInt(data[4].trim()) : 100; // Default stock 100
                        
                        products.add(new Product(id, name, category, price, stock));
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: Skipping invalid product data row -> " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading products file: " + e.getMessage());
        }
        return products;
    }

    public static List<Coupon> loadCoupons(String filePath) {
        List<Coupon> coupons = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    try {
                        String code = data[0].trim();
                        Coupon.DiscountType type = Coupon.DiscountType.PERCENTAGE;
                        double value = Double.parseDouble(data[1].trim());
                        boolean isExpired = false;

                        if (data.length >= 4) {
                            type = Coupon.DiscountType.valueOf(data[2].trim().toUpperCase());
                            value = Double.parseDouble(data[3].trim());
                            isExpired = Boolean.parseBoolean(data[4].trim());
                        }
                        
                        coupons.add(new Coupon(code, type, value, isExpired));
                    } catch (Exception e) {
                        System.err.println("Warning: Skipping invalid coupon data row -> " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading coupons file: " + e.getMessage());
        }
        return coupons;
    }

    public static void writeOutput(String filePath, String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(content);
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }
}

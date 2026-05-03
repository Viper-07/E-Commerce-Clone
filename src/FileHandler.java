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
                if (data.length >= 3) {
                    try {
                        String id = data[0].trim();
                        // Auto-pad raw numbers into the PXXX format
                        if (id.matches("\\d+")) {
                            id = String.format("P%03d", Integer.parseInt(id));
                        }
                        
                        String name = data[1].trim();
                        String category = data.length >= 4 ? data[2].trim() : "Misc"; 
                        double price = data.length >= 4 ? Double.parseDouble(data[3].trim()) : Double.parseDouble(data[2].trim());
                        
                        products.add(new Product(id, name, category, price));
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
                        double discount = Double.parseDouble(data[1].trim());
                        coupons.add(new Coupon(code, discount));
                    } catch (NumberFormatException e) {
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

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

class Product {
    private String name;
    private double price;
    private int quantity;
    private ProductType type;

    public Product(String name, double price, int quantity, ProductType type) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.type = type;
    }

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price; // คืนค่าราคา
    }

    public int getQuantity() {
        return quantity;
    }

    public ProductType getType() {
        return type;
    }

    public double getPrice(Customer customer) {
        if (customer != null && customer.isMember()) {
            return price * 0.95; // ลดราคา 5% สำหรับสมาชิก
        }
        return price; // ไม่ใช่สมาชิกหรือ customer เป็น null
    }

    public void reduceStock(int amount) {
        if (quantity >= amount) {
            quantity -= amount;
        } else {
            System.out.println("Insufficient stock for product: " + name);
        }
    }

    public int getStock() {
        return quantity;
    }
    public boolean isInStock() {
        return quantity > 0;
    }

    // Static method to load products from a JSON file
    public static void loadProductsFromJson(Manager manager, String filename) {
        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader(filename)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray productsArray = (JSONArray) jsonObject.get("products");

            for (Object obj : productsArray) {
                JSONObject productJson = (JSONObject) obj;
                String name = (String) productJson.get("name");
                double price = ((Number) productJson.get("price")).doubleValue(); // Safely retrieve double
                long quantity = (long) productJson.get("quantity");
                String type = (String) productJson.get("type");

                ProductType productType = ProductType.valueOf(type.toUpperCase());
                manager.addProduct(new Product(name, price, (int) quantity, productType));
            }
        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("Error parsing the JSON data: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid product type in JSON: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return name + " (" + (type != null ? type : "No Type") + ") - Price: " + price + " Quantity: " + quantity;
    }
}

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class Bill {
    private int billNumber;
    private List<Product> products;
    private double totalAmount;
    private String date;

    public Bill(int billNumber, double totalAmount, String date, List<Product> products) {
        this.billNumber = billNumber;
        this.totalAmount = totalAmount;
        this.date = date;
        this.products = products;
    }

    public int getBillNumber() {
        return billNumber;
    }

    public List<Product> getProducts() {
        return products;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getDate() {
        return date;
    }

    public int getTotalQuantity() {
        int totalQuantity = 0;
        List<GroupedProduct> groupedProducts = groupProducts();
        for (int i = 0; i < groupedProducts.size(); i++) {
            totalQuantity += groupedProducts.get(i).getQuantity();
        }
        return totalQuantity;
    }

    public List<GroupedProduct> groupProducts() {
        List<GroupedProduct> groupedProducts = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Product currentProduct = products.get(i);
            boolean found = false;
            for (int j = 0; j < groupedProducts.size(); j++) {
                GroupedProduct groupedProduct = groupedProducts.get(j);
                if (groupedProduct.getProduct().getName().equals(currentProduct.getName())) {
                    groupedProduct.addQuantity(1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                groupedProducts.add(new GroupedProduct(currentProduct, 1));
            }
        }
        return groupedProducts;
    }

    @SuppressWarnings("unchecked")
    public void saveBillToJson() {
        JSONObject newBillDetails = new JSONObject();
        newBillDetails.put("billNumber", billNumber);
        newBillDetails.put("totalAmount", totalAmount);
        newBillDetails.put("date", date.toString());
    
        JSONArray productsArray = new JSONArray();
        for (GroupedProduct gp : groupProducts()) {
            JSONObject productDetails = new JSONObject();
            productDetails.put("name", gp.getProduct().getName());
            productDetails.put("price", gp.getProduct().getPrice());
            productDetails.put("quantity", gp.getQuantity());
            productsArray.add(productDetails);
        }
    
        newBillDetails.put("products", productsArray);
    
        // Load existing data from data.json
        JSONParser jsonParser = new JSONParser();
        JSONObject existingData = new JSONObject();
    
        try (FileReader reader = new FileReader("data.json")) {
            existingData = (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            // If the file doesn't exist or is empty, we start with an empty JSON object
            System.out.println("Could not load existing data. Creating new data file.");
        }
    
        // Preserve existing data
        JSONArray existingBills = (JSONArray) existingData.get("bills");
        if (existingBills == null) {
            existingBills = new JSONArray();
        }
        existingBills.add(newBillDetails); // Add the new bill to the existing bills array
    
        // Save back to data.json
        existingData.put("bills", existingBills);
    
        // Save the other sections to preserve products and customers
        // Assuming they are already in the existingData from the file
        JSONArray existingProducts = (JSONArray) existingData.get("products");
        JSONArray existingCustomers = (JSONArray) existingData.get("customers");
    
        existingData.put("products", existingProducts); // Preserve existing products
        existingData.put("customers", existingCustomers); // Preserve existing customers
    
        // Write the complete data back to data.json
        try (FileWriter file = new FileWriter("data.json")) {
            file.write(existingData.toJSONString());
            file.flush();
            System.out.println("Bill saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving bill: " + e.getMessage());
        }
    }





    public static void loadBillsFromJson() {
        JSONParser jsonParser = new JSONParser();
    
        try (FileReader reader = new FileReader("data.json")) {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray billsArray = (JSONArray) jsonObject.get("bills");
    
            for (Object billObj : billsArray) {
                JSONObject billDetails = (JSONObject) billObj;
                System.out.println("Bill Number: " + billDetails.get("billNumber"));
                System.out.println("Total Amount: " + billDetails.get("totalAmount"));
                System.out.println("Date: " + billDetails.get("date"));
                JSONArray products = (JSONArray) billDetails.get("products");
                for (Object productObj : products) {
                    JSONObject product = (JSONObject) productObj;
                    System.out.println("  - " + product.get("name") + " (Price: " + product.get("price") + ")");
                }
                System.out.println();
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error loading bills: " + e.getMessage());
        }
    }

    public void printBillDetails() {
        System.out.println("----- Bill Details -----");
        System.out.println("Bill Number: " + billNumber);
        System.out.println("Date: " + date.toString());
        System.out.println("Total Amount: " + totalAmount);
        System.out.println("Products:");
        for (GroupedProduct gp : groupProducts()) {
            System.out.println("  - " + gp.getProduct().getName() + " (Price: " + gp.getProduct().getPrice() + ", Quantity: " + gp.getQuantity() + ")");
        }
        System.out.println("------------------------");
    }
    
}

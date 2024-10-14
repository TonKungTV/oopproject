import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager("John");
        Cashier cashier = new Cashier("Alice");
        List<Customer> customers = new ArrayList<>();
        List<Bill> bills = new ArrayList<>(); // ถ้ายังไม่มีบิล ก็ให้สร้างรายการที่ว่างเปล่า
        loadProductsFromJson(manager, "data.json");
        customers = Customer.loadCustomersFromFile("data.json");
        List<Bill> allBills = Customer.loadBillsFromFile("data.json");

        Scanner scanner = new Scanner(System.in);

        boolean running = true;

        // Role selection
        while (running) {
            System.out.println("Select your role:");
            System.out.println("1. Customer");
            System.out.println("2. Cashier");
            System.out.println("3. Manager");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int roleChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (roleChoice) {
                case 1: // Customer role
                    System.out.print("Enter Membership ID to access your account or leave blank to view all bills: ");
                    String inputId = scanner.nextLine();
                    if (inputId.isEmpty()) {
                        // ลูกค้าทุกคนสามารถดูบิลย้อนหลังได้ (โหลดบิลจากไฟล์ JSON)
                        Customer.loadBillsFromFile("data.json");
                    } else {
                        // ถ้ากรอก Membership ID ให้ค้นหาและทำการเติมเงิน
                        Customer currentCustomer = Customer.findCustomerByMembership(customers, inputId);
                        if (currentCustomer != null) {
                            currentCustomer.handleCustomerRole(scanner, customers,allBills);
                        } else {
                            System.out.println("Invalid Membership ID.");
                        }
                    }
                    break;

                case 2: // Cashier
                    // Call handleCashierRole from the Cashier object
                    cashier.handleCashierRole(customers, scanner, manager);
                    break;
                case 3: // Manager
                    // Call handleManagerRole from the Manager object
                    manager.handleManagerRole(scanner);
                    break;
                case 4: // Exit
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
        System.out.println("Thank you for using the system!");
    }

    private static void loadProductsFromJson(Manager manager, String filename) {
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

}
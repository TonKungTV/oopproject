import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class Customer {
    private String name;
    private double wallet;
    private boolean isMember;
    private String membershipId;
    private int points;
    
    private List<Bill> billHistory = new ArrayList<>();

    public Customer(String name, String membershipId, double wallet, int points) {
        this.name = name;
        this.membershipId = membershipId;
        this.wallet = wallet;
        this.isMember = true; // แก้ไขเป็นสมาชิกทุกคน
        this.points = points; // กำหนดคะแนน
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int amount) {
        points += amount;
    }

    public void addMoney(double amount) {
        wallet += amount;
    }

    public void deductFromWallet(double amount) {
        if (wallet >= amount) {
            wallet -= amount;
        } else {
            System.out.println("Insufficient funds in wallet.");
        }
    }

    public double getWallet() {
        return wallet;
    }
    
    public void setMembership(boolean isMember, String membershipId) {
        this.isMember = isMember;
        this.membershipId = membershipId;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public boolean isMember() {
        return isMember;
    }

    public String getName() {
        return name;
    }

    public void addBill(Bill bill) {
        billHistory.add(bill);
        // เพิ่มคะแนนจากยอดเงินของบิล
        int earnedPoints = (int) bill.getTotalAmount(); // 1 point per unit currency spent
        addPoints(earnedPoints);
        System.out.println("Earned points: " + earnedPoints);
    }

    public static List<Bill> loadBillsFromFile(String filePath) {
        List<Bill> bills = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(filePath)) {
            // Parse the JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray billArray = (JSONArray) jsonObject.get("bills");

            // Convert each JSON object into a Bill object
            for (Object billObj : billArray) {
                JSONObject billJson = (JSONObject) billObj;
                long billNumber = (long) billJson.get("billNumber");
                double totalAmount = (double) billJson.get("totalAmount");
                String date = (String) billJson.get("date");
                JSONArray productsArray = (JSONArray) billJson.get("products");

                // Create list of products from the bill
                List<Product> products = new ArrayList<>();
                for (Object productObj : productsArray) {
                    JSONObject productJson = (JSONObject) productObj;
                    String productName = (String) productJson.get("name");
                    double productPrice = (double) productJson.get("price");
                    long quantity = (long) productJson.get("quantity");

                    Product product = new Product(productName, productPrice, (int) quantity, null);
                    products.add(product);
                }

                // Create and add Bill to the list
                Bill bill = new Bill(billNumber, totalAmount, date, pro);
                bills.add(bill);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return bills;
    }

    public void viewBill(List<Bill> allBills, int billNumber) {
        Bill foundBill = null;
        for (Bill bill : allBills) {
            if (bill.getBillNumber() == billNumber) {
                foundBill = bill;
                break;
            }
        }

        if (foundBill != null) {
            System.out.println("Bill found: ");
            foundBill.printBillDetails();
        } else {
            System.out.println("No bill found with number: " + billNumber);
        }
    }

    public void viewAllBills(List<Bill> allBills) {
        if (allBills.isEmpty()) {
            System.out.println("No bills found.");
        } else {
            System.out.println("----- All Bills -----");
            for (Bill bill : allBills) {
                bill.printBillDetails();
                System.out.println("---------------------");
            }
        }
    }

    public static List<Customer> loadCustomersFromFile(String filePath) {
        List<Customer> customers = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(filePath)) {
            // Parse ไฟล์ JSON เป็น JSONObject
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray customerList = (JSONArray) jsonObject.get("customers");

            // แปลงข้อมูลแต่ละตัวใน JSONArray เป็น Customer
            for (Object customerObj : customerList) {
                JSONObject customerJson = (JSONObject) customerObj;

                String name = (String) customerJson.get("name");
                String membershipId = (String) customerJson.get("membershipId");
                double wallet = (double) customerJson.get("wallet");
                long points = (long) customerJson.get("points");

                Customer customer = new Customer(name, membershipId, wallet, (int) points);
                customers.add(customer);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return customers;
    }

    public static Customer findCustomerByMembership(List<Customer> customers, String membershipId) {
        for (Customer customer : customers) {
            if (customer.getMembershipId().equals(membershipId)) {
                return customer;
            }
        }
        return null; // If no customer found
    }

    public void handleCustomerRole(Scanner scanner, List<Customer> customers, List<Bill> allBills) {
        boolean customerRunning = true;
        while (customerRunning) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. View Bill History");
            System.out.println("2. Add Money to Wallet");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int customerChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
    
            switch (customerChoice) {
                case 1:
                    System.out.print("Enter Bill Number or leave blank for all bills: ");
                    String input = scanner.nextLine();
                    if (input.isEmpty()) {
                        this.viewAllBills(allBills); // Load and display all bills
                    } else {
                        int billNumber = Integer.parseInt(input);
                        this.viewBill(allBills, billNumber); // Load and display the specific bill
                    }
                    break;
                case 2:
                    System.out.print("Enter Membership ID: ");
                    String membershipId = scanner.nextLine();
                    Customer customer = findCustomerByMembership(customers, membershipId);
                    if (customer == null) {
                        System.out.println("Customer not found.");
                        break;
                    }
    
                    System.out.print("Enter amount to add to wallet: ");
                    double amount = scanner.nextDouble();
                    customer.addMoney(amount);
                    System.out.println("Money added successfully.");
                    break;
                case 3:
                    customerRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    
}

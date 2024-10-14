import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Cashier {
    private String name;
    private PaymentProcessor paymentProcessor = new PaymentProcessor();
    private ReceiptPrinter receiptPrinter = new ReceiptPrinter();
    private Customer customer;

    public Cashier(String name) {
        this.name = name;
    }

    public void displayProducts(List<Product> products) {
        System.out.println("\n--- Product List ---");
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.printf("%d. %s\n", (i + 1), product);
        }
        System.out.println("----------------------");
    }

    public Bill processPayment(Customer customer, List<Product> products, boolean payWithWallet, Manager manager) {
        Bill bill = paymentProcessor.processPayment(customer, products, true, manager);
        if (bill != null) {
            System.out.println("Payment and bill generation successful.");
            manager.addBill(bill); // Add bill to Manager's allBills
            bill.saveBillToJson(); // Save the bill to a JSON file

            // เรียกใช้การพิมพ์ใบเสร็จ
            this.printReceipt(bill); // ส่ง customer เข้าไปในฟังก์ชัน printReceipt
        } else {
            System.out.println("Payment failed.");
        }
        return bill;
    }

    public void registerMembership(Customer customer, Scanner scanner) {
        System.out.print("Would you like to register for membership? (yes/no): ");
        String registerResponse = scanner.next();
        switch (registerResponse.toLowerCase()) {
            case "yes":
                System.out.print("Enter a membership ID: ");
                String membershipId = scanner.next();
                customer.setMembership(true, membershipId);
                System.out.println("Membership registered successfully!");
                break;
            case "no":
                System.out.println("Proceeding without membership.");
                break;
            default:
                System.out.println("Invalid response. Proceeding without membership.");
                break;
        }
    }

    public void checkMembershipStatus(Customer customer, Scanner scanner) {
        System.out.print("Is the customer a member? (yes/no): ");
        String isMemberResponse = scanner.next();
        switch (isMemberResponse.toLowerCase()) {
            case "yes":
                System.out.print("Enter membership ID: ");
                String membershipId = scanner.next();
                customer.setMembership(true, membershipId);
                System.out.println("Membership verified.");
                break;
            case "no":
                registerMembership(customer, scanner);
                break;
            default:
                System.out.println("Invalid response. Registering as non-member.");
                registerMembership(customer, scanner);
                break;
        }
    }

    public void printReceipt(Bill bill) {
        String storeName = "NextMart";
        String storeAddress = "123 Main St, City, Country";
        String storePhone = "Phone: 123-456-7890";

        String separator = "--------------------------------------";
        String lineSeparator = "======================================";

        System.out.printf("Bill Number: %-29s\n", bill.getBillNumber());
        System.out.println("\n" + lineSeparator);
        System.out.printf("%-20s %20s\n", storeName, " ");
        System.out.printf("%-20s %20s\n", storeAddress, " ");
        System.out.printf("%-20s %20s\n", storePhone, " ");
        System.out.println(lineSeparator);
        System.out.printf("Date: %-29s\n", bill.getDate());
        System.out.println(separator);
        System.out.println("Items:");
        List<GroupedProduct> groupedProducts = bill.groupProducts();
        for (GroupedProduct gp : groupedProducts) {
            System.out.printf("%-25s %10.2f x %d\n", gp.getProduct().getName(), gp.getProduct().getPrice(null),
                    gp.getQuantity());
        }
        System.out.println(separator);
        System.out.printf("Total Quantity: %-23d\n", bill.getTotalQuantity());
        System.out.printf("Total Amount: %-25.2f\n", bill.getTotalAmount());
        System.out.println(separator);

        // แสดงข้อมูลสมาชิกถ้ามี
        if (bill.getTotalAmount() > 0) {
            System.out.println(
                    "Member information: " + (customer.isMember() ? customer.getMembershipId() : "Non-Member"));
        }

        System.out.println(lineSeparator);
        System.out.println("Thank you for shopping at " + storeName + "!");
        System.out.println(lineSeparator);
    }

    public void handleCashierRole(List<Customer> customers, Scanner scanner, Manager manager) {
        System.out.print("Enter Membership ID (leave blank if not a member): ");
        String membershipId = scanner.nextLine();

        Customer customer = null;
        if (!membershipId.isEmpty()) {
            customer = findCustomerByMembership(customers, membershipId);
        }

        if (customer == null) {
            System.out.println("Customer not found. Proceeding as non-member.");
            customer = new Customer("Guest", membershipId, 100.0, 0); // สร้างลูกค้า guest ถ้าไม่เจอ
            customers.add(customer);
        }

        boolean cashierRunning = true;
        while (cashierRunning) {
            System.out.println("\n--- Cashier Menu ---");
            System.out.println("1. Process Payment");
            System.out.println("2. Register Membership");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int cashierChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (cashierChoice) {
                case 1:
                    this.displayProducts(manager.getProducts());

                    List<Product> products = new ArrayList<>();
                    boolean selectingProducts = true;

                    while (selectingProducts) {
                        System.out.print("Enter Product ID (or 'done' to finish): ");
                        String input = scanner.nextLine();

                        if (input.equalsIgnoreCase("done")) {
                            selectingProducts = false;
                            continue; // Exit product selection loop
                        }

                        try {
                            int productId = Integer.parseInt(input.trim()) - 1; // Convert input to zero-based index
                            if (productId >= 0 && productId < manager.getProducts().size()) {
                                Product selectedProduct = manager.getProducts().get(productId);

                                System.out.print("Enter quantity for " + selectedProduct.getName() + ": ");
                                int quantity = scanner.nextInt();
                                scanner.nextLine(); // Consume newline

                                // Validate quantity
                                if (quantity > 0 && quantity <= selectedProduct.getQuantity()) {
                                    // Create copies of the product for the specified quantity
                                    for (int i = 0; i < quantity; i++) {
                                        products.add(selectedProduct);
                                    }
                                    System.out
                                            .println(quantity + " of " + selectedProduct.getName() + " added to cart.");
                                } else {
                                    System.out.println("Invalid quantity. Please enter a quantity between 1 and "
                                            + selectedProduct.getQuantity());
                                }
                            } else {
                                System.out.println("Invalid Product ID: " + (productId + 1));
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a valid Product ID or 'done' to finish.");
                        }
                    }

                    System.out.print("Pay with Wallet? (yes/no): ");
                    boolean payWithWallet = scanner.next().equalsIgnoreCase("yes");
                    PaymentProcessor paymentProcessor = new PaymentProcessor();
                    scanner.nextLine(); // Consume newline

                    // After processing the payment in handleCashierRole
                    Bill bill = paymentProcessor.processPayment(customer, products, true, manager);
                    if (bill != null) {
                        System.out.println("Payment and bill generation successful.");
                        manager.addBill(bill); // Add bill to Manager's allBills
                        bill.saveBillToJson(); // Save the bill to a JSON file
                    } else {
                        System.out.println("Payment failed.");
                    }

                    break;

                case 2:
                    this.registerMembership(customer, scanner);
                    break;
                case 3:
                    cashierRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static Customer findCustomerByMembership(List<Customer> customers, String membershipId) {
        for (Customer customer : customers) {
            if (customer.getMembershipId().equals(membershipId)) {
                return customer;
            }
        }
        return null; // If no customer found
    }
}

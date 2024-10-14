import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.text.SimpleDateFormat;

class Manager {
    private String name;
    private int nextBillNumber;
    private List<Bill> billHistory = new ArrayList<>();
    private List<Bill> allBills = new ArrayList<>();
    private List<Product> products = new ArrayList<>();

    public Manager(String name) {
        this.name = name;
        this.products = new ArrayList<>();
        this.allBills = new ArrayList<>();
        this.nextBillNumber = 1;
    }

    public void addBill(Bill bill) {
        allBills.add(bill);
        billHistory.add(bill); // เพิ่มบิลใน billHistory ด้วย
    }

    public List<Bill> getAllBills() {
        return allBills;
    }

    public void viewAllBills() {
        for (Bill bill : allBills) { // เปลี่ยนจาก billHistory เป็น allBills
            System.out.println(bill);
        }
    }

    public void viewBill(int billNumber) {
        for (Bill bill : allBills) { // เปลี่ยนจาก billHistory เป็น allBills
            if (bill.getBillNumber() == billNumber) {
                System.out.println(bill);
                return;
            }
        }
        System.out.println("Bill not found.");
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getProducts() {
        return products;
    }

    public void displayProducts() {
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

    public void viewIncome(String period) {
        double totalIncome = 0.0;
        Date now = new Date();

        // ฟอร์แมตวันเวลา
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat weekFormat = new SimpleDateFormat("yyyy-ww");
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        // เก็บวันเวลาปัจจุบัน
        String currentDate = sdf.format(now);
        String currentWeek = weekFormat.format(now);
        String currentMonth = monthFormat.format(now);
        String currentYear = yearFormat.format(now);

        for (int i = 0; i < allBills.size(); i++) {
            Bill bill = allBills.get(i);
            String billDate = sdf.format(bill.getDate());
            String billWeek = weekFormat.format(bill.getDate());
            String billMonth = monthFormat.format(bill.getDate());
            String billYear = yearFormat.format(bill.getDate());

            switch (period.toLowerCase()) {
                case "daily":
                    if (billDate.equals(currentDate)) {
                        totalIncome += bill.getTotalAmount();
                    }
                    break;
                case "weekly":
                    if (billWeek.equals(currentWeek)) {
                        totalIncome += bill.getTotalAmount();
                    }
                    break;
                case "monthly":
                    if (billMonth.equals(currentMonth)) {
                        totalIncome += bill.getTotalAmount();
                    }
                    break;
                case "yearly":
                    if (billYear.equals(currentYear)) {
                        totalIncome += bill.getTotalAmount();
                    }
                    break;
                default:
                    System.out.println("Invalid period. Choose from daily, weekly, monthly, yearly.");
                    return;
            }
        }

        // แสดงผลรวมรายได้โดยฟอร์แมตให้สวยงาม
        System.out.printf("Total %s income: %.2f\n", period, totalIncome);
    }

    public int getNextBillNumber() {
        return nextBillNumber++;
    }
    // Add the method here
    public void handleManagerRole(Scanner scanner) {
        boolean managerRunning = true;
        while (managerRunning) {
            System.out.println("\n--- Manager Menu ---");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. View Income");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int managerChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (managerChoice) {
                case 1:
                    System.out.print("Enter Product Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Product Price: ");
                    double price = scanner.nextDouble();
                    System.out.print("Enter Product Quantity: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter Product Type (SNACK, DRINK, FOOD): ");
                    String type = scanner.nextLine();
                    ProductType productType = ProductType.valueOf(type.toUpperCase());
                    this.addProduct(new Product(name, price, quantity, productType));
                    System.out.println("Product added successfully.");
                    break;
                case 2:
                    this.displayProducts();
                    break;
                case 3:
                    System.out.println("Select the period for income view:");
                    System.out.println("1. Daily");
                    System.out.println("2. Weekly");
                    System.out.println("3. Monthly");
                    System.out.println("4. Yearly");
                    System.out.print("Choose an option: ");
                    int viewIncomeChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    switch (viewIncomeChoice) {
                        case 1:
                            this.viewIncome("daily");
                            break;
                        case 2:
                            this.viewIncome("weekly");
                            break;
                        case 3:
                            this.viewIncome("monthly");
                            break;
                        case 4:
                            this.viewIncome("yearly");
                            break;
                        default:
                            System.out.println("Invalid option. Please try again.");
                            break;
                    }
                    break;
                case 4:
                    managerRunning = false; // Exit manager menu
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}

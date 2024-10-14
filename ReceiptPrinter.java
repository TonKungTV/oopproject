import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class ReceiptPrinter {
    public void printReceipt(Bill bill, Customer customer) {
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
            System.out.printf("%-25s %10.2f x %d\n", gp.getProduct().getName(), gp.getProduct().getPrice(null), gp.getQuantity());
        }
        System.out.println(separator);
        System.out.printf("Total Quantity: %-23d\n", bill.getTotalQuantity());
        System.out.printf("Total Amount: %-25.2f\n", bill.getTotalAmount());
        System.out.println(separator);
    
        // แสดงข้อมูลสมาชิกถ้ามี
        if (customer != null) {
            if (customer.isMember()) {
                System.out.println("Member information: " + customer.getMembershipId());
            } else {
                System.out.println("Member information: Non-Member");
            }
        } else {
            System.out.println("Customer information: Unknown");
        }
    
        System.out.println(lineSeparator);
        System.out.println("Thank you for shopping at " + storeName + "!");
        System.out.println(lineSeparator);
    }
    
}

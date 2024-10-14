import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class PaymentProcessor {
    private static int billCounter = 0; // ตัวนับหมายเลขบิล

    public Bill processPayment(Customer customer, List<Product> products, boolean payWithWallet, Manager manager) {
        if (customer == null) {
            System.out.println("Customer is null. Cannot process payment.");
            return null; // คืนค่า null ถ้า customer เป็น null
        }

        double totalAmount = 0.0;
        int totalPoints = 0;
        List<Product> purchasedProducts = new ArrayList<>();

        for (Product product : products) {
            // ตรวจสอบว่าสินค้ามีในสต็อกเพียงพอหรือไม่
            if (product.getStock() > 0) {
                double productPrice = product.getPrice(customer); // ราคาลดสำหรับสมาชิก
                totalAmount += productPrice;

                // ลดจำนวนสต็อกสินค้า
                product.reduceStock(1);

                // สะสมคะแนน (1 คะแนนต่อสินค้า)
                totalPoints += productPrice;

                purchasedProducts.add(product); // เพิ่มสินค้าเข้าไปในรายการที่ซื้อ
            } else {
                System.out.println("Product " + product.getName() + " is out of stock.");
            }
        }

        if (purchasedProducts.isEmpty()) {
            System.out.println("No products purchased due to insufficient stock.");
            return null; // ถ้าไม่มีสินค้าเลย ไม่สร้างบิล
        }

        if (payWithWallet) {
            if (customer.getWallet() >= totalAmount) {
                customer.deductFromWallet(totalAmount); // หักเงินจาก Wallet
                System.out.println("Payment successful from Wallet!");
                customer.addPoints(totalPoints);
                System.out.println("Earned " + totalPoints + " points!");
            } else {
                System.out.println("Insufficient funds in wallet.");
                return null;
            }
        } else {
            customer.addPoints(totalPoints);
            System.out.println("Earned " + totalPoints + " points!");
            System.out.println("Payment successful with Cash!");
        }

        Bill bill = new Bill(++billCounter, purchasedProducts, totalAmount, new Date()); // สร้างบิลใหม่และเพิ่มหมายเลขบิล
        customer.addBill(bill); // เพิ่มบิลเข้าไปในประวัติลูกค้า

        if (manager != null) {
            manager.addBill(bill); // บันทึกบิลลงใน Manager ด้วย
        }

        return bill;
    }
}

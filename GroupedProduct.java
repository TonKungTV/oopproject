class GroupedProduct {
    private Product product;
    private int quantity;

    public GroupedProduct(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int amount) {
        quantity += amount;
    }

    public double getTotalPrice(Customer customer) {
        // คำนวณราคาสินค้าทั้งหมดตามจำนวน
        double unitPrice = product.getPrice(customer);
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        return String.format("%s x%d - Total: %.2f", product.getName(), quantity, getTotalPrice(null));
    }
}

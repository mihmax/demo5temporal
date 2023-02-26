package ua.dp.maxym.demo5.order.domain;

import org.springframework.data.annotation.Id;

import java.util.Objects;
import java.util.UUID;

public class Order {
    @Id
    private UUID id;
    private OrderStatus status;
    private String statusReason;
    private String user;
    private String goods;
    private Integer quantity;
    private Double price;

    public Order() {}

    public Order(
            UUID id,
            OrderStatus status,
            String statusReason,
            String user,
            String goods,
            Integer quantity,
            Double price
    ) {
        this.id = id;
        this.status = status;
        this.statusReason = statusReason;
        this.user = user;
        this.goods = goods;
        this.quantity = quantity;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Order) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.status, that.status) &&
                Objects.equals(this.statusReason, that.statusReason) &&
                Objects.equals(this.user, that.user) &&
                Objects.equals(this.goods, that.goods) &&
                Objects.equals(this.quantity, that.quantity) &&
                Objects.equals(this.price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, statusReason, user, goods, quantity, price);
    }

    @Override
    public String toString() {
        return "Order[" +
                "id=" + id + ", " +
                "status=" + status + ", " +
                "statusReason=" + statusReason + ", " +
                "user=" + user + ", " +
                "goods=" + goods + ", " +
                "quantity=" + quantity + ", " +
                "price=" + price + ']';
    }

}

package com.example.webinanao.Dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {

    @NotNull(message = "UserId không được để trống")
    private Integer userId;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String receiverName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String receiverPhone;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String shippingAddress;

    @NotBlank(message = "Thành phố không được để trống")
    private String city;

    private String discountCode; // optional

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod; // BankTransfer, COD, ...

    private String note;

    @NotNull(message = "Danh sách sản phẩm không được để trống")
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        @NotNull
        private Integer productId;

        private Integer designId; // optional

        @NotBlank
        private String size;

        @NotBlank
        private String color;

        @NotNull
        private Integer quantity;

        @NotNull
        private BigDecimal unitPrice;

        private String note;
    }
}

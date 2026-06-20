package com.example.webinanao.Controller;

import com.example.webinanao.Dto.request.OrderRequest;
import com.example.webinanao.Dto.response.OrderResponse;
import com.example.webinanao.Entity.Order;
import com.example.webinanao.Entity.OrderHistory;
import com.example.webinanao.Repo.OrderRepository;
import com.example.webinanao.Service.OrderService;
import com.example.webinanao.Repo.OrderHistoryRepository;
import com.example.webinanao.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;


    // Đặt hàng mới
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    // Lấy danh sách đơn hàng của user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    // Xem chi tiết một đơn hàng
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Integer orderId,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String cancelledBy) {
        String canceller = cancelledBy != null ? cancelledBy : "user";
        return ResponseEntity.ok(orderService.cancelOrder(orderId, canceller, reason));
    }

    // 👉 SỬA LẠI - Thêm tham số updatedBy và note
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Integer orderId,
            @RequestParam String status,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) String updatedBy) {
        String updater = updatedBy != null ? updatedBy : "admin";
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status, updater, note));
    }


    // Admin: lấy tất cả đơn hàng
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/admin/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetailForAdmin(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // 👉 THÊM ENDPOINT KIỂM TRA TRẠNG THÁI THANH TOÁN
    @GetMapping("/{orderId}/payment-status")
    public ResponseEntity<?> checkPaymentStatus(@PathVariable Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        // Đã thanh toán nếu status không phải pending hoặc cancelled
        boolean isPaid = !"pending".equalsIgnoreCase(order.getStatus())
                && !"cancelled".equalsIgnoreCase(order.getStatus());

        return ResponseEntity.ok(Map.of(
                "paid", isPaid,
                "status", order.getStatus(),
                "orderId", orderId,
                "orderNumber", order.getOrderNumber()
        ));
    }

    @GetMapping("/{orderId}/history")
    public ResponseEntity<List<OrderHistory>> getOrderHistory(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderHistory(orderId));
    }

    // ========== EXPORT EXCEL ==========
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportOrdersToExcel(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {

        System.out.println("=== EXPORT EXCEL START ===");
        System.out.println("Status: " + status);
        System.out.println("Keyword: " + keyword);

        List<OrderResponse> orders = orderService.getAllOrders();
        System.out.println("Total orders: " + orders.size());

        if (status != null && !status.isEmpty()) {
            orders = orders.stream()
                    .filter(order -> order.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
            System.out.println("After status filter: " + orders.size());
        }

        if (keyword != null && !keyword.isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            orders = orders.stream()
                    .filter(order ->
                            order.getOrderNumber().toLowerCase().contains(lowerKeyword) ||
                                    order.getReceiverName().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
            System.out.println("After keyword filter: " + orders.size());
        }

        byte[] excelBytes = createExcelFile(orders);
        System.out.println("Excel created, size: " + excelBytes.length + " bytes");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = "don_hang_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    private byte[] createExcelFile(List<OrderResponse> orders) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("DonHang");

            // Tạo header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"STT", "Mã đơn", "Khách hàng", "SĐT", "Địa chỉ",
                    "Thành phố", "Tổng tiền", "Trạng thái", "Phương thức TT", "Ngày đặt"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                sheet.setColumnWidth(i, 4000);
            }

            // Đổ dữ liệu
            int rowNum = 1;
            for (OrderResponse order : orders) {
                try {
                    Row row = sheet.createRow(rowNum);

                    row.createCell(0).setCellValue(rowNum);
                    row.createCell(1).setCellValue(getSafeValue(order.getOrderNumber()));
                    row.createCell(2).setCellValue(getSafeValue(order.getReceiverName()));
                    row.createCell(3).setCellValue(getSafeValue(order.getReceiverPhone()));
                    row.createCell(4).setCellValue(getSafeValue(order.getShippingAddress()));
                    row.createCell(5).setCellValue(getSafeValue(order.getCity()));

                    double total = order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0;
                    row.createCell(6).setCellValue(total);

                    row.createCell(7).setCellValue(getStatusText(order.getStatus()));
                    row.createCell(8).setCellValue(getSafeValue(order.getPaymentMethod()));

                    String dateStr = order.getCreatedAt() != null ?
                            order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
                    row.createCell(9).setCellValue(dateStr);

                    rowNum++;
                } catch (Exception e) {
                    System.err.println("Lỗi dòng " + rowNum + ": " + e.getMessage());
                }
            }

            // Auto resize
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            System.err.println("Lỗi tạo Excel: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi tạo file Excel: " + e.getMessage());
        }
    }

    private String getSafeValue(String value) {
        return value != null ? value : "";
    }

    private String getStatusText(String status) {
        if (status == null) return "";
        switch (status.toLowerCase()) {
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "shipping": return "Đang giao";
            case "delivered": return "Đã giao";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }
}
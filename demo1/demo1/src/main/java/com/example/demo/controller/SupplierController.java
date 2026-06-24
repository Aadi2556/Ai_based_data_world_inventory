package com.example.demo.controller;

import com.example.demo.dto.PurchaseOrderRequest;
import com.example.demo.dto.SupplierRequest;
import com.example.demo.model.PurchaseOrder;
import com.example.demo.model.Supplier;
import com.example.demo.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/suppliers")
@PreAuthorize("hasRole('ADMIN')")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    // ===================== PAGE NAVIGATION =====================

    @GetMapping
    public String suppliersPage(Model model) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("orders", supplierService.getAllPurchaseOrders());
        return "suppliers";
    }

    // ===================== SUPPLIER REST API =====================

    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        return supplierService.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<?> addSupplier(@RequestBody SupplierRequest request) {
        try {
            Supplier supplier = supplierService.addSupplier(request);
            return ResponseEntity.ok(supplier);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/api/orders/{id}")
    @ResponseBody
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            return supplierService.getPurchaseOrderById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/api/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateSupplier(@PathVariable Long id, @RequestBody SupplierRequest request) {
        try {
            Supplier supplier = supplierService.updateSupplier(id, request);
            return ResponseEntity.ok(supplier);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        try {
            supplierService.deleteSupplier(id);
            return ResponseEntity.ok(Map.of("message", "Supplier deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===================== PURCHASE ORDER REST API =====================

    @GetMapping("/api/orders")
    @ResponseBody
    public ResponseEntity<List<PurchaseOrder>> getAllOrders() {
        return ResponseEntity.ok(supplierService.getAllPurchaseOrders());
    }

    @GetMapping("/api/orders/supplier/{supplierId}")
    @ResponseBody
    public ResponseEntity<List<PurchaseOrder>> getOrdersBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(supplierService.getOrdersBySupplier(supplierId));
    }

    @PostMapping("/api/orders/place")
    @ResponseBody
    public ResponseEntity<?> placePurchaseOrder(@RequestBody PurchaseOrderRequest request) {
        try {
            PurchaseOrder order = supplierService.placePurchaseOrder(request);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/api/orders/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            PurchaseOrder order = supplierService.updateOrderStatus(id, body.get("status"));
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/api/orders/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            supplierService.deletePurchaseOrder(id);
            return ResponseEntity.ok(Map.of("message", "Order deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
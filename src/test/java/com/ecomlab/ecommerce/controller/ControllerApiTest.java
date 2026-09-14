package com.ecomlab.ecommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.dto.request.AddressRequest;
import com.ecomlab.ecommerce.dto.request.AdjustInventoryRequest;
import com.ecomlab.ecommerce.dto.request.CartItemRequest;
import com.ecomlab.ecommerce.dto.request.CategoryRequest;
import com.ecomlab.ecommerce.dto.request.CreatePaymentRequest;
import com.ecomlab.ecommerce.dto.request.CreateProductRequest;
import com.ecomlab.ecommerce.dto.request.CreateProductVariantRequest;
import com.ecomlab.ecommerce.dto.request.CreateReturnRequest;
import com.ecomlab.ecommerce.dto.request.PaymentWebhookRequest;
import com.ecomlab.ecommerce.dto.request.ShippingZoneRequest;
import com.ecomlab.ecommerce.dto.request.UpsertInventoryRequest;
import com.ecomlab.ecommerce.dto.request.WarehouseRequest;
import com.ecomlab.ecommerce.dto.request.WarehouseShippingZoneRequest;
import com.ecomlab.ecommerce.dto.response.AddressResponse;
import com.ecomlab.ecommerce.dto.response.CartResponse;
import com.ecomlab.ecommerce.dto.response.CategoryResponse;
import com.ecomlab.ecommerce.dto.response.CheckoutResponse;
import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.InventoryResponse;
import com.ecomlab.ecommerce.dto.response.NotificationResponse;
import com.ecomlab.ecommerce.dto.response.OrderResponse;
import com.ecomlab.ecommerce.dto.response.PageResponse;
import com.ecomlab.ecommerce.dto.response.PaymentResponse;
import com.ecomlab.ecommerce.dto.response.ProductAdminResponse;
import com.ecomlab.ecommerce.dto.response.ProductSummaryResponse;
import com.ecomlab.ecommerce.dto.response.ProductVariantAdminResponse;
import com.ecomlab.ecommerce.dto.response.ProductVariantDetailResponse;
import com.ecomlab.ecommerce.dto.response.ReturnResponse;
import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import com.ecomlab.ecommerce.dto.response.ShipmentSummaryResponse;
import com.ecomlab.ecommerce.dto.response.ShippingZoneResponse;
import com.ecomlab.ecommerce.dto.response.TokenPairResponse;
import com.ecomlab.ecommerce.dto.response.UserResponse;
import com.ecomlab.ecommerce.dto.response.WarehouseResponse;
import com.ecomlab.ecommerce.dto.response.WarehouseShippingZoneResponse;
import com.ecomlab.ecommerce.service.AdminProductService;
import com.ecomlab.ecommerce.service.AuthenticationService;
import com.ecomlab.ecommerce.service.CartService;
import com.ecomlab.ecommerce.service.CategoryService;
import com.ecomlab.ecommerce.service.CheckoutService;
import com.ecomlab.ecommerce.service.CustomerShipmentService;
import com.ecomlab.ecommerce.service.InventoryAdminService;
import com.ecomlab.ecommerce.service.NotificationService;
import com.ecomlab.ecommerce.service.OrderService;
import com.ecomlab.ecommerce.service.PaymentService;
import com.ecomlab.ecommerce.service.ProductCatalogService;
import com.ecomlab.ecommerce.service.ReturnService;
import com.ecomlab.ecommerce.service.ShipmentAdminService;
import com.ecomlab.ecommerce.service.ShipperShipmentService;
import com.ecomlab.ecommerce.service.ShippingZoneService;
import com.ecomlab.ecommerce.service.UserService;
import com.ecomlab.ecommerce.service.WarehouseService;
import com.ecomlab.ecommerce.service.WarehouseShippingZoneService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = {
      AdminInventoryController.class,
      AdminNotificationController.class,
      AdminProductController.class,
      AdminShipmentController.class,
      AdminShippingZoneController.class,
      AdminWarehouseController.class,
      AuthController.class,
      CartController.class,
      CategoryController.class,
      CheckoutController.class,
      NotificationController.class,
      OrderController.class,
      PaymentController.class,
      ProductController.class,
      ReturnController.class,
      ShipmentController.class,
      ShipperShipmentController.class,
      UserController.class
    })
@AutoConfigureMockMvc(addFilters = false)
class ControllerApiTest {
  private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
  private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
  private static final UUID VARIANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");
  private static final UUID WAREHOUSE_ID = UUID.fromString("00000000-0000-0000-0000-000000000005");
  private static final UUID ADDRESS_ID = UUID.fromString("00000000-0000-0000-0000-000000000006");
  private static final UUID SHIPMENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000007");
  private static final UUID NOTIFICATION_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000008");
  private static final UUID PAYMENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000009");
  private static final UUID SHIPPING_ZONE_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000010");

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AdminProductService adminProductService;
  @MockitoBean private AuthenticationService authenticationService;
  @MockitoBean private CartService cartService;
  @MockitoBean private CategoryService categoryService;
  @MockitoBean private CheckoutService checkoutService;
  @MockitoBean private CustomerShipmentService customerShipmentService;
  @MockitoBean private InventoryAdminService inventoryAdminService;
  @MockitoBean private NotificationService notificationService;
  @MockitoBean private OrderService orderService;
  @MockitoBean private PaymentService paymentService;
  @MockitoBean private ProductCatalogService productCatalogService;
  @MockitoBean private ReturnService returnService;
  @MockitoBean private ShipmentAdminService shipmentAdminService;
  @MockitoBean private ShipperShipmentService shipperShipmentService;
  @MockitoBean private ShippingZoneService shippingZoneService;
  @MockitoBean private UserService userService;
  @MockitoBean private WarehouseService warehouseService;
  @MockitoBean private WarehouseShippingZoneService warehouseShippingZoneService;

  @Test
  void admin_inventory_api_endpoints_delegate_to_service() throws Exception {
    when(inventoryAdminService.list(WAREHOUSE_ID, VARIANT_ID))
        .thenReturn(List.of(inventoryResponse()));
    when(inventoryAdminService.upsert(any(UpsertInventoryRequest.class)))
        .thenReturn(inventoryResponse());
    when(inventoryAdminService.adjust(any(AdjustInventoryRequest.class)))
        .thenReturn(inventoryResponse());

    mockMvc
        .perform(
            get("/api/v1/admin/inventory")
                .param("warehouseId", WAREHOUSE_ID.toString())
                .param("variantId", VARIANT_ID.toString()))
        .andExpect(status().isOk());
    mockMvc
        .perform(putJson("/api/v1/admin/inventory", inventoryRequest()))
        .andExpect(status().isOk());
    mockMvc
        .perform(postJson("/api/v1/admin/inventory/adjust", inventoryAdjustRequest()))
        .andExpect(status().isOk());

    verify(inventoryAdminService).list(WAREHOUSE_ID, VARIANT_ID);
    verify(inventoryAdminService).upsert(any(UpsertInventoryRequest.class));
    verify(inventoryAdminService).adjust(any(AdjustInventoryRequest.class));
  }

  @Test
  void admin_notification_api_endpoint_delegates_to_service() throws Exception {
    when(notificationService.adminPage("PENDING", 0, 20))
        .thenReturn(
            PageResponse.<NotificationResponse>builder()
                .content(List.of(notificationResponse()))
                .page(0)
                .size(20)
                .totalElements(1)
                .build());

    mockMvc
        .perform(
            get("/api/v1/admin/notifications")
                .param("status", "PENDING")
                .param("page", "0")
                .param("size", "20"))
        .andExpect(status().isOk());

    verify(notificationService).adminPage("PENDING", 0, 20);
  }

  @Test
  void admin_product_api_endpoints_delegate_to_service() throws Exception {
    when(adminProductService.create(any(CreateProductRequest.class)))
        .thenReturn(productAdminResponse());
    when(adminProductService.update(eq(PRODUCT_ID), any(CreateProductRequest.class)))
        .thenReturn(productAdminResponse());
    when(adminProductService.createVariant(eq(PRODUCT_ID), any(CreateProductVariantRequest.class)))
        .thenReturn(productVariantAdminResponse());
    when(adminProductService.updateVariant(
            eq(PRODUCT_ID), eq(VARIANT_ID), any(CreateProductVariantRequest.class)))
        .thenReturn(productVariantAdminResponse());

    mockMvc
        .perform(postJson("/api/v1/admin/products", productRequest()))
        .andExpect(status().isCreated());
    mockMvc
        .perform(patchJson("/api/v1/admin/products/" + PRODUCT_ID, productRequest()))
        .andExpect(status().isOk());
    mockMvc
        .perform(delete("/api/v1/admin/products/{id}", PRODUCT_ID))
        .andExpect(status().isNoContent());
    mockMvc
        .perform(postJson("/api/v1/admin/products/" + PRODUCT_ID + "/variants", variantRequest()))
        .andExpect(status().isCreated());
    mockMvc
        .perform(
            patchJson(
                "/api/v1/admin/products/" + PRODUCT_ID + "/variants/" + VARIANT_ID,
                variantRequest()))
        .andExpect(status().isOk());
    mockMvc
        .perform(
            delete(
                "/api/v1/admin/products/{productId}/variants/{variantId}", PRODUCT_ID, VARIANT_ID))
        .andExpect(status().isNoContent());

    verify(adminProductService).create(any(CreateProductRequest.class));
    verify(adminProductService).update(eq(PRODUCT_ID), any(CreateProductRequest.class));
    verify(adminProductService).delete(PRODUCT_ID);
    verify(adminProductService)
        .createVariant(eq(PRODUCT_ID), any(CreateProductVariantRequest.class));
    verify(adminProductService)
        .updateVariant(eq(PRODUCT_ID), eq(VARIANT_ID), any(CreateProductVariantRequest.class));
    verify(adminProductService).deleteVariant(PRODUCT_ID, VARIANT_ID);
  }

  @Test
  void admin_shipment_api_endpoints_delegate_to_service() throws Exception {
    when(shipmentAdminService.assign(SHIPMENT_ID, USER_ID)).thenReturn(shipmentResponse());
    when(shipmentAdminService.readyForPickup(SHIPMENT_ID)).thenReturn(shipmentResponse());

    mockMvc
        .perform(postJson("/api/v1/admin/shipments/" + SHIPMENT_ID + "/assign", assignRequest()))
        .andExpect(status().isOk());
    mockMvc
        .perform(post("/api/v1/admin/shipments/{shipmentId}/ready-for-pickup", SHIPMENT_ID))
        .andExpect(status().isOk());

    verify(shipmentAdminService).assign(SHIPMENT_ID, USER_ID);
    verify(shipmentAdminService).readyForPickup(SHIPMENT_ID);
  }

  @Test
  void admin_warehouse_api_endpoints_delegate_to_service() throws Exception {
    when(warehouseService.warehouses()).thenReturn(List.of(warehouseResponse()));
    when(warehouseService.create(any(WarehouseRequest.class))).thenReturn(warehouseResponse());
    when(warehouseService.update(eq(WAREHOUSE_ID), any(WarehouseRequest.class)))
        .thenReturn(warehouseResponse());
    when(warehouseShippingZoneService.zones(WAREHOUSE_ID))
        .thenReturn(List.of(warehouseShippingZoneResponse()));
    when(warehouseShippingZoneService.replace(
            eq(WAREHOUSE_ID), any(WarehouseShippingZoneRequest.class)))
        .thenReturn(List.of(warehouseShippingZoneResponse()));

    mockMvc.perform(get("/api/v1/admin/warehouses")).andExpect(status().isOk());
    mockMvc
        .perform(postJson("/api/v1/admin/warehouses", warehouseRequest()))
        .andExpect(status().isCreated());
    mockMvc
        .perform(patchJson("/api/v1/admin/warehouses/" + WAREHOUSE_ID, warehouseRequest()))
        .andExpect(status().isOk());
    mockMvc
        .perform(delete("/api/v1/admin/warehouses/{id}", WAREHOUSE_ID))
        .andExpect(status().isNoContent());
    mockMvc
        .perform(get("/api/v1/admin/warehouses/{id}/shipping-zones", WAREHOUSE_ID))
        .andExpect(status().isOk());
    mockMvc
        .perform(
            putJson(
                "/api/v1/admin/warehouses/" + WAREHOUSE_ID + "/shipping-zones",
                warehouseShippingZoneRequest()))
        .andExpect(status().isOk());

    verify(warehouseService).warehouses();
    verify(warehouseService).create(any(WarehouseRequest.class));
    verify(warehouseService).update(eq(WAREHOUSE_ID), any(WarehouseRequest.class));
    verify(warehouseService).delete(WAREHOUSE_ID);
    verify(warehouseShippingZoneService).zones(WAREHOUSE_ID);
    verify(warehouseShippingZoneService)
        .replace(eq(WAREHOUSE_ID), any(WarehouseShippingZoneRequest.class));
  }

  @Test
  void admin_shipping_zone_api_endpoints_delegate_to_service() throws Exception {
    when(shippingZoneService.shippingZones()).thenReturn(List.of(shippingZoneResponse()));
    when(shippingZoneService.create(any(ShippingZoneRequest.class)))
        .thenReturn(shippingZoneResponse());
    when(shippingZoneService.update(eq(SHIPPING_ZONE_ID), any(ShippingZoneRequest.class)))
        .thenReturn(shippingZoneResponse());

    mockMvc.perform(get("/api/v1/admin/shipping-zones")).andExpect(status().isOk());
    mockMvc
        .perform(postJson("/api/v1/admin/shipping-zones", shippingZoneRequest()))
        .andExpect(status().isCreated());
    mockMvc
        .perform(
            patchJson("/api/v1/admin/shipping-zones/" + SHIPPING_ZONE_ID, shippingZoneRequest()))
        .andExpect(status().isOk());
    mockMvc
        .perform(delete("/api/v1/admin/shipping-zones/{id}", SHIPPING_ZONE_ID))
        .andExpect(status().isNoContent());

    verify(shippingZoneService).shippingZones();
    verify(shippingZoneService).create(any(ShippingZoneRequest.class));
    verify(shippingZoneService).update(eq(SHIPPING_ZONE_ID), any(ShippingZoneRequest.class));
    verify(shippingZoneService).delete(SHIPPING_ZONE_ID);
  }

  @Test
  void auth_api_endpoints_delegate_to_service() throws Exception {
    when(authenticationService.register("user@example.com", "strong-pass-123", "User", "web"))
        .thenReturn(tokenPairResponse());
    when(authenticationService.login("user@example.com", "strong-pass-123", "web"))
        .thenReturn(tokenPairResponse());
    when(authenticationService.refresh("refresh-token", "web")).thenReturn(tokenPairResponse());

    mockMvc
        .perform(postJson("/api/v1/auth/register", registerRequest()))
        .andExpect(status().isCreated());
    mockMvc.perform(postJson("/api/v1/auth/login", loginRequest())).andExpect(status().isOk());
    mockMvc.perform(postJson("/api/v1/auth/refresh", refreshRequest())).andExpect(status().isOk());
    mockMvc
        .perform(postJson("/api/v1/auth/logout", refreshRequest()))
        .andExpect(status().isNoContent());

    verify(authenticationService).register("user@example.com", "strong-pass-123", "User", "web");
    verify(authenticationService).login("user@example.com", "strong-pass-123", "web");
    verify(authenticationService).refresh("refresh-token", "web");
    verify(authenticationService).logout("refresh-token");
  }

  @Test
  void cart_api_endpoints_delegate_to_service() throws Exception {
    when(cartService.cart(USER_ID)).thenReturn(cartResponse());
    when(cartService.addCartItem(eq(USER_ID), any(CartItemRequest.class)))
        .thenReturn(cartResponse());
    when(cartService.updateCartItem(USER_ID, VARIANT_ID, 3)).thenReturn(cartResponse());

    mockMvc.perform(get("/api/v1/cart").principal(userPrincipal())).andExpect(status().isOk());
    mockMvc
        .perform(postJson("/api/v1/cart/items", cartItemRequest()).principal(userPrincipal()))
        .andExpect(status().isCreated());
    mockMvc
        .perform(
            patch("/api/v1/cart/items/{id}", VARIANT_ID)
                .principal(userPrincipal())
                .param("quantity", "3"))
        .andExpect(status().isOk());
    mockMvc
        .perform(delete("/api/v1/cart/items/{id}", VARIANT_ID).principal(userPrincipal()))
        .andExpect(status().isNoContent());
    mockMvc
        .perform(delete("/api/v1/cart").principal(userPrincipal()))
        .andExpect(status().isNoContent());

    verify(cartService).cart(USER_ID);
    verify(cartService).addCartItem(eq(USER_ID), any(CartItemRequest.class));
    verify(cartService).updateCartItem(USER_ID, VARIANT_ID, 3);
    verify(cartService).removeCartItem(USER_ID, VARIANT_ID);
    verify(cartService).clearCart(USER_ID);
  }

  @Test
  void category_api_endpoints_delegate_to_service() throws Exception {
    when(categoryService.categories()).thenReturn(List.of(categoryResponse()));
    when(categoryService.create(any(CategoryRequest.class))).thenReturn(categoryResponse());
    when(categoryService.update(eq(PRODUCT_ID), any(CategoryRequest.class)))
        .thenReturn(categoryResponse());

    mockMvc.perform(get("/api/v1/categories")).andExpect(status().isOk());
    mockMvc
        .perform(postJson("/api/v1/categories", categoryRequest()))
        .andExpect(status().isCreated());
    mockMvc
        .perform(patchJson("/api/v1/categories/" + PRODUCT_ID, categoryRequest()))
        .andExpect(status().isOk());
    mockMvc
        .perform(delete("/api/v1/categories/{id}", PRODUCT_ID))
        .andExpect(status().isNoContent());

    verify(categoryService).categories();
    verify(categoryService).create(any(CategoryRequest.class));
    verify(categoryService).update(eq(PRODUCT_ID), any(CategoryRequest.class));
    verify(categoryService).delete(PRODUCT_ID);
  }

  @Test
  void checkout_api_endpoint_delegates_to_service() throws Exception {
    when(checkoutService.checkout(USER_ID, ADDRESS_ID)).thenReturn(checkoutResponse());

    mockMvc
        .perform(postJson("/api/v1/checkout", checkoutRequest()).principal(userPrincipal()))
        .andExpect(status().isCreated());

    verify(checkoutService).checkout(USER_ID, ADDRESS_ID);
  }

  @Test
  void notification_api_endpoints_delegate_to_service() throws Exception {
    when(notificationService.getMyNotifications(USER_ID))
        .thenReturn(List.of(notificationResponse()));
    when(notificationService.read(USER_ID, NOTIFICATION_ID)).thenReturn(notificationResponse());

    mockMvc
        .perform(get("/api/v1/notifications").principal(userPrincipal()))
        .andExpect(status().isOk());
    mockMvc
        .perform(
            post("/api/v1/notifications/{id}/read", NOTIFICATION_ID).principal(userPrincipal()))
        .andExpect(status().isOk());

    verify(notificationService).getMyNotifications(USER_ID);
    verify(notificationService).read(USER_ID, NOTIFICATION_ID);
  }

  @Test
  void order_api_endpoints_delegate_to_service() throws Exception {
    when(orderService.getMyOrders(USER_ID, OrderStatus.CONFIRMED, "cursor", 10))
        .thenReturn(
            CursorPageResponse.<OrderResponse>builder()
                .content(List.of(orderResponse()))
                .size(10)
                .hasNext(false)
                .build());
    when(orderService.getDetails(USER_ID, ORDER_ID)).thenReturn(orderResponse());
    when(orderService.cancel(USER_ID, ORDER_ID)).thenReturn(orderResponse());
    when(customerShipmentService.getMyOrderShipments(USER_ID, ORDER_ID))
        .thenReturn(List.of(shipmentResponse()));

    mockMvc
        .perform(
            get("/api/v1/orders")
                .principal(userPrincipal())
                .param("status", "CONFIRMED")
                .param("cursor", "cursor")
                .param("size", "10"))
        .andExpect(status().isOk());
    mockMvc
        .perform(get("/api/v1/orders/{id}", ORDER_ID).principal(userPrincipal()))
        .andExpect(status().isOk());
    mockMvc
        .perform(get("/api/v1/orders/{id}/shipments", ORDER_ID).principal(userPrincipal()))
        .andExpect(status().isOk());
    mockMvc
        .perform(post("/api/v1/orders/{id}/cancel", ORDER_ID).principal(userPrincipal()))
        .andExpect(status().isOk());

    verify(orderService).getMyOrders(USER_ID, OrderStatus.CONFIRMED, "cursor", 10);
    verify(orderService).getDetails(USER_ID, ORDER_ID);
    verify(customerShipmentService).getMyOrderShipments(USER_ID, ORDER_ID);
    verify(orderService).cancel(USER_ID, ORDER_ID);
  }

  @Test
  void payment_api_endpoints_delegate_to_service() throws Exception {
    when(paymentService.create(eq(USER_ID), eq(ORDER_ID), any(CreatePaymentRequest.class)))
        .thenReturn(paymentResponse());
    when(paymentService.getMyOrderPayments(USER_ID, ORDER_ID))
        .thenReturn(List.of(paymentResponse()));
    when(paymentService.webhook(any(PaymentWebhookRequest.class))).thenReturn(paymentResponse());

    mockMvc
        .perform(
            postJson("/api/v1/orders/" + ORDER_ID + "/payments", paymentRequest())
                .principal(userPrincipal()))
        .andExpect(status().isCreated());
    mockMvc
        .perform(get("/api/v1/orders/{orderId}/payments", ORDER_ID).principal(userPrincipal()))
        .andExpect(status().isOk());
    mockMvc
        .perform(postJson("/api/v1/payments/webhook", paymentWebhookRequest()))
        .andExpect(status().isOk());

    verify(paymentService).create(eq(USER_ID), eq(ORDER_ID), any(CreatePaymentRequest.class));
    verify(paymentService).getMyOrderPayments(USER_ID, ORDER_ID);
    verify(paymentService).webhook(any(PaymentWebhookRequest.class));
  }

  @Test
  void product_api_endpoints_delegate_to_service() throws Exception {
    when(productCatalogService.detail(PRODUCT_ID)).thenReturn(List.of(productDetailResponse()));
    when(productCatalogService.search(
            "phone",
            PRODUCT_ID,
            new BigDecimal("100.00"),
            new BigDecimal("500.00"),
            false,
            null,
            "cursor",
            10))
        .thenReturn(
            CursorPageResponse.<ProductSummaryResponse>builder()
                .content(List.of(productSummaryResponse()))
                .size(10)
                .hasNext(false)
                .build());

    mockMvc.perform(get("/api/v1/products/{productId}", PRODUCT_ID)).andExpect(status().isOk());
    mockMvc
        .perform(
            get("/api/v1/products")
                .param("keyword", "phone")
                .param("categoryId", PRODUCT_ID.toString())
                .param("minPrice", "100.00")
                .param("maxPrice", "500.00")
                .param("cursor", "cursor")
                .param("size", "10"))
        .andExpect(status().isOk());

    verify(productCatalogService).detail(PRODUCT_ID);
    verify(productCatalogService)
        .search(
            "phone",
            PRODUCT_ID,
            new BigDecimal("100.00"),
            new BigDecimal("500.00"),
            false,
            null,
            "cursor",
            10);
  }

  @Test
  void return_api_endpoints_delegate_to_service() throws Exception {
    when(returnService.getMyReturns(USER_ID)).thenReturn(List.of(returnResponse()));
    when(returnService.create(eq(USER_ID), any(CreateReturnRequest.class)))
        .thenReturn(returnResponse());

    mockMvc.perform(get("/api/v1/returns").principal(userPrincipal())).andExpect(status().isOk());
    mockMvc
        .perform(postJson("/api/v1/returns", returnRequest()).principal(userPrincipal()))
        .andExpect(status().isCreated());

    verify(returnService).getMyReturns(USER_ID);
    verify(returnService).create(eq(USER_ID), any(CreateReturnRequest.class));
  }

  @Test
  void shipment_api_endpoint_delegates_to_service() throws Exception {
    when(customerShipmentService.getMyShipments(USER_ID)).thenReturn(List.of(shipmentResponse()));

    mockMvc.perform(get("/api/v1/shipments").principal(userPrincipal())).andExpect(status().isOk());

    verify(customerShipmentService).getMyShipments(USER_ID);
  }

  @Test
  void shipper_shipment_api_endpoints_delegate_to_service() throws Exception {
    when(shipperShipmentService.getAssignedShipments(USER_ID))
        .thenReturn(List.of(shipmentResponse()));

    mockMvc
        .perform(get("/api/v1/shipper/shipments").principal(userPrincipal()))
        .andExpect(status().isOk());
    mockMvc
        .perform(
            postJson("/api/v1/shipper/shipments/" + SHIPMENT_ID + "/tracking", trackingRequest())
                .principal(userPrincipal()))
        .andExpect(status().isNoContent());

    verify(shipperShipmentService).getAssignedShipments(USER_ID);
    verify(shipperShipmentService)
        .updateTracking(
            SHIPMENT_ID,
            USER_ID,
            "IN_TRANSIT",
            "moving",
            new BigDecimal("10.000000"),
            new BigDecimal("106.000000"),
            "proof.jpg");
  }

  @Test
  void user_api_endpoints_delegate_to_service() throws Exception {
    when(userService.me(USER_ID)).thenReturn(userResponse());
    when(userService.update(USER_ID, "New Name")).thenReturn(userResponse());
    when(userService.addresses(USER_ID)).thenReturn(List.of(addressResponse()));
    when(userService.createAddress(eq(USER_ID), any(AddressRequest.class)))
        .thenReturn(addressResponse());
    when(userService.updateAddress(eq(USER_ID), eq(ADDRESS_ID), any(AddressRequest.class)))
        .thenReturn(addressResponse());

    mockMvc.perform(get("/api/v1/users/me").principal(userPrincipal())).andExpect(status().isOk());
    mockMvc
        .perform(
            patchJson("/api/v1/users/me", Map.of("displayName", "New Name"))
                .principal(userPrincipal()))
        .andExpect(status().isOk());
    mockMvc
        .perform(get("/api/v1/users/me/addresses").principal(userPrincipal()))
        .andExpect(status().isOk());
    mockMvc
        .perform(
            postJson("/api/v1/users/me/addresses", addressRequest()).principal(userPrincipal()))
        .andExpect(status().isCreated());
    mockMvc
        .perform(
            patchJson("/api/v1/users/me/addresses/" + ADDRESS_ID, addressRequest())
                .principal(userPrincipal()))
        .andExpect(status().isOk());
    mockMvc
        .perform(delete("/api/v1/users/me/addresses/{id}", ADDRESS_ID).principal(userPrincipal()))
        .andExpect(status().isNoContent());
    mockMvc
        .perform(
            patch("/api/v1/users/me/addresses/{id}/default", ADDRESS_ID).principal(userPrincipal()))
        .andExpect(status().isNoContent());

    verify(userService).me(USER_ID);
    verify(userService).update(USER_ID, "New Name");
    verify(userService).addresses(USER_ID);
    verify(userService).createAddress(eq(USER_ID), any(AddressRequest.class));
    verify(userService).updateAddress(eq(USER_ID), eq(ADDRESS_ID), any(AddressRequest.class));
    verify(userService).deleteAddress(USER_ID, ADDRESS_ID);
    verify(userService).setDefaultAddress(USER_ID, ADDRESS_ID);
  }

  private Principal userPrincipal() {
    return new UsernamePasswordAuthenticationToken(USER_ID.toString(), "n/a");
  }

  private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder postJson(
      String url, Object body) throws Exception {
    return post(url).contentType(MediaType.APPLICATION_JSON).content(json(body));
  }

  private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder putJson(
      String url, Object body) throws Exception {
    return put(url).contentType(MediaType.APPLICATION_JSON).content(json(body));
  }

  private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder patchJson(
      String url, Object body) throws Exception {
    return patch(url).contentType(MediaType.APPLICATION_JSON).content(json(body));
  }

  private String json(Object body) throws Exception {
    return objectMapper.writeValueAsString(body);
  }

  private Map<String, Object> registerRequest() {
    return Map.of(
        "email", "user@example.com",
        "password", "strong-pass-123",
        "displayName", "User",
        "deviceName", "web");
  }

  private Map<String, Object> loginRequest() {
    return Map.of("email", "user@example.com", "password", "strong-pass-123", "deviceName", "web");
  }

  private Map<String, Object> refreshRequest() {
    return Map.of("refreshToken", "refresh-token", "deviceName", "web");
  }

  private Map<String, Object> productRequest() {
    return Map.of("categoryId", PRODUCT_ID.toString(), "name", "Phone", "description", "Desc");
  }

  private Map<String, Object> variantRequest() {
    return Map.of("sku", "SKU-1", "name", "Black", "price", "199.99");
  }

  private Map<String, Object> warehouseRequest() {
    return Map.of(
        "code", "WH-1",
        "name", "Main Warehouse",
        "addressLine", "1 Street",
        "priorityArea", "Hà Nội",
        "latitude", "10.000000",
        "longitude", "106.000000");
  }

  private Map<String, Object> shippingZoneRequest() {
    return Map.of(
        "code", "NORTH",
        "name", "Miền Bắc",
        "matchedCities", List.of("Hà Nội", "Ha Noi"));
  }

  private Map<String, Object> warehouseShippingZoneRequest() {
    return Map.of(
        "zones",
        List.of(
            Map.of("shippingZoneId", SHIPPING_ZONE_ID.toString(), "priority", 1, "active", true)));
  }

  private Map<String, Object> inventoryRequest() {
    return Map.of(
        "warehouseId", WAREHOUSE_ID.toString(),
        "variantId", VARIANT_ID.toString(),
        "availableQuantity", 10);
  }

  private Map<String, Object> inventoryAdjustRequest() {
    return Map.of(
        "warehouseId", WAREHOUSE_ID.toString(),
        "variantId", VARIANT_ID.toString(),
        "quantityDelta", 5);
  }

  private Map<String, Object> assignRequest() {
    return Map.of("shipperId", USER_ID.toString());
  }

  private Map<String, Object> cartItemRequest() {
    return Map.of("variantId", VARIANT_ID.toString(), "quantity", 2);
  }

  private Map<String, Object> categoryRequest() {
    return Map.of("name", "Electronics", "description", "Devices");
  }

  private Map<String, Object> checkoutRequest() {
    return Map.of("addressId", ADDRESS_ID.toString());
  }

  private Map<String, Object> paymentRequest() {
    return Map.of("provider", "COD", "idempotencyKey", "pay-key");
  }

  private Map<String, Object> paymentWebhookRequest() {
    return Map.of(
        "paymentId", PAYMENT_ID.toString(), "status", "SUCCEEDED", "providerReference", "ref-1");
  }

  private Map<String, Object> returnRequest() {
    return Map.of("orderId", ORDER_ID.toString(), "reason", "Broken");
  }

  private Map<String, Object> trackingRequest() {
    return Map.of(
        "status", "IN_TRANSIT",
        "note", "moving",
        "latitude", "10.000000",
        "longitude", "106.000000",
        "proofUrl", "proof.jpg");
  }

  private Map<String, Object> addressRequest() {
    return Map.of(
        "recipientName", "User",
        "phone", "0900000000",
        "addressLine", "1 Street",
        "city", "HCM",
        "latitude", "10.000000",
        "longitude", "106.000000",
        "defaultAddress", true);
  }

  private TokenPairResponse tokenPairResponse() {
    return TokenPairResponse.builder().accessToken("access").refreshToken("refresh").build();
  }

  private InventoryResponse inventoryResponse() {
    return InventoryResponse.builder()
        .id(UUID.randomUUID())
        .warehouseId(WAREHOUSE_ID)
        .variantId(VARIANT_ID)
        .availableQuantity(10)
        .reservedQuantity(0)
        .build();
  }

  private ProductAdminResponse productAdminResponse() {
    return ProductAdminResponse.builder()
        .id(PRODUCT_ID)
        .categoryId(PRODUCT_ID)
        .name("Phone")
        .description("Desc")
        .active(true)
        .build();
  }

  private ProductVariantAdminResponse productVariantAdminResponse() {
    return ProductVariantAdminResponse.builder()
        .id(VARIANT_ID)
        .productId(PRODUCT_ID)
        .sku("SKU-1")
        .name("Black")
        .price(new BigDecimal("199.99"))
        .active(true)
        .build();
  }

  private ShipmentResponse shipmentResponse() {
    return ShipmentResponse.builder()
        .id(SHIPMENT_ID)
        .orderId(ORDER_ID)
        .warehouseId(WAREHOUSE_ID)
        .shipperId(USER_ID)
        .status("IN_TRANSIT")
        .build();
  }

  private WarehouseResponse warehouseResponse() {
    return WarehouseResponse.builder()
        .id(WAREHOUSE_ID)
        .code("WH-1")
        .name("Main Warehouse")
        .addressLine("1 Street")
        .priorityArea("Hà Nội")
        .latitude(new BigDecimal("10.000000"))
        .longitude(new BigDecimal("106.000000"))
        .active(true)
        .build();
  }

  private ShippingZoneResponse shippingZoneResponse() {
    return ShippingZoneResponse.builder()
        .id(SHIPPING_ZONE_ID)
        .code("NORTH")
        .name("Miền Bắc")
        .matchedCities(List.of("Hà Nội", "Ha Noi"))
        .active(true)
        .build();
  }

  private WarehouseShippingZoneResponse warehouseShippingZoneResponse() {
    return WarehouseShippingZoneResponse.builder()
        .id(UUID.randomUUID())
        .shippingZoneId(SHIPPING_ZONE_ID)
        .shippingZoneCode("NORTH")
        .shippingZoneName("Miền Bắc")
        .priority(1)
        .active(true)
        .build();
  }

  private CartResponse cartResponse() {
    return CartResponse.builder()
        .id(UUID.randomUUID())
        .items(List.of())
        .totalAmount(BigDecimal.ZERO)
        .build();
  }

  private CategoryResponse categoryResponse() {
    return CategoryResponse.builder()
        .id(PRODUCT_ID)
        .name("Electronics")
        .description("Devices")
        .build();
  }

  private CheckoutResponse checkoutResponse() {
    return CheckoutResponse.builder()
        .orderId(ORDER_ID)
        .orderNumber("ORD-1")
        .totalAmount(new BigDecimal("199.99"))
        .shipments(List.of())
        .build();
  }

  private NotificationResponse notificationResponse() {
    return NotificationResponse.builder()
        .id(NOTIFICATION_ID)
        .subject("Subject")
        .body("Body")
        .status("PENDING")
        .attemptCount(0)
        .build();
  }

  private OrderResponse orderResponse() {
    return OrderResponse.builder()
        .id(ORDER_ID)
        .orderNumber("ORD-1")
        .status("CONFIRMED")
        .totalAmount(new BigDecimal("199.99"))
        .city("HCM")
        .items(List.of())
        .build();
  }

  private PaymentResponse paymentResponse() {
    return PaymentResponse.builder()
        .id(PAYMENT_ID)
        .orderId(ORDER_ID)
        .provider("COD")
        .idempotencyKey("pay-key")
        .amount(new BigDecimal("199.99"))
        .status("PENDING")
        .build();
  }

  private ProductVariantDetailResponse productDetailResponse() {
    return ProductVariantDetailResponse.builder()
        .productId(PRODUCT_ID)
        .productName("Phone")
        .variantId(VARIANT_ID)
        .sku("SKU-1")
        .variantName("Black")
        .price(new BigDecimal("199.99"))
        .availableQuantity(10)
        .reservedQuantity(0)
        .build();
  }

  private ProductSummaryResponse productSummaryResponse() {
    return ProductSummaryResponse.builder()
        .id(PRODUCT_ID)
        .name("Phone")
        .categoryId(PRODUCT_ID)
        .active(true)
        .variantId(VARIANT_ID)
        .sku("SKU-1")
        .variantName("Black")
        .price(new BigDecimal("199.99"))
        .availableQuantity(10)
        .reservedQuantity(0)
        .build();
  }

  private ReturnResponse returnResponse() {
    return ReturnResponse.builder()
        .id(UUID.randomUUID())
        .orderId(ORDER_ID)
        .status("REQUESTED")
        .reason("Broken")
        .build();
  }

  private ShipmentSummaryResponse shipmentSummaryResponse() {
    return ShipmentSummaryResponse.builder()
        .id(SHIPMENT_ID)
        .status("IN_TRANSIT")
        .warehouseName("Main Warehouse")
        .warehouseAddress("1 Street")
        .build();
  }

  private UserResponse userResponse() {
    return UserResponse.builder()
        .id(USER_ID)
        .email("user@example.com")
        .displayName("User")
        .role("CUSTOMER")
        .build();
  }

  private AddressResponse addressResponse() {
    return AddressResponse.builder()
        .id(ADDRESS_ID)
        .recipientName("User")
        .phone("0900000000")
        .addressLine("1 Street")
        .city("HCM")
        .latitude(new BigDecimal("10.000000"))
        .longitude(new BigDecimal("106.000000"))
        .defaultAddress(true)
        .build();
  }
}

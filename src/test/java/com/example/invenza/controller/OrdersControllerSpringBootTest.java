package com.example.invenza.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.example.invenza.entity.Orders;
import com.example.invenza.repository.OrdersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
@DisplayName("OrdersController Spring Boot 完整容器整合測試")
public class OrdersControllerSpringBootTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJGMDAwMDEiLCJuYW1lIjoiQWRtaW4iLCJlbWFpbCI6InVzZXIxQGVtYWlsLmNvbSIsInBob25lIjoiMDkxMjM0NTY3OCIsInJvbGUiOiJGIiwiaWF0IjoxNzUwNDc1Mjc3LCJleHAiOjE3NTA4MzUyNzd9.n6nDm4ErtJ6V10YmHZ6lxam7gSIanvuEyHm4llrft7A";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Orders testOrder;
    
    @BeforeEach
    void setUp() {
        // 創建測試訂單資料
        testOrder = new Orders();
        testOrder.setCommodityName("test apple");
        testOrder.setCommodityType("Z232");
        testOrder.setUnitPrice(new BigDecimal("35.0"));
        testOrder.setQuantity(15.0);
        testOrder.setDealerName("Test Dealer");
        testOrder.setDealerId("T001");
        testOrder.setDealerEmail("testdealer@email.com");
        testOrder.setDealerPhone("0933333333");
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setDeadlineDate(LocalDateTime.now().plusDays(7));
        testOrder.setEmployeeName("Test Employee");
        testOrder.setEmployeeId("F00001");
        testOrder.setEmployeeEmail("employee@email.com");
        testOrder.setEmployeePhone("0944444444");

        ordersRepository.save(testOrder);
    }

    @Test
    @DisplayName("測試取得訂單資料 - 成功")
    void testGetOrdersData_Success() throws Exception {
        // Given - 測試資料已在 data-test.sql 中插入

        // When & Then
        mockMvc.perform(get("/api/sales/get-data")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(0))))
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].commodity.name").exists())
                .andExpect(jsonPath("$.data[0].commodity.type").exists())
                .andExpect(jsonPath("$.data[0].commodity.transactionValue.unitPrice").exists())
                .andExpect(jsonPath("$.data[0].commodity.transactionValue.quantity").exists())
                .andExpect(jsonPath("$.data[0].commodity.transactionValue.totalCost").exists())
                .andExpect(jsonPath("$.data[0].distributor.name").exists())
                .andExpect(jsonPath("$.data[0].distributor.id").exists())
                .andExpect(jsonPath("$.data[0].responsible.name").exists())
                .andExpect(jsonPath("$.data[0].orderTimeStamp").exists())
                .andExpect(jsonPath("$.data[0].deadlineTimeStamp").exists());

        log.info("測試取得訂單資料成功");
    }

    @Test
    @DisplayName("測試取得訂單資料 - 帶參數篩選")
    void testGetOrdersDataWithParams_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/sales/get-data")
                .param("commodityName", "apple")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        log.info("測試帶參數取得訂單資料成功");
    }

    @Test
    @DisplayName("測試新增訂單 - 成功")
    void testAddOrders_Success() throws Exception {
        // Given


        // 準備更新資料
        String json = String.format("""
            {
                "id": null,
                "commodity": {
                    "name": "dd",
                    "type": "gh",
                    "transactionValue": {
                        "unitPrice": 89.0,
                        "quantity": 59.0,
                        "totalCost": 5251.0
                    }
                },
                "distributor": {
                    "name": "gj",
                    "id": "ghj",
                    "association": {
                        "email": "fhj@gmail.com",
                        "phone": "89"
                    }
                },
                "orderTimeStamp": "2025-06-18 19:50",
                "deadlineTimeStamp": "2025-06-18 19:50",
                "responsible": {
                    "name": "sales",
                    "id": "100003",
                    "association": {
                        "email": "sales@gmail.com",
                        "phone": "0912345678"
                    }
                }
            }
            """);

        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> updateData = mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        String requestJson = objectMapper.writeValueAsString(updateData);

        // When & Then
        mockMvc.perform(post("/api/sales/add-data")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());

        log.info("測試新增訂單成功");
    }
    
    @Test
    @DisplayName("測試新增訂單 - 失敗（無效資料）")
    void testAddOrders_Failure() throws Exception {
        // Given - 不完整的訂單資料
        String json = String.format("""
            {
                "id": null,
                "commodity": {
                    "name": "",
                    "type": "",
                    "transactionValue": {
                        "unitPrice": null,
                        "quantity": null,
                        "totalCost": null
                    }
                },
                "distributor": {
                    "name": "",
                    "id": "",
                    "association": {
                        "email": "",
                        "phone": ""
                    }
                },
                "orderTimeStamp": "",
                "deadlineTimeStamp": "",
                "responsible": {
                    "name": "",
                    "id": "",
                    "association": {
                        "email": "",
                        "phone": ""
                    }
                }
            }
            """);
            
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> invalidOrderData = mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        String requestJson = objectMapper.writeValueAsString(invalidOrderData);

        // When & Then
        mockMvc.perform(post("/api/sales/add-data")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        log.info("測試新增無效訂單資料成功攔截");
    }
    
    @Test
    @DisplayName("測試更新訂單 - 成功")
    void testUpdateOrders_Success() throws Exception {
        // Given - 先保存一筆測試資料
        Orders savedOrder = ordersRepository.save(testOrder);
        
        // 準備更新資料
        String json = String.format("""
            {
                "id": %d,
                "commodity": {
                    "name": "更新後的商品",
                    "type": "Z232",
                    "transactionValue": {
                        "unitPrice": 40.0,
                        "quantity": 20.0,
                        "totalCost": 800.0
                    }
                },
                "distributor": {
                    "name": "更新後的經銷商",
                    "id": "T001",
                    "association": {
                        "email": "updateddealer@email.com",
                        "phone": "0966666666"
                    }
                },
                "orderTimeStamp": "2025-06-22 14:00",
                "deadlineTimeStamp": "2025-06-29 14:00",
                "responsible": {
                    "name": "Admin",
                    "id": "F00001",
                    "association": {
                        "email": "user1@email.com",
                        "phone": "0912345678"
                    }
                }
            }
            """, savedOrder.getId());
          ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> updateData = mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        String requestJson = objectMapper.writeValueAsString(updateData);

        // When & Then
        mockMvc.perform(put("/api/sales/update-data")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());

        // 驗證資料是否真的被更新
        Orders updatedOrder = ordersRepository.findById(savedOrder.getId()).orElse(null);
        assertNotNull(updatedOrder);
        assertEquals("更新後的商品", updatedOrder.getCommodityName());

        log.info("測試更新訂單成功");
    }
    
    @Test
    @DisplayName("測試更新訂單 - 失敗（訂單不存在）")
    void testUpdateOrders_Failure() throws Exception {
        // Given - 不存在的訂單ID
        String json = String.format("""
            {
                "id": 99999,
                "commodity": {
                    "name": "更新後的商品",
                    "type": "Z232",
                    "transactionValue": {
                        "unitPrice": 40.0,
                        "quantity": 20.0,
                        "totalCost": 800.0
                    }
                },
                "distributor": {
                    "name": "更新後的經銷商",
                    "id": "T001",
                    "association": {
                        "email": "updateddealer@email.com",
                        "phone": "0966666666"
                    }
                },
                "orderTimeStamp": "2025-06-22 14:00",
                "deadlineTimeStamp": "2025-06-29 14:00",
                "responsible": {
                    "name": "Admin",
                    "id": "F00001",
                    "association": {
                        "email": "user1@email.com",
                        "phone": "0912345678"
                    }
                }
            }
            """);
        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> updateData = mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        String requestJson = objectMapper.writeValueAsString(updateData);

        // When & Then
        mockMvc.perform(put("/api/sales/update-data")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        log.info("測試更新不存在的訂單成功攔截");
    }

    @Test
    @DisplayName("測試刪除訂單 - 成功")
    void testDeleteOrders_Success() throws Exception {
        // Given - 先保存一筆測試資料
        Orders savedOrder = ordersRepository.save(testOrder);
        Long orderId = savedOrder.getId();

        // When & Then
        mockMvc.perform(delete("/api/sales/delete-data")
                .param("id", orderId.toString())
                .header("Authorization", AUTH_TOKEN))
                .andExpect(status().isOk());

        // 驗證資料是否真的被刪除
        assertFalse(ordersRepository.existsById(orderId));

        log.info("測試刪除訂單成功");
    }

    @Test
    @DisplayName("測試刪除訂單 - 失敗（訂單不存在）")
    void testDeleteOrders_Failure() throws Exception {
        // Given - 不存在的訂單ID
        Long nonExistentId = 99999L;

        // When & Then
        mockMvc.perform(delete("/api/sales/delete-data")
                .param("id", nonExistentId.toString())
                .header("Authorization", AUTH_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        log.info("測試刪除不存在的訂單成功攔截");
    }

    @Test
    @DisplayName("測試取得訂單資料 - 驗證日期格式")
    void testGetOrdersData_DateFormat() throws Exception {
        // Given - 確保有測試資料
        ordersRepository.save(testOrder);
        
        // When & Then
        mockMvc.perform(get("/api/sales/get-data")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].orderTimeStamp").value(matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")))
                .andExpect(jsonPath("$.data[0].deadlineTimeStamp").value(matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")));

        log.info("測試日期格式驗證成功");
    }
    
    @Test
    @DisplayName("測試取得訂單資料 - 驗證計算欄位")
    void testGetOrdersData_CalculatedFields() throws Exception {
        
        testOrder = new Orders();
        testOrder.setCommodityName("計算測試商品");
        testOrder.setCommodityType("TEST");
        testOrder.setUnitPrice(new BigDecimal("10.0"));
        testOrder.setQuantity(5.0);
        testOrder.setDealerName("測試經銷商");
        testOrder.setDealerId("T001");
        testOrder.setDealerEmail("test@email.com");
        testOrder.setDealerPhone("0912345678");
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setDeadlineDate(LocalDateTime.now().plusDays(7));
        testOrder.setEmployeeName("測試員工");
        testOrder.setEmployeeId("F00001");
        testOrder.setEmployeeEmail("staff@email.com");
        testOrder.setEmployeePhone("0987654321");
        
        Orders savedOrder = ordersRepository.save(testOrder);
        Long testOrderId = savedOrder.getId();
        
        mockMvc.perform(get("/api/sales/get-data")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[?(@.id == " + testOrderId + ")].commodity.transactionValue.unitPrice").value(10.0))
                .andExpect(jsonPath("$.data[?(@.id == " + testOrderId + ")].commodity.transactionValue.quantity").value(5.0))
                .andExpect(jsonPath("$.data[?(@.id == " + testOrderId + ")].commodity.transactionValue.totalCost").value(50.0));

        log.info("測試計算欄位驗證成功");
    }

    @Test
    @DisplayName("測試取得訂單資料 - 驗證回應結構")
    void testGetOrdersData_ResponseStructure() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/sales/get-data")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[*].commodity").exists())
                .andExpect(jsonPath("$.data[*].commodity.name").exists())
                .andExpect(jsonPath("$.data[*].commodity.type").exists())
                .andExpect(jsonPath("$.data[*].commodity.transactionValue").exists())
                .andExpect(jsonPath("$.data[*].commodity.transactionValue.unitPrice").exists())
                .andExpect(jsonPath("$.data[*].commodity.transactionValue.quantity").exists())
                .andExpect(jsonPath("$.data[*].commodity.transactionValue.totalCost").exists())
                .andExpect(jsonPath("$.data[*].distributor").exists())
                .andExpect(jsonPath("$.data[*].distributor.name").exists())
                .andExpect(jsonPath("$.data[*].distributor.id").exists())
                .andExpect(jsonPath("$.data[*].distributor.association").exists())
                .andExpect(jsonPath("$.data[*].distributor.association.email").exists())
                .andExpect(jsonPath("$.data[*].distributor.association.phone").exists())
                .andExpect(jsonPath("$.data[*].responsible").exists())
                .andExpect(jsonPath("$.data[*].responsible.name").exists())
                .andExpect(jsonPath("$.data[*].responsible.id").exists())
                .andExpect(jsonPath("$.data[*].responsible.association").exists())
                .andExpect(jsonPath("$.data[*].responsible.association.email").exists())
                .andExpect(jsonPath("$.data[*].responsible.association.phone").exists())
                .andExpect(jsonPath("$.data[*].id").exists())
                .andExpect(jsonPath("$.data[*].orderTimeStamp").exists())
                .andExpect(jsonPath("$.data[*].deadlineTimeStamp").exists());

        log.info("測試回應結構驗證成功");
    }
}
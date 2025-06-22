package com.example.invenza.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.invenza.entity.Procurement;
import com.example.invenza.repository.ProcurementRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Boot 完整容器整合測試
 * 使用實際的 Spring 應用程式上下文和資料庫
 */
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional // 每個測試後回滾數據
@DisplayName("整合測試 - ProcurementController Spring Boot 測試")
class ProcurementControllerSpringBootTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJGMDAwMDEiLCJuYW1lIjoiQWRtaW4iLCJlbWFpbCI6InVzZXIxQGVtYWlsLmNvbSIsInBob25lIjoiMDkxMjM0NTY3OCIsInJvbGUiOiJGIiwiaWF0IjoxNzUwNTQxMjE0LCJleHAiOjE3ODY1NDEyMTR9.9qHMFfXM4IeKSWFvdHJQxgCk2x5DTCVxzoqlTiU5e0k";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProcurementRepository procurementRepository;

    @Autowired
    private ObjectMapper objectMapper;

      @BeforeEach
    void setUp() {
        // 使用data-test.sql中的測試資料，不需要再手動建立資料
        // 確保所有測試開始時數據庫狀態一致
        // 這是由spring.sql.init.data-locations=classpath:data-test.sql配置自動載入的
        
        // 如果需要在某些測試前檢查或修改初始資料，可以在此處理
        // 例如驗證初始數據是否正確載入
        log.info("測試開始 - 使用data-test.sql中的測試資料");
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/procurement/get-data 完整流程")
    void integrationTest_GetProcurementData_FullFlow() throws Exception {
        mockMvc.perform(get("/api/procurement/get-data")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andDo(print()) // 印出完整請求和回應
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json; charset=utf-8"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2)) // data-test.sql中有3筆採購資料，但只有2筆未過期
                .andExpect(jsonPath("$.data[0].commodity.name").exists())
                .andExpect(jsonPath("$.data[0].commodity.type").exists())
                .andExpect(jsonPath("$.data[0].supplier.name").exists())
                .andExpect(jsonPath("$.data[0].supplier.id").exists())
                .andExpect(jsonPath("$.data[0].commodity.transactionValue.unitPrice").exists())
                .andExpect(jsonPath("$.data[0].commodity.transactionValue.quantity").exists())
                .andExpect(jsonPath("$.data[0].commodity.transactionValue.totalCost").exists());
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/procurement/get-data 帶查詢參數的請求")
    void integrationTest_GetProcurementDataWithParams() throws Exception {
        // 測試存在的商品名稱 - 使用data-test.sql中的資料
        mockMvc.perform(get("/api/procurement/get-data")
                .param("commodityName", "apple")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].commodity.name").value("apple"));

        // 測試不存在的商品名稱
        mockMvc.perform(get("/api/procurement/get-data")
                .param("commodityName", "不存在的商品")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/procurement/get-data 供應商篩選")
    void integrationTest_FilterBySupplier() throws Exception {
        mockMvc.perform(get("/api/procurement/get-data")
                .param("businessPartner", "WEEE")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].supplier.name").value("WEEE"));

        mockMvc.perform(get("/api/procurement/get-data")
                .param("businessPartnerId", "W213")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].supplier.id").value("W213"));
    }
    
    @Test    @DisplayName("整合測試 - GET /api/procurement/get-data 商品類型篩選")
    void integrationTest_FilterByCommodityType() throws Exception {
        mockMvc.perform(get("/api/procurement/get-data")
                .param("commodityType", "Z232")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].commodity.type").value("Z232"));

        mockMvc.perform(get("/api/procurement/get-data")
                .param("commodityType", "M414")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].commodity.type").value("M414"))
                .andExpect(jsonPath("$.data[1].commodity.type").value("M414"));
    }

    @Test
    @DisplayName("整合測試 - PUT /api/procurement/update-data 更新操作")
    void integrationTest_UpdateProcurement() throws Exception {

        long id = procurementRepository.findAll().get(0).getId();
        
        // 準備更新資料
        String json = String.format("""
            {
                "id": %d,
                "commodity": {
                    "name": "jhgfdryhfghhfgjjhgszxvgj",
                    "type": "hhj",
                    "transactionValue": {
                    "unitPrice": 999.0,
                    "quantity": 300.0,
                    "totalCost": 4995.0
                    }
                },
                "supplier": {
                    "name": "vbm",
                    "id": "gj",
                    "association": {
                    "email": "h@gmail.com",
                    "phone": "996"
                    }
                },
                "orderTimeStamp": "2025-06-30 15:58",
                "deadlineTimeStamp": "2025-07-01 15:58",
                "responsible": {
                    "name": "admin",
                    "id": "F00001",
                    "association": {
                    "email": "admin@gmail.com",
                    "phone": "0911222333"
                    }
                }
            }
            """, id);

        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> updateData = mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        
        mockMvc.perform(put("/api/procurement/update-data")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
        .content(objectMapper.writeValueAsString(updateData)))
        .andDo(print())
        .andExpect(status().isOk());
        
        procurementRepository.flush();
        
        // 直接從數據庫查詢，檢查是否已經更新
        Procurement updatedProcurement = procurementRepository.findById(id).orElseThrow();
        log.info("🔄 資料庫中的記錄 - ID: {}, 名稱: {}, 類型: {}, 價格: {}, 數量: {}", 
                updatedProcurement.getId(), 
                updatedProcurement.getCommodityName(),
                updatedProcurement.getCommodityType(),
                updatedProcurement.getUnitPrice(),
                updatedProcurement.getQuantity());// 驗證更新結果
        mockMvc.perform(get("/api/procurement/get-data?commodityName=jhgfdryhfghhfgjjhgszxvgj")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].commodity.name").value("jhgfdryhfghhfgjjhgszxvgj"))
                .andExpect(jsonPath("$.data[0].commodity.transactionValue.unitPrice").value(999.0))
                .andExpect(jsonPath("$.data[0].commodity.transactionValue.quantity").value(300))
                .andExpect(jsonPath("$.data[0].commodity.transactionValue.totalCost").value(299700.0));
    }

    @Test    @DisplayName("整合測試 - PUT /api/procurement/update-data 更新不存在記錄返回404")
    void integrationTest_UpdateNonExistentProcurement_ShouldReturn404() throws Exception {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("id", 999999L); // 不存在的ID
        updateData.put("commodityName", "不存在的記錄");
        mockMvc.perform(put("/api/procurement/update-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData))
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/procurement/get-data 多筆資料處理")
    void integrationTest_MultipleRecords() throws Exception {

        // 直接驗證取得所有資料 - data-test.sql中已有3筆採購資料
        mockMvc.perform(get("/api/procurement/get-data")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));  // 直接驗證取得所有資料 - data-test.sql中已有3筆採購資料，但只有2筆未過期
                
        mockMvc.perform(get("/api/procurement/get-data")
                .param("commodityType", "M414")
                .header("Authorization", AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));

        // 驗證按類型篩選
        mockMvc.perform(get("/api/procurement/get-data")
                .param("commodityType", "Z232")
                .header("Authorization", AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].commodity.name").value("apple"));

    }

    @Test
    @DisplayName("整合測試 - GET /api/procurement/get-data 日期格式化驗證")
    void integrationTest_DateTimeFormatting() throws Exception {
        mockMvc.perform(get("/api/procurement/get-data")
                .header("Authorization", AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].orderTimeStamp").exists())
                .andExpect(jsonPath("$.data[0].deadlineTimeStamp").exists())
                // 驗證日期格式為 yyyy-MM-dd HH:mm
                .andExpect(jsonPath("$.data[0].orderTimeStamp").value(org.hamcrest.Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")))
                .andExpect(jsonPath("$.data[0].deadlineTimeStamp").value(org.hamcrest.Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")));
    }

    @Test
    @DisplayName("整合測試 - PUT /api/procurement/update-data 錯誤處理")
    void integrationTest_ErrorHandling() throws Exception {
        // 測試無效的JSON格式
        mockMvc.perform(put("/api/procurement/update-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }")
                .header("Authorization", AUTH_TOKEN))
                .andExpect(status().isBadRequest());

        // 測試空的請求體
        mockMvc.perform(put("/api/procurement/update-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .header("Authorization", AUTH_TOKEN))
                .andExpect(status().isNotFound()); // 空請求體應該被允許但不做任何操作
    }

    @Test
    @DisplayName("整合測試 - DELETE /api/procurement/delete-data 刪除操作")
    void integrationTest_DeleteProcurement() throws Exception {
        // 使用已知的ID (data-test.sql中定義的ID=1)
        long id = 1L;
        
        // 執行刪除操作
        mockMvc.perform(delete("/api/procurement/delete-data")
                .param("id", String.valueOf(id))
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andDo(print())
                .andExpect(status().isOk());
        
        // 驗證資料已被刪除 - 檢查特定ID的資料是否不存在
        mockMvc.perform(get("/api/procurement/get-data")
                .param("commodityName", "apple")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("整合測試 - DELETE /api/procurement/delete-data 不存在ID返回400")
    void integrationTest_DeleteNonExistentProcurement_ShouldReturn400() throws Exception {
        // 使用不存在的ID
        mockMvc.perform(delete("/api/procurement/delete-data")
                .param("id", "999999")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("整合測試 - POST /api/procurement/add-data 新增資料")
        void integrationTest_AddProcurement() throws Exception {
                // 準備新增資料
                long id = 13L;
                List<Procurement> existingProcurements = procurementRepository.findAll();
                for (Procurement procurement : existingProcurements) {
                        if(procurement.getId() == id) {
                                id += procurement.getId();
                        }
                }
                String json = String.format("""
                {
                        "id": %d,
                        "commodity": {
                                "name": "jhgfdryhfghhfgjjhgszxvgj",
                                "type": "hhj",
                                "transactionValue": {
                                "unitPrice": 999.0,
                                "quantity": 300.0,
                                "totalCost": 4995.0
                                }
                        },
                        "supplier": {
                                "name": "vbm",
                                "id": "gj",
                                "association": {
                                "email": "h@gmail.com",
                                "phone": "996"
                                }
                        },
                        "orderTimeStamp": "2025-06-14 15:58",
                        "deadlineTimeStamp": "2025-06-14 15:58",
                        "responsible": {
                                "name": "admin",
                                "id": "F00001",
                                "association": {
                                "email": "admin@gmail.com",
                                "phone": "0911222333"
                                }
                        }
                }
                """, id);

                mockMvc.perform(post("/api/procurement/add-data")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", AUTH_TOKEN))
                        .andExpect(status().is(org.hamcrest.Matchers.oneOf(201, 200)));

                // 驗證新增資料是否成功
                mockMvc.perform(get("/api/procurement/get-data?commodityName=jhgfdryhfghhfgjjhgszxvgj")
                                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.length()").value(1))
                        .andExpect(jsonPath("$.data[0].commodity.name").value("jhgfdryhfghhfgjjhgszxvgj"))
                        .andExpect(jsonPath("$.data[0].commodity.transactionValue.unitPrice").value(999.0))
                        .andExpect(jsonPath("$.data[0].commodity.transactionValue.quantity").value(300))
                        .andExpect(jsonPath("$.data[0].commodity.transactionValue.totalCost").value(299700.0));
        }

        @Test
        @DisplayName("整合測試 - POST /api/procurement/add-data 空資料返回404")
        void integrationTest_AddProcurementWithDuplicateId_ShouldReturn404() throws Exception {
                String json = String.format("""
                {
                        
                }
                """);

                mockMvc.perform(post("/api/procurement/add-data")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", AUTH_TOKEN))
                                .andDo(result -> log.info("Response: {}", result.getResponse().getContentAsString()))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").exists());
        }
}

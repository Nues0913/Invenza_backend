package com.example.invenza.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
// @Transactional // 每個測試後回滾數據
@DisplayName("ProcurementController Spring Boot 完整容器整合測試")
class ProcurementControllerSpringBootTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJGMDAwMDEiLCJuYW1lIjoiQWRtaW4iLCJlbWFpbCI6InVzZXIxQGVtYWlsLmNvbSIsInBob25lIjoiMDkxMjM0NTY3OCIsInJvbGUiOiJGIiwiaWF0IjoxNzUwNDc1Mjc3LCJleHAiOjE3NTA4MzUyNzd9.n6nDm4ErtJ6V10YmHZ6lxam7gSIanvuEyHm4llrft7A";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProcurementRepository procurementRepository;

    @Autowired
    private ObjectMapper objectMapper;

    
    @BeforeEach
    void setUp() {
        // 清空並準備測試數據
        procurementRepository.deleteAll();
        
        Procurement sampleProcurement = new Procurement();
        sampleProcurement.setCommodityName("整合測試電腦");
        sampleProcurement.setCommodityType("3C產品");
        sampleProcurement.setUnitPrice(new BigDecimal("35000"));
        sampleProcurement.setQuantity(2);
        sampleProcurement.setSupplierName("整合測試供應商");
        sampleProcurement.setSupplierId("INT001");
        sampleProcurement.setSupplierEmail("integration@test.com");
        sampleProcurement.setSupplierPhone("02-1111-1111");
        sampleProcurement.setOrderDate(LocalDateTime.now().minusDays(2));
        sampleProcurement.setDeadlineDate(LocalDateTime.now().plusDays(15));
        sampleProcurement.setEmployeeName("測試人員");
        sampleProcurement.setEmployeeId("EMP001");
        sampleProcurement.setEmployeeEmail("test@email.com");
        sampleProcurement.setEmployeePhone("0911222333");
        
        // 保存到實際資料庫
        procurementRepository.save(sampleProcurement);
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
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].commodity.name").value("整合測試電腦"))
                .andExpect(jsonPath("$.data[0].commodity.type").value("3C產品"))
                .andExpect(jsonPath("$.data[0].supplier.name").value("整合測試供應商"))
                .andExpect(jsonPath("$.data[0].supplier.id").value("INT001"))
                .andExpect(jsonPath("$.data[0].commodity.transactionValue.unitPrice").value(35000))
                .andExpect(jsonPath("$.data[0].commodity.transactionValue.quantity").value(2))
                .andExpect(jsonPath("$.data[0].commodity.transactionValue.totalCost").value(70000));
    }

    @Test
    @DisplayName("整合測試 - 帶查詢參數的請求")
    void integrationTest_GetProcurementDataWithParams() throws Exception {        // 測試存在的商品名稱
        mockMvc.perform(get("/api/procurement/get-data")
                .param("commodityName", "整合測試電腦")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].commodity.name").value("整合測試電腦"));

        // 測試不存在的商品名稱
        mockMvc.perform(get("/api/procurement/get-data")
                .param("commodityName", "不存在的商品")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("整合測試 - 測試供應商篩選")
    void integrationTest_FilterBySupplier() throws Exception {        mockMvc.perform(get("/api/procurement/get-data")
                .param("businessPartner", "整合測試供應商")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].supplier.name").value("整合測試供應商"));

        mockMvc.perform(get("/api/procurement/get-data")
                .param("businessPartnerId", "INT001")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].supplier.id").value("INT001"));
    }

    @Test
    @DisplayName("整合測試 - 測試商品類型篩選")
    void integrationTest_FilterByCommodityType() throws Exception {        mockMvc.perform(get("/api/procurement/get-data")
                .param("commodityType", "3C產品")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].commodity.type").value("3C產品"));

        mockMvc.perform(get("/api/procurement/get-data")
                .param("commodityType", "辦公用品")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("整合測試 - PUT /api/procurement/update-data 更新操作")
    @Rollback(false)
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
                    "quantity": 300,
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

    @Test
    @DisplayName("整合測試 - 更新不存在的記錄應該返回404")
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
    @DisplayName("整合測試 - 測試多筆資料的處理")
    void integrationTest_MultipleRecords() throws Exception {
        // 新增第二筆測試資料
        Procurement secondProcurement = new Procurement();
        secondProcurement.setCommodityName("第二個商品");
        secondProcurement.setCommodityType("辦公用品");
        secondProcurement.setUnitPrice(new BigDecimal("1500"));
        secondProcurement.setQuantity(10);
        secondProcurement.setSupplierName("第二個供應商");
        secondProcurement.setSupplierId("SEC001");
        secondProcurement.setSupplierEmail("second@supplier.com");
        secondProcurement.setSupplierPhone("02-2222-2222");
        secondProcurement.setOrderDate(LocalDateTime.now().minusDays(1));
        secondProcurement.setDeadlineDate(LocalDateTime.now().plusDays(20));
        secondProcurement.setEmployeeName("測試人員");
        secondProcurement.setEmployeeId("EMP001");
        secondProcurement.setEmployeeEmail("test@email.com");
        secondProcurement.setEmployeePhone("0911222333");
        procurementRepository.save(secondProcurement);

        // 驗證取得所有資料
        mockMvc.perform(get("/api/procurement/get-data")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));

        // 驗證按類型篩選
        mockMvc.perform(get("/api/procurement/get-data")
                .param("commodityType", "3C產品")
                .header("Authorization", AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].commodity.name").value("整合測試電腦"));

        mockMvc.perform(get("/api/procurement/get-data")
                .param("commodityType", "辦公用品")
                .header("Authorization", AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].commodity.name").value("第二個商品"));
    }

    @Test
    @DisplayName("整合測試 - 測試日期格式化")
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
    @DisplayName("整合測試 - 錯誤處理")
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
}

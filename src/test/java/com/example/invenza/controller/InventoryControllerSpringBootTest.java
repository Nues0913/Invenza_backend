package com.example.invenza.controller;

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

import com.example.invenza.repository.CommodityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
@DisplayName("整合測試 - InventoryController Spring Boot 測試")
public class InventoryControllerSpringBootTest {
    
    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJGMDAwMDEiLCJuYW1lIjoiQWRtaW4iLCJlbWFpbCI6InVzZXIxQGVtYWlsLmNvbSIsInBob25lIjoiMDkxMjM0NTY3OCIsInJvbGUiOiJGIiwiaWF0IjoxNzUwNTQxMjE0LCJleHAiOjE3ODY1NDEyMTR9.9qHMFfXM4IeKSWFvdHJQxgCk2x5DTCVxzoqlTiU5e0k";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommodityRepository commodityRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        log.info("測試開始 - 使用data-test.sql中的測試資料");
    }

    @Test
    @DisplayName("整合測試 - GET /api/inventory/get-data 完整流程")
    void testGetInventoryData() throws Exception {
        mockMvc.perform(get("/api/inventory/get-data")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3)) // 基於測試資料中有3個商品
                .andExpect(jsonPath("$.data[0].commodity").exists())
                .andExpect(jsonPath("$.data[0].stockQuantity").exists())
                .andExpect(jsonPath("$.data[0].expectedImportQuantity").exists())
                .andExpect(jsonPath("$.data[0].expectedExportQuantity").exists())
                .andExpect(jsonPath("$.data[0].futureStockQuantity").exists());
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/inventory/get-data 未來庫存排序")
    void testGetInventoryDataSortedByFutureStock() throws Exception {
        mockMvc.perform(get("/api/inventory/get-data")
                .param("inventoryFilterType", "inventoryFuture")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                // 驗證結果已經按照未來庫存排序（升序）
                // 基於測試數據：
                // apple: 30 - 20 + 10 = 20
                // banana: 12 - 7 + 5 = 10
                // cake: 60 - 22 + 10 = 48
                // 排序後應該是: banana, apple, cake
                .andExpect(jsonPath("$.data[0].commodity.name").value("banana"))
                .andExpect(jsonPath("$.data[1].commodity.name").value("apple"))
                .andExpect(jsonPath("$.data[2].commodity.name").value("cake"));
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/inventory/get-data 當前庫存排序")
    void testGetInventoryDataSortedByCurrentStock() throws Exception {
        mockMvc.perform(get("/api/inventory/get-data")
                .param("inventoryFilterType", "inventoryNow")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                // 驗證結果已經按照當前庫存排序（升序）
                // 基於測試數據：banana(12), apple(30), cake(60)
                .andExpect(jsonPath("$.data[0].commodity.name").value("banana"))
                .andExpect(jsonPath("$.data[1].commodity.name").value("apple"))
                .andExpect(jsonPath("$.data[2].commodity.name").value("cake"));
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/inventory/get-data 商品類型過濾")
    void testGetInventoryDataFilteredByType() throws Exception {
        mockMvc.perform(get("/api/inventory/get-data")
                .param("commodityType", "Z232")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                // 測試數據中，只有apple的類型是Z232
                .andExpect(jsonPath("$.data[0].commodity.type").value("Z232"))
                .andExpect(jsonPath("$.data[0].commodity.name").value("apple"));
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/inventory/get-data 商品名稱過濾")
    void testGetInventoryDataFilteredByName() throws Exception {
        mockMvc.perform(get("/api/inventory/get-data")
                .param("commodityName", "cake")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                // 測試數據中，名稱為cake的商品
                .andExpect(jsonPath("$.data[0].commodity.name").value("cake"));
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/inventory/get-data 未來庫存計算準確性")
    void testFutureStockCalculation() throws Exception {
        mockMvc.perform(get("/api/inventory/get-data")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                // 驗證未來庫存計算是否正確
                // apple: 30 - 20 + 10 = 20
                // banana: 12 - 7 + 5 = 10
                // cake: 60 - 22 + 10 = 48
                .andExpect(jsonPath("$.data[?(@.commodity.name=='apple')].futureStockQuantity").value(20.0))
                .andExpect(jsonPath("$.data[?(@.commodity.name=='banana')].futureStockQuantity").value(10.0))
                .andExpect(jsonPath("$.data[?(@.commodity.name=='cake')].futureStockQuantity").value(48.0));
    }

    @Test
    @DisplayName("整合測試 - GET /api/inventory/get-data 當前庫存(stockQuantity)範圍過濾(min, max)")
    void testGetInventoryDataFilteredByCurrentStockRange() throws Exception {
        mockMvc.perform(get("/api/inventory/get-data")
                .param("inventoryFilterType", "inventoryNow")
                .param("minAmount", "20")
                .param("maxAmount", "50")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                // 驗證結果只包含當前庫存在20到50之間的商品
                // 基於測試數據：banana(12), apple(30), cake(60)
                // 只有apple符合條件
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].commodity.name").value("apple"))
                .andExpect(jsonPath("$.data[0].stockQuantity").value(30.0));
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/inventory/get-data 當前庫存(stockQuantity)只設定最小值過濾(min)")
    void testGetInventoryDataFilteredByMinCurrentStock() throws Exception {
        mockMvc.perform(get("/api/inventory/get-data")
                .param("inventoryFilterType", "inventoryNow")
                .param("minAmount", "30")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                // 驗證結果只包含當前庫存大於等於30的商品
                // 基於測試數據：banana(12), apple(30), cake(60)
                // apple和cake符合條件
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[?(@.commodity.name=='apple')].stockQuantity").value(30.0))
                .andExpect(jsonPath("$.data[?(@.commodity.name=='cake')].stockQuantity").value(60.0));
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/inventory/get-data 當前庫存(stockQuantity)只設定最大值過濾(max)")
    void testGetInventoryDataFilteredByMaxCurrentStock() throws Exception {
        mockMvc.perform(get("/api/inventory/get-data")
                .param("inventoryFilterType", "inventoryNow")
                .param("maxAmount", "30")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                // 驗證結果只包含當前庫存小於等於30的商品
                // 基於測試數據：banana(12), apple(30), cake(60)
                // banana和apple符合條件
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[?(@.commodity.name=='banana')].stockQuantity").value(12.0))
                .andExpect(jsonPath("$.data[?(@.commodity.name=='apple')].stockQuantity").value(30.0));
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/inventory/get-data 未來庫存(futureStockQuantity)範圍過濾(min, max)")
    void testGetInventoryDataFilteredByFutureStockRange() throws Exception {
        mockMvc.perform(get("/api/inventory/get-data")
                .param("inventoryFilterType", "inventoryFuture")
                .param("minAmount", "15")
                .param("maxAmount", "30")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                // 驗證結果只包含未來庫存在15到30之間的商品
                // 基於測試數據：
                // apple: 30 - 20 + 10 = 20
                // banana: 12 - 7 + 5 = 10
                // cake: 60 - 22 + 10 = 48
                // 只有apple符合條件
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].commodity.name").value("apple"))
                .andExpect(jsonPath("$.data[0].futureStockQuantity").value(20.0));
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/inventory/get-data 未來庫存(futureStockQuantity)只設定最小值過濾(min)")
    void testGetInventoryDataFilteredByMinFutureStock() throws Exception {
        mockMvc.perform(get("/api/inventory/get-data")
                .param("inventoryFilterType", "inventoryFuture")
                .param("minAmount", "20")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                // 驗證結果只包含未來庫存大於等於20的商品
                // 基於測試數據：
                // apple: 30 - 20 + 10 = 20
                // banana: 12 - 7 + 5 = 10
                // cake: 60 - 22 + 10 = 48
                // apple和cake符合條件
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[?(@.commodity.name=='apple')].futureStockQuantity").value(20.0))
                .andExpect(jsonPath("$.data[?(@.commodity.name=='cake')].futureStockQuantity").value(48.0));
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/inventory/get-data 未來庫存(futureStockQuantity)只設定最大值過濾(max)")
    void testGetInventoryDataFilteredByMaxFutureStock() throws Exception {
        mockMvc.perform(get("/api/inventory/get-data")
                .param("inventoryFilterType", "inventoryFuture")
                .param("maxAmount", "20")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                // 驗證結果只包含未來庫存小於等於20的商品
                // 基於測試數據：
                // apple: 30 - 20 + 10 = 20
                // banana: 12 - 7 + 5 = 10
                // cake: 60 - 22 + 10 = 48
                // banana和apple符合條件
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[?(@.commodity.name=='banana')].futureStockQuantity").value(10.0))
                .andExpect(jsonPath("$.data[?(@.commodity.name=='apple')].futureStockQuantity").value(20.0));
    }
    
    @Test
    @DisplayName("整合測試 - GET /api/inventory/get-data 過濾條件組合 (min, max, inventoryFilterType, commodityType)")
    void testGetInventoryDataWithMultipleFilters() throws Exception {
        mockMvc.perform(get("/api/inventory/get-data")
                .param("inventoryFilterType", "inventoryFuture")
                .param("minAmount", "10")
                .param("maxAmount", "50")
                .param("commodityType", "Z232")
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                // 驗證結果包含未來庫存在10到50之間且類型為Z232的商品
                // 基於測試數據中，只有apple的類型是Z232且未來庫存為20
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].commodity.type").value("Z232"))
                .andExpect(jsonPath("$.data[0].commodity.name").value("apple"))
                .andExpect(jsonPath("$.data[0].futureStockQuantity").value(20.0));
    }
}

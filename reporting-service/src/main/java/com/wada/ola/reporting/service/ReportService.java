package com.wada.ola.reporting.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wada.ola.reporting.dto.DashboardReport;
import com.wada.ola.reporting.dto.FinanceSummary;
import com.wada.ola.reporting.dto.InventorySummary;
import com.wada.ola.reporting.dto.PersonnelSummary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReportService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ReportService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public DashboardReport getDashboard() {
        return getDashboard(null);
    }

    public DashboardReport getDashboard(Long commandId) {
        String suffix = commandId != null ? "?commandId=" + commandId : "";
        PersonnelSummary personnel = fetch(
                "http://personnel-service/api/personnel/reports/summary" + suffix,
                PersonnelSummary.class);
        InventorySummary inventory = fetch(
                "http://inventory-service/api/inventory/reports/summary" + suffix,
                InventorySummary.class);
        FinanceSummary finance = fetch(
                "http://finance-service/api/finance/reports/summary" + suffix,
                FinanceSummary.class);
        return new DashboardReport(personnel, inventory, finance);
    }

    private <T> T fetch(String url, Class<T> type) {
        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            return objectMapper.treeToValue(response.get("data"), type);
        } catch (Exception e) {
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                return null;
            }
        }
    }
}

package com.wada.ola.reporting.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wada.ola.reporting.dto.AuditLogEntry;
import com.wada.ola.reporting.dto.AuditLogPage;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuditLogAggregationService {

    // service name -> its own gateway-routed audit-logs path
    private static final Map<String, String> SOURCES = new LinkedHashMap<>();
    static {
        SOURCES.put("auth-service", "/api/auth/audit-logs");
        SOURCES.put("personnel-service", "/api/personnel/audit-logs");
        SOURCES.put("inventory-service", "/api/inventory/audit-logs");
        SOURCES.put("finance-service", "/api/finance/audit-logs");
    }

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AuditLogAggregationService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public AuditLogPage search(String action, String targetTable, Long commandId,
                                String from, String to,
                                int page, int size, String authorization, String authRole) {
        // Over-fetch enough of each service's most-recent rows to cover the requested page
        // after merging — simple and correct for realistic data volumes, not true distributed
        // pagination across very deep pages.
        int fetchSize = (page + 1) * Math.max(size, 1);

        List<AuditLogEntry> merged = new ArrayList<>();
        for (Map.Entry<String, String> source : SOURCES.entrySet()) {
            merged.addAll(fetchFrom(source.getKey(), source.getValue(),
                    action, targetTable, commandId, from, to, fetchSize, authorization, authRole));
        }
        merged.sort(Comparator.comparing(AuditLogEntry::getCreatedAt).reversed());

        long total = merged.size();
        int fromIndex = Math.min(page * size, merged.size());
        int toIndex = Math.min(fromIndex + size, merged.size());
        List<AuditLogEntry> pageContent = merged.subList(fromIndex, toIndex);
        return new AuditLogPage(pageContent, total, page, size);
    }

    private List<AuditLogEntry> fetchFrom(String serviceName, String path,
                                           String action, String targetTable, Long commandId,
                                           String from, String to,
                                           int size, String authorization, String authRole) {
        List<AuditLogEntry> result = new ArrayList<>();
        try {
            UriComponentsBuilder uri = UriComponentsBuilder
                    .fromUriString("http://" + serviceName + path)
                    .queryParam("page", 0)
                    .queryParam("size", size);
            if (action != null) uri.queryParam("action", action);
            if (targetTable != null) uri.queryParam("targetTable", targetTable);
            if (commandId != null) uri.queryParam("commandId", commandId);
            if (from != null) uri.queryParam("from", from);
            if (to != null) uri.queryParam("to", to);

            HttpHeaders headers = new HttpHeaders();
            if (authorization != null) headers.set("Authorization", authorization);
            if (authRole != null) headers.set("X-Auth-Role", authRole);

            JsonNode response = restTemplate.exchange(
                    uri.build().toUri(), HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class).getBody();
            JsonNode content = response != null ? response.path("data").path("content") : null;
            if (content != null && content.isArray()) {
                for (JsonNode node : content) {
                    AuditLogEntry entry = objectMapper.treeToValue(node, AuditLogEntry.class);
                    entry.setSourceService(serviceName);
                    result.add(entry);
                }
            }
        } catch (Exception e) {
            // A single service being unreachable shouldn't break the aggregated view.
        }
        return result;
    }
}

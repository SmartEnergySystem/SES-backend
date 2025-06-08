package com.SES.entity;

import com.SES.dto.policyItem.PolicyItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Policy {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private Long id;
    private String name;
    private Long deviceId;
    private LocalDateTime createtime;
    private LocalDateTime updatetime;

//    public String toJson(PolicyMapper policyMapper) {
//        try {
//            List<PolicyItemDTO> items = policyMapper.getByPolicyId(this.id);
//
//            PolicyJsonDTO dto = new PolicyJsonDTO();
//            dto.setId(this.id);
//            dto.setName(this.name);
//            dto.setDeviceId(this.deviceId);
//            dto.setCreatetime(this.createtime);
//            dto.setUpdatetime(this.updatetime);
//            dto.setItems(items);
//
//            return objectMapper.writeValueAsString(dto);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to serialize policy to JSON", e);
//        }
//    }
//
//    @Data
//    private static class PolicyJsonDTO {
//        private Long id;
//        private String name;
//        private Long deviceId;
//        private LocalDateTime createtime;
//        private LocalDateTime updatetime;
//        private List<PolicyItemDTO> items;
//    }
}
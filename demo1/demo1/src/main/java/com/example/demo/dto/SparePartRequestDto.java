// com/example/demo/dto/SparePartRequestDto.java
package com.example.demo.dto;

public class SparePartRequestDto {

    private Long   sparePartId;        // optional — links to inventory
    private String sparePartName;      // required if no id
    private String partNumber;
    private Integer requestedQuantity;
    private String urgency;            // LOW / MEDIUM / HIGH / CRITICAL
    private String reason;

    public Long getSparePartId() { return sparePartId; }
    public void setSparePartId(Long sparePartId) { this.sparePartId = sparePartId; }

    public String getSparePartName() { return sparePartName; }
    public void setSparePartName(String sparePartName) { this.sparePartName = sparePartName; }

    public String getPartNumber() { return partNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

    public Integer getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(Integer requestedQuantity) { this.requestedQuantity = requestedQuantity; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
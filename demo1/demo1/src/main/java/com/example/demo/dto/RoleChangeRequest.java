package com.example.demo.dto;

/**
 * Request body for POST /api/admin/role-change
 *
 * {
 *   "fromRole": "technician",   // current role: admin | technician | staff | manager
 *   "fromId":   3,              // primary key in the source table
 *   "toRole":   "manager"       // target role
 * }
 */
public class RoleChangeRequest {

    private String fromRole;
    private Long   fromId;
    private String toRole;

    public String getFromRole() { return fromRole; }
    public void   setFromRole(String fromRole) { this.fromRole = fromRole; }

    public Long getFromId() { return fromId; }
    public void setFromId(Long fromId) { this.fromId = fromId; }

    public String getToRole() { return toRole; }
    public void   setToRole(String toRole) { this.toRole = toRole; }
}
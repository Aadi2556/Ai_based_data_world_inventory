package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_evaluations")
public class SupplierEvaluation {

    public enum Grade { A_PLUS, A, B, C, D, F }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    // ── Scoring dimensions (1–5 each) ──
    @Column(name = "delivery_score", nullable = false)
    private Integer deliveryScore;           // On-time delivery rate

    @Column(name = "quality_score", nullable = false)
    private Integer qualityScore;            // Product/material quality

    @Column(name = "pricing_score", nullable = false)
    private Integer pricingScore;            // Value for money / competitive pricing

    @Column(name = "communication_score", nullable = false)
    private Integer communicationScore;      // Responsiveness and clarity

    @Column(name = "order_accuracy_score", nullable = false)
    private Integer orderAccuracyScore;      // Correct items, quantities, specs

    // ── Computed ──
    @Column(name = "overall_score")
    private Double overallScore;             // Weighted average (auto-calculated)

    @Enumerated(EnumType.STRING)
    @Column(name = "grade")
    private Grade grade;

    // ── Context ──
    @Column(name = "evaluation_period")
    private String evaluationPeriod;         // e.g. "Q1 2026", "Jan 2026"

    @Column(name = "evaluated_by")
    private String evaluatedBy;

    @Column(name = "evaluation_date")
    private LocalDate evaluationDate;

    @Column(name = "linked_order_number")
    private String linkedOrderNumber;        // Optional PO reference

    @Column(length = 1000)
    private String strengths;

    @Column(length = 1000)
    private String improvements;

    @Column(length = 1000)
    private String comments;

    @Column(name = "recommend_continue")
    private Boolean recommendContinue = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        computeScore();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        computeScore();
    }

    /**
     * Weighted scoring:
     * Delivery      25%
     * Quality       30%
     * Pricing       15%
     * Communication 15%
     * Accuracy      15%
     */
    public void computeScore() {
        if (deliveryScore == null || qualityScore == null || pricingScore == null
                || communicationScore == null || orderAccuracyScore == null) return;

        overallScore = (deliveryScore * 0.25)
                + (qualityScore * 0.30)
                + (pricingScore * 0.15)
                + (communicationScore * 0.15)
                + (orderAccuracyScore * 0.15);

        // Assign grade
        if (overallScore >= 4.7)      grade = Grade.A_PLUS;
        else if (overallScore >= 4.0) grade = Grade.A;
        else if (overallScore >= 3.0) grade = Grade.B;
        else if (overallScore >= 2.0) grade = Grade.C;
        else if (overallScore >= 1.0) grade = Grade.D;
        else                          grade = Grade.F;
    }

    // ── Getters & Setters ──
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public Integer getDeliveryScore() { return deliveryScore; }
    public void setDeliveryScore(Integer deliveryScore) { this.deliveryScore = deliveryScore; }

    public Integer getQualityScore() { return qualityScore; }
    public void setQualityScore(Integer qualityScore) { this.qualityScore = qualityScore; }

    public Integer getPricingScore() { return pricingScore; }
    public void setPricingScore(Integer pricingScore) { this.pricingScore = pricingScore; }

    public Integer getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(Integer communicationScore) { this.communicationScore = communicationScore; }

    public Integer getOrderAccuracyScore() { return orderAccuracyScore; }
    public void setOrderAccuracyScore(Integer orderAccuracyScore) { this.orderAccuracyScore = orderAccuracyScore; }

    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }

    public Grade getGrade() { return grade; }
    public void setGrade(Grade grade) { this.grade = grade; }

    public String getEvaluationPeriod() { return evaluationPeriod; }
    public void setEvaluationPeriod(String evaluationPeriod) { this.evaluationPeriod = evaluationPeriod; }

    public String getEvaluatedBy() { return evaluatedBy; }
    public void setEvaluatedBy(String evaluatedBy) { this.evaluatedBy = evaluatedBy; }

    public LocalDate getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; }

    public String getLinkedOrderNumber() { return linkedOrderNumber; }
    public void setLinkedOrderNumber(String linkedOrderNumber) { this.linkedOrderNumber = linkedOrderNumber; }

    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }

    public String getImprovements() { return improvements; }
    public void setImprovements(String improvements) { this.improvements = improvements; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Boolean getRecommendContinue() { return recommendContinue; }
    public void setRecommendContinue(Boolean recommendContinue) { this.recommendContinue = recommendContinue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
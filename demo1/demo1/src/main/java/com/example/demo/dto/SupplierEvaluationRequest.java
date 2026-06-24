package com.example.demo.dto;

import java.time.LocalDate;

public class SupplierEvaluationRequest {

    private Long supplierId;
    private Integer deliveryScore;
    private Integer qualityScore;
    private Integer pricingScore;
    private Integer communicationScore;
    private Integer orderAccuracyScore;
    private String evaluationPeriod;
    private String evaluatedBy;
    private LocalDate evaluationDate;
    private String linkedOrderNumber;
    private String strengths;
    private String improvements;
    private String comments;
    private Boolean recommendContinue;

    // Getters & Setters
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

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
}
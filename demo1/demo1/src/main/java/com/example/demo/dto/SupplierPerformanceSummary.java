package com.example.demo.dto;

/**
 * Aggregate performance summary for one supplier (used in leaderboard & supplier detail).
 */
public class SupplierPerformanceSummary {

    private Long supplierId;
    private String supplierName;
    private String supplierCategory;
    private Long totalEvaluations;

    private Double avgOverallScore;
    private Double avgDeliveryScore;
    private Double avgQualityScore;
    private Double avgPricingScore;
    private Double avgCommunicationScore;
    private Double avgOrderAccuracyScore;

    private String latestGrade;         // A+, A, B, C, D, F
    private String latestPeriod;
    private Double latestOverallScore;
    private String trend;               // UP, DOWN, STABLE (compared to previous eval)
    private Boolean recommendContinue;
    private Integer rank;               // Position in leaderboard

    // Builder-style setters for convenience
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getSupplierCategory() { return supplierCategory; }
    public void setSupplierCategory(String supplierCategory) { this.supplierCategory = supplierCategory; }

    public Long getTotalEvaluations() { return totalEvaluations; }
    public void setTotalEvaluations(Long totalEvaluations) { this.totalEvaluations = totalEvaluations; }

    public Double getAvgOverallScore() { return avgOverallScore; }
    public void setAvgOverallScore(Double avgOverallScore) { this.avgOverallScore = avgOverallScore; }

    public Double getAvgDeliveryScore() { return avgDeliveryScore; }
    public void setAvgDeliveryScore(Double avgDeliveryScore) { this.avgDeliveryScore = avgDeliveryScore; }

    public Double getAvgQualityScore() { return avgQualityScore; }
    public void setAvgQualityScore(Double avgQualityScore) { this.avgQualityScore = avgQualityScore; }

    public Double getAvgPricingScore() { return avgPricingScore; }
    public void setAvgPricingScore(Double avgPricingScore) { this.avgPricingScore = avgPricingScore; }

    public Double getAvgCommunicationScore() { return avgCommunicationScore; }
    public void setAvgCommunicationScore(Double avgCommunicationScore) { this.avgCommunicationScore = avgCommunicationScore; }

    public Double getAvgOrderAccuracyScore() { return avgOrderAccuracyScore; }
    public void setAvgOrderAccuracyScore(Double avgOrderAccuracyScore) { this.avgOrderAccuracyScore = avgOrderAccuracyScore; }

    public String getLatestGrade() { return latestGrade; }
    public void setLatestGrade(String latestGrade) { this.latestGrade = latestGrade; }

    public String getLatestPeriod() { return latestPeriod; }
    public void setLatestPeriod(String latestPeriod) { this.latestPeriod = latestPeriod; }

    public Double getLatestOverallScore() { return latestOverallScore; }
    public void setLatestOverallScore(Double latestOverallScore) { this.latestOverallScore = latestOverallScore; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }

    public Boolean getRecommendContinue() { return recommendContinue; }
    public void setRecommendContinue(Boolean recommendContinue) { this.recommendContinue = recommendContinue; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
}
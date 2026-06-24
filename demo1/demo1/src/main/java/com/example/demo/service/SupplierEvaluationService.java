package com.example.demo.service;

import com.example.demo.dto.SupplierEvaluationRequest;
import com.example.demo.dto.SupplierPerformanceSummary;
import com.example.demo.model.Supplier;
import com.example.demo.model.SupplierEvaluation;
import com.example.demo.repository.SupplierEvaluationRepository;
import com.example.demo.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplierEvaluationService {

    @Autowired
    private SupplierEvaluationRepository evalRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    // ===================== CRUD =====================

    public List<SupplierEvaluation> getAllEvaluations() {
        return evalRepository.findAllOrderByScoreDesc();
    }

    public List<SupplierEvaluation> getEvaluationsBySupplier(Long supplierId) {
        return evalRepository.findBySupplierIdOrderByEvaluationDateDesc(supplierId);
    }

    public Optional<SupplierEvaluation> getEvaluationById(Long id) {
        return evalRepository.findById(id);
    }

    @Transactional
    public SupplierEvaluation createEvaluation(SupplierEvaluationRequest request) {
        validateScores(request);

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + request.getSupplierId()));

        SupplierEvaluation eval = new SupplierEvaluation();
        eval.setSupplier(supplier);
        mapRequestToEntity(request, eval);
        eval.computeScore();
        return evalRepository.save(eval);
    }

    @Transactional
    public SupplierEvaluation updateEvaluation(Long id, SupplierEvaluationRequest request) {
        validateScores(request);

        SupplierEvaluation eval = evalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluation not found: " + id));

        if (!eval.getSupplier().getId().equals(request.getSupplierId())) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            eval.setSupplier(supplier);
        }

        mapRequestToEntity(request, eval);
        eval.computeScore();
        return evalRepository.save(eval);
    }

    @Transactional
    public void deleteEvaluation(Long id) {
        evalRepository.deleteById(id);
    }

    private void mapRequestToEntity(SupplierEvaluationRequest r, SupplierEvaluation e) {
        e.setDeliveryScore(r.getDeliveryScore());
        e.setQualityScore(r.getQualityScore());
        e.setPricingScore(r.getPricingScore());
        e.setCommunicationScore(r.getCommunicationScore());
        e.setOrderAccuracyScore(r.getOrderAccuracyScore());
        e.setEvaluationPeriod(r.getEvaluationPeriod());
        e.setEvaluatedBy(r.getEvaluatedBy());
        e.setEvaluationDate(r.getEvaluationDate() != null ? r.getEvaluationDate() : LocalDate.now());
        e.setLinkedOrderNumber(r.getLinkedOrderNumber());
        e.setStrengths(r.getStrengths());
        e.setImprovements(r.getImprovements());
        e.setComments(r.getComments());
        e.setRecommendContinue(r.getRecommendContinue() != null ? r.getRecommendContinue() : true);
    }

    private void validateScores(SupplierEvaluationRequest r) {
        int[] scores = {
                r.getDeliveryScore(), r.getQualityScore(), r.getPricingScore(),
                r.getCommunicationScore(), r.getOrderAccuracyScore()
        };
        for (int s : scores) {
            if (s < 1 || s > 5) throw new RuntimeException("All scores must be between 1 and 5.");
        }
    }

    // ===================== LEADERBOARD =====================

    public List<SupplierPerformanceSummary> getLeaderboard() {
        List<Object[]> rankings = evalRepository.findSupplierRankings();
        List<SupplierPerformanceSummary> result = new ArrayList<>();

        int rank = 1;
        for (Object[] row : rankings) {
            Long supplierId = ((Number) row[0]).longValue();
            Double avgScore = ((Number) row[1]).doubleValue();

            Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
            if (supplier == null) continue;

            SupplierPerformanceSummary summary = buildSummary(supplier, supplierId, avgScore, rank++);
            result.add(summary);
        }
        return result;
    }

    public SupplierPerformanceSummary getSupplierSummary(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        Object[] raw = evalRepository.getAverageScoresBySupplierId(supplierId);
        if (raw == null || raw.length == 0) return null;

        Double avgOverall = raw[1] != null ? ((Number) raw[1]).doubleValue() : null;
        SupplierPerformanceSummary summary = buildSummary(supplier, supplierId, avgOverall, 0);

        // Detailed avg scores
        if (raw[2] != null) summary.setAvgDeliveryScore(((Number) raw[2]).doubleValue());
        if (raw[3] != null) summary.setAvgQualityScore(((Number) raw[3]).doubleValue());
        if (raw[4] != null) summary.setAvgPricingScore(((Number) raw[4]).doubleValue());
        if (raw[5] != null) summary.setAvgCommunicationScore(((Number) raw[5]).doubleValue());
        if (raw[6] != null) summary.setAvgOrderAccuracyScore(((Number) raw[6]).doubleValue());
        if (raw[7] != null) summary.setTotalEvaluations(((Number) raw[7]).longValue());

        return summary;
    }

    private SupplierPerformanceSummary buildSummary(Supplier supplier, Long supplierId,
                                                    Double avgScore, int rank) {
        SupplierPerformanceSummary s = new SupplierPerformanceSummary();
        s.setSupplierId(supplierId);
        s.setSupplierName(supplier.getName());
        s.setSupplierCategory(supplier.getCategory());
        s.setAvgOverallScore(avgScore != null ? round2(avgScore) : null);
        s.setRank(rank);

        // Latest evaluation
        List<SupplierEvaluation> evals = evalRepository.findBySupplierIdOrderByEvaluationDateDesc(supplierId);
        s.setTotalEvaluations((long) evals.size());

        if (!evals.isEmpty()) {
            SupplierEvaluation latest = evals.get(0);
            s.setLatestOverallScore(round2(latest.getOverallScore()));
            s.setLatestGrade(latest.getGrade() != null ? latest.getGrade().name().replace("_PLUS", "+") : "—");
            s.setLatestPeriod(latest.getEvaluationPeriod());
            s.setRecommendContinue(latest.getRecommendContinue());

            // Trend: compare latest vs previous
            if (evals.size() >= 2) {
                double prev = evals.get(1).getOverallScore();
                double curr = latest.getOverallScore();
                if (curr > prev + 0.2)       s.setTrend("UP");
                else if (curr < prev - 0.2)  s.setTrend("DOWN");
                else                         s.setTrend("STABLE");
            } else {
                s.setTrend("NEW");
            }
        }
        return s;
    }

    // ===================== DASHBOARD STATS =====================

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        List<SupplierEvaluation> all = evalRepository.findAll();
        stats.put("totalEvaluations", all.size());

        OptionalDouble avg = all.stream().mapToDouble(SupplierEvaluation::getOverallScore).average();
        stats.put("platformAvgScore", avg.isPresent() ? round2(avg.getAsDouble()) : 0.0);

        long topSuppliers = all.stream()
                .filter(e -> e.getOverallScore() != null && e.getOverallScore() >= 4.0).count();
        stats.put("topSupplierCount", topSuppliers);

        long atRisk = all.stream()
                .filter(e -> e.getOverallScore() != null && e.getOverallScore() < 2.5).count();
        stats.put("atRiskCount", atRisk);

        return stats;
    }

    // ===================== TREND DATA (for charts) =====================

    public List<Map<String, Object>> getSupplierTrendData(Long supplierId) {
        return evalRepository.findBySupplierIdOrderByEvaluationDateDesc(supplierId)
                .stream()
                .sorted(Comparator.comparing(SupplierEvaluation::getEvaluationDate))
                .map(e -> {
                    Map<String, Object> point = new LinkedHashMap<>();
                    point.put("period", e.getEvaluationPeriod());
                    point.put("date", e.getEvaluationDate().toString());
                    point.put("overall", round2(e.getOverallScore()));
                    point.put("delivery", e.getDeliveryScore());
                    point.put("quality", e.getQualityScore());
                    point.put("pricing", e.getPricingScore());
                    point.put("communication", e.getCommunicationScore());
                    point.put("accuracy", e.getOrderAccuracyScore());
                    return point;
                })
                .collect(Collectors.toList());
    }

    private double round2(double v) { return Math.round(v * 100.0) / 100.0; }
}
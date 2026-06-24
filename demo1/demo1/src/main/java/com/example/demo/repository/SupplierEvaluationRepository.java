package com.example.demo.repository;

import com.example.demo.model.SupplierEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierEvaluationRepository extends JpaRepository<SupplierEvaluation, Long> {

    // All evaluations for one supplier, newest first
    List<SupplierEvaluation> findBySupplierIdOrderByEvaluationDateDesc(Long supplierId);

    // Latest evaluation per supplier (for leaderboard)
    @Query("""
        SELECT e FROM SupplierEvaluation e
        WHERE e.evaluationDate = (
            SELECT MAX(e2.evaluationDate) FROM SupplierEvaluation e2
            WHERE e2.supplier.id = e.supplier.id
        )
        ORDER BY e.overallScore DESC
    """)
    List<SupplierEvaluation> findLatestEvaluationPerSupplier();

    // Average scores per supplier (for trend analytics)
    @Query("""
        SELECT e.supplier.id,
               AVG(e.overallScore),
               AVG(e.deliveryScore),
               AVG(e.qualityScore),
               AVG(e.pricingScore),
               AVG(e.communicationScore),
               AVG(e.orderAccuracyScore),
               COUNT(e)
        FROM SupplierEvaluation e
        WHERE e.supplier.id = :supplierId
        GROUP BY e.supplier.id
    """)
    Object[] getAverageScoresBySupplierId(@Param("supplierId") Long supplierId);

    // All evals ordered by score desc — full leaderboard
    @Query("SELECT e FROM SupplierEvaluation e ORDER BY e.overallScore DESC")
    List<SupplierEvaluation> findAllOrderByScoreDesc();

    // Count evaluations per period
    List<SupplierEvaluation> findByEvaluationPeriod(String period);

    // Top N suppliers by average overall score
    @Query("""
        SELECT e.supplier.id, AVG(e.overallScore) as avg
        FROM SupplierEvaluation e
        GROUP BY e.supplier.id
        ORDER BY avg DESC
    """)
    List<Object[]> findSupplierRankings();

    boolean existsBySupplierIdAndEvaluationPeriod(Long supplierId, String period);
}
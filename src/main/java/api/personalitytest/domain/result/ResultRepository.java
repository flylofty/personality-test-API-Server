package api.personalitytest.domain.result;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {

    @Query("SELECT r FROM Result r WHERE r.testId = :testId AND r.resultId = :resultId")
    List<Result> findTestResultList(@Param("testId") Long testId, @Param("resultId") String resultId);

    @Query("SELECT r FROM Result r WHERE r.testId = :testId")
    List<Result> findAllByTestId(@Param("testId") Long testId);

    @Modifying
    @Query("DELETE FROM Result r WHERE r.testId = :testId")
    void deleteResultsByTestId(@Param("testId") Long testId);
}

package api.personalitytest.domain.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, Long> {

    @Query("SELECT t FROM Test t WHERE t.id = :id AND t.writer = :writer AND t.password = :password")
    Optional<Test> findByDeleteRequest(@Param("id") Long id, @Param("writer") String writer, @Param("password") String password);
}

package api.personalitytest.domain.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.testId = :testId ORDER BY i.id ASC")
    List<Item> findAllByTestId(@Param("testId") Long testId);

    @Modifying
    @Query("DELETE FROM Item i WHERE i.testId = :testId")
    void deleteItemsByTestId(@Param("testId") Long testId);

}

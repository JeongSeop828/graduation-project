package com.example._th_project.repository;

import com.example._th_project.domain.dto.InquiryDTO;
import com.example._th_project.domain.table.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    @Query("""
    select i.inquiryId
    from Inquiry i
    join i.user u
    where u.userId = :userId
""")
    List<Long> findInquiryIdbyUserId(@Param("userId") Long userId);

    @Query("""
    select new com.example._th_project.domain.dto.InquiryDTO(i.inquiryId, i.title, i.status)
    from Inquiry i
    join i.user u
    where u.userId = :userId
""")
    List<InquiryDTO> findInquiryDTObyUserId(@Param("userId") Long userId);

    @Query("SELECT i FROM Inquiry i JOIN FETCH i.user")
    List<Inquiry> findAllWithUser();

    @Query("SELECT i FROM Inquiry i JOIN FETCH i.user WHERE i.id = :id")
    Optional<Inquiry> findByIdWithUser(@Param("id") Long id);
}

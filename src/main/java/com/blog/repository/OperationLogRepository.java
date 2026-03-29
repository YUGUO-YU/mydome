package com.blog.repository;

import com.blog.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    Page<OperationLog> findByUserId(Long userId, Pageable pageable);
    Page<OperationLog> findByOperation(String operation, Pageable pageable);
}

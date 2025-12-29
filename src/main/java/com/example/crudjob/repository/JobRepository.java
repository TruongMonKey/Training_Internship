package com.example.crudjob.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.crudjob.entity.Job;

/**
 * Repository interface cung cấp các phương thức truy vấn cơ sở dữ liệu cho thực
 * thể Job.
 * Kế thừa từ JpaRepository để sử dụng các phương thức CRUD mặc định.
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    /**
     * Tìm kiếm công việc theo tiêu đề với tìm kiếm không phân biệt chữ hoa/thường.
     * Sử dụng toán tử LIKE trong câu truy vấn SQL để tìm kiếm từng phần tiêu đề.
     * 
     * @param title    tiêu đề công việc cần tìm kiếm (không bắt buộc phải khớp toàn
     *                 bộ)
     * @param pageable thông tin phân trang gồm số trang và số lượng bản ghi trên
     *                 mỗi trang
     * @return một Page chứa danh sách công việc khớp với tiêu đề tìm kiếm
     */
    Page<Job> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Tìm kiếm công việc theo công ty với tìm kiếm không phân biệt chữ hoa/thường.
     * Sử dụng toán tử LIKE trong câu truy vấn SQL để tìm kiếm từng phần tên công
     * ty.
     * 
     * @param company  tên công ty cần tìm kiếm (không bắt buộc phải khớp toàn bộ)
     * @param pageable thông tin phân trang gồm số trang và số lượng bản ghi trên
     *                 mỗi trang
     * @return một Page chứa danh sách công việc của công ty khớp với tìm kiếm
     */
    Page<Job> findByCompanyContainingIgnoreCase(String company, Pageable pageable);
}

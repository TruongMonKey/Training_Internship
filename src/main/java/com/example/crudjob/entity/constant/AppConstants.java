package com.example.crudjob.entity.constant;

/**
 * Lớp chứa các hằng số được sử dụng trong ứng dụng
 */
public class AppConstants {

    // ============ Pagination Constants ============
    /** Trang mặc định trong phân trang (dạng String cho @RequestParam) */
    public static final String DEFAULT_PAGE_STR = "0";

    /**
     * Kích thước trang mặc định trong phân trang (dạng String cho @RequestParam)
     */
    public static final String DEFAULT_PAGE_SIZE_STR = "10";

    // ============ HTTP Status Messages ============
    /** Thông báo tạo công việc thành công */
    public static final String JOB_CREATED_SUCCESS = "Job created successfully";

    /** Thông báo lấy danh sách công việc thành công */
    public static final String JOB_LIST_SUCCESS = "Get job list successfully";

    /** Thông báo lấy chi tiết công việc thành công */
    public static final String JOB_GET_SUCCESS = "Get job successfully";

    /** Thông báo cập nhật công việc thành công */
    public static final String JOB_UPDATED_SUCCESS = "Job updated successfully";

    /** Thông báo xoá công việc thành công */
    public static final String JOB_DELETED_SUCCESS = "Job deleted successfully";

    /** Thông báo tìm kiếm theo tiêu đề thành công */
    public static final String SEARCH_BY_TITLE_SUCCESS = "Search by title successfully";

    /** Thông báo tìm kiếm theo công ty thành công */
    public static final String SEARCH_BY_COMPANY_SUCCESS = "Search by company successfully";

    // ============ Exception Messages ============
    /** Thông báo lỗi validation */
    public static final String VALIDATION_FAILED = "Validation failed";

    /** Thông báo lỗi server */
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";

    // Prevent instantiation
    private AppConstants() {
        throw new AssertionError("Cannot instantiate AppConstants class");
    }
}

package com.example.crudjob.utils;

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

    // ============ Encryption/Decryption Error Messages ============
    /** Thông báo lỗi encryption */
    public static final String ENCRYPTION_ERROR = "Data encryption failed. Please try again or contact support.";

    /** Thông báo lỗi decryption */
    public static final String DECRYPTION_ERROR = "Data decryption failed. The data may be corrupted or encrypted with a different key.";

    /** Thông báo dữ liệu encrypted không hợp lệ */
    public static final String INVALID_ENCRYPTED_DATA = "Invalid encrypted data format";

    // Prevent instantiation
    private AppConstants() {
        throw new AssertionError("Cannot instantiate AppConstants class");
    }
}
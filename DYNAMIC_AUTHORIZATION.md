# Dynamic Role-Permission Authorization

## 📋 Giới thiệu

Hệ thống phân quyền **động** từ Role mà **không cần annotation** ở từng endpoint.

### Quy trình hoạt động:

```
Token (User + Roles)
         ↓
   JwtAuthenticationFilter
         ↓
   RolePermissionResolver
         ↓
   SecurityContext (Roles + Permissions)
         ↓
   SecurityConfig Checks
         ↓
   Endpoint Access
```

## 🔑 Các Component Chính

### 1. **JwtService** - Tạo Token
```java
// Token chỉ chứa userId, username, roles
generateAccessToken(userId, username, roles)
// Output: {sub, userId, roles, iat, exp}
```

### 2. **RolePermissionResolver** - Map Role → Permissions
```java
// Input: ["ROLE_USER", "ROLE_ADMIN"]
// Output: [ROLE_USER, ROLE_ADMIN, VIEW_JOBS, CREATE_JOBS, ...]
Collection<GrantedAuthority> resolveAuthorities(roles)
```

**Cách hoạt động:**
```
Role từ Token: ROLE_USER
              ↓
        Database Query
              ↓
        Find Role Entity
              ↓
        Get All Permissions
              ↓
        Return GrantedAuthorities
```

### 3. **JwtAuthenticationFilter** - Xác thực & Resolve
```java
// 1. Lấy token từ header
String jwt = extractTokenFromRequest(request);

// 2. Validate token
jwtService.validateToken(jwt);

// 3. Parse & lấy roles
List<String> roles = claims.get("roles", List.class);

// 4. Resolve permissions từ roles (tự động!)
Collection<GrantedAuthority> authorities = 
    rolePermissionResolver.resolveAuthorities(roles);

// 5. Set vào SecurityContext
authentication.setAuthorities(authorities);
```

### 4. **SecurityConfig** - Check Authorization
```java
.authorizeHttpRequests(auth -> auth
    // Role-based checks (tự động sử dụng permissions!)
    .requestMatchers(GET, "/api/jobs/**")
        .hasAnyRole("USER", "ADMIN")
    
    .requestMatchers(POST, "/api/jobs/**")
        .hasRole("ADMIN")
    
    .anyRequest().authenticated())
```

## 💡 Ví dụ Thực Tế

### Scenario 1: ROLE_USER access GET /api/jobs

**Token payload:**
```json
{
  "sub": "user123",
  "userId": 1,
  "roles": ["ROLE_USER"],
  "iat": 1767714709,
  "exp": 1767801109
}
```

**RolePermissionResolver resolves:**
```
ROLE_USER → Database
         → Query Role table
         → Find Role with name = ROLE_USER
         → Get permissions: [VIEW_JOBS]
         → Return: [ROLE_USER, VIEW_JOBS]
```

**SecurityContext authorities:**
```
[
  SimpleGrantedAuthority("ROLE_USER"),
  SimpleGrantedAuthority("VIEW_JOBS")
]
```

**SecurityConfig check:**
```
GET /api/jobs → .hasAnyRole("USER", "ADMIN")
             → Check if has ROLE_USER ✅
             → Access granted!
```

### Scenario 2: ROLE_USER access POST /api/jobs

**SecurityConfig check:**
```
POST /api/jobs → .hasRole("ADMIN")
              → Check if has ROLE_ADMIN ❌
              → Access denied! 403 Forbidden
```

### Scenario 3: ROLE_ADMIN access POST /api/jobs

**Token payload:**
```json
{
  "sub": "admin123",
  "userId": 2,
  "roles": ["ROLE_ADMIN"],
  "iat": 1767714709,
  "exp": 1767801109
}
```

**RolePermissionResolver resolves:**
```
ROLE_ADMIN → Database
          → Query Role table
          → Find Role with name = ROLE_ADMIN
          → Get permissions: [VIEW_JOBS, CREATE_JOBS, EDIT_JOBS, DELETE_JOBS, ...]
          → Return: [ROLE_ADMIN, VIEW_JOBS, CREATE_JOBS, ...]
```

**SecurityConfig check:**
```
POST /api/jobs → .hasRole("ADMIN")
              → Check if has ROLE_ADMIN ✅
              → Access granted!
```

## 🎯 Lợi ích

| Lợi ích | Chi tiết |
|---------|----------|
| **Không cần annotation** | Không phải dùng `@PreAuthorize` ở endpoint |
| **Động** | Thay đổi permissions trong DB → tức thì có hiệu lực |
| **Centralized** | Tất cả rules ở SecurityConfig, dễ manage |
| **Token nhẹ** | Token không chứa permissions → kích thước nhỏ |
| **Type-safe** | Enum ERole → lỗi compile-time |
| **Database-driven** | Permissions từ DB → linh hoạt |

## 🔧 Cách Sửa Permission Trong Database

### Example: Thêm permission mới cho ROLE_USER

1. **Tạo permission trong database:**
```sql
INSERT INTO permissions (id, name) VALUES (7, 'EDIT_JOBS');
```

2. **Link permission với role:**
```sql
INSERT INTO role_permission (role_id, permission_id) 
VALUES (1, 7);  -- ROLE_USER (id=1) có EDIT_JOBS (id=7)
```

3. **Restart application** hoặc clear cache

4. **User với ROLE_USER sẽ tự động có EDIT_JOBS permission!**

## ⚙️ Kiến trúc Code

```
src/main/java/com/example/crudjob/
├── service/
│   ├── JwtService.java              ← Tạo token (roles only)
│   └── RolePermissionResolver.java  ← Map roles → permissions
├── config/
│   ├── JwtAuthenticationFilter.java ← Parse token + resolve
│   └── SecurityConfig.java          ← Cấu hình rules
└── entity/
    ├── Role.java                    ← Role entity với permissions
    └── Permission.java              ← Permission entity
```

## 📝 Kết luận

Hệ thống này cho phép:
- ✅ **Không cần annotation** ở endpoints
- ✅ **Phân quyền dựa trên Role** lưu trong token
- ✅ **Permission được resolve động** từ database
- ✅ **Thay đổi permissions mà không cần code** - chỉ update database

**Token payload đơn giản:**
```json
{
  "sub": "username",
  "userId": 1,
  "roles": ["ROLE_USER"],
  "iat": 1767714709,
  "exp": 1767801109
}
```

**Authorities trong SecurityContext phong phú:**
```json
[
  "ROLE_USER",
  "VIEW_JOBS"
]
```

Mọi quyết định truy cập được quyết định bởi `SecurityConfig` - nơi tập trung tất cả rules! 🎯

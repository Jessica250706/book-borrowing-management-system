包结构如下所示：

web
├── book                     # 图书模块
│   ├── controller           # 图书控制层
│   │   └── BookController.java
│   ├── service              # 图书服务层接口
│   │   ├── BookCategoryService.java
│   │   └── BookInfoService.java
│   ├── service.impl         # 图书服务层实现
│   │   └── BookInfoServiceImpl.java
│   ├── mapper
│   │   ├── BookCategoryMapper.java
│   │   ├── BookInfoMapper.java
│   │   └── BookReservationMapper.java
│   ├── entity
│   │   ├── BookCategory.java
│   │   ├── BookInfo.java
│   │   ├── BookQueryParam.java
│   │   ├── BookReservation.java
│   │   └── BorrowRequest.java
│   └── dto                  # 图书数据传输对象
│       ├── BookAdminDTO.java
│       ├── BookDetailDTO.java
│       ├── BookListDTO.java
│       ├── BookListVO.java
│       ├── BorrowResultDTO.java
│       └── ReserveResultDTO.java
├── borrow
│   ├── record
│   │   ├── controller
│   │   │   ├── BookOperationLogController.java
│   │   │   └── BorrowRecordController.java
│   │   ├── service
│   │   │   ├── BookBorrowService.java
│   │   │   └── BookOperationLogService.java
│   │   ├── service.impl
│   │   │   ├── BookBorrowServiceImpl.java
│   │   │   └── BookOperationLogServiceImpl.java
│   │   ├── mapper
│   │   │   ├── BookBorrowMapper.java
│   │   │   └── BookOperationLogMapper.java
│   │   ├── entity
│   │   │   ├── BatchOperateParam.java
│   │   │   ├── BookBorrow.java
│   │   │   ├── BookOperationLog.java
│   │   │   ├── BorrowParam.java
│   │   │   └── CurrentBorrowQueryParam.java
│   │   └── dto
│   │       ├── BaseBorrowRecordDTO.java
│   │       ├── BookInfoVO.java
│   │       ├── BorrowRecordDTO.java
│   │       └── CurrentBorrowDTO.java
│   └── renew
│       ├── controller
│       │   └── BookRenewController.java
│       ├── service
│       │   └── BookRenewService.java
│       ├── service.impl
│       │   └── BookRenewServiceImpl.java
│       ├── mapper
│       │   └── BookRenewMapper.java
│       ├── entity
│       │   └── BookRenew.java
│       └── dto
│           └── RemainingRenewDaysDTO.java
└── system                   # 系统模块
    ├── menu                 # 菜单子模块
    │   ├── controller       # 菜单控制层
    │   │   └── SysMenuController.java
    │   ├── service          # 菜单服务层接口
    │   │   └── SysMenuService.java
    │   ├── service.impl     # 菜单服务层实现
    │   │   └── SysMenuServiceImpl.java
    │   ├── mapper
    │   │   └── SysMenuMapper.java
    │   ├── entity           # 菜单实体类
    │   │   └── SysMenu.java
    │   └── dto              # 菜单数据传输对象
    │       └── MenuDTO.java
    ├── message              
    ├── role                 # 角色子模块
    │   ├── controller       # 角色控制层
    │   │   └── SysRoleController.java
    │   ├── service          # 角色服务层接口
    │   │   └── SysRoleService.java
    │   ├── service.impl     # 角色服务层实现
    │   │   └── SysRoleServiceImpl.java
    │   ├── mapper
    │   │   └── SysRoleMapper.java
    │   ├── entity           # 角色实体类/参数
    │   │   ├── SysRole.java
    │   │   └── RoleParam.java
    │   └── dto
    │       ├── RoleInfoDTO.java
    │       └── SysRoleDetailDTO.java
    └── user                 # 用户子模块
        ├── controller       # 角色控制层
        │   └── SysUserController.java
        ├── service          # 角色服务层接口
        │   ├── SysUserRoleService.java
        │   │── SysUserService.java
        │   └── SysUserService.java
        ├── service.impl     # 角色服务层实现
        │   ├── SysUserRoleServiceImpl.java
        │   │── SysUserServiceImpl.java
        │   └── SysUserServiceImpl.java
        ├── mapper
        │   ├── SysUserMapper.java
        │   └── SysUserRoleMapper.java
        ├── entity           # 角色实体类/参数
        │   ├── SysUser.java
        │   └── SysUserRole.java
        └── dto
            ├── AccountStatusDTO.java
            ├── CreditInfoDTO.java
            ├── RegisterRequestVO.java
            ├── RegisterResponseDTO.java
            ├── SysUserDTO.java
            ├── UserInfoVO.java
            ├── UserListRequestVO.java
            ├── UserListResponseDTO.java
            ├── UserRoleUpdateRequestVO.java
            ├── UserRoleUpdateResponseDTO.java
            ├── UserRoleUpgradeRequestVO.java
            └── UserRoleUpgradeResponseDTO.java

## 包结构

```
com.xq.web
├── system/                 // 系统管理模块
│   ├── user/               // 用户管理子模块
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   └── entity/
│   ├── role/               // 角色管理子模块
│   ├── menu/               // 菜单管理子模块
│   └── message/            // 消息管理子模块
├── book/                   // 书籍管理模块
│   ├── category/           // 分类管理子模块
│   ├── info/               // 书籍信息子模块
│   └── preview/            // 预览管理子模块
├── borrow/                 // 借阅管理模块
│   ├── record/             // 借阅记录子模块
│   └── renew/              // 续借管理子模块
└── reservation/            // 预约管理模块
    ├── controller/
    ├── service/
    └── ...
```
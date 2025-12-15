import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";
import { ElMessage } from "element-plus";
import { tokenValidator } from "@/utils/token";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    redirect: "/login",
    meta: {
      requiresAuth: true,
    },
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/login/index.vue"),
    meta: {
      title: "登录 - 图书借阅管理系统",
      requiresAuth: false,
      hidden: true,
    },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/register/index.vue"),
    meta: {
      title: "注册 - 图书借阅管理系统",
      requiresAuth: false,
      hidden: true,
    },
  },
  {
    path: "/borrow",
    component: () => import("@/layout/index.vue"),
    redirect: "/borrow/newBooks",
    name: "borrow",
    meta: {
      title: "借阅中心",
      icon: "Reading",
      roles: ["sys:borrow"],
      requiresAuth: true,
    },
    children: [
      {
        path: "newBooks",
        component: () => import("@/views/borrow/NewBooks/Index.vue"),
        name: "newBooks",
        meta: {
          title: "新书推荐",
          icon: "Star",
          roles: ["sys:newBooks"],
          requiresAuth: true,
        },
      },
      {
        path: "bookBorrow",
        component: () =>
        import("@/views/borrow/BookBorrow/Index.vue"),
        name: "bookBorrow",
        meta: {
          title: "图书借阅",
          icon: "Notebook",
          roles: ["sys:bookBorrow"],
          requiresAuth: true,
        },
      },
      {
        path: "BookBorrow/BookCreate",
        component: () => import("@/views/borrow/BookBorrow/BookCreate.vue"),
        name: "bookCreate",
        meta: {
          title: "创建书籍",
          icon: "Plus",
          roles: ["sys:bookBorrow"],
          hidden: true,
          requiresAuth: true,
        },
      },
      {
        path: "BookBorrow/BookEdit/:id",
        component: () => import("@/views/borrow/BookBorrow/BookCreate.vue"),
        name: "bookEdit",
        meta: {
          title: "编辑书籍",
          icon: "Plus",
          roles: ["sys:bookBorrow"],
          hidden: true,
          requiresAuth: true,
        },
      },
      {
        path: "BookBorrow/BookDetail/:id",
        component: () => import("@/views/borrow/BookBorrow/BookDetail.vue"),
        name: "bookDetail",
        meta: {
          title: "书籍详情",
          icon: "Document",
          roles: ["sys:bookBorrow","sys:currentBorrow"],
          hidden: true,
          requiresAuth: true,
        },
      },
      {
        path: "currentBorrow",
        component: () => import("@/views/borrow/CurrentBorrow/index.vue"),
        name: "currentBorrow",
        meta: {
          title: "当前借阅",
          icon: "Collection",
          roles: ["sys:currentBorrow"],
          requiresAuth: true,
        },
      },
      {
        path: "currentReturn",
        component: () => import("@/views/borrow/CurrentReturn/index.vue"),
        name: "currentReturn",
        meta: {
          title: "当前归还",
          icon: "Collection",
          roles: ["sys:currentReturn"],
          requiresAuth: true,
        },
      },
      {
        path: "borrowRecord",
        component: () => import("@/views/borrow/BorrowRecord/index.vue"),
        name: "borrowRecord",
        meta: {
          title: "借阅记录",
          icon: "Document",
          roles: ["sys:borrowRecord"],
          requiresAuth: true,
        },
      },
    ],
  },
  {
    path: "/manage",
    component: () => import("@/layout/index.vue"),
    name: "manage",
    meta: {
      title: "管理中心",
      icon: "Setting",
      roles: ["sys:manage"],
      requiresAuth: true,
    },
    children: [
      {
        path: "personalCenter",
        component: () => import("@/views/manage/PersonalCenter/index.vue"),
        name: "personalCenter",
        meta: {
          title: "个人中心",
          icon: "User",
          roles: ["sys:personal"],
          requiresAuth: true,
        },
      },
      {
        path: "messageList",
        component: () => import("@/views/manage/MessageList/index.vue"),
        name: "messageList",
        meta: {
          title: "消息列表",
          icon: "ChatDotRound",
          roles: ["sys:message"],
          requiresAuth: true,
        },
      },
      {
        path: "userList",
        component: () => import("@/views/manage/UserList/index.vue"),
        name: "userList",
        meta: {
          title: "用户列表",
          icon: "UserFilled",
          roles: ["sys:user"],
          requiresAuth: true,
        },
      },
    ],
  },
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    redirect: "/borrow/newBooks",
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  strict: false,
  // 切换页面，滚动到最顶部
  scrollBehavior: () => ({ left: 0, top: 0 }),
});

/**
 * 验证 token 有效性
 */
const validateToken = async (): Promise<boolean> => {
  const token = localStorage.getItem("token");
  if (!token) {
    return false;
  }

  try {
    return await tokenValidator.validateToken(token);
  } catch (error) {
    console.error("Token 验证失败:", error);
    return false;
  }
};

/**
 * 清除用户认证信息
 */
const clearAuthInfo = async (): Promise<void> => {
  localStorage.removeItem("token");
  localStorage.removeItem("userInfo");

  // 清除 store 中的用户信息
  try {
    const { useUserStore } = await import("@/store");
    const store = useUserStore();
    if (store && typeof store.clearUser === "function") {
      store.clearUser();
    }
  } catch (error) {
    console.error("清除 store 用户信息失败:", error);
  }
};

/**
 * 处理需要认证的路由
 */
const handleAuthRoute = async (to: any, next: any): Promise<void> => {
  const token = localStorage.getItem("token");

  if (!token) {
    // token 不存在，跳转到登录页
    ElMessage.warning("请先登录");
    next({
      path: "/login",
      query: { redirect: to.fullPath },
    });
    return;
  }

  // 验证 token 有效性
  const isValid = await validateToken();
  if (!isValid) {
    ElMessage.warning("登录已过期，请重新登录");
    clearAuthInfo();
    next({
      path: "/login",
      query: { redirect: to.fullPath },
    });
  } else {
    // token 有效，正常访问
    next();
  }
};

/**
 * 处理公开路由（登录/注册页）
 */
const handlePublicRoute = async (to: any, next: any): Promise<void> => {
  const token = localStorage.getItem("token");

  if (!token) {
    // 未登录，允许访问登录/注册页
    next();
    return;
  }

  // 验证 token 有效性
  const isValid = await validateToken();
  if (isValid) {
    // 已登录且 token 有效，跳转到首页
    ElMessage.info("您已登录，将跳转到首页");
    next("/borrow/newBooks");
  } else {
    // token 无效，清除存储并允许访问登录页
    clearAuthInfo();
    next();
  }
};

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title as string;
  }

  try {
    // 检查路由是否需要认证
    if (to.meta.requiresAuth) {
      await handleAuthRoute(to, next);
    } else if (to.path === "/login" || to.path === "/register") {
      await handlePublicRoute(to, next);
    } else {
      // 其他公开页面，正常访问
      next();
    }
  } catch (error) {
    console.error("路由守卫执行错误:", error);
    // 发生错误时跳转到登录页
    ElMessage.error("系统错误，请重新登录");
    clearAuthInfo();
    next("/login");
  }
});

// 路由后置守卫 - 用于页面统计等
router.afterEach((to, from) => {
  // 可以在这里添加页面访问统计等
  console.log(`路由跳转: ${from.path} -> ${to.path}`);

  // 记录页面访问历史（可选）
  const visitHistory = {
    path: to.path,
    name: to.name,
    timestamp: new Date().toISOString(),
    from: from.path,
  };

  // 保存最近5次访问记录
  try {
    const history = JSON.parse(localStorage.getItem("visitHistory") || "[]");
    history.unshift(visitHistory);
    if (history.length > 5) {
      history.pop();
    }
    localStorage.setItem("visitHistory", JSON.stringify(history));
  } catch (error) {
    console.error("保存访问历史失败:", error);
  }
});

// 导出路由实例
export default router;

// 导出一些工具函数供其他模块使用
export { validateToken, clearAuthInfo };

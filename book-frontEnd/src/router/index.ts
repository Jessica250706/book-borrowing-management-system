import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/index.vue'),
        meta: {
            title: '登录 - 图书借阅管理系统',
            requiresAuth: false,
            hidden: true
        }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/register/index.vue'),
        meta: {
            title: '注册 - 图书借阅管理系统',
            requiresAuth: false,
            hidden: true
        }
    },
    {
        path: '/',
        redirect: '/login',
    },
    {
        path: "/borrow",
        component: () => import('@/layout/index.vue'),
        redirect: '/borrow/newBooks',
        name: "borrow",
        meta: {
            title: "借阅中心",
            icon: "Reading",
            roles: ["sys:borrow"],
            requiresAuth: true
        },
        children: [
            {
                path: "newBooks",
                component: () => import('@/views/borrow/NewBooks/ReaderNewBooks.vue'),
                name: "newBooks",
                meta: {
                    title: "新书推荐",
                    icon: "Star",
                    roles: ["sys:newBooks"],
                    requiresAuth: true
                },
            },
            {
                path: "bookBorrow",
                component: () => import('@/views/borrow/BookBorrow/AdminBookBorrow.vue'),
                name: "bookBorrow",
                meta: {
                    title: "图书借阅",
                    icon: "Notebook",
                    roles: ["sys:bookBorrow"],
                    requiresAuth: true
                },
            },
            {
                path: "BookBorrow/BookCreate",
                component: () => import('@/views/borrow/BookBorrow/BookCreate.vue'),
                name: "bookCreate",
                meta: {
                    title: "创建书籍",
                    icon: "Plus",
                    roles: ["sys:bookBorrow"],
                    hidden: true,
                    requiresAuth: true
                },
            },
            {
                path: "BookBorrow/BookDetail",
                component: () => import('@/views/borrow/BookBorrow/BookDetail.vue'),
                name: "bookDetail",
                meta: {
                    title: "书籍详情",
                    icon: "Document",
                    roles: ["sys:bookBorrow"],
                    hidden: true,
                    requiresAuth: true
                },
            },
            {
                path: "currentBorrow",
                component: () => import('@/views/borrow/CurrentBorrow/index.vue'),
                name: "currentBorrow",
                meta: {
                    title: "当前借阅",
                    icon: "Collection",
                    roles: ["sys:currentBorrow"],
                    requiresAuth: true
                },
            },
            {
                path: "borrowRecord",
                component: () => import('@/views/borrow/BorrowRecord/index.vue'),
                name: "borrowRecord",
                meta: {
                    title: "借阅记录",
                    icon: "Document",
                    roles: ["sys:borrowRecord"],
                    requiresAuth: true
                },
            },
        ]
    },
    {
        path: "/manage",
        component: () => import('@/layout/index.vue'),
        name: "manage",
        meta: {
            title: "管理中心",
            icon: "Setting",
            roles: ["sys:manage"],
            requiresAuth: true
        },
        children: [
            {
                path: "personalCenter",
                component: () => import('@/views/manage/PersonalCenter/index.vue'),
                name: "personalCenter",
                meta: {
                    title: "个人中心",
                    icon: "User",
                    roles: ["sys:personal"],
                    requiresAuth: true
                },
            },
            {
                path: "messageList",
                component: () => import('@/views/manage/MessageList/index.vue'),
                name: "messageList",
                meta: {
                    title: "消息列表",
                    icon: "ChatDotRound",
                    roles: ["sys:message"],
                    requiresAuth: true
                },
            },
            {
                path: "userList",
                component: () => import('@/views/manage/UserList/index.vue'),
                name: "userList",
                meta: {
                    title: "用户列表",
                    icon: "UserFilled",
                    roles: ["sys:user"],
                    requiresAuth: true
                },
            },
        ],
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        redirect: '/borrow'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
    strict: false,
    // 切换页面，滚动到最顶部
    scrollBehavior: () => ({ left: 0, top: 0 }),
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')

    // 设置页面标题
    if (to.meta.title) {
        document.title = to.meta.title as string
    }

    // 检查路由是否需要认证
    if (to.meta.requiresAuth) {
        if (!token) {
            // 需要登录但未登录，跳转到登录页
            next({
                path: '/login',
                query: { redirect: to.fullPath } // 保存目标路径，登录后可以跳转回来
            })
        } else {
            // 已登录，正常访问
            next()
        }
    } else if ((to.path === '/login' || to.path === '/register') && token) {
        // 已登录但访问登录/注册页，跳转到首页
        next('/')
    } else {
        // 公开页面，正常访问
        next()
    }
})

// 路由后置守卫 - 用于页面统计等
router.afterEach((to, from) => {
    // 可以在这里添加页面访问统计等
    console.log(`路由跳转: ${from.path} -> ${to.path}`)
})

export default router
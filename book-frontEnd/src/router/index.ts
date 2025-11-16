import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        redirect: '/borrow', // 重定向到借阅中心
    },
    {
        path: "/borrow",
        component: () => import('@/layout/index.vue'),
        redirect: '/borrow/newBooks', // 借阅中心重定向到新书推荐
        name: "borrow",
        meta: {
            title: "借阅中心",
            icon: "Reading",
            roles: ["sys:borrow"]
        },
        children: [
            {
                path: "newBooks", // 相对路径
                component: () => import('@/views/borrow/NewBooks/ReaderNewBooks.vue'),
                name: "newBooks",
                meta: {
                    title: "新书推荐",
                    icon: "Star",
                    roles: ["sys:newBooks"]
                },
            },
            {
                path: "bookBorrow",
                component: () => import('@/views/borrow/BookBorrow/ReaderBookBorrow.vue'),
                name: "bookBorrow",
                meta: {
                    title: "图书借阅",
                    icon: "Notebook",
                    roles: ["sys:bookBorrow"]
                },
            },
            {
                path: "currentBorrow",
                component: () => import('@/views/borrow/CurrentBorrow/index.vue'),
                name: "currentBorrow",
                meta: {
                    title: "当前借阅",
                    icon: "Collection",
                    roles: ["sys:currentBorrow"]
                },
            },
            {
                path: "borrowRecord",
                component: () => import('@/views/borrow/BorrowRecord/index.vue'),
                name: "borrowRecord",
                meta: {
                    title: "借阅记录",
                    icon: "Document",
                    roles: ["sys:borrowRecord"]
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
                },
            },
        ],
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
    strict: false,
    // 切换页面，滚动到最顶部
    scrollBehavior: () => ({ left: 0, top: 0 }),
})

export default router
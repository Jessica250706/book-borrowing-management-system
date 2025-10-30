import { createRouter,createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";
// import Layout from "@/components/HelloWorld.vue";

const routes: Array<RouteRecordRaw> = [
    {
        path: '/home',
        name: 'Home',
        component: () => import('@/layout/index.vue'),
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
    strict: false,
    // 切换页面，滚动到最顶部
    scrollBehavior:() => ({left: 0, top: 0}),
})

export default router
<template>
    <el-breadcrumb class="bred" :separator-icon="ArrowRight">
        <el-breadcrumb-item 
            v-for="(item, index) in breadcrumbItems" 
            :key="index"
        >
            <span 
                v-if="!item.path || item.noLink" 
                class="breadcrumb-text"
            >
                {{ item.title }}
            </span>
            <router-link 
                v-else 
                :to="item.path" 
                class="breadcrumb-link"
            >
                {{ item.title }}
            </router-link>
        </el-breadcrumb-item>
    </el-breadcrumb>
</template>

<script setup lang="ts">
    import { computed } from "vue";
    import { useRoute } from "vue-router";
    import type { RouteLocationMatched } from "vue-router";
    import { ArrowRight } from '@element-plus/icons-vue'
    
    const route = useRoute();

    // 计算面包屑项
    const breadcrumbItems = computed(() => {
        const matched = route.matched.filter((item) => item.meta && item.meta.title);
        
        // 如果是创建书籍页面，手动构建面包屑
        if (route.name === 'bookCreate') {
            return [
                { 
                    title: '借阅中心', 
                    path: '',  
                    noLink: true
                },
                { 
                    title: '图书借阅', 
                    path: '/borrow/bookBorrow',
                    noLink: false
                },
                { 
                    title: '创建书籍', 
                    path: '',
                    noLink: true  // 当前页面，不可点击
                }
            ];
        }
        
        // 其他页面构建面包屑
        return matched.map((item, index) => {
            const isLast = index === matched.length - 1;
            const isBorrowOrManage = item.meta?.title === '借阅中心' || item.meta?.title === '管理中心';
            
            return {
                title: item.meta.title as string,
                path: isLast || isBorrowOrManage ? '' : item.path, // 最后一项和借阅中心/管理中心不可点击
                noLink: isLast || isBorrowOrManage
            };
        });
    });
</script>

<style scoped lang="scss">
    :deep(.el-breadcrumb__inner) {
        color: #757575 !important;
        font-weight: normal !important; 
    }
    
    // 箭头颜色
    :deep(.el-breadcrumb__separator) {
        color: #999999 !important;
    }
    
    // 当前子目录（最后一项）字体颜色
    :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
        color: #619cff !important;
        font-weight: normal !important; 
    }
    
    .breadcrumb-link {
        color: inherit !important; 
        text-decoration: none;
        font-weight: normal !important;
        
        &:hover {
            color: #409EFF !important;
            font-weight: normal !important;
        }
    }
    
    .breadcrumb-text {
        color: inherit !important;
        cursor: default;
        font-weight: normal !important;
    }
    
    .bred {
        margin-left: 20px;
    }
</style>
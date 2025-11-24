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
    import { ArrowRight } from '@element-plus/icons-vue'
    
    const route = useRoute();

    // 页面名称映射
    const pageNameMap: { [key: string]: string } = {
        newBooks: '新书推荐',
        bookBorrow: '图书借阅',
        bookManage: '书籍管理'
    }

    // 页面路由映射
    const pageRouteMap: { [key: string]: string } = {
        newBooks: '/borrow/newBooks',
        bookBorrow: '/borrow/bookBorrow',
        bookManage: '/borrow/bookManage'
    }

    // 计算面包屑项
    const breadcrumbItems = computed(() => {
        const matched = route.matched.filter((item) => item.meta && item.meta.title);
        
        // 如果是创建书籍页面或编辑书籍页面
        if (route.name === 'bookCreate') {
            const isEditMode = route.query.edit === 'true';
            const fromPage = route.query.from as string;
            
            // 根据来源页面动态显示
            const fromPageTitle = pageNameMap[fromPage] || '图书借阅';
            const fromPagePath = pageRouteMap[fromPage] || '/borrow/bookBorrow';
            
            return [
                { 
                    title: '借阅中心', 
                    path: '',  
                    noLink: true
                },
                { 
                    title: fromPageTitle, 
                    path: fromPagePath,
                    noLink: false
                },
                { 
                    title: isEditMode ? '编辑书籍' : '创建书籍',
                    path: '',
                    noLink: true
                }
            ];
        }
        
        // 如果是书籍详情页面
        if (route.name === 'bookDetail') {
            const fromPage = route.query.from as string;
            const fromPageTitle = pageNameMap[fromPage] || '图书借阅';
            const fromPagePath = pageRouteMap[fromPage] || '/borrow/bookBorrow';
            
            return [
                { 
                    title: '借阅中心', 
                    path: '',  
                    noLink: true
                },
                { 
                    title: fromPageTitle, 
                    path: fromPagePath,
                    noLink: false
                },
                { 
                    title: '书籍详情', 
                    path: '',
                    noLink: true
                }
            ];
        }
        
        // 其他页面构建面包屑
        return matched.map((item, index) => {
            const isLast = index === matched.length - 1;
            const isBorrowOrManage = item.meta?.title === '借阅中心' || item.meta?.title === '管理中心';
            
            return {
                title: item.meta.title as string,
                path: isLast || isBorrowOrManage ? '' : item.path,
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
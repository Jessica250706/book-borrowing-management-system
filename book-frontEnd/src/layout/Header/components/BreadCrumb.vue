<template>
    <el-breadcrumb class="bred" :separator-icon="ArrowRight">
        <el-breadcrumb-item v-for="item in tabs">{{ item.meta.title }}</el-breadcrumb-item>
    </el-breadcrumb>
</template>

<script setup lang="ts">
    import { ref, watch } from "vue";
    import type { Ref } from "vue";
    import { useRoute } from "vue-router";
    import type { RouteLocationMatched } from "vue-router";
    import { ArrowRight } from '@element-plus/icons-vue'
    
    const tabs: Ref<RouteLocationMatched[]> = ref([]);
    const route = useRoute();

    const getBredcrumb = () => {
        //从路由里面获取所有有meta和title
        let mached = route.matched.filter((item) => item.meta && item.meta.title);
        
        // 匹配的路由
        tabs.value = mached;
    };
    
    getBredcrumb();
    watch(
        () => route.path,
        () => getBredcrumb()
    );
</script>

<style scoped lang="scss">
    :deep(.el-breadcrumb__inner) {
        color: #fff !important;
    }
    :deep(.el-breadcrumb__separator) {
        color: #fff !important;
    }
    .bred {
        margin-left: 20px;
    }
</style>
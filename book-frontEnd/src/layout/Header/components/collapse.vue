<script setup lang="ts">
    import { computed } from "vue";
    import { Fold, Expand } from "@element-plus/icons-vue";
    import { useMenuStore } from "@/store/modules/menu";

    // 获取store
    const store = useMenuStore();
    // 获取collapse状态
    const status = computed(() => {
    return !store.isCollapse; // 直接访问 isCollapse 状态
    });
    // 改变collapse状态
    const setCollapse = () => {
    store.setCollapse(); // 调用 store 中的 setCollapse 方法
    }
</script>

<template>
    <el-icon class="icons" @click="setCollapse">
        <component :is="status ? Fold : Expand" />
    </el-icon>
</template>

<style lang="scss" scoped>
    .icons {
    color: #757575;
    font-size: 24px;
    }
</style>
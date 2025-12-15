<template>
  <el-input
    v-model="searchValue"
    placeholder="请输入书籍名称"
    @input="handleInput"
  />
</template>

<script setup lang="ts">
  //import { defineEmits } from 'vue';
import { ref } from 'vue';

const searchValue = ref('');
const emits = defineEmits(['search']);

const handleInput = (val: string) => {
  emits('search', val);
};
</script>

<style scoped>
/* 可根据项目样式需求调整 */
</style>
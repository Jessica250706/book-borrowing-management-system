<template>
  <el-select
    v-model="statusValue"
    placeholder="书籍状态"
    @change="handleChange"
    filterable
    allow-create
  >
    <el-option
      v-for="item in statusOptions"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    />
  </el-select>
</template>

<script setup lang="ts">
import { ref, defineEmits } from 'vue';

const statusOptions = [
  { label: '所有', value: '' },
  { label: '待上架', value: '待上架' },
  { label: '可借阅', value: '可借阅' },
  { label: '已借光', value: '已借光' },
  { label: '待发布', value: '待发布' }
];

const statusValue = ref('');
const emits = defineEmits(['change']);

const handleChange = (val: string) => {
  emits('change', val);
};
</script>

<style scoped>
/* 可根据项目样式需求调整 */
</style>
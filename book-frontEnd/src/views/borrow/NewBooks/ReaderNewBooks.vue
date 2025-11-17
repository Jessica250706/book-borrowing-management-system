<template>
  <div class="demo-container">
    <h2>状态筛选框演示</h2>
    
    <div class="search-section">
      <BookStatusSelect 
        :options="statusOptions"
        placeholder="所有状态"
        v-model="selectedStatus"
        @change="handleStatusChange"
        style="width: 200px" 
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import BookStatusSelect from '@/components/BookScreen/BookStatusSelect.vue';

// 状态选项
const statusOptions = [
  { label: '所有状态', value: '' },
  { label: '未发布', value: '未发布' },
  { label: '待上架', value: '待上架' },
  { label: '可借阅', value: '可借阅' },
  { label: '已借光', value: '已借光' }
]

const selectedStatus = ref('')

const handleStatusChange = (val: string) => {
  console.log('所有状态:', val)
}
</script>

<style scoped>
.demo-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h2 {
  margin-bottom: 30px;
  color: #303133;
}

.search-section {
  display: flex;
  align-items: center;
  gap: 25px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
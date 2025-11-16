<template>
  <div class="demo-container">
    <h2>书籍状态对话框演示</h2>
    
    <div class="demo-buttons">
      <el-button type="primary" @click="showReserveDialog1">
        待上架状态
      </el-button>
      
      <el-button type="success" @click="showReserveDialog2">
        已借光状态
      </el-button>
      
      <el-button type="warning" @click="showBorrowDialog">
        可借阅状态
      </el-button>
      
      <el-button type="danger" @click="showCancelReserveDialog">
        可预约状态
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ElMessage } from 'element-plus'
  import { showConfirmDialog, confirm } from '@/components/Dialog/customDialog/CustomDialog.vue'

  // 1.1 待上架状态
  const showReserveDialog1 = async () => {
    const result = await showConfirmDialog({
      title: '预约',
      message: '是否预约书籍？若预约成功，则书籍上架时会发送消息提醒。',
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        // 执行预约逻辑
        ElMessage.success('预约成功！书籍上架时会通知您')
      }
    })
  }

  // 1.2 已售完状态
  const showReserveDialog2 = async () => {
    const result = await showConfirmDialog({
      title: '预约',
      message: '是否预约书籍？若预约成功，则书籍有库存时会发送消息提醒。',
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        // 执行预约逻辑
        ElMessage.success('预约成功！书籍有库存时会通知您')
      }
    })
  }

  // 2. 可借阅状态
  const showBorrowDialog = async () => {
    const borrowDays = 30 // 可借阅天数
    const result = await showConfirmDialog({
      title: '借阅',
      message: `是否借阅书籍？书籍可借阅天数为${borrowDays}天。`,
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        // 执行借阅逻辑
        ElMessage.success(`借阅成功！请在${borrowDays}天内归还`)
      }
    })
  }

  // 3. 可预约状态（取消预约）
  const showCancelReserveDialog = async () => {
    const result = await showConfirmDialog({
      title: '取消预约',
      message: '是否取消预约书籍？',
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        // 执行取消预约逻辑
        ElMessage.success('取消预约成功！')
      }
    })
  }
</script>

<style scoped>
  .demo-container {
    padding: 40px;
    text-align: center;
  }

  .demo-buttons {
    margin-top: 30px;
    display: flex;
    gap: 15px;
    justify-content: center;
    flex-wrap: wrap;
  }
</style>
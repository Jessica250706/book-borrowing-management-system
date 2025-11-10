<template>
  <div>
    <button @click="myDialogRef?.openDialog()">打开对话框</button>
    
    <MyDialog 
      ref="myDialogRef"
      title="用户信息对话框"
      width="500px"
      :data="dialogData"
    >
      <!-- 自定义内容 -->
      <template #default="{ data }">
        <div>
          <p>对话框内容区域</p>
          <p>传递的数据：{{ data }}</p>
        </div>
      </template>
      
      <!-- 自定义底部 -->
      <template #footer>
        <el-button @click="myDialogRef?.closeDialog()">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
      </template>
    </MyDialog>
  </div>
</template>


<script setup lang="ts">
  import { ref } from 'vue'
  import MyDialog from '@/components/Dialog/mydialog/MyDialog.vue'

  const myDialogRef = ref<{
    openDialog: () => void
    closeDialog: () => void
  }>()

  const dialogData = {
    id: 1,
    type: 'user'
  }

  const openDialog = () => {
    myDialogRef.value?.openDialog()
  }

  const handleConfirm = () => {
    console.log('确认操作')
    myDialogRef.value?.closeDialog()
  }
</script>

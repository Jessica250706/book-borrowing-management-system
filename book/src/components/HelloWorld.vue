<script setup lang="ts">
  import {GlobalStore} from '@/store'
  const store = GlobalStore()
  function showUserInfoId() {
    console.log(store.userInfo.id)
  }
</script>

<template>
  <div>Hello World</div>
  <el-button 
    @click="showUserInfoId"  
    type="primary"
  >
    Primary Button
  </el-button>
</template>

<style lang="scss" scoped>

</style>
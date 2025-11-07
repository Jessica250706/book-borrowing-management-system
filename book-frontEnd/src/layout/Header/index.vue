<script setup lang="ts">
import Collapse from './components/collapse.vue';
import BreadCrumb from './components/BreadCrumb.vue';
import UserMenu from './components/UserMenu.vue'; // 新增导入
</script>

<template>
  <div class="header-container">
    <div class="header-left">
      <Collapse />
      <BreadCrumb />
    </div>
    <UserMenu />
  </div>
</template>

<style scoped lang="scss">
.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  
  .header-left {
    display: flex;
    align-items: center;
  }
}
</style>
<script lang="ts" setup>
    import CustomHeader from './Header/index.vue'
    import CustomMenu from './Menu/index.vue'
</script>

<template>
  <div class="full-screen">
    <el-container class="layout">
      <el-aside width="auto" class="aside">
        <custom-menu />
      </el-aside>
      <el-container>
        <el-header class="header">
          <custom-header />
        </el-header>
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="scss">
.full-screen {
  height: 100vh; /* 视口高度 */
  width: 100vw; /* 视口宽度 */
}
.layout {
  height: 100%;
  .aside {
      height: 100%;
      background-color: #fff;
  }
  .header {
      background-color: #fff;
      display: flex;
      align-items: center;
  }
  .main {
      background-color: #f5f5f5;
      padding: 20px;
  }
}
</style>
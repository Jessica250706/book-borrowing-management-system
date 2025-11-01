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
        background-color: rgb(162, 139, 182);
    }
    .header {
        background-color: rgb(225, 170, 132);
        display: flex;
        align-items: center;
    }
    .main {
        background-color: rgb(153, 198, 198);
    }
    }
</style>
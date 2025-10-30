<template>
    <el-container class="layout">
        <el-aside width="200px" class="aside">Aside</el-aside>
        <el-container>
            <el-header class="header">Header</el-header>
            <el-main class="main">Main</el-main>
        </el-container>
    </el-container>
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
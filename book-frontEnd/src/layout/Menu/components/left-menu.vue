<script setup lang="ts">
    defineProps(["menuList"]);
</script>

<template>
  <template
    v-for="menu in menuList"
    :key="menu.path"
  >
    <el-sub-menu
      v-if="menu.children && menu.children.length > 0" 
      :index="menu.path"
    >
        <template #title>
          <el-icon>
              <component :is="menu.meta.icon"></component>
          </el-icon>
          <span>{{ menu.meta.title }}</span>
        </template>
        <left-menu :menuList="menu.children"></left-menu>
    </el-sub-menu>
    <el-menu-item
      v-else
      style="color: #f4f4f5" 
      :index="menu.path"
    >
      <el-icon>
        <component :is="menu.meta.icon"></component>
      </el-icon>
      <template #title>{{ menu.meta.title }}</template>
    </el-menu-item>
  </template>
</template>
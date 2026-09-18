<!-- src\views\borrow\BookBorrow\Index.vue -->
<template>
  <!-- 管理员界面 -->
  <AdminBookBorrow v-if="userStore.isAdmin" />

  <!-- 读者界面 -->
  <ReaderBookBorrow v-else />
</template>

<script setup lang="ts">
import { useUserStore } from "@/store/modules/user";
import AdminBookBorrow from "./AdminBookBorrow.vue";
import ReaderBookBorrow from "./ReaderBookBorrow.vue";

const userStore = useUserStore();
</script>

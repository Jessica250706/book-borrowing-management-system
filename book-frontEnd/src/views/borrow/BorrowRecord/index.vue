<!-- src\views\borrow\BorrowRecord\Index.vue -->
<template>
  <!-- 管理员界面 -->
  <AdminBorrowRecord v-if="userStore.isAdmin" />

  <!-- 读者界面 -->
  <ReaderBorrowRecord v-else />
</template>

<script setup lang="ts">
import { useUserStore } from "@/store/modules/user";
import AdminBorrowRecord from "./AdminBorrowRecord.vue";
import ReaderBorrowRecord from "./ReaderBorrowRecord.vue";

const userStore = useUserStore();
</script>

<!-- src\views\borrow\NewBooks\Index.vue -->
<template>
  <!-- 管理员界面 -->
  <AdminNewBooks v-if="userStore.isAdmin" />

  <!-- 读者界面 -->
  <ReaderNewBooks v-else />
</template>

<script setup lang="ts">
import { useUserStore } from "@/store/modules/user";
import AdminNewBooks from "./AdminNewBooks.vue";
import ReaderNewBooks from "./ReaderNewBooks.vue";

const userStore = useUserStore();
</script>

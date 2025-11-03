<template>
  <div>
    <el-button type="primary" size="default" @click="addBtn">新增</el-button>
    <sys-dialog
      :title="dialog.title"
      :visible="dialog.visible"
      :width="dialog.width"
      :height="dialog.height"
      @onClose="onClose"
      @onConfirm="onConfirm"
    >
      <template v-slot:content>
        <div>我是弹框</div>
      </template>
    </sys-dialog>
  </div>
</template>

<script setup lang="ts">
  import SysDialog from "@/components/Dialog/sys-dialog/SysDialog.vue";
  import useDialog from "@/hooks/useDialog";
  //弹框属性
  const {dialog,onClose,onConfirm} = useDialog()
  const addBtn = () => {
    dialog.visible = true;
  };
</script>

<style scoped></style>
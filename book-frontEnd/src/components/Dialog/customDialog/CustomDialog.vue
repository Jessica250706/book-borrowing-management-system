<template>
  <div></div>
</template>

<script lang="ts">
import { ElMessageBox } from "element-plus";

// 对话框配置接口
interface DialogOptions {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  width?: number;
  dangerouslyUseHTMLString?: boolean; // 新增：是否将 message 作为 HTML 处理
  onConfirm?: () => void | Promise<void>;
  onCancel?: () => void;
}

// 导出对话框方法
export const showConfirmDialog = async (
  options: DialogOptions
): Promise<boolean> => {
  try {
    await ElMessageBox({
      title: options.title,
      message: options.message,
      confirmButtonText: options.confirmText || "确认",
      cancelButtonText: options.cancelText || "取消",
      customClass: "custom-dialog",
      showCancelButton: true,
      type: "",
      closeOnClickModal: true,
      closeOnPressEscape: false,
      dangerouslyUseHTMLString: options.dangerouslyUseHTMLString || false,
      beforeClose: async (action, instance, done) => {
        if (action === "confirm") {
          if (options.onConfirm) {
            try {
              await options.onConfirm();
              done();
            } catch (error) {
              // 错误处理由调用方处理
              done();
            }
          } else {
            done();
          }
        } else {
          if (options.onCancel) {
            options.onCancel();
          }
          done();
        }
      },
    });
    return true;
  } catch (error) {
    if (error === "cancel") {
      return false;
    }
    throw error;
  }
};

// 确认对话框 - 修改参数结构
export const confirm = (
  title: string,
  message: string,
  options?: {
    dangerouslyUseHTMLString?: boolean;
    confirmText?: string;
    cancelText?: string;
    width?: number;
    onConfirm?: () => void | Promise<void>;
    onCancel?: () => void;
  }
): Promise<boolean> => {
  // 将 options 中的所有属性都传递给 showConfirmDialog
  return showConfirmDialog({
    title,
    message,
    confirmText: options?.confirmText,
    cancelText: options?.cancelText,
    width: options?.width,
    dangerouslyUseHTMLString: options?.dangerouslyUseHTMLString,
    onConfirm: options?.onConfirm,
    onCancel: options?.onCancel,
  });
};

// 也可以创建一个专门的 HTML 确认函数
export const confirmHTML = (
  title: string,
  message: string,
  options?: {
    confirmText?: string;
    cancelText?: string;
    width?: number;
    onConfirm?: () => void | Promise<void>;
    onCancel?: () => void;
  }
): Promise<boolean> => {
  return showConfirmDialog({
    title,
    message,
    dangerouslyUseHTMLString: true, // 总是启用 HTML
    confirmText: options?.confirmText,
    cancelText: options?.cancelText,
    width: options?.width,
    onConfirm: options?.onConfirm,
    onCancel: options?.onCancel,
  });
};
</script>

<style>
/* 样式保持不变 */
.custom-dialog {
  width: 600px !important;
  max-width: 90vw !important;
  height: 250px !important;
}

.custom-dialog .el-message-box__header {
  padding: 8px 15px 10px !important;
  border-bottom: 1px solid #e8e8e8 !important;
}

.custom-dialog .el-message-box__title {
  font-size: 18px !important;
  font-weight: 600 !important;
  color: #333 !important;
}

.custom-dialog .el-message-box__content {
  padding: 30px 20px !important;
  min-height: 50px !important;
}

.custom-dialog .el-message-box__container {
  display: flex !important;
  align-items: flex-start !important;
}

.custom-dialog .el-message-box__status {
  display: none !important;
}

.custom-dialog .el-message-box__message {
  padding: 0 !important;
  padding-left: 20px !important;
  font-size: 16px !important;
  line-height: 1.6 !important;
  color: #666 !important;
  text-align: left !important;
}

.custom-dialog .el-message-box__btns {
  padding: 15px 20px 20px !important;
  margin-top: 10px !important;
  text-align: right !important;
  display: flex !important;
  justify-content: flex-end !important;
  gap: 10px !important;
}

.custom-dialog .el-button {
  padding: 10px 15px !important;
  font-size: 14px !important;
}

.custom-dialog .el-button--primary {
  background-color: #1890ff !important;
  border-color: #1890ff !important;
}

.custom-dialog .el-button--primary:focus {
  outline: none !important;
  border-color: #1890ff !important;
}

.custom-dialog .el-message-box__headerbtn {
  top: 15px !important;
  right: 20px !important;
  font-size: 18px !important;
}
</style>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useMenuStore } from "@/store/modules/menu";
import LeftMenu from "./components/left-menu.vue";
import MenuLogo from "@/assets/logo.png";
import { getCurrentUserMenus } from "@/apis/menu/index";

const route = useRoute();
const router = useRouter();
const colstore = useMenuStore();
const isCollapse = computed(() => {
  return !colstore.isCollapse;
});

const goHome = () => {
  router.push("/");
};

// 将menuList改为ref，以便动态更新
const menuList = ref<any[]>([]);
const loading = ref(false);

// 转换后端数据为前端需要的格式
const transformMenuData = (backendData: any[]) => {
  return backendData.map((item) => {
    const menuItem: any = {
      path: item.path,
      component: item.url === "layout/index.vue" ? "Layout" : item.url,
      name: item.name,
      meta: {
        title: item.title,
        icon: item.icon,
        code: item.code, // 保留权限code，如果需要的话
      },
      // 保留原始数据，以防需要其他字段
      rawData: item,
    };

    // 如果有子菜单，递归处理
    if (item.children && item.children.length > 0) {
      menuItem.children = transformMenuData(item.children);
    }

    return menuItem;
  });
};

// 获取菜单数据
const fetchMenuData = async () => {
  try {
    loading.value = true;
    const response = await getCurrentUserMenus();

    if (response.code === 200 && response.data) {
      // 转换后端数据为前端需要的格式
      const transformedMenus = transformMenuData(response.data);
      menuList.value = transformedMenus;
    } else {
      console.error("获取菜单数据失败:", response.message);
      // 可以设置默认菜单或显示错误信息
    }
  } catch (error) {
    console.error("获取菜单数据异常:", error);
    // 可以设置默认菜单或显示错误信息
  } finally {
    loading.value = false;
  }
};

// 在组件挂载时获取菜单数据
onMounted(() => {
  fetchMenuData();
});

// 如果需要监听用户权限变化重新获取菜单，可以添加以下代码
// import { storeToRefs } from 'pinia';
// import { useUserStore } from '@/store/modules/user';
// const userStore = useUserStore();
// const { userInfo } = storeToRefs(userStore);
//
// watch(userInfo, (newVal) => {
//   if (newVal) {
//     fetchMenuData();
//   }
// }, { immediate: true });

//获取激活的菜单
const activeIndex = computed(() => {
  const { path } = route;
  return path;
});

// 计算当前页面所属的父级模块路径
const currentParentPath = computed(() => {
  const path = route.path;
  const segments = path.split("/");
  if (segments.length >= 2 && segments[1]) {
    return "/" + segments[1];
  }
  return "";
});
</script>

<template>
  <el-menu
    :default-active="activeIndex"
    class="el-menu-vertical-demo"
    :collapse="isCollapse"
    router
  >
    <div class="logo" @click="goHome">
      <!-- 展开状态：显示图片和文字 -->
      <div v-if="!isCollapse" class="logo-expand">
        <img :src="MenuLogo" alt="logo" />
        <img  class="title"  src="@/assets/title.png" alt="title" />
      </div>
      <!-- 折叠状态：只显示图片 -->
      <div v-else class="logo-collapse">
        <img :src="MenuLogo" alt="logo" />
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="menu-loading">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- 正常显示菜单 -->
    <left-menu
      v-else
      :menuList="menuList"
      :currentParentPath="currentParentPath"
    />
  </el-menu>
</template>

<style lang="scss" scoped>
.logo {
  display: flex;
  width: 100%;
  height: 80px;
  line-height: 60px;
  text-align: center;
  cursor: pointer;
  align-items: center;
  justify-content: center;
  background-color: #ffffff;

  .logo-expand {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    width: 100%;
    padding: 0 28px;

    img {
      width: 45px;
      height: 45px;
      margin-right: 10px;
    }

    .title{
      width: 70px;
      height: 36px;
      margin-right: 0;
      margin-top: 2px;
    }


  }

  .logo-collapse {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;

    img {
      width: 32px;
      height: 32px;
    }
  }
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 180px;
  min-height: 400px;
}

.el-menu {
  height: 100%;
  border-right: 1px solid #dddfe6;
  background-color: #ffffff;
}

.menu-loading {
  padding: 20px;
}

// 默认菜单项样式
:deep(.el-sub-menu__title),
:deep(.el-menu-item) {
  color: #757575 !important; // 文字颜色

  .el-icon {
    color: #757575 !important; // 图标颜色
  }
}

// 当前选中的页面
:deep(.el-menu-item.is-active) {
  color: #639eff !important; // 选中项文字颜色
  background-color: transparent !important;
}

// 当前页面所属模块的高亮样式
:deep(.is-current-parent) {
  > .el-sub-menu__title {
    background-color: #ddecff !important;
    color: #619cff !important;
    border-right: 3px solid #619cff !important;

    .el-icon {
      color: #619cff !important;
    }
  }
}

// 鼠标悬停效果
:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #f5f5f5 !important; // 悬停背景色
}
</style>

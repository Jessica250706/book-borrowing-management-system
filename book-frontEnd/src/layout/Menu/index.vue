<script setup lang="ts">
  import { reactive, computed } from "vue"
  import { useRoute, useRouter } from 'vue-router'
  import { useMenuStore } from "@/store/modules/menu"
  import LeftMenu from "./components/left-menu.vue"
  import MenuLogo from '@/assets/logo.jpg'

  const route = useRoute()
  const router = useRouter()
  const colstore = useMenuStore();
  const isCollapse = computed(() => {
    return !colstore.isCollapse;
  });

  const goHome = () => {
    router.push('/')
  }

  const menuList = reactive([
    {
      path: "/borrow",
      component: "Layout",
      name: "borrow",
      meta: {
        title: "借阅中心",
        icon: "Reading",
        roles: ["sys:borrow"],
      },
      children: [
        {
          path: "/borrow/newBooks", 
          component: "/borrow/NewBooks",
          name: "newBooks",
          meta: {
            title: "新书推荐",
            icon: "Star",
            roles: ["sys:newBooks"],
          },
        },
        {
          path: "/borrow/bookBorrow", 
          component: "/borrow/BookBorrow",
          name: "bookBorrow",
          meta: {
            title: "图书借阅",
            icon: "Notebook",
            roles: ["sys:bookBorrow"],
          },
        },
        {
          path: "/borrow/currentBorrow", 
          component: "/borrow/CurrentBorrow",
          name: "currentBorrow",
          meta: {
            title: "当前借阅",
            icon: "Collection",
            roles: ["sys:currentBorrow"],
          },
        },
        {
          path: "/borrow/borrowRecord", 
          component: "/borrow/BorrowRecord",
          name: "borrowRecord",
          meta: {
            title: "借阅记录",
            icon: "Document",
            roles: ["sys:borrowRecord"],
          },
        },
      ],
    },
    {
      path: "/manage",
      component: "Layout",
      name: "manage",
      meta: {
        title: "管理中心",
        icon: "Setting",
        roles: ["sys:manage"],
      },
      children: [
        {
          path: "/manage/personalCenter",
          component: "/manage/PersonalCenter",
          name: "personalCenter",
          meta: {
            title: "个人中心",
            icon: "User",
            roles: ["sys:personal"],
          },
        },
        {
          path: "/manage/messageList", 
          component: "/manage/MessageList",
          name: "messageList",
          meta: {
            title: "消息列表",
            icon: "ChatDotRound",
            roles: ["sys:message"],
          },
        },
        {
          path: "/manage/userList",
          component: "/manage/UserList",
          name: "userList",
          meta: {
            title: "用户列表",
            icon: "UserFilled",
            roles: ["sys:user"],
          },
        },
      ],
    },
  ])

  //获取激活的菜单
  const activeIndex = computed(()=>{
    const {path} = route;
    return path;
  })

  // 计算当前页面所属的父级模块路径
  const currentParentPath = computed(() => {
    const path = route.path;
    const segments = path.split('/');
    if (segments.length >= 2 && segments[1]) {
      return '/' + segments[1];
    }
    return '';
  })
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
        <span class="logo-title">
          <div>图书借阅</div>
          <div>管理系统</div>
        </span>
      </div>
      <!-- 折叠状态：只显示图片 -->
      <div v-else class="logo-collapse">
        <img :src="MenuLogo" alt="logo" />
      </div>
    </div>
    <left-menu 
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
        width: 36px;
        height: 36px;
        margin-right: 12px;
      }

      .logo-title {
        color: #0e518b;
        font-weight: 800;
        font-size: 18px;
        line-height: normal;
        white-space: nowrap;

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
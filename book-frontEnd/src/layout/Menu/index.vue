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
</script>

<template>
  <el-menu
    :default-active="activeIndex"
    class="el-menu-vertical-demo"
    :collapse="isCollapse"
    background-color="#304156"
    router
  >
    <div class="logo" @click="goHome">
      <!-- 展开状态：显示图片和文字 -->
      <div v-if="!isCollapse" class="logo-expand">
        <img :src="MenuLogo" alt="logo" />
        <span class="logo-title">图书借阅管理系统</span>
      </div>
      <!-- 折叠状态：只显示图片 -->
      <div v-else class="logo-collapse">
        <img :src="MenuLogo" alt="logo" />
      </div>
    </div>
    <left-menu :menuList="menuList" />
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

    // &:hover {
    //   opacity: 0.8;
    // }

    .logo-expand {
      display: flex;
      align-items: center;
      justify-content: flex-start;
      width: 100%;
      padding: 0 12px;
      
      img {
        width: 36px;
        height: 36px;
        margin-right: 12px;
      }

      .logo-title {
        color: #fff;
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
    width: 213px;
    min-height: 400px;
  }
  .el-menu {
    height: 100%;
    border-right: none;
  }
  :deep(.el-sub-menu .el-sub-menu__title){
    color: #f4f4f5 !important;
  }
  :deep(.el-menu .el-menu-item){
    color: #bfcbd9;
  }
  /* 菜单点中文字的颜色 */
  :deep(.el-menu-item.is-active){
    color: #409eff !important;
  }
  /* 当前打开菜单的所有子菜单颜色 */
  :deep(.is-opened .el-menu-item){
    background-color: #1f2d3d !important;
  }
  /* 鼠标移动菜单的颜色 */
  :deep(.el-menu-item:hover){
    background-color: #001528 !important;
  }
</style>
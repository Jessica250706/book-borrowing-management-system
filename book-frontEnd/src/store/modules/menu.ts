import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { MenuOptions } from '../interface'

export const useMenuStore = defineStore('MenuState', () => {
    // state
    const isCollapse = ref(false)
    const menuList = ref<MenuOptions[]>([])
    const activeMenu = ref('learning-square')
    const closeMenuList = ref<string[]>([])

    // getters
    const getActiveMenu = computed(() => activeMenu.value)
    const getCloseMenuList = computed(() => closeMenuList.value)
    const getMenuList = computed(() => menuList.value)

    // actions
    function setCollapse() {
        isCollapse.value = !isCollapse.value
    }

    function setMenuList(list: MenuOptions[]) {
        menuList.value = list
    }

    function setActiveMenu(menu: string) {
        activeMenu.value = menu
    }

    function setCloseMenuList(list: string[]) {
        closeMenuList.value = list
    }

    return {
        // state
        isCollapse,
        menuList,
        activeMenu,
        closeMenuList,
        // getters
        getActiveMenu,
        getCloseMenuList,
        getMenuList,
        // actions
        setCollapse,
        setMenuList,
        setActiveMenu,
        setCloseMenuList,
    }
}, {
    // persist: {
    //   paths: ['activeMenu'],
    // },
    persist: true,
})
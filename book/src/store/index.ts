import { defineStore } from 'pinia'
import { ref, reactive, computed } from 'vue'

export const GlobalStore = defineStore('GlobalState', () => {
    // State
    const token = ref('')
    const userInfo = reactive({
        username: '',
        id: -1,
        token: '',
    })

    // Getters (使用 computed)
    const isLoggedIn = computed(() => !!token.value)
    const userId = computed(() => userInfo.id)
    const userName = computed(() => userInfo.username)

    // Actions (函数)
    function setToken(newToken: string) {
        token.value = newToken
        userInfo.token = newToken
    }

    function setUserInfo(info: Partial<typeof userInfo>) {
        Object.assign(userInfo, info)
    }

    function clearUser() {
        token.value = ''
        Object.assign(userInfo, {
            username: '',
            id: -1,
            token: '',
        })
    }

    return {
        // State
        token,
        userInfo,

        // Getters
        isLoggedIn,
        userId,
        userName,

        // Actions
        setToken,
        setUserInfo,
        clearUser,
    }
})
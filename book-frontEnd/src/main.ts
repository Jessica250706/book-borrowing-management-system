import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from '../src/router/index'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// 引入pinia构造函数
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate' //插件
import zhCn from 'element-plus/es/locale/lang/zh-cn' // 引入中文语言包

// 实例化pinia
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

const app = createApp(App)

// 注意：要先使用 pinia，再挂载
app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  locale: zhCn, // 配置为中文
})

//全局注册图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
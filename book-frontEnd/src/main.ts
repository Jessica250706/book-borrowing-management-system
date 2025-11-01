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

// 实例化pinia
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

const app = createApp(App)

app.use(router)
app.mount('#app')
app.use(ElementPlus)
app.use(pinia)

//全局注册图标组件
for (const [key, component] of 
Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
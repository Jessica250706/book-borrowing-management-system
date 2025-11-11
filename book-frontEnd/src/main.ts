/*
 * @Author: Jessica Wang 1271736670@qq.com
 * @Date: 2025-11-02 15:58:38
 * @LastEditors: Jessica Wang 1271736670@qq.com
 * @LastEditTime: 2025-11-11 11:41:57
 * @FilePath: \book-frontEnd\src\main.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
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

app.use(router)
app.mount('#app')
app.use(ElementPlus, {
  locale: zhCn, // 配置为中文
})
app.use(pinia)

//全局注册图标组件
for (const [key, component] of 
Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
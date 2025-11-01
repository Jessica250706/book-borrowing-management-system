import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0', // 任何ip都可以访问
    port: 8080, // 项目端口号
    hmr: true, //开启热加载
    open: true, //自动打开浏览器
  },
  resolve: {
    alias: [
      {
        find: '@',
        replacement: resolve(__dirname,'src')
      }
    ]
  }
})

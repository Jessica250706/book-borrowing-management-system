import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server:{
    host: '0.0.0.0', //允许任何ip都可以访问当前项目
    port: 8080, //项目的端口号
    hmr: true, //启用热加载
    open: true //项目启动之后自动打开浏览器
  },
  resolve: {
    alias:[
      {
        find: '@',
        replacement: resolve(__dirname,'src')
      }
    ]
  },
  define:{
    'process.env': {
      'BASE_API':"http://localhost:8089"
    }
}
})

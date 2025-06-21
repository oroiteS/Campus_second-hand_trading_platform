const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    host: "0.0.0.0",
    port: 8079,
    // proxy: {
    //   '/cart': {
    //     target: 'http://localhost:8085', // 你的 Spring Boot 后端端口
    //     changeOrigin: true
    //   }
    // }
  }
})

module.exports = {
  devServer: {
    port: 9418,
    open: true,
    host: '0.0.0.0',
    // proxy: {
    //   '/api-8092': {
    //     target: 'http://localhost:3000',
    //     changeOrigin: true,
    //     secure: false,
    //     logLevel: 'debug'
    //   }
    // }
  }
};

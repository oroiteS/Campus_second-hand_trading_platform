module.exports = {
  devServer: {
    port: 9418,
    open: true,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8092',
        changeOrigin: true,
        secure: false,
        logLevel: 'debug'
      }
    }
  }
};

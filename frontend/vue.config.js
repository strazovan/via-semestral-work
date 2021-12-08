module.exports = {
  transpileDependencies: [
    'vuetify'
  ],
  devServer: {
    proxy: {
      '/be': {
        target: 'http://localhost:18080/'
      }
    }
  }
}

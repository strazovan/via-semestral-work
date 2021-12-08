module.exports = {
  transpileDependencies: [
    'vuetify'
  ],
  devServer: {
    proxy: {
      '/be': {
        target: 'http://localhost:1808/'
      }
    }
  }
}
